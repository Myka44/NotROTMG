module com.notrotmg.notrotmg {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires org.java_websocket;
    requires com.fasterxml.jackson.databind;


    opens com.notrotmg.notrotmg to javafx.fxml;
    exports com.notrotmg.notrotmg;
}
