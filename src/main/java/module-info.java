module com.notrotmg.notrotmg {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.notrotmg.notrotmg to javafx.fxml;
    exports com.notrotmg.notrotmg;
}