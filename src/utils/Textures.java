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

    public Textures() {
        try {
            textureBackground = loadTexture(ImageIO.read(this.getClass().getResourceAsStream("background.png")));
            textureEntities = loadTexture(ImageIO.read(this.getClass().getResourceAsStream("entities.png")));
            textureMovingEntities = loadTexture(ImageIO.read(this.getClass().getResourceAsStream("moving_entities.png")));
            textureGameParts = loadTexture(ImageIO.read(this.getClass().getResourceAsStream("game_parts.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getTextureBackground() {
        return textureBackground;
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

    public static void bindTexture(int texture) {
        glBindTexture(GL_TEXTURE_2D, texture);
    }

    public static final int loadTexture(BufferedImage image) {
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