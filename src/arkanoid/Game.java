package arkanoid;

import static arkanoid.Configuration.*;
import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.Nifty;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <code>Game</code> class is the class implementing basic game logic.
 * <code>Game</code> class will initialize a graphical user interface when the
 * application start, but will not load assets until the method
 * {@link arkanoid.Game#initGame()} is called.
 * <p>
 * Usually, all game logic should be processed in this class, and should not
 * shift to other classes.</p>
 * <p>
 * Issues that beyond game logic such as GUI and audio should not be handled in
 * this class. Instead, independent classes should be created and extending
 * {@link arkanoid.AbstractGameState} class to deal with events.</p>
 * <p>
 * The structure of nodes</p>
 * <pre>
 * rootNode
 * |-- table
 * |   |-- plane
 * |   |-- borders
 * |   |   |-- borderL
 * |   |   |-- borderR
 * |   |   |-- borderT
 * |   |-- obstacles
 * |-- board
 * |   |-- [board4, board3, borad2]
 * |-- balls
 * |   |-- (ballG)*
 * |-- launch
 * |   |-- ballR
 * |   |-- arrow
 * |-- props
 * |   |-- magnet
 * |   |-- bomb
 * |   |   |-- flash
 * |   |   |-- flame
 * </pre>
 *
 * @author Cong Bao
 * @see com.jme3.app.SimpleApplication
 */
public class Game extends SimpleApplication {

    // the event manager
    private AbstractGameState gameState;

    // scene assets
    private Spatial plane;
    private Spatial borderL, borderR, borderT;
    private Spatial board4, board3, board2;
    private Geometry ballG, ballR;
    private Geometry arrow;
    private Geometry magnet;
    private ParticleEmitter flash, flame;
    private Node table;
    private Node borders;
    private Node obstacles;
    private Node board;
    private Node balls;
    private Node launch;
    private Node props;
    private Node bomb;

    // lights
    private AmbientLight al;
    private DirectionalLight dl;

    // volatile parameters
    private Vector3f direction;
    private float ballSpeed;
    private float boardSpeed;

    // displayed parameters
    private int level = START_LEVEL;
    private int score = START_SCORE;
    private int combo = START_COMBO;
    private int item1 = START_ITEM1;
    private int item2 = START_ITEM2;

    // game flags
    private boolean initialized = false;
    private boolean start = false;
    private boolean running = false;
    private boolean clear = false;
    private boolean absorbing = false;
    private boolean exploding = false;

    private AnalogListener analogHandler = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            if (!start) {
                if (name.equals("Left")) {
                    Float posX = arrow.getUserData("posX");
                    posX = posX <= -1.732f ? -1.732f : posX - tpf;
                    ((Arrow) arrow.getMesh()).setArrowExtent(new Vector3f(posX, FastMath.sqrt(4 - FastMath.sqr(posX)), 0));
                    arrow.setUserData("posX", posX);
                } else if (name.equals("Right")) {
                    Float posX = arrow.getUserData("posX");
                    posX = posX >= 1.732f ? 1.732f : posX + tpf;
                    ((Arrow) arrow.getMesh()).setArrowExtent(new Vector3f(posX, FastMath.sqrt(4 - FastMath.sqr(posX)), 0));
                    arrow.setUserData("posX", posX);
                }
                return;
            }
            if (!running) {
                return;
            }
            if (name.equals("Left")) {
                if (board.getLocalTranslation().x >= levConfig[level - 1].get("boardMinX").floatValue()) {
                    board.move(Vector3f.UNIT_X.mult(-boardSpeed * tpf));
                }
            } else if (name.equals("Right")) {
                if (board.getLocalTranslation().x <= levConfig[level - 1].get("boardMaxX").floatValue()) {
                    board.move(Vector3f.UNIT_X.mult(boardSpeed * tpf));
                }
            }
        }
    };

    private ActionListener actionHandler = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (!isPressed && name.equals("Escape")) {
                stop();
            }
            if (!start) {
                if (!isPressed && name.equals("Space")) {
                    Float posX = arrow.getUserData("posX");
                    direction = new Vector3f(posX, FastMath.sqrt(4 - FastMath.sqr(posX)), 0).normalize();
                    start = true;
                    running = true;
                    launch.detachChild(arrow);
                    gameState.onGameStarted();
                }
                return;
            }
            if (!isPressed && name.equals("Num1")) {
                if (item1 > 0 && !absorbing) {
                    absorbing = true;
                    props.attachChild(magnet);
                    item1--;
                    gameState.onAbsorptionStarted();
                } else if (absorbing) {
                    absorbing = false;
                    props.detachChild(magnet);
                    gameState.onAbsorptionEnded();
                }
            }
            if (!isPressed && name.equals("Num2")) {
                if (item2 > 0 && !exploding) {
                    exploding = true;
                    item2--;
                    gameState.onExplosionStarted();
                }
            }
            if (!isPressed && name.equals("Space")) {
                running = !running;
                if (running) {
                    gameState.onGameResumed();
                } else {
                    gameState.onGamePaused();
                }
            }
        }
    };

    /**
     * Initialize the GUI.
     * This method is called by {@link #simpleInitApp()} automatically.
     * Do not call it manually.
     */
    public void initGui() {
        ScreenManager screenManager = new ScreenManager(this);
        gameState = screenManager;

        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
        Nifty nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/screen.xml", "start", screenManager);
        guiViewPort.addProcessor(niftyDisplay);
    }

    /**
     * Initialize the game, which includes scene initialization and light initialization.
     * This method should be called when loading assets and create a base game scene.
     * Usually, this method should be called only once.
     * As a result, the flag <code>initialized</code> will be set as <code>true<code/>.
     */
    public void initGame() {
        if (initialized) {
            return;
        }
        initScene();
        initLight();
        initialized = true;
        gameState.onGameInitialized();
    }

    /**
     * Initialize the scene.
     * This method is called by {@link #initGame()} automatically.
     * Do not call it manually.
     */
    public void initScene() {
        // initialize table
        plane = assetManager.loadModel("Models/Plane.j3o");
        borderL = assetManager.loadModel("Models/BorderL.j3o");
        borderR = assetManager.loadModel("Models/BorderR.j3o");
        borderT = assetManager.loadModel("Models/BorderT.j3o");
        borders = new Node("Borders");
        borders.attachChild(borderL);
        borders.attachChild(borderR);
        borders.attachChild(borderT);
        obstacles = new Node("Obstacles");
        table = new Node("Table");
        table.attachChild(plane);
        table.attachChild(borders);
        table.attachChild(obstacles);
        rootNode.attachChild(table);

        // initialize boards
        board4 = assetManager.loadModel("Models/Board4.j3o");
        board3 = assetManager.loadModel("Models/Board3.j3o");
        board2 = assetManager.loadModel("Models/Board2.j3o");
        board = new Node("Board");
        rootNode.attachChild(board);

        // initialize balls
        ballG = new Geometry("ballG", new Sphere(50, 50, .5f));
        ballR = new Geometry("ballR", new Sphere(50, 50, .5f));
        Material matG = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        Material matR = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        matG.setTexture("DiffuseMap", assetManager.loadTexture("Textures/green.jpg"));
        matR.setTexture("DiffuseMap", assetManager.loadTexture("Textures/red.jpg"));
        ballG.setMaterial(matG);
        ballR.setMaterial(matR);
        balls = new Node("Balls");
        launch = new Node("Launch");
        rootNode.attachChild(balls);
        rootNode.attachChild(launch);

        // initialize arrow
        arrow = new Geometry("arrow", new Arrow(Vector3f.UNIT_Y.mult(2)));
        Material matA = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matA.setColor("Color", ColorRGBA.Red);
        arrow.setMaterial(matA);
        arrow.move(14, 1.5f, DEPTH);

        // initialize magnet
        magnet = new Geometry("magnet", new Sphere(50, 50, 1));
        Material matM = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matM.setColor("Color", ColorRGBA.DarkGray);
        magnet.setMaterial(matM);
        magnet.move(14, 14, DEPTH);
        magnet.setUserData("time", 0f);

        // initialize bomb
        flash = new ParticleEmitter("Flash", ParticleMesh.Type.Point, 32);
        flash.setSelectRandomImage(true);
        flash.setStartColor(new ColorRGBA(1f, .8f, .36f, 1f));
        flash.setEndColor(new ColorRGBA(1f, .8f, .36f, 0f));
        flash.setStartSize(.1f);
        flash.setEndSize(3f);
        flash.setShape(new EmitterSphereShape(Vector3f.ZERO, .05f));
        flash.setParticlesPerSec(0);
        flash.setGravity(0, 0, 0);
        flash.setLowLife(.2f);
        flash.setHighLife(.2f);
        flash.setInitialVelocity(new Vector3f(0, 5f, 0));
        flash.setVelocityVariation(1);
        flash.setImagesX(2);
        flash.setImagesY(2);
        Material matSh = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        matSh.setTexture("Texture", assetManager.loadTexture("Textures/Effects/flash.png"));
        matSh.setBoolean("PointSprite", true);
        flash.setMaterial(matSh);
        flame = new ParticleEmitter("Flame", ParticleMesh.Type.Point, 32);
        flame.setSelectRandomImage(true);
        flame.setStartColor(new ColorRGBA(1f, .4f, .05f, 1f));
        flame.setEndColor(new ColorRGBA(.4f, .22f, .12f, 0f));
        flame.setStartSize(1.3f);
        flame.setEndSize(2f);
        flame.setShape(new EmitterSphereShape(Vector3f.ZERO, 1f));
        flame.setParticlesPerSec(0);
        flame.setGravity(0, -5, 0);
        flame.setLowLife(.4f);
        flame.setHighLife(.5f);
        flame.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 7, 0));
        flame.getParticleInfluencer().setVelocityVariation(1f);
        flame.setImagesX(2);
        flame.setImagesY(2);
        Material matMe = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        matMe.setTexture("Texture", assetManager.loadTexture("Textures/Effects/flame.png"));
        matMe.setBoolean("PointSprite", true);
        flame.setMaterial(matMe);
        bomb = new Node("Bomb");
        bomb.attachChild(flash);
        bomb.attachChild(flame);
        bomb.move(14, 14, DEPTH);
        bomb.setUserData("time", 0f);
        bomb.setUserData("state", 0);
        renderManager.preloadScene(bomb);
        props = new Node("Props");
        props.attachChild(bomb);
        rootNode.attachChild(props);
    }

    /**
     * Initialize the light in scene.
     * This method is called by {@link #initGame()} automatically.
     * Do not call it manually.
     */
    public void initLight() {
        al = new AmbientLight();
        al.setColor(AL_COLOR);
        rootNode.addLight(al);

        dl = new DirectionalLight();
        dl.setColor(DL_COLOR);
        dl.setDirection(Vector3f.UNIT_Z.negate());
        rootNode.addLight(dl);
    }

    /**
     * Enable the keyboard inputs.
     * Call this method to initialize key mappings.
     */
    public void enableKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A), new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D), new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("Num1", new KeyTrigger(KeyInput.KEY_1), new KeyTrigger(KeyInput.KEY_NUMPAD1));
        inputManager.addMapping("Num2", new KeyTrigger(KeyInput.KEY_2), new KeyTrigger(KeyInput.KEY_NUMPAD2));
        inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Escape", new KeyTrigger(KeyInput.KEY_ESCAPE));
        inputManager.addListener(analogHandler, "Left", "Right");
        inputManager.addListener(actionHandler, "Num1", "Num2", "Space", "Esc");
    }

    /**
     * Disable the keyboard inputs.
     */
    public void disableKeys() {
        inputManager.removeListener(analogHandler);
        inputManager.removeListener(actionHandler);
        inputManager.deleteMapping("Left");
        inputManager.deleteMapping("Right");
        inputManager.deleteMapping("Num1");
        inputManager.deleteMapping("Num2");
        inputManager.deleteMapping("Space");
        inputManager.deleteMapping("Escape");
    }

    /**
     * Configure the scene of each level.
     * This method should be called when initializing the scene, or when the level is changed.
     */
    public void configureLevel() {
        // configure board
        boardSpeed = levConfig[level - 1].get("boardSpeed").floatValue();
        try {
            board.detachAllChildren();
            board.attachChild((Spatial) getClass().getDeclaredField("board" + levConfig[level - 1].get("boardLength").intValue()).get(this));
            board.setLocalTranslation(12, 0, 2);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }

        // configure red ball and arrow
        launch.detachAllChildren();
        direction = new Vector3f(1, 1, 0).normalize();
        ballSpeed = levConfig[level - 1].get("ballSpeed").floatValue();
        ballR.setLocalTranslation(14, 1.5f, DEPTH);
        launch.attachChild(ballR);
        arrow.setUserData("posX", 0f);
        ((Arrow) arrow.getMesh()).setArrowExtent(Vector3f.UNIT_Y.mult(2));
        launch.attachChild(arrow);

        // configure green balls
        balls.detachAllChildren();
        for (int i = 0; i < levConfig[level - 1].get("ballNum").intValue(); i++) {
            Geometry ball = ballG.clone();
            ball.setName("ballG" + i);
            ball.setLocalTranslation(ballMap[level - 1][i]);
            balls.attachChild(ball);
        }

        // configure props
        props.detachChild(magnet);
        item1 = START_ITEM1;
        item2 = START_ITEM2;
    }

    /**
     * @see com.jme3.app.SimpleApplication#simpleInitApp()
     */
    @Override
    public void simpleInitApp() {
        viewPort.setBackgroundColor(ColorRGBA.Gray);
        flyCam.setEnabled(false);
        cam.setFrustumPerspective(45, (float) settings.getWidth() / (float) settings.getHeight(), 1, 100);
        cam.setLocation(new Vector3f(14, 14, 35));
        cam.lookAt(new Vector3f(14, 14, 0), Vector3f.UNIT_Y);

        initGui();
    }

    /**
     * @see com.jme3.app.SimpleApplication#simpleUpdate(float)
     * @param tpf time per frame
     */
    @Override
    public void simpleUpdate(float tpf) {
        // if not running or haven't started just return
        if (!running || !start) {
            return;
        }

        // test if it should upgrade the level or finish the game
        if (clear && level < TOTAL_LEVELS) {
            level++;
            score = START_SCORE;
            combo = START_COMBO;
            item1 = START_ITEM1;
            gameState.onBallCleared();
            start = running = clear = absorbing = exploding = false;
            return;
        } else if (clear && level >= TOTAL_LEVELS) {
            level = START_LEVEL;
            score = START_SCORE;
            combo = START_COMBO;
            item1 = START_ITEM1;
            gameState.onLevelCleared();
            start = running = clear = absorbing = exploding = false;
            return;
        }

        // test if all green balls are cleared
        if (balls.getChildren().isEmpty()) {
            clear = true;
            return;
        }

        // test if the red ball is lost
        if (ballR.getLocalTranslation().y < DEATH_BOUNDARY) {
            score = START_SCORE;
            combo = START_COMBO;
            item1 = START_ITEM1;
            gameState.onBallLost();
            start = running = clear = absorbing = exploding = false;
            return;
        }

        // keep red ball moving
        ballR.move(direction.mult(ballSpeed * tpf));

        // test if the magnet is using
        if (absorbing) {
            magnet.setUserData("time", (Float) magnet.getUserData("time") + tpf);
            direction = magnet.getLocalTranslation().subtract(ballR.getLocalTranslation()).normalize().mult(MAGNET_GRAVITATION).add(direction).normalize();
            CollisionResults magnetResults = new CollisionResults();
            magnet.collideWith(ballR.getWorldBound(), magnetResults);
            if (magnetResults.size() > 0) {
                Vector3f normal = magnet.getLocalTranslation().subtract(ballR.getLocalTranslation());
                direction = normal.mult((direction.dot(normal) * 2) / normal.dot(normal)).subtract(direction).negate();
                combo = START_COMBO;
                gameState.onCollided();
            }
            if ((Float) magnet.getUserData("time") > 5f) {
                absorbing = false;
                props.detachChild(magnet);
                magnet.setUserData("time", 0f);
                gameState.onAbsorptionEnded();
            }
        }

        // test if the bomb is using
        if (exploding) {
            bomb.setUserData("time", (Float) bomb.getUserData("time") + tpf);
            if ((Float) bomb.getUserData("time") > 0f && (Integer) bomb.getUserData("state") == 0) {
                ballSpeed = 0;
                al.setColor(ColorRGBA.DarkGray);
                dl.setColor(ColorRGBA.Black);
                bomb.setUserData("state", (Integer) bomb.getUserData("state") + 1);
            }
            if ((Float) bomb.getUserData("time") > .5f && (Integer) bomb.getUserData("state") == 1) {
                flash.emitAllParticles();
                bomb.setUserData("state", (Integer) bomb.getUserData("state") + 1);
            }
            if ((Float) bomb.getUserData("time") > .55f && (Integer) bomb.getUserData("state") == 2) {
                flame.emitAllParticles();
                bomb.setUserData("state", (Integer) bomb.getUserData("state") + 1);
            }
            if ((Float) bomb.getUserData("time") > 2f && (Integer) bomb.getUserData("state") == 3) {
                flash.killAllParticles();
                flame.killAllParticles();
                exploding = false;
                ballSpeed = levConfig[level - 1].get("ballSpeed").floatValue();
                al.setColor(AL_COLOR);
                dl.setColor(DL_COLOR);
                bomb.setUserData("time", 0f);
                bomb.setUserData("state", 0);
                bomb.setUserData("ballSpeed", null);
                gameState.onExplosionEnded();
            }
        }

        // test the collision with borders
        if (ballR.getLocalTranslation().x <= LEFT_BOUNDARY) {
            direction.x = FastMath.abs(direction.x);
            combo = START_COMBO;
            gameState.onCollided();
        } else if (ballR.getLocalTranslation().x >= RIGHT_BOUNDARY) {
            direction.x = -FastMath.abs(direction.x);
            combo = START_COMBO;
            gameState.onCollided();
        } else if (ballR.getLocalTranslation().y >= TOP_BOUNDARY) {
            direction.y = -FastMath.abs(direction.y);
            combo = START_COMBO;
            gameState.onCollided();
        }

        // test the collision with board
        CollisionResults boardResults = new CollisionResults();
        board.collideWith(ballR.getWorldBound(), boardResults);
        if (boardResults.size() > 0) {
            direction.y = FastMath.abs(direction.y);
            combo = START_COMBO;
            gameState.onCollided();
        }

        // test the collision with green balls
        CollisionResults ballResults = new CollisionResults();
        for (Spatial ball : balls.getChildren()) {
            ball.collideWith(ballR.getWorldBound(), ballResults);
            if (ballResults.size() > 0) {
                ballResults.clear();
                balls.setUserData("remove", ball);
                balls.setUserData("normal", ball.getLocalTranslation().subtract(ballR.getLocalTranslation()));
            }
        }
        if (balls.getUserData("remove") != null) {
            combo++;
            score += combo;
            gameState.onCollided();
            balls.detachChild((Spatial) balls.getUserData("remove"));
            gameState.onBallRemoved();
            Vector3f normal = balls.getUserData("normal");
            direction = normal.mult((direction.dot(normal) * 2) / normal.dot(normal)).subtract(direction).negate();
            balls.setUserData("remove", null);
            balls.setUserData("normal", null);
        }
    }

    /**
     * @see com.jme3.app.Application#destroy()
     */
    @Override
    public void destroy() {
        super.destroy();
        gameState.onGameDestroied();
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getCombo() {
        return combo;
    }

    public void setCombo(int combo) {
        this.combo = combo;
    }

    public int getItem1() {
        return item1;
    }

    public void setItem1(int item1) {
        this.item1 = item1;
    }

    public int getItem2() {
        return item2;
    }

    public void setItem2(int item2) {
        this.item2 = item2;
    }

    public boolean isStart() {
        return start;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isClear() {
        return clear;
    }

    public boolean isAbsorbing() {
        return absorbing;
    }

    public AppSettings getSettings() {
        return settings;
    }

}
