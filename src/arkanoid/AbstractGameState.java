package arkanoid;

/**
 * <code>AbstractGameState</code> class provides several events for handling.
 * All issues except game logic should be handling by extending this class.
 *
 * @author Cong Bao
 */
public abstract class AbstractGameState {

    public enum CollisionType {
        BALL, WALL, BOARD, MAGNET
    }

    /**
     * The instance of {@link arkanoid.Game} class.
     */
    protected Game game;

    /**
     * All sub-classes of
     * <code>AbstractGameState</code> should provide an instance of
     * {@link arkanoid.Game} class.
     *
     * @param game an instance of {@link arkanoid.Game} class
     */
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
     * Triggered each time a collision occurred.
     * @param type the type of collided object
     */
    public void onCollided(CollisionType type) {
    }

    /**
     * Triggered each time the magnet begin to absorb the ball.
     */
    public void onAbsorptionStarted() {
    }

    /**
     * Triggered each time the absorption finished.
     */
    public void onAbsorptionEnded() {
    }

    /**
     * Triggered each time the bomb exploded.
     */
    public void onExplosionStarted() {
    }

    /**
     * Triggered each time the explosion finished.
     */
    public void onExplosionEnded() {
    }

    /**
     * Triggered each time a ball is collided and removed.
     */
    public void onBallRemoved() {
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
