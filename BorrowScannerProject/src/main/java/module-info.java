module be.esi.prj {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;
    requires com.google.zxing.javase;
    requires com.google.zxing;
    requires java.desktop;
    requires javafx.base;

    opens be.esi.prj.controller to javafx.fxml;
    exports be.esi.prj;
}