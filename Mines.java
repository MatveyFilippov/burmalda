package samoe_kreativnoe.burmalda;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;


public class Mines {
    private static Label next_label;
    public static double can_get;
    public static boolean is_play = false;
    private static TextField can_get_str;
    private static float bid;
    public static double[] kaf = {1.82, 3.65, 7.63, 16.8, 39.21, 98.04, 266.12, 798.26, 2714.44, 10857.76, 54288.84, 380021.92, 4940284.99};
    public static int open;
    private static int[] square;

    public static void start(TextField textField, GridPane gridPane, Label label) {
        next_label = label;
        can_get_str = textField;
        is_play = true;
        can_get = bid = Math.abs(Float.parseFloat(can_get_str.getText()));
        gridPane.setDisable(false);
        ObservableList<Node> children = gridPane.getChildren();
        for (Node btn : children) {
            if (btn instanceof Button) {
                ((Button) btn).setText("?");
                btn.setDisable(false);
            }
        }
        open = 0;
        square = new int[25];
        for (int i = 0; i < kaf.length; i++) {
            int diamond;
            do {
                diamond = (int) (Math.random() * 25);
            } while (square[diamond] != 0);
            square[diamond] = 1;
        }
    }
    public static String sqrToString(){
        StringBuilder str = new StringBuilder();
        for (int k : square) {
            String smvl = "\uD83D\uDCA3";
            if (k == 1){
                smvl = "\uD83D\uDC8E";
            }
            str.append(" "+ smvl +" ");
        }
        return String.valueOf(str);
    }
    public static boolean is_lose(int col, int row) {
        if (square[row + (5 * col)] == 0){
            can_get = 0;
            can_get_str.setPromptText("Можно забрать: " + can_get + "₽");
            next_label.setText("Проигрыш");
            return true;
        }else{
            can_get = bid * kaf[open];
            open++;
            if (kaf.length == open){
                can_get_str.setPromptText("Заберите " + can_get + "₽");
                next_label.setText("Победа!");
            }else{
                can_get_str.setPromptText("Можно забрать: " + can_get + "₽");
                next_label.setText("Следующая: x" + kaf[open]);
            }
            return false;
        }
    }
    public static void another_kef(String mines) {
        if (mines.equals("1"))
            kaf = new double[]{1.01, 1.03, 1.07, 1.13, 1.18, 1.24, 1.31, 1.39, 1.48, 1.58, 1.69, 1.82, 1.97, 2.15, 2.37,
                    2.63, 2.96, 3.39, 3.95, 4.47, 5.93, 7.91, 11.87, 23.74};
        if (mines.equals("6"))
            kaf = new double[]{1.25, 1.66, 2.25, 3.1, 4.34, 6.2, 9.06, 13.59, 21, 33.61, 56.02, 98.04, 182.08, 364.16,
                    801.16, 2002.91, 6008.75, 24035, 168245};
        if (mines.equals("12"))
            kaf = new double[]{1.82, 3.65, 7.63, 16.8, 39.21, 98.04, 266.12, 798.26, 2714.44, 10857.76, 54288.84,
                    380021.92, 4940284.99};
        if (mines.equals("18"))
            kaf = new double[]{3.39, 13.57, 62.42, 343.35, 2403.5, 24035, 456665};
        if (mines.equals("22"))
            kaf = new double[]{7.91, 95, 2185};
    }
    public static void open_btns(GridPane gridPane, boolean win) {
        ObservableList<Node> children = gridPane.getChildren();
        for (Node btn : children) {
            if (btn instanceof Button) {
                int row = 0, col = 0;
                try{
                    row = GridPane.getRowIndex(btn);
                } catch (NullPointerException ignored){}
                try {
                    col = GridPane.getColumnIndex(btn);
                } catch (NullPointerException ignored){}
                if (square[row + (5 * col)] == 0){
                    if (!win)
                        ((Button) btn).setText("\uD83D\uDCA3");
                } else{
                    ((Button) btn).setText("\uD83D\uDC8E");
                }
            }
        }
        gridPane.setDisable(true);
    }
}
