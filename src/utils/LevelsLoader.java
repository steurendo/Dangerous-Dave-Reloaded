package utils;

import entities.Entity;
import entities.EntityChain;
import entities.MovingEntity;
import game.Level;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

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
    }

    public Level loadLevelsStructure() {
        try {
            int x, y;
            BufferedReader in;
            char[] line;
            BufferedImage tilemapPicture;
            BufferedImage[][] tilemap;

            tilemapPicture = ImageIO.read(ResourceLoader.load(this, "tilemap.png"));
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
            return createLevelsStructure(in, tilemap, 1, decrypt(line));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
        spawnpoint.y += OFFSET_Y;
        //LINK
        in.read(line, 0, 8);
        hasWarpzone = decrypt(line) >= 0;
        //MAPPA + DISEGNO LIVELLO
        map = new boolean[width][TILES_ALONG_Y];
        background = new BufferedImage(width * 32, 320, BufferedImage.TYPE_INT_ARGB);
        gD = background.getGraphics();
        entities = new EntityChain[width];
        movingEntities = new ArrayList<>();
        entitiesMap = new Entity[width][TILES_ALONG_Y];
        for (x = 0; x < width; x++) {
            for (y = OFFSET_Y; y < TILES_ALONG_Y - (3 - OFFSET_Y); y++) {
                in.read(line, 0, 8);
                tileCode = decrypt(line);
                tile = new Point();
                tile.y = tileCode / 8;
                tile.x = tileCode - 8 * tile.y;
                if (tileCode >= 1 && tileCode <= 18) { //BLOCCO
                    map[x][y] = true;
                    gD.drawImage(tilemap[tile.x][tile.y], x * 32, (y - OFFSET_Y) * 32, null);
                    entitiesMap[x][y] = null;
                } else if (tileCode != 0) { //ENTITA'
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
}
