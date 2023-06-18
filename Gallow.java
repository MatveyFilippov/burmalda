package samoe_kreativnoe.burmalda;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import java.util.Random;


public class Gallow {
    private static int to_lose;
    public static String word_qst;
    public static boolean is_play = false;
    public static float will_be;
    private static ProgressBar prbar;
    private static Label lable;
    private static ImageView img;
    public static final String where_imgs = "file:"+System.getProperty("user.dir");

    public static void start(String num, AnchorPane close, AnchorPane open, TextField sum, Text bal, Label theme, Text bid2win, Label word, ProgressBar pb, ImageView ph) {
        if (!sum.getText().equals("")){
            try {
                float bid = Math.abs(Float.parseFloat(sum.getText()));
                if (Controll_helper.balance - Math.abs(bid) >= 0){
                    is_play = true;
                    to_lose = 0;
                    lable = word;
                    prbar = pb;
                    img = ph;
                    will_be = (float) (bid * 1.75);
                    if (num.equals("2"))
                        will_be = (float) (bid * 3.5);
                    Controll_helper.balance -= bid;
                    bid2win.setText(bid + "₽ -> " + will_be + "₽");
                    prbar.setProgress(0);
                    Image image = new Image(where_imgs+"/src/main/resources/samoe_kreativnoe/burmalda/gall_ph/gallow_0.png");
                    img.setImage(image);
                    Controll_helper.visable_pane(close, open);
                    bal.setText("Баланс: " + Controll_helper.balance + "₽");
                    sum.setPromptText("Введите cумму");
                    sum.setStyle("-fx-prompt-text-fill: grey;");
                    sum.setText("");
                    String word_gell = get_word(num);
                    if (!word_gell.equals("ERROR 404")){
                        if (word_gell.contains("animals"))
                            theme.setText("Тема: животные");
                        if (word_gell.contains("food"))
                            theme.setText("Тема: еда");
                        if (word_gell.contains("sport"))
                            theme.setText("Тема: спорт");
                        lable.setText(word_qst.replaceAll("\\S", " __ "));
                    }else{
                        theme.setText("Тема: " + word_gell);
                        lable.setText(" __  __  __  __  __  __ ");
                    }
                } else {
                    sum.setText("");
                    sum.setPromptText("Недостаточно средств");
                    sum.setStyle("-fx-prompt-text-fill: red;");
                }
            } catch (NumberFormatException e) {
                sum.setText("");
                sum.setPromptText("Требуется число");
                sum.setStyle("-fx-prompt-text-fill: red;");
            }
        }else {
            sum.setPromptText("Обязательное поле");
            sum.setStyle("-fx-prompt-text-fill: red;");
        }
    }
    public static double letter_in(String letter){
        String old = lable.getText();

        if (old.charAt(0) == ' ')
            old = old.substring(1);
        if (old.charAt(old.length() - 1) == ' ')
            old = old.substring(0, old.length() - 1);

        String[] close_letter = old.split("  ");
        int index = word_qst.indexOf(letter.toLowerCase());
        StringBuilder new_word = new StringBuilder();
        float times = 0;
        for (int i = 0; i < word_qst.length(); i++) {
            if (i == index){
                new_word.append(" " + letter + " ");
                index = word_qst.indexOf(letter.toLowerCase(), index + 1);
                times++;
            }
            else
                new_word.append(" " + close_letter[i]+ " ");
        }
        lable.setText(String.valueOf(new_word));
        double progress = prbar.getProgress() + (times/word_qst.length());
        if (String.valueOf(new_word).replace(" ", "").toLowerCase().equals(word_qst))
            progress = 1;
        prbar.setProgress(progress);
        return progress;
    }
    public static int no_letter(){
        to_lose++;
        if (to_lose < 9){
            Image image = new Image(where_imgs+"/src/main/resources/samoe_kreativnoe/burmalda/gall_ph/gallow_"+to_lose+".png");
            img.setImage(image);
        }
        if (to_lose == 8){
            StringBuilder result = new StringBuilder();
            char[] chars = word_qst.toUpperCase().toCharArray();
            for (char aChar : chars) {
                result.append(" " + aChar + " ");
            }
            lable.setText(String.valueOf(result));
        }
        return to_lose;
    }
    public static void disable_btns(AnchorPane when_play_gell, boolean bool){
        ObservableList<Node> children = when_play_gell.getChildren();
        for (Node node : children) {
            if (node instanceof ToolBar) {
                ObservableList<Node> buttons = ((ToolBar) node).getItems();
                for (Node button : buttons) {
                    if (button instanceof Button) {
                        button.setDisable(bool);
                    }
                }
            }
        }
    }
    private static String get_word(String num) {
        try{
            String[] name_of_tema = {"animals_"+num, "food_"+num, "sport_"+num};
            String random_tema = name_of_tema[(int) (Math.random() * name_of_tema.length)];
            String bd = Controll_helper.get_info_from_bd("https://burmalda.dvervevre.repl.co/gell_word/level_" + num + "/" + random_tema);
            if (bd.equals("ERROR 404")){
                word_qst = "ошибка";
                return bd;
            }
            String[] words = bd.split(" ");
            Random random = new Random();
            word_qst = words[random.nextInt(words.length)];
            return random_tema;
        }catch (Exception e){
            word_qst = "ошибка";
            return "ERROR 404";
        }
    }
}
