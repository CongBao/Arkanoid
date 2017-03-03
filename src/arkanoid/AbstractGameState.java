package arkanoid;

/**
 * AbstractGameState
 *
 * @author Cong Bao
 */
public abstract class AbstractGameState {

    protected Game game;

    protected AbstractGameState(Game game) {
        this.game = game;
    }

    /**
     * Triggered when the game initialization completed.
     */
    public void onGameInitialized() {
    }

    /**
     * Triggered each time the ball was lunched.
     */
    public void onGameStarted() {
    }

    /**
     * Triggered when the game is paused.
     */
    public void onGamePaused() {
    }

    /**
     * Triggered when the game resumed from pause.
     */
    public void onGameResumed() {
    }

    /**
     * Triggered when the game was destroied.
     */
    public void onGameDestroied() {
    }

    /**
     * Triggered when the balls in a level is cleared.
     */
    public void onBallCleared() {
    }

    /**
     * Triggered when the ball is out of boundary.
     */
    public void onBallLost() {
    }

    /**
     * Triggered when all levels in the game is cleared.
     */
    public void onLevelCleared() {
    }

}
