package uz.khurozov.audiochat;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Controller {
    @FXML
    private TextField ip;
    @FXML
    private TextField port;
    @FXML
    private Button connectBtn;
    @FXML
    private ToggleButton playBtn;
    @FXML
    private ToggleButton micBtn;
    @FXML
    private ProgressBar playBar;
    @FXML
    private ProgressBar listenBar;

    private static ListenAndSend listenAndSend;
    private static ReceiveAndPlay receiveAndPlay;

    public Controller() {
    }

    @FXML
    void connect() {
        try {
            listenAndSend = new ListenAndSend(InetAddress.getByName(ip.getText()), Integer.parseInt(port.getText()));
            listenAndSend.attachBar(listenBar);

            receiveAndPlay = new ReceiveAndPlay(Integer.parseInt(port.getText()));
            receiveAndPlay.attachBar(playBar);

            connectBtn.setDisable(true);
            ip.setEditable(false);
            port.setEditable(false);

            micBtn.setDisable(false);
            playBtn.setDisable(false);
        } catch (Exception var2) {
            new Alert(AlertType.ERROR, "Something went wrong", new ButtonType[0]).showAndWait();
        }

    }

    @FXML
    void speak() {
        if (micBtn.isSelected()) {
            listenAndSend.start();
        } else {
            listenAndSend.stop();
        }

    }

    @FXML
    void listen() {
        if (playBtn.isSelected()) {
            receiveAndPlay.start();
        } else {
            receiveAndPlay.stop();
        }

    }
}