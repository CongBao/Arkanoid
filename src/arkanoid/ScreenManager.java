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
 * ScreenManager
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

    public void quitGame() {
        game.stop();
    }

    public void tryAgain() {
        delayLoading("level", 1000);
    }

    public int getLevel() {
        return game.getLevel();
    }

    public int getScore() {
        return game.getScore();
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
    public void onBallCleared() {
        game.disableKeys();
        nifty.getScreen("level").findElementByName("level_text").getRenderer(TextRenderer.class).setText("Level " + getLevel());
        delayLoading("level", 1000);
    }

    @Override
    public void onBallLost() {
        game.disableKeys();
        delayLoading("game_over", 1000);
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
            Element backBtn = screen.findElementByName("BackBtn");
            backBtn.setConstraintWidth(width);
            backBtn.setConstraintHeight(height);
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
