package samoe_kreativnoe.burmolda;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.util.Objects;


public class Controller {
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
    void look_in_bd() {
        if (!user_login.getText().equals("") & !user_password.getText().equals("")){ //Прописать проверку на наличие человека в базе данных
            Controll_helper.visable_pane(welcome, home);
        }else{ //Иначе запросить зарегестрироваться
            no_pas_log.setText("Ой, неправильный логин или пароль");
        }
    }
    @FXML
    void go_welcome() {Controll_helper.visable_pane(home, welcome);}
    @FXML
    void go_home_roll() {
        Controll_helper.visable_pane(roll_desk, home);
        profile_balance.setText(Controll_helper.get_balance() + "₽");
    }
    @FXML
    void go_home_mines() {
        Controll_helper.visable_pane(mines_desk, home);
        profile_balance.setText(Controll_helper.get_balance() + "₽");
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
    public static float get_balance() {//Прописать получение баланса
        return 10;
    }
}
