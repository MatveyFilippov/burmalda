package samoe_kreativnoe.burmalda;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.awt.Desktop;


public class Controller {
    @FXML
    private GridPane grid_min, grid_kef_min;
    @FXML
    private ImageView ph_gall;
    @FXML
    private Button close_btn_gall, st_btn_min;
    @FXML
    private Hyperlink prof_gall;
    @FXML
    private ProgressBar progress_gall;
    @FXML
    private Text balance_in_gall, bid2win_gall, sha_in_proof, de_sha_in_proof, balance_in_mines;
    @FXML
    private TextArea text_to_send_callback;
    @FXML
    private AnchorPane welcome, home, message_back, mines_desk, gall_desk, when_play_gall, to_start_gall, prof;
    @FXML
    private MenuButton profile_balance;
    @FXML
    private Label error_server, no_pas_log, word_gall, theme_gall, theme_mines;
    @FXML
    private TextField user_login, sum_on_what_play_gall, sum_on_what_play_min;
    @FXML
    private PasswordField user_password;

    @FXML
    void go_enter() throws Exception {
        if (!user_login.getText().equals("") && !user_password.getText().equals("")){
            String in = Controll_helper.in_bd(user_login.getText(), user_password.getText());
            if (in.equals("in")) {
                Controll_helper.visable_pane(welcome, home);
                profile_balance.setText(Controll_helper.balance + "₽");
            }else{
                if (in.equals("ERROR 404"))
                    no_pas_log.setText("Извините, попробуйте позже...");
                else
                    no_pas_log.setText("Неправильный логин или пароль");
            }
        } else {
            user_password.setPromptText("Обязательное поле");
            user_password.setStyle("-fx-prompt-text-fill: red;");
            user_login.setPromptText("Обязательное поле");
            user_login.setStyle("-fx-prompt-text-fill: red;");
        }
    }
    @FXML
    void go_to_TGbot() throws Exception {
        Desktop.getDesktop().browse(new URI("https://t.me/BurmaldaAutorizationBot"));
        Controll_helper.get_info_from_bd("https://burmalda.dvervevre.repl.co");
    }
    @FXML
    void go_welcome() {Controll_helper.visable_pane(home, welcome);}
    @FXML
    void open_call_back() {
        message_back.setVisible(!message_back.isVisible());
        text_to_send_callback.setPromptText("Напишите что-нибудь)");
        text_to_send_callback.setStyle("-fx-prompt-text-fill: grey;");
    }
    @FXML
    void send_callback() throws Exception {
        if (!text_to_send_callback.getText().equals("")){
            String ok = Controll_helper.get_info_from_bd("https://burmalda.dvervevre.repl.co/send_callback");
            if (!ok.equals("ERROR 404")) {
                text_to_send_callback.setPromptText("Отправлено!");
                text_to_send_callback.setStyle("-fx-prompt-text-fill: green;");
            }else{
                text_to_send_callback.setPromptText("Ошибка! Данный функционал не доработан, сообщение не отправлено");
                text_to_send_callback.setStyle("-fx-prompt-text-fill: red;");
            }
            text_to_send_callback.setText("");
        } else {
            text_to_send_callback.setPromptText("Обязательное поле!");
            text_to_send_callback.setStyle("-fx-prompt-text-fill: red;");
        }
    }
    @FXML
    void open_mines() throws Exception {
        if (mines_desk.isVisible()){
            Controll_helper.visable_pane(mines_desk, home);
            String bal = Controll_helper.update_balance(user_login.getText());
            if (bal.equals("ERROR 404")){
                error_server.setVisible(true);
                profile_balance.setText("Профиль");
            } else{
                error_server.setVisible(false);
                profile_balance.setText(bal);
            }
        }else{
            balance_in_mines.setText("Баланс: " + Controll_helper.balance + "₽");
            Controll_helper.visable_pane(home, mines_desk);
        }
    }
    @FXML
    void open_gall() throws Exception {
        if (gall_desk.isVisible()){
            Controll_helper.visable_pane(gall_desk, home);
            String bal = Controll_helper.update_balance(user_login.getText());
            if (bal.equals("ERROR 404")){
                error_server.setVisible(true);
                profile_balance.setText("Профиль");
            } else{
                error_server.setVisible(false);
                profile_balance.setText(bal);
            }
        }else{
            balance_in_gall.setText("Баланс: " + Controll_helper.balance + "₽");
            Controll_helper.visable_pane(home, gall_desk);
        }
    }
    @FXML
    void gall_175(){
        de_sha_in_proof.setText("будет известен после игры");
        Gallow.start("1", to_start_gall, when_play_gall, sum_on_what_play_gall, balance_in_gall, theme_gall, bid2win_gall, word_gall, progress_gall, ph_gall);
    }
    @FXML
    void gall_35() {
        de_sha_in_proof.setText("будет известен после игры");
        Gallow.start("2", to_start_gall, when_play_gall, sum_on_what_play_gall, balance_in_gall, theme_gall, bid2win_gall, word_gall, progress_gall, ph_gall);
    }
    @FXML
    void get_letter_gall(ActionEvent event) {
        Button clicked = (Button) event.getSource();
        clicked.setDisable(true);
        if (Gallow.word_qst.contains(clicked.getText().toLowerCase())){
            if (Gallow.letter_in(clicked.getText()) == 1){
                Gallow.disable_btns(when_play_gall, true);
                Controll_helper.balance += Gallow.will_be;
                close_btn_gall.setText("ЗАКОНЧИТЬ");
                theme_gall.setText("Победа!");
                de_sha_in_proof.setText(Gallow.word_qst);
            }
        } else{
            if (Gallow.no_letter() > 7){
                Gallow.disable_btns(when_play_gall, true);
                close_btn_gall.setText("ЗАКОНЧИТЬ");
                theme_gall.setText("Проигрыш");
                de_sha_in_proof.setText(Gallow.word_qst);
            }
        }
    }
    @FXML
    void gall_stop() throws Exception {
        Controll_helper.visable_pane(when_play_gall, to_start_gall);
        theme_gall.setText("Виселица");
        balance_in_gall.setText("Баланс: " + Controll_helper.update_balance(user_login.getText()));
        Gallow.disable_btns(when_play_gall, false);
        Gallow.is_play = false;
        close_btn_gall.setText("СДАТЬСЯ");
        Image image = new Image(Gallow.where_imgs+"/src/main/resources/samoe_kreativnoe/burmalda/gall_ph/gallow_0.png");
        ph_gall.setImage(image);
    }
    @FXML
    void min_start_stop() throws Exception {
        if (st_btn_min.getText().equals("Начать игру")){
            if (!sum_on_what_play_min.getText().equals("")){
                try {
                    if (Controll_helper.balance - Math.abs(Float.parseFloat(sum_on_what_play_min.getText())) >= 0){
                        Controll_helper.balance -= Math.abs(Float.parseFloat(sum_on_what_play_min.getText()));
                        balance_in_mines.setText("Баланс: " + Controll_helper.balance + "₽");
                        Mines.start(sum_on_what_play_min, grid_min, theme_mines);
                        grid_kef_min.setDisable(true);
                        de_sha_in_proof.setText("будет известен после игры");
                        sum_on_what_play_min.setPromptText("Можно забрать: " + sum_on_what_play_min.getText() + "₽");
                        sum_on_what_play_min.setStyle("-fx-prompt-text-fill: grey;");
                        sum_on_what_play_min.setText("");
                        sum_on_what_play_min.setDisable(true);
                        st_btn_min.setText("Забрать");
                        theme_mines.setText("Следующая: x" + Mines.kaf[0]);
                    } else {
                        sum_on_what_play_min.setText("");
                        sum_on_what_play_min.setPromptText("Недостаточно средств");
                        sum_on_what_play_min.setStyle("-fx-prompt-text-fill: red;");
                    }
                } catch (NumberFormatException e) {
                    sum_on_what_play_min.setText("");
                    sum_on_what_play_min.setPromptText("Требуется число");
                    sum_on_what_play_min.setStyle("-fx-prompt-text-fill: red;");
                }
            }else {
                sum_on_what_play_min.setPromptText("Обязательное поле");
                sum_on_what_play_min.setStyle("-fx-prompt-text-fill: red;");
            }
        } else {
            Controll_helper.balance += Mines.can_get;
            grid_min.setDisable(true);
            grid_kef_min.setDisable(false);
            balance_in_mines.setText("Баланс: " + Controll_helper.update_balance(user_login.getText()));
            st_btn_min.setText("Начать игру");
            Mines.is_play = false;
            sum_on_what_play_min.setDisable(false);
            sum_on_what_play_min.setPromptText("Введите ставку");
            theme_mines.setText("Мины");
        }
    }
    @FXML
    void click_mines(ActionEvent event){
        Button clicked = (Button) event.getSource();
        clicked.setDisable(true);
        int row = 0, col = 0;
        try{
            row = GridPane.getRowIndex(clicked);
        } catch (NullPointerException ignored){}
        try {
            col = GridPane.getColumnIndex(clicked);
        } catch (NullPointerException ignored){}
        if (Mines.is_lose(col, row)){
            Mines.open_btns(grid_min, false);
            de_sha_in_proof.setText(Mines.sqrToString());
            st_btn_min.setText("Закончить");
        } else {
            if (Mines.kaf.length == Mines.open){
                Mines.open_btns(grid_min, true);
                de_sha_in_proof.setText(Mines.sqrToString());
            }else{
                clicked.setText(String.valueOf(Mines.kaf[Mines.open-1]));
            }
        }
    }
    @FXML
    void switch_kef_min(ActionEvent event){
        ObservableList<Node> children = grid_kef_min.getChildren();
        for (Node btn : children) {
            if (btn instanceof Button) {
                btn.setDisable(false);
            }
        }
        Button clicked = (Button) event.getSource();
        clicked.setDisable(true);
        String[] str = clicked.getText().split(" ");
        Mines.another_kef(str[0]);
    }
    @FXML
    void prof_res(MouseEvent event) throws NoSuchAlgorithmException {
        prof.setVisible(true);
        sha_in_proof.setText("Будет известен после начала игры");
        Hyperlink clickedLink = (Hyperlink) event.getSource();
        String id = clickedLink.getId();
        if (id.equals(prof_gall.getId())){
            if (Gallow.is_play) {
                sha_in_proof.setText(Controll_helper.get_SHA_hash(Gallow.word_qst));
            } else de_sha_in_proof.setText("");
        } else{
            if (Mines.is_play){
                sha_in_proof.setText(Controll_helper.get_SHA_hash(Mines.sqrToString()));
            } else de_sha_in_proof.setText("");
        }
    }
    @FXML
    void copy_text(MouseEvent event){
        Text clickedText = (Text) event.getSource();
        String text = clickedText.getText();
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
    }
    @FXML
    void close_prof(){prof.setVisible(false);}
    @FXML
    void go_sha_gen()throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://www.movable-type.co.uk/scripts/sha256.html"));
    }
}


class Controll_helper{

    public static float balance;

    public static void visable_pane(AnchorPane close, AnchorPane open) {
        open.setVisible(true);
        close.setVisible(false);
    }
    public static String update_balance(String login) throws Exception {
        String bd = get_info_from_bd("https://burmalda.dvervevre.repl.co/update_balance/" + login + "/" + balance);
        int i = 0;
        while (bd.equals("ERROR 404")){
            Thread.sleep(4000);
            bd = get_info_from_bd("https://burmalda.dvervevre.repl.co/update_balance/" + login + "/" + balance);
            i++;
            if (i >= 5)
                break;
        }
        if (!bd.equals(String.valueOf(balance)))
            return "ERROR 404";
        return balance + "₽";
    }
    public static String in_bd(String login, String pass) throws Exception {
        String bd = get_info_from_bd("https://burmalda.dvervevre.repl.co");
        int i = 0;
        while (bd.equals("ERROR 404")){
            Thread.sleep(4000);
            bd = get_info_from_bd("https://burmalda.dvervevre.repl.co");
            i++;
            if (i >= 5)
                return "ERROR 404";
        }
        int start = 0;
        while ((start = bd.indexOf(">>>", start)) != -1) {
            int end = bd.indexOf("<<<", start);
            if (end == -1) {
                break;
            }
            String substring = bd.substring(start + 3, end);
            String[] parts = substring.split(" :: | ;; ");
            String ac_log = parts[0], ac_pass = parts[1];
            start = end + 3;
            if (ac_log.equals(login) && ac_pass.equals(get_SHA_hash(pass))) {
                balance = Float.parseFloat(parts[2]);
                return "in";
            }
        }
        return "not_in";
    }
    public static String get_info_from_bd(String link) throws Exception{
        HttpURLConnection con;
        URL url = new URL(link);

        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            return String.valueOf(content);
        }catch (Exception ex){
            return "ERROR 404";
        }
    }
    public static String get_SHA_hash(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder result = new StringBuilder();
        for (byte b : hash) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
