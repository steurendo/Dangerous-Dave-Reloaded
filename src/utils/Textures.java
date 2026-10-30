package utils;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.*;
import java.nio.*;

import javax.imageio.*;

import org.lwjgl.*;
import org.lwjgl.opengl.*;

public class Textures {
    private int textureBackground;
    private int textureEntities;
    private int textureMovingEntities;
    private int textureGameParts;
    private int textureLevelsLeft;
    private int textureScoreParts;
    private int textureTilemap;
    private int texturePickupEffects;

    public Textures() {
        try {
            textureBackground = loadTexture(ImageIO.read(ResourceLoader.load(this, "background.png")));
            BufferedImage entities = ImageIO.read(ResourceLoader.load(this, "entities.png"));
            textureEntities = loadTexture(entities);
            texturePickupEffects = loadTexture(withoutBlack(entities));
            textureMovingEntities = loadTexture(ImageIO.read(ResourceLoader.load(this, "moving_entities.png")));
            textureGameParts = loadTexture(ImageIO.read(ResourceLoader.load(this, "game_parts.png")));
            textureLevelsLeft = loadTexture(ImageIO.read(ResourceLoader.load(this, "levels_left.png")));
            textureScoreParts = loadTexture(ImageIO.read(ResourceLoader.load(this, "score_parts.png")));
            textureTilemap = loadTexture(ImageIO.read(ResourceLoader.load(this, "tilemap.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getTextureBackground() {
        return textureBackground;
    }

    public int getTexturePickupEffects() {
        return texturePickupEffects;
    }

    private static BufferedImage withoutBlack(BufferedImage image) {
        BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < image.getHeight(); y++)
            for (int x = 0; x < image.getWidth(); x++) {
                int argb = image.getRGB(x, y);
                copy.setRGB(x, y, (argb & 0xFFFFFF) == 0 ? 0 : argb);
            }
        return copy;
    }

    public int getTextureEntities() {
        return textureEntities;
    }

    public int getTextureMovingEntities() {
        return textureMovingEntities;
    }

    public int getTextureGameParts() {
        return textureGameParts;
    }

    public int getTextureLevelsLeft() {
        return textureLevelsLeft;
    }

    public int getTextureScoreParts() {
        return textureScoreParts;
    }

    public int getTextureTilemap() {
        return textureTilemap;
    }

    public static void bindTexture(int texture) {
        glBindTexture(GL_TEXTURE_2D, texture);
    }

    public static int loadTexture(BufferedImage image) {
        int x, y, pixel, textureID;
        int[] pixels;
        ByteBuffer buffer;

        pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
        for (y = 0; y < image.getHeight(); y++)
            for (x = 0; x < image.getWidth(); x++) {
                pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                buffer.put((byte) ((pixel) & 0xFF));
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        buffer.flip();
        textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        glBindTexture(GL_TEXTURE_2D, 0);

        return textureID;
    }
}