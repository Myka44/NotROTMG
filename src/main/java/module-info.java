module com.notrotmg.game {
    requires javafx.controls;
    requires java.net.http;
    requires io.javalin;
    requires com.fasterxml.jackson.databind;

    exports com.notrotmg.client;
    exports com.notrotmg.common.json;
    exports com.notrotmg.domain;
    exports com.notrotmg.domain.enemy;
    exports com.notrotmg.domain.item;
    exports com.notrotmg.protocol;
    exports com.notrotmg.server;

    opens com.notrotmg.protocol to com.fasterxml.jackson.databind;
}
