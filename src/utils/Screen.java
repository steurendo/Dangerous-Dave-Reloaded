package utils;

public final class Screen {
    private static volatile double pixelScale = 2;

    private Screen() {
    }

    public static double pixelScale() {
        return pixelScale;
    }

    public static void setFramebufferWidth(int width) {
        if (width > 0) pixelScale = width / 640.0;
    }
}
