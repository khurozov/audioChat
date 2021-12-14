module uz.ilyoskhurozov.audiochat {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens uz.ilyoskhurozov.audiochat to javafx.fxml;
    exports uz.ilyoskhurozov.audiochat;
}