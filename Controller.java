package samoe_kreativnoe.burmolda;


import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Controller {
    public float balance;
    @FXML
    private MenuButton profile_balance;
    @FXML
    private AnchorPane mines_desk;
    @FXML
    private AnchorPane roll_desk;
    @FXML
    private AnchorPane home;
    @FXML
    private AnchorPane welcome;
    @FXML
    private Label no_pas_log;
    @FXML
    private TextField user_login;
    @FXML
    private PasswordField user_password;

    @FXML
    void go_enter() throws Exception {
        if (!user_login.getText().equals("") && !user_password.getText().equals("")){
            if (Controll_helper.is_in_bd(user_login.getText(), user_password.getText())) {
                Controll_helper.visable_pane(welcome, home);
                balance = Controll_helper.update_balance(user_login.getText());
                profile_balance.setText(balance + "₽");
            }else{ //Иначе запросить зарегестрироваться
                no_pas_log.setText("Ой, неправильный логин или пароль");
            }
        } else {
            user_password.setPromptText("Обязательное поле");
            user_password.setStyle("-fx-prompt-text-fill: red;");
            user_login.setPromptText("Обязательное поле");
            user_login.setStyle("-fx-prompt-text-fill: red;");
        }
    }
    @FXML
    void go_welcome() {Controll_helper.visable_pane(home, welcome);}
    @FXML
    void go_home_roll() {
        Controll_helper.visable_pane(roll_desk, home);
        profile_balance.setText(balance + "₽");
    }
    @FXML
    void go_home_mines() {
        Controll_helper.visable_pane(mines_desk, home);
        profile_balance.setText(balance + "₽");
    }
    @FXML
    void play_mines() {Controll_helper.visable_pane(home, mines_desk);}
    @FXML
    void play_roll() {Controll_helper.visable_pane(home, roll_desk);}

}
class Controll_helper{
    public static void visable_pane(AnchorPane close, AnchorPane open) {
        open.setVisible(true);
        close.setVisible(false);
    }
    public static float update_balance(String log) throws Exception {
        String bd = get_info_from_bd();
        int start = 0;
        while ((start = bd.indexOf(">>>", start)) != -1) {
            int end = bd.indexOf("<<<", start);
            if (end == -1) {
                break;
            }
            String substring = bd.substring(start + 3, end);
            start = end + 3;
            String[] parts = substring.split(" :: | ;; ");
            String ac_log = parts[0];
            int user_balance = Integer.parseInt(parts[2]);
            if (ac_log.equals(log))
                return user_balance;
        }
        return 0;
    }
    public static boolean is_in_bd(String login, String pass) throws Exception {
        String bd = get_info_from_bd();
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
            if (ac_log.equals(login) && ac_pass.equals(get_SHA_hash(pass)))
                return true;
        }
        return false;
    }
    private static String get_info_from_bd() throws Exception{
        HttpURLConnection con;
        URL url = new URL("https://burmoldabot.dvervevre.repl.co");

        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        return String.valueOf(content);
    }
    private static String get_SHA_hash(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder result = new StringBuilder();
        for (byte b : hash) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
