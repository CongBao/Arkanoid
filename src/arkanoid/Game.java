package arkanoid;

import static arkanoid.Configuration.*;
import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
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
 * Game
 *
 * @author Cong Bao
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
    private Node table;
    private Node borders;
    private Node obstacles;
    private Node board;
    private Node balls;
    private Node launch;

    // volatile parameters
    private Vector3f direction;
    private float  ballSpeed;
    private float boardSpeed;

    // displayed parameters
    private int level = 1;
    private int score = 0;

    // game flags
    private boolean initialized = false;
    private boolean start = false;
    private boolean running = false;
    private boolean clear = false;

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
    }

    /**
     * Initialize the light in scene.
     * This method is called by {@link #initGame()} automatically.
     * Do not call it manually.
     */
    public void initLight() {
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(2f));
        rootNode.addLight(al);

        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White.mult(.4f));
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
        inputManager.addMapping("Space", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Escape", new KeyTrigger(KeyInput.KEY_ESCAPE));
        inputManager.addListener(analogHandler, "Left", "Right");
        inputManager.addListener(actionHandler, "Space", "Esc");
    }

    /**
     * Disable the keyboard inputs.
     */
    public void disableKeys() {
        inputManager.removeListener(analogHandler);
        inputManager.removeListener(actionHandler);
        inputManager.deleteMapping("Left");
        inputManager.deleteMapping("Right");
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
        if (!running || !start) {
            return;
        }
        if (clear && level < TOTAL_LEVELS) {
            level++;
            gameState.onBallCleared();
            start = running = clear = false;
            return;
        } else if (clear && level >= TOTAL_LEVELS) {
            gameState.onLevelCleared();
            start = running = clear = false;
            return;
        }
        if (balls.getChildren().isEmpty()) {
            clear = true;
            return;
        }
        if (ballR.getLocalTranslation().y < DEATH_BOUNDARY) {
            gameState.onBallLost();
            start = running = clear = false;
            return;
        }

        ballR.move(direction.mult(ballSpeed * tpf));

        if (ballR.getLocalTranslation().x <= LEFT_BOUNDARY) {
            direction.x = FastMath.abs(direction.x);
        } else if (ballR.getLocalTranslation().x >= RIGHT_BOUNDARY) {
            direction.x = -FastMath.abs(direction.x);
        } else if (ballR.getLocalTranslation().y >= TOP_BOUNDARY) {
            direction.y = -FastMath.abs(direction.y);
        }

        CollisionResults boardResults = new CollisionResults();
        board.collideWith(ballR.getWorldBound(), boardResults);
        if (boardResults.size() > 0) {
            direction.y = FastMath.abs(direction.y);
        }

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
            score++;
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

    public boolean isStart() {
        return start;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isClear() {
        return clear;
    }

    public AppSettings getSettings() {
        return settings;
    }

}
