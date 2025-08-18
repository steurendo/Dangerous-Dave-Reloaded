package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GamedataReader {
    private static BufferedReader fileReader = null;

    public static void init() throws IOException {
        fileReader = new BufferedReader(new FileReader("gamedata.dat"));
    }

    public static void read(char[] out) throws IOException {
        if (fileReader == null) throw new IOException("The class \"GamedataReader\" was not initialized.");
        int readCount = fileReader.read(out, 0, 8);
    }
}
