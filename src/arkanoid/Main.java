package arkanoid;

import com.jme3.system.AppSettings;

/**
 * Main
 *
 * @author Cong Bao
 */
public class Main {

    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Arkanoid");
        settings.setResolution(1280, 720);
        Game game = new Game();
        game.setSettings(settings);
        // game.setDisplayFps(false);
        // game.setDisplayStatView(false);
        game.setPauseOnLostFocus(false);
        game.start();
    }

}
