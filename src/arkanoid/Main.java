package arkanoid;

import com.jme3.system.AppSettings;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * The launch class.
 *
 * @author Cong Bao
 */
public class Main {

    public static void main(String[] args) {
        try {
            FileHandler fh = new FileHandler("log.txt");
            fh.setLevel(Level.ALL);
            fh.setFormatter(new SimpleFormatter());
            Logger.getLogger("com.jme3").addHandler(fh);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        AppSettings settings = new AppSettings(true);
        settings.setTitle("Arkanoid");
        settings.setResolution(1280, 720);
        Game game = new Game();
        game.setSettings(settings);
//        game.setShowSettings(false);
//        game.setDisplayFps(false);
//        game.setDisplayStatView(false);
        game.setPauseOnLostFocus(false);
        game.start();
    }

}
