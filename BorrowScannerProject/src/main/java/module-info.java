module be.esi.prj {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;

    opens be.esi.prj.controller to javafx.fxml;
    exports be.esi.prj;
}