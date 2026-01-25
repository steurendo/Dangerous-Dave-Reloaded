package utils;

import entities.Entity;
import entities.EntityChain;
import entities.MovingEntity;
import game.Level;
import game.LevelType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static game.Level.OFFSET_Y;
import static game.Level.TILES_ALONG_Y;

public class LevelsLoader {
    private final static int LV = 1997;
    private final static int PW = 94;

    private final Textures textures;
    private final int[] tileCodes;
    private final int[] figuresNumber;
    private final double[] widths;
    private final double[] heights;
    private final int[] scoreValues;
    private final boolean[] mortals;
    private final boolean[] climbables;
    private int levelsCount;

    private static class LogicalLevel {
        Level level;
        int nextLevelId;
        int warpzoneId;

        public boolean hasWarpzone() {
            return warpzoneId != -1;
        }
    }

    private static class LogicalStructure {
        int firstLevelId;
        HashMap<Integer, LogicalLevel> structure = new HashMap<>();
    }

    public LevelsLoader(Textures textures) {
        this.textures = textures;
        tileCodes = new int[]{-1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, 0, 1, 2, 3, 4,
                5, 8, 9, 10, 11, 12, 13, 14,
                15, 16, 17, 18, 19, 20, 21, 6,
                7, 22, 23, 24, 25, 26, 27, 28,
                0, 1, 2, 3, 4, 5, 6, 7};
        figuresNumber = new int[]{-1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, 1, 1, 5, 1, 1,
                1, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 1, 1, 1, 1,
                1, 4, 5, 6, 4, 4, 1, 1,
                4, 4, 4, 4, 4, 4, 4, 4};
        widths = new double[]{-1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, 32, 32, 32, 32, 32,
                32, 32, 32, 32, 32, 32, 32, 32,
                32, 32, 32, 32, 32, 32, 32, 32,
                32, 32, 32, 32, 32, 32, 32, 32,
                54, 42, 42, 48, 28, 24, 30, 32};
        heights = new double[]{-1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, 32, 32, 32, 32, 32,
                32, 32, 32, 32, 32, 32, 32, 32,
                32, 32, 32, 32, 32, 32, 32, 32,
                32, 32, 32, 32, 32, 32, 32, 32,
                30, 34, 42, 40, 10, 16, 30, 32};
        scoreValues = new int[]{0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, -1, -2, 1000, 2000, 0,
                0, 0, 0, 0, 0, 0, 0, 50,
                100, 150, 200, 300, 500, 1000, 1500, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                100, 200, 250, 300, 400, 500, 750, 1000};
        mortals = new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, true, true, true, true, true, true, true,
                true, true, true, true, true, true, true, true,};
        climbables = new boolean[]{false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,
                false, true, true, true, true, true, true, false,
                false, false, false, false, false, false, false, true,
                true, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false,};
        levelsCount = 0;
    }

    public Level loadLevelsStructure() {
        Level firstLevel = null;
        try {
            int x, y;
            BufferedImage tilemapPicture;
            BufferedImage[][] tilemap;

            tilemapPicture = ImageIO.read(ResourceLoader.load(this, "tilemap.png"));
            // Definisco i tiles disponibili
            tilemap = new BufferedImage[8][7];
            for (x = 0; x < 8; x++)
                for (y = 0; y < 7; y++)
                    tilemap[x][y] = tilemapPicture.getSubimage(x * 32, y * 32, 32, 32);

            System.out.println("Initializing levels structure");
            LogicalStructure logicalStructure = loadLevels(tilemap);
            firstLevel = buildLevelStructure(logicalStructure);
        } catch (Exception e) {
            System.out.println("loadLevelsStructure: " + e.getMessage());
            e.printStackTrace();
        }
        return firstLevel;
    }

    public int getLevelsCount() {
        return levelsCount;
    }

    private LogicalStructure loadLevels(BufferedImage[][] tilemap) throws Exception {
        char[] line;
        int readCount;
        LogicalStructure logicalStructure = new LogicalStructure();
        logicalStructure.firstLevelId = -1;
        LogicalLevel logicalLevel;
        int levelId;

        // Inizializzo il reader
        try {
            GamedataReader.init();
        } catch (IOException e) {
            ErrorDialog.show("Error: file 'gamedata.dat' not found.");
            System.exit(0);
        }
        line = new char[8];

        GamedataReader.read(line);
        logicalStructure.firstLevelId = decrypt(line);

        // Leggo sequenzialmente le righe
        // Prima riga del livello (<id>;<width>;<spawnpointX>;<spawnpointY>;<nextLevelId>;<warpzoneId>)
        do {
            readCount = GamedataReader.read(line);
            if (readCount <= 0) continue;

            // Leggo il livello
            levelId = decrypt(line);
            logicalLevel = readLevel(tilemap);
            logicalStructure.structure.put(levelId, logicalLevel);
        } while (readCount > 0);

        return logicalStructure;
    }

    private Level buildLevelStructure(LogicalStructure logicalStructure) {
        HashMap<Integer, LogicalLevel> structure = logicalStructure.structure;
        levelsCount = 0;
        return consumeLogicalLevel(structure, structure.get(logicalStructure.firstLevelId), 1);
    }

    private Level consumeLogicalLevel(HashMap<Integer, LogicalLevel> structure, LogicalLevel logicalLevel, int number) {
        // Livello
        Level level = logicalLevel.level;
        if (logicalLevel.nextLevelId == -1) {
            level.setLevelType(LevelType.ENDGAME);
            return level;
        }
        level.setLevelType(LevelType.LEVEL);
        level.setNumber(number);
        levelsCount += 1;

        // Transizione di livello
        LogicalLevel logicalTransitionLevel = structure.get(logicalLevel.nextLevelId);
        Level transitionLevel = logicalTransitionLevel.level;
        transitionLevel.setLevelType(LevelType.TRANSITION_LEVEL);
        transitionLevel.setNumber(number);

        // Collegamenti
        if (logicalTransitionLevel.nextLevelId != -1) {
            Level nextLevel = consumeLogicalLevel(structure, structure.get(logicalTransitionLevel.nextLevelId), number + 1);
            transitionLevel.setNext(nextLevel);
        }
        level.setNext(transitionLevel);

        // Warpzone
        if (logicalLevel.hasWarpzone()) {
            // Transizione di warpzone
            LogicalLevel logicalTransitionWarpzone = structure.get(logicalLevel.warpzoneId);
            Level transitionWarpzone = logicalTransitionWarpzone.level;
            transitionWarpzone.setLevelType(LevelType.TRANSITION_WARPZONE);
            transitionWarpzone.setNumber(number);

            // Warpzone
            LogicalLevel logicalWarpzone = structure.get(logicalTransitionWarpzone.nextLevelId);
            Level warpzone = logicalWarpzone.level;
            warpzone.setLevelType(LevelType.WARPZONE);
            warpzone.setNumber(number);

            // Transizione da warpzone a livello
            LogicalLevel logicalTransitionFromWarpzone = structure.get(logicalWarpzone.nextLevelId);
            Level transitionFromWarpzone = logicalTransitionFromWarpzone.level;
            transitionFromWarpzone.setLevelType(LevelType.TRANSITION_FROM_WARPZONE);
            transitionFromWarpzone.setNumber(number);

            // Collegamenti
            level.setWarpzone(transitionWarpzone);
            transitionWarpzone.setNext(warpzone);
            warpzone.setNext(transitionFromWarpzone);
            transitionFromWarpzone.setNext(level);
        }

        return level;
    }

    private void addEntity(EntityChain entities, Entity entity) {
        if (entities.getNext() != null)
            addEntity(entities.getNext(), entity);
        else
            entities.setNext(entity);
    }


    private int toDec(String x) {
        int ret, i;

        ret = 0;
        for (i = 0; i < 8; i++)
            ret += (x.charAt(i) >= 65 && x.charAt(i) <= 70 ? (10 + (int) x.charAt(i) - 65) : Integer.parseInt("" + x.charAt(i))) * (int) Math.pow(16, (7 - i));

        return ret;
    }

    //private int decrypt(String src) { return decrypt(src, LV, PW); }
    private int decrypt(char[] src) {
        StringBuilder line;
        int i;

        line = new StringBuilder();
        for (i = 0; i < src.length; i++)
            line.append(src[i]);
        return decrypt(line.toString());
    }

    private int decrypt(String src) {
        return ((toDec(src) / LevelsLoader.PW) - LevelsLoader.LV);
    }

    private LogicalLevel readLevel(BufferedImage[][] tilemap) throws Exception {
        char[] line;
        boolean[][] map;
        boolean[][] climbables;
        int[][] mapCodes;
        LogicalLevel logicalLevel;
        int tileCode, levelWidth;
        Point spawnpoint;
        EntityChain[] entities;
        ArrayList<MovingEntity> movingEntities;
        Entity[][] entitiesMap;
        BufferedImage background;
        Graphics gD;

        line = new char[8];
        logicalLevel = new LogicalLevel();

        // Lunghezza del livello
        GamedataReader.read(line);
        levelWidth = decrypt(line);

        // Spawnpoint
        GamedataReader.read(line);
        spawnpoint = new Point();
        spawnpoint.x = decrypt(line);
        GamedataReader.read(line);
        spawnpoint.y = decrypt(line);
        spawnpoint.y += OFFSET_Y;

        // Link al prossimo livello
        GamedataReader.read(line);
        logicalLevel.nextLevelId = decrypt(line);

        // Link alla warpzone
        GamedataReader.read(line);
        logicalLevel.warpzoneId = decrypt(line);

        // Mappa logica del livello + disegno
        map = new boolean[levelWidth][TILES_ALONG_Y];
        climbables = new boolean[levelWidth][TILES_ALONG_Y];
        mapCodes = new int[levelWidth][TILES_ALONG_Y];
        entities = new EntityChain[levelWidth];
        movingEntities = new ArrayList<>();
        entitiesMap = new Entity[levelWidth][TILES_ALONG_Y];
        for (int x = 0; x < levelWidth; x++) {
            for (int y = OFFSET_Y; y < TILES_ALONG_Y - (3 - OFFSET_Y); y++) {
                GamedataReader.read(line);
                tileCode = decrypt(line);
                mapCodes[x][y] = tileCode;
                climbables[x][y] = this.climbables[tileCode];
                if (tileCode >= 1 && tileCode <= 18) { //BLOCCO
                    map[x][y] = true;
                    entitiesMap[x][y] = null;
                } else if (tileCode != 0) { //ENTITA'
                    Entity entity;

                    map[x][y] = false;
                    if (tileCode < 48) {
                        entity = new Entity(textures.getTextureEntities(),
                                tileCodes[tileCode],
                                figuresNumber[tileCode],
                                new PointD(x * 32 + 16, y * 32 + 16),
                                widths[tileCode],
                                heights[tileCode],
                                scoreValues[tileCode],
                                mortals[tileCode]);
                        entitiesMap[x][y] = entity;
                        if (entities[x] == null)
                            entities[x] = new EntityChain(entity);
                        else
                            addEntity(entities[x], entity);
                    } else {
                        entity = new MovingEntity(textures.getTextureMovingEntities(), tileCodes[tileCode], figuresNumber[tileCode], new PointD(x * 32 + 16, y * 32 + 16), widths[tileCode], heights[tileCode], scoreValues[tileCode], mortals[tileCode]);
                        movingEntities.add((MovingEntity) entity);
                    }
                }
            }
        }

        // Disegno del livello
        background = new BufferedImage(levelWidth * 32, 320, BufferedImage.TYPE_INT_ARGB);
        gD = background.getGraphics();

        for (int x = 0; x < levelWidth; x++) {
            for (int y = OFFSET_Y; y < TILES_ALONG_Y - (3 - OFFSET_Y); y++) {
                tileCode = mapCodes[x][y];
                Point tile = new Point();
                tile.y = tileCode / 8;
                tile.x = tileCode - 8 * tile.y;
                if (tileCode >= 1 && tileCode <= 18) {
                    gD.drawImage(tilemap[tile.x][tile.y], x * 32, (y - OFFSET_Y) * 32, null);
                }
            }
        }
        gD.dispose();

        logicalLevel.level = new Level(
                map,
                climbables,
                levelWidth,
                spawnpoint,
                entities,
                movingEntities,
                entitiesMap,
                Textures.loadTexture(background)
        );

        return logicalLevel;
    }
}
