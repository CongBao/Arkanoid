package arkanoid;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import java.util.Timer;
import java.util.TimerTask;

/**
 * <code>ScreenManager</code> is the class to handle GUI events.
 *
 * @author Cong Bao
 */
public class ScreenManager extends AbstractGameState implements ScreenController {

    private Nifty nifty;
    private Screen screen;

    private final SizeValue width, height;

    public ScreenManager(Game game) {
        super(game);
        width = new SizeValue(String.valueOf(game.getSettings().getWidth() / 5));
        height = new SizeValue(String.valueOf(game.getSettings().getHeight() / 15));
    }

    public void chooseScreen(String id) {
        nifty.gotoScreen(id);
    }

    public void chooseLevel(String level) {
        game.setLevel(Integer.parseInt(level));
        nifty.getScreen("level").findElementByName("level_text").getRenderer(TextRenderer.class).setText("Level " + getLevel());
        chooseScreen("level");
    }

    public void quitGame() {
        game.stop();
    }

    public int getLevel() {
        return game.getLevel();
    }

    public int getScore() {
        return game.getScore();
    }

    public int getCombo() {
        return game.getCombo();
    }

    @Override
    public void onGamePaused() {
        if (screen.getScreenId().equals("hud")) {
            screen.findElementByName("pause_text").getRenderer(TextRenderer.class).setText("Game Paused");
        }
    }

    @Override
    public void onGameResumed() {
        if (screen.getScreenId().equals("hud")) {
            screen.findElementByName("pause_text").getRenderer(TextRenderer.class).setText("");
        }
    }

    @Override
    public void onGameDestroied() {
        nifty.exit();
    }

    @Override
    public void onCollided() {
        if (screen.getScreenId().equals("hud")) {
            screen.findElementByName("combo_text").getRenderer(TextRenderer.class).setText("x " + getCombo());
        }
    }

    @Override
    public void onBallRemoved() {
        if (screen.getScreenId().equals("hud")) {
            screen.findElementByName("score_text").getRenderer(TextRenderer.class).setText(String.valueOf(getScore()));
        }
    }

    @Override
    public void onBallCleared() {
        game.disableKeys();
        nifty.getScreen("level").findElementByName("level_text").getRenderer(TextRenderer.class).setText("Level " + getLevel());
        delayLoading("win", 1000);
    }

    @Override
    public void onBallLost() {
        game.disableKeys();
        delayLoading("game_over", 1000);
    }

    @Override
    public void onLevelCleared() {
        game.disableKeys();
        nifty.getScreen("win").findElementByName("NextBtn").setVisible(false);
        delayLoading("win", 1000);
    }

    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = nifty.getCurrentScreen();
        configureStyle();
    }

    @Override
    public void onStartScreen() {
        screen = nifty.getCurrentScreen();
        if (screen.getScreenId().equals("start")) {
            game.setLevel(Configuration.START_LEVEL);
            game.setScore(Configuration.START_SCORE);
            game.setCombo(Configuration.START_COMBO);
            nifty.getScreen("level").findElementByName("level_text").getRenderer(TextRenderer.class).setText("Level " + getLevel());
            nifty.getScreen("hud").findElementByName("score_text").getRenderer(TextRenderer.class).setText(String.valueOf(getScore()));
            nifty.getScreen("hud").findElementByName("combo_text").getRenderer(TextRenderer.class).setText("x " + getCombo());
        }
        if (screen.getScreenId().equals("hud")) {
            screen.findElementByName("level_text").getRenderer(TextRenderer.class).setText(String.valueOf(getLevel()));
            screen.findElementByName("score_text").getRenderer(TextRenderer.class).setText(String.valueOf(getScore()));
            screen.findElementByName("combo_text").getRenderer(TextRenderer.class).setText("x " + getCombo());
        }
        if (screen.getScreenId().equals("level")) {
            delayLoading("hud", 2000);
        }
    }

    @Override
    public void onEndScreen() {
        if (screen.getScreenId().equals("level")) {
            game.initGame();
            game.enableKeys();
            game.configureLevel();
        }
    }

    private void delayLoading(final String id, final int ms) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                chooseScreen(id);
                timer.cancel();
            }
        }, ms);
    }

    private void configureStyle() {
        if (screen.getScreenId().equals("start")) {
            Element startBtn = screen.findElementByName("StartBtn");
            Element levelBtn = screen.findElementByName("LevelBtn");
            Element quitBtn = screen.findElementByName("QuitBtn");
            startBtn.setConstraintWidth(width);
            startBtn.setConstraintHeight(height);
            levelBtn.setConstraintWidth(width);
            levelBtn.setConstraintHeight(height);
            quitBtn.setConstraintWidth(width);
            quitBtn.setConstraintHeight(height);
        } else if (screen.getScreenId().equals("info")) {
            Element playBtn = screen.findElementByName("PlayBtn");
            playBtn.setConstraintWidth(width);
            playBtn.setConstraintHeight(height);
        } else if (screen.getScreenId().equals("choose_level")) {
            final SizeValue levelBtnWidth = new SizeValue(String.valueOf(game.getSettings().getWidth() / 10));
            final SizeValue levelBtnHeight = new SizeValue(String.valueOf(game.getSettings().getHeight() / 10));
            for (int i = 0; i < Configuration.TOTAL_LEVELS; i++) {
                Element levelBtn = screen.findElementByName("Level" + (i + 1) + "Btn");
                levelBtn.setConstraintWidth(levelBtnWidth);
                levelBtn.setConstraintHeight(levelBtnHeight);
            }
            Element backBtn = screen.findElementByName("BackBtn");
            backBtn.setConstraintWidth(width);
            backBtn.setConstraintHeight(height);
        } else if (screen.getScreenId().equals("win")) {
            Element nextBtn = screen.findElementByName("NextBtn");
            Element returnBtn = screen.findElementByName("ReturnBtn");
            nextBtn.setConstraintWidth(width);
            nextBtn.setConstraintHeight(height);
            returnBtn.setConstraintWidth(width);
            returnBtn.setConstraintHeight(height);
        } else if (screen.getScreenId().equals("game_over")) {
            Element againBtn = screen.findElementByName("AgainBtn");
            Element returnBtn = screen.findElementByName("ReturnBtn");
            againBtn.setConstraintWidth(width);
            againBtn.setConstraintHeight(height);
            returnBtn.setConstraintWidth(width);
            returnBtn.setConstraintHeight(height);
        }
        screen.layoutLayers();
    }

}
