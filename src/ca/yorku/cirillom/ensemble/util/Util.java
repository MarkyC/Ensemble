package ca.yorku.cirillom.ensemble.util;

import javax.swing.*;
import javax.swing.border.Border;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * User: Marco
 * Email: cirillom@yorku.ca
 * Date: 2/9/14 8:36 PM.
 * Helpful utilities.
 */
public class Util {

    private Util() { /* Util can't be created */ }

    /**
     * Creates titled JPanel Borders
     * @param title - the title of the border
     * @return a Border
     */
    public static Border createBorder(String title) {
        return BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title);
    }

    /**
     * Gets the file extension
     * @param f - the file to get the extension for
     * @return the File's extension
     */
    public static String getExtension(File f) {
        String filename = f.getName();
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    public static String[] replace (String[] input, String oldStr, String newStr) {
        for (int i = 0; i < input.length; i++) {
            input[i] = input[i].replaceAll(oldStr, newStr);
        }

        return input;
    }

    public static int getFileLength(File f) {
        int lines = 0;
        try (BufferedReader in = new BufferedReader(new FileReader(f))) {
            while (in.readLine() != null) lines++;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }

}
