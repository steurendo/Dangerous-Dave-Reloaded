package utils;

import ui.Game;

import javax.swing.*;

public class ErrorDialog {
    public static void show(String message) {
        JOptionPane errorPane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE);
        JDialog dialog = errorPane.createDialog(Game.WINDOW_TITLE);
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }
}
