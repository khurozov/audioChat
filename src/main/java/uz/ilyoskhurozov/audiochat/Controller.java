package uz.ilyoskhurozov.audiochat;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import javax.sound.sampled.LineUnavailableException;
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
    private ToggleButton listenBtn;
    @FXML
    private ToggleButton speakBtn;
    private static ListenAndSend listenAndSend;
    private static ReceiveAndPlay receiveAndPlay;

    public Controller() {
    }

    @FXML
    void connect() {
        try {
            listenAndSend = new ListenAndSend(InetAddress.getByName(this.ip.getText()), Integer.parseInt(this.port.getText()));
            receiveAndPlay = new ReceiveAndPlay(Integer.parseInt(this.port.getText()));
            this.connectBtn.setDisable(true);
            this.ip.setEditable(false);
            this.port.setEditable(false);
            this.listenBtn.setDisable(false);
            this.speakBtn.setDisable(false);
        } catch (LineUnavailableException | UnknownHostException var2) {
            (new Alert(AlertType.ERROR, "Something went wrong", new ButtonType[0])).showAndWait();
        }

    }

    @FXML
    void speak() {
        if (this.speakBtn.isSelected()) {
            listenAndSend.start();
        } else {
            listenAndSend.stop();
        }

    }

    @FXML
    void listen() {
        if (this.listenBtn.isSelected()) {
            receiveAndPlay.start();
        } else {
            receiveAndPlay.stop();
        }

    }
}