import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

import javax.imageio.*;

public class Model {
    private final static int LV = 1997;
    private final static int PW = 94;

    private Level levelsRoot;
    private Level currentLevel;
    private Player player;
    private Textures textures;
    private int[] tileCodes;
    private int[] figuresNumber;
    private double[] widths;
    private double[] heights;
    private int[] scoreValues;
    private boolean[] mortals;
    private int state;

    public Model(Textures textures) {
        state = 0;
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
        initializeLevelsStructure();
        currentLevel = levelsRoot;
        player = new Player();
        player.setLocation(new PointD(currentLevel.getSpawnpoint().x * 32 + 16, currentLevel.getSpawnpoint().y * 32 + (32 - Player.HEIGHT / 2)));
    }

    public int getState() {
        return state;
    }

    public Player getPlayer() {
        return player;
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public void reset() {
        state = 0;
        player.reset();
        currentLevel = levelsRoot;
        currentLevel.init();
        player.setLocation(new PointD(currentLevel.getSpawnpoint().x * 32 + 16, currentLevel.getSpawnpoint().y * 32 + (32 - Player.HEIGHT / 2)));
    }

    public void start() {
        state = 1;
    }

    public void nextLevel() {
        currentLevel = currentLevel.getNext();
        nextGenericLevel();
    }

    public void nextWarpzone() {
        currentLevel = currentLevel.getWarpzone();
        nextGenericLevel();
    }

    public void nextGenericLevel() {
        currentLevel.init();
        player.passLevel();
        player.setLocation(new PointD(currentLevel.getSpawnpoint().x * 32 + 16, currentLevel.getSpawnpoint().y * 32 + (32 - Player.HEIGHT / 2)));
    }

    private void initializeLevelsStructure() {
        try {
            int x, y;
            BufferedReader in;
            char[] line;
            BufferedImage tilemapPicture;
            BufferedImage[][] tilemap;

            tilemapPicture = ImageIO.read(this.getClass().getResourceAsStream("tilemap.png"));
            //DEFINISCO I TILES DISPONIBILI
            tilemap = new BufferedImage[8][7];
            for (x = 0; x < 8; x++)
                for (y = 0; y < 7; y++)
                    tilemap[x][y] = tilemapPicture.getSubimage(x * 32, y * 32, 32, 32);
            //INTESTAZIONE
            in = new BufferedReader(new FileReader("gamedata.dat"));//new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("gamedata.dat")));
            line = new char[8];
            in.read(line, 0, 8);
            //SKIPPA IL NUMERO DI WARPZONE
            in.read(new char[8], 0, 8);
            //CARICA I LIVELLI
            levelsRoot = createLevelsStructure(in, tilemap, 1, decrypt(line));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Level createLevelsStructure(BufferedReader in, BufferedImage[][] tilemap, int number, int remains)
            throws Exception {
        if (remains == 0)
            return null;

        char[] line;
        boolean hasWarpzone;
        boolean[][] map;
        int width, x, y, tileCode;
        Point spawnpoint, tile;
        EntityChain[] entities;
        ArrayList<MovingEntity> movingEntities;
        Entity[][] entitiesMap;
        Level level;
        BufferedImage background;
        Graphics gD;

        line = new char[8];
        //LUNGHEZZA LIVELLO
        in.read(line, 0, 8);
        width = decrypt(line);
        //SPAWNPOINT
        in.read(line, 0, 8);
        spawnpoint = new Point();
        spawnpoint.y = decrypt(line) / width;
        spawnpoint.x = decrypt(line) - width * spawnpoint.y;
        //LINK
        in.read(line, 0, 8);
        hasWarpzone = decrypt(line) >= 0;
        //MAPPA + DISEGNO LIVELLO
        map = new boolean[width][10];
        background = new BufferedImage(width * 32, 320, BufferedImage.TYPE_INT_ARGB);
        gD = background.getGraphics();
        entities = new EntityChain[width];
        movingEntities = new ArrayList<MovingEntity>();
        entitiesMap = new Entity[width][10];
        for (x = 0; x < width; x++) {
            for (y = 0; y < 10; y++) {
                in.read(line, 0, 8);
                tileCode = decrypt(line);
                tile = new Point();
                tile.y = tileCode / 8;
                tile.x = tileCode - 8 * tile.y;
                if (tileCode >= 1 && tileCode <= 18) //BLOCCO
                {
                    map[x][y] = true;
                    gD.drawImage(tilemap[tile.x][tile.y], x * 32, y * 32, null);
                    entitiesMap[x][y] = null;
                } else if (tileCode != 0) //ENTITA'
                {
                    Entity entity;

                    map[x][y] = false;
                    if (tileCode < 48) {
                        entity = new Entity(textures.getTextureEntities(), tileCodes[tileCode], figuresNumber[tileCode], new PointD(x * 32 + 16, y * 32 + 16), widths[tileCode], heights[tileCode], scoreValues[tileCode], mortals[tileCode]);
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
        gD.dispose();
        level = new Level(map, width, spawnpoint, entities, movingEntities, entitiesMap, Textures.loadTexture(background), number, createLevelsStructure(in, tilemap, number + 1, remains - 1));
        //if (hasWarpzone)
        //level.setWarpzone(createWarpzone(level, in, tilemap));

        return level;
    }

    private Level createWarpzone(Level linkedLevel, BufferedReader in, BufferedImage[][] tilemap)
            throws Exception {
        char[] line;
        boolean[][] map;
        int width, x, y, tileCode;
        Point spawnpoint, tile;
        EntityChain[] entities;
        ArrayList<MovingEntity> movingEntities;
        Entity[][] entitiesMap;
        Level level;
        BufferedImage background;
        Graphics gD;

        line = new char[8];
        //LUNGHEZZA LIVELLO
        in.read(line, 0, 8);
        width = decrypt(line);
        //SPAWNPOINT
        in.read(line, 0, 8);
        spawnpoint = new Point();
        spawnpoint.y = decrypt(line) / width;
        spawnpoint.x = decrypt(line) - width * spawnpoint.y;
        //LINK
        in.read(line, 0, 8);
        //MAPPA + DISEGNO LIVELLO
        map = new boolean[width][10];
        background = new BufferedImage(width * 32, 320, BufferedImage.TYPE_INT_ARGB);
        gD = background.getGraphics();
        entities = new EntityChain[width];
        movingEntities = new ArrayList<MovingEntity>();
        entitiesMap = new Entity[width][10];
        for (x = 0; x < width; x++)
            for (y = 0; y < 10; y++) {
                in.read(line, 0, 8);
                tileCode = decrypt(line);
                tile = new Point();
                tile.y = tileCode / 8;
                tile.x = tileCode - 8 * tile.y;
                if (tileCode >= 1 && tileCode <= 18) //BLOCCO
                {
                    map[x][y] = true;
                    gD.drawImage(tilemap[tile.x][tile.y], x * 32, y * 32, null);
                } else //ENTITA'
                {
                    Entity entity;

                    map[x][y] = false;
                    if (tileCode < 48) {
                        entity = new Entity(textures.getTextureEntities(), tileCodes[tileCode], figuresNumber[tileCode], new PointD(x * 32 + 16, y * 32 + 16), widths[tileCode], heights[tileCode], scoreValues[tileCode], mortals[tileCode]);
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
        gD.dispose();
        level = new Level(map, width, spawnpoint, entities, movingEntities, entitiesMap, Textures.loadTexture(background), linkedLevel.getNumber(), linkedLevel);

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
        String line;
        int i;

        line = "";
        for (i = 0; i < src.length; i++)
            line += src[i];
        return decrypt(line, LV, PW);
    }

    private int decrypt(String src, int lv, int pw) {
        return ((toDec(src) / pw) - lv);
    }
}