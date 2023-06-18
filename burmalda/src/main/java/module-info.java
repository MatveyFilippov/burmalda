module samoe_kreativnoe.burmalda {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens samoe_kreativnoe.burmalda to javafx.fxml;
    exports samoe_kreativnoe.burmalda;
}