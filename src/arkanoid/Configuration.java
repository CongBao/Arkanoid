package arkanoid;

import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import java.util.HashMap;

/**
 * <code>Configuration</code> class defines some constants in game.
 * Ideally, all constants used in the game should refer from this class rather than defining independently.
 *
 * @author Cong Bao
 */
public final class Configuration {

    public static final int TOTAL_LEVELS = 6;
    public static final int START_LEVEL = 1;
    public static final int START_SCORE = 0;
    public static final int START_COMBO = 0;
    public static final int START_ITEM1 = 3;
    public static final int START_ITEM2 = 1;
    public static final int TABLE_WIDTH = 28;
    public static final float ARROW_LENGTH = 2f;
    public static final float ARROW_HALF_MIN = FastMath.PI / 12;
    public static final float ARROW_HALF_MAX = 5 * FastMath.PI / 12;
    public static final float MAGNET_GRAVITATION = .05f;
    public static final float LEFT_BOUNDARY = 2.5f;
    public static final float RIGHT_BOUNDARY = 25.5f;
    public static final float TOP_BOUNDARY = 25.5f;
    public static final float BOTTOM_BOUNDARY = 1.5f;
    public static final float DEATH_BOUNDARY = 1f;
    public static final float DEPTH = 1.5f;
    public static final ColorRGBA AL_COLOR = ColorRGBA.White.mult(2f);
    public static final ColorRGBA DL_COLOR = ColorRGBA.White.mult(.4f);
    public static final Vector3f BRICK_SIZE = new Vector3f(2, 2, 2);

    public static final HashMap<String, Number>[] levConfig;
    public static final Vector3f[][] ballMap;
    public static final Vector3f[][] brickMap;

    static {
        levConfig = new HashMap[TOTAL_LEVELS];
        // level 1
        levConfig[0] = new HashMap<String, Number>();
        levConfig[0].put("ballSpeed", 8f);
        levConfig[0].put("ballNum", 9);
        levConfig[0].put("brickNum", 0);
        levConfig[0].put("boardLength", 4);
        levConfig[0].put("boardSpeed", 13f);
        levConfig[0].put("boardMinX", 2f);
        levConfig[0].put("boardMaxX", 22f);
        // level 2
        levConfig[1] = new HashMap<String, Number>();
        levConfig[1].put("ballSpeed", 10f);
        levConfig[1].put("ballNum", 9);
        levConfig[1].put("brickNum", 4);
        levConfig[1].put("boardLength", 4);
        levConfig[1].put("boardSpeed", 13f);
        levConfig[1].put("boardMinX", 2f);
        levConfig[1].put("boardMaxX", 22f);
        // level 3
        levConfig[2] = new HashMap<String, Number>();
        levConfig[2].put("ballSpeed", 10f);
        levConfig[2].put("ballNum", 10);
        levConfig[2].put("brickNum", 9);
        levConfig[2].put("boardLength", 3);
        levConfig[2].put("boardSpeed", 14f);
        levConfig[2].put("boardMinX", 1.5f);
        levConfig[2].put("boardMaxX", 22.5f);
        // level 4
        levConfig[3] = new HashMap<String, Number>();
        levConfig[3].put("ballSpeed", 12f);
        levConfig[3].put("ballNum", 12);
        levConfig[3].put("brickNum", 7);
        levConfig[3].put("boardLength", 3);
        levConfig[3].put("boardSpeed", 14f);
        levConfig[3].put("boardMinX", 1.5f);
        levConfig[3].put("boardMaxX", 22.5f);
        // level 5
        levConfig[4] = new HashMap<String, Number>();
        levConfig[4].put("ballSpeed", 12f);
        levConfig[4].put("ballNum", 7);
        levConfig[4].put("brickNum", 11);
        levConfig[4].put("boardLength", 2);
        levConfig[4].put("boardSpeed", 15f);
        levConfig[4].put("boardMinX", 1f);
        levConfig[4].put("boardMaxX", 23f);
        // level 6
        levConfig[5] = new HashMap<String, Number>();
        levConfig[5].put("ballSpeed", 15f);
        levConfig[5].put("ballNum", 13);
        levConfig[5].put("brickNum", 11);
        levConfig[5].put("boardLength", 2);
        levConfig[5].put("boardSpeed", 15f);
        levConfig[5].put("boardMinX", 1f);
        levConfig[5].put("boardMaxX", 23f);

        ballMap = new Vector3f[TOTAL_LEVELS][];
        // level 1
        ballMap[0] = new Vector3f[levConfig[0].get("ballNum").intValue()];
        ballMap[0][0] = new Vector3f(8, 22, DEPTH);
        ballMap[0][1] = new Vector3f(14, 22, DEPTH);
        ballMap[0][2] = new Vector3f(20, 22, DEPTH);
        ballMap[0][3] = new Vector3f(8, 16, DEPTH);
        ballMap[0][4] = new Vector3f(14, 16, DEPTH);
        ballMap[0][5] = new Vector3f(20, 16, DEPTH);
        ballMap[0][6] = new Vector3f(8, 10, DEPTH);
        ballMap[0][7] = new Vector3f(14, 10, DEPTH);
        ballMap[0][8] = new Vector3f(20, 10, DEPTH);
        // level 2
        ballMap[1] = new Vector3f[levConfig[1].get("ballNum").intValue()];
        ballMap[1][0] = new Vector3f(14, 22, DEPTH);
        ballMap[1][1] = new Vector3f(9.757f, 20.243f, DEPTH);
        ballMap[1][2] = new Vector3f(18.243f, 20.243f, DEPTH);
        ballMap[1][3] = new Vector3f(8, 16, DEPTH);
        ballMap[1][4] = new Vector3f(14, 16, DEPTH);
        ballMap[1][5] = new Vector3f(20, 16, DEPTH);
        ballMap[1][6] = new Vector3f(9.757f, 11.757f, DEPTH);
        ballMap[1][7] = new Vector3f(18.243f, 11.757f, DEPTH);
        ballMap[1][8] = new Vector3f(14, 10, DEPTH);
        // level 3
        ballMap[2] = new Vector3f[levConfig[2].get("ballNum").intValue()];
        ballMap[2][0] = new Vector3f(14, 22, DEPTH);
        ballMap[2][1] = new Vector3f(8.294f, 17.854f, DEPTH);
        ballMap[2][2] = new Vector3f(12.653f, 17.8541f, DEPTH);
        ballMap[2][3] = new Vector3f(15.347f, 17.8541f, DEPTH);
        ballMap[2][4] = new Vector3f(19.706f, 17.854f, DEPTH);
        ballMap[2][5] = new Vector3f(11.82f, 15.292f, DEPTH);
        ballMap[2][6] = new Vector3f(16.18f, 15.292f, DEPTH);
        ballMap[2][7] = new Vector3f(14, 13.708f, DEPTH);
        ballMap[2][8] = new Vector3f(10.473f, 11.146f, DEPTH);
        ballMap[2][9] = new Vector3f(17.527f, 11.146f, DEPTH);
        // level 4
        ballMap[3] = new Vector3f[levConfig[3].get("ballNum").intValue()];
        ballMap[3][0] = new Vector3f(14, 22, DEPTH);
        ballMap[3][1] = new Vector3f(8.804f, 19, DEPTH);
        ballMap[3][2] = new Vector3f(12.268f, 19f, DEPTH);
        ballMap[3][3] = new Vector3f(15.732f, 19f, DEPTH);
        ballMap[3][4] = new Vector3f(19.196f, 19f, DEPTH);
        ballMap[3][5] = new Vector3f(11, 16, DEPTH);
        ballMap[3][6] = new Vector3f(17, 16, DEPTH);
        ballMap[3][7] = new Vector3f(8.804f, 13f, DEPTH);
        ballMap[3][8] = new Vector3f(12.268f, 13f, DEPTH);
        ballMap[3][9] = new Vector3f(15.732f, 13f, DEPTH);
        ballMap[3][10] = new Vector3f(19.196f, 13f, DEPTH);
        ballMap[3][11] = new Vector3f(14, 10, DEPTH);
        // level 5
        ballMap[4] = new Vector3f[levConfig[4].get("ballNum").intValue()];
        ballMap[4][0] = new Vector3f(5, 16, DEPTH);
        ballMap[4][1] = new Vector3f(8, 13, DEPTH);
        ballMap[4][2] = new Vector3f(11, 10, DEPTH);
        ballMap[4][3] = new Vector3f(14, 13, DEPTH);
        ballMap[4][4] = new Vector3f(17, 16, DEPTH);
        ballMap[4][5] = new Vector3f(20, 19, DEPTH);
        ballMap[4][6] = new Vector3f(23, 22, DEPTH);
        // level 6
        ballMap[5] = new Vector3f[levConfig[5].get("ballNum").intValue()];
        ballMap[5][0] = new Vector3f(5, 22, DEPTH);
        ballMap[5][1] = new Vector3f(23, 22, DEPTH);
        ballMap[5][2] = new Vector3f(8, 19, DEPTH);
        ballMap[5][3] = new Vector3f(20, 19, DEPTH);
        ballMap[5][4] = new Vector3f(11, 16, DEPTH);
        ballMap[5][5] = new Vector3f(17, 16, DEPTH);
        ballMap[5][6] = new Vector3f(14, 13, DEPTH);
        ballMap[5][7] = new Vector3f(11, 10, DEPTH);
        ballMap[5][8] = new Vector3f(17, 10, DEPTH);
        ballMap[5][9] = new Vector3f(8, 7, DEPTH);
        ballMap[5][10] = new Vector3f(20, 7, DEPTH);
        ballMap[5][11] = new Vector3f(5, 4, DEPTH);
        ballMap[5][12] = new Vector3f(23, 4, DEPTH);

        brickMap = new Vector3f[TOTAL_LEVELS][]; // TODO
        // level 1
        brickMap[0] = new Vector3f[levConfig[0].get("brickNum").intValue()];
        // level 2
        brickMap[1] = new Vector3f[levConfig[1].get("brickNum").intValue()];
        brickMap[1][0] = new Vector3f(13, 13, 0);
        brickMap[1][1] = new Vector3f(11, 15, 0);
        brickMap[1][2] = new Vector3f(15, 15, 0);
        brickMap[1][3] = new Vector3f(13, 17, 0);
        // level 3
        brickMap[2] = new Vector3f[levConfig[2].get("brickNum").intValue()];
        brickMap[2][0] = new Vector3f(11, 21, 0);
        brickMap[2][1] = new Vector3f(15, 21, 0);
        brickMap[2][2] = new Vector3f(13, 19, 0);
        brickMap[2][3] = new Vector3f(7.473f, 10.146f, 0);
        brickMap[2][4] = new Vector3f(11.473f, 10.146f, 0);
        brickMap[2][5] = new Vector3f(14.527f, 10.146f, 0);
        brickMap[2][6] = new Vector3f(18.527f, 10.146f, 0);
        brickMap[2][7] = new Vector3f(9.473f, 8.146f, 0);
        brickMap[2][8] = new Vector3f(16.527f, 8.146f, 0);
        // level 4
        brickMap[3] = new Vector3f[levConfig[3].get("brickNum").intValue()];
        brickMap[3][0] = new Vector3f(10, 21, 0);
        brickMap[3][1] = new Vector3f(16, 21, 0);
        brickMap[3][2] = new Vector3f(7.804f, 15, 0);
        brickMap[3][3] = new Vector3f(13, 15, 0);
        brickMap[3][4] = new Vector3f(18.196f, 15, 0);
        brickMap[3][5] = new Vector3f(10, 9, 0);
        brickMap[3][6] = new Vector3f(16, 9, 0);
        // level 5
        brickMap[4] = new Vector3f[levConfig[4].get("brickNum").intValue()];
        brickMap[4][0] = new Vector3f(10, 21, 0);
        brickMap[4][1] = new Vector3f(10, 18, 0);
        brickMap[4][2] = new Vector3f(10, 15, 0);
        brickMap[4][3] = new Vector3f(10, 12, 0);
        brickMap[4][4] = new Vector3f(4, 6, 0);
        brickMap[4][5] = new Vector3f(7, 6, 0);
        brickMap[4][6] = new Vector3f(10, 6, 0);
        brickMap[4][7] = new Vector3f(13, 6, 0);
        brickMap[4][8] = new Vector3f(16, 6, 0);
        brickMap[4][9] = new Vector3f(19, 6, 0);
        brickMap[4][10] = new Vector3f(22, 6, 0);
        // level 6
        brickMap[5] = new Vector3f[levConfig[5].get("brickNum").intValue()];
        brickMap[5][0] = new Vector3f(13, 21, 0);
        brickMap[5][1] = new Vector3f(13, 18, 0);
        brickMap[5][2] = new Vector3f(13, 15, 0);
        brickMap[5][3] = new Vector3f(4, 12, 0);
        brickMap[5][4] = new Vector3f(7, 12, 0);
        brickMap[5][5] = new Vector3f(10, 12, 0);
        brickMap[5][6] = new Vector3f(16, 12, 0);
        brickMap[5][7] = new Vector3f(19, 12, 0);
        brickMap[5][8] = new Vector3f(22, 12, 0);
        brickMap[5][9] = new Vector3f(13, 9, 0);
        brickMap[5][10] = new Vector3f(13, 6, 0);
    }

    private Configuration() {
    }

}
