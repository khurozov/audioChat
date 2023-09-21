module uz.ilyoskhurozov.audiochat {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens uz.khurozov.audiochat to javafx.fxml;
    exports uz.khurozov.audiochat;
}