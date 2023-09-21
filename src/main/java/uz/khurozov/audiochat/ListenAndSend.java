package uz.khurozov.audiochat;

import javafx.scene.control.ProgressBar;

import javax.sound.sampled.*;
import javax.sound.sampled.DataLine.Info;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ListenAndSend {
    private final TargetDataLine tdl;
    private final InetAddress address;
    private final int port;
    private ProgressBar listenBar;
    boolean isListening;

    public ListenAndSend(InetAddress address, int port) throws LineUnavailableException {
        this.address = address;
        this.port = port;
        Info info = new Info(TargetDataLine.class, getAudioFormat());
        if (!AudioSystem.isLineSupported(info)) {
            System.out.println("Line not supported");
            System.exit(0);
        }

        tdl = (TargetDataLine) AudioSystem.getLine(info);
    }

    private AudioFormat getAudioFormat() {
        float sampleRate = 96000.0F;
        int sampleSizeInBits = 16;
        int channels = 2;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, true, true);
    }

    void start() {
        try {
            isListening = true;
            tdl.open(getAudioFormat());
            tdl.start();
            if (listenBar != null) listenBar.setDisable(false);

            AudioInputStream ais = new AudioInputStream(tdl);
            DatagramSocket socket = new DatagramSocket();
            byte[] audio = new byte[128];
            Thread thread = new Thread(() -> {
                while (isListening) {
                    try {
                        int len = ais.read(audio);
                        if (len == -1) {
                            return;
                        }

                        if (listenBar != null) {
                            int sum = 16384; //midpoint
                            for (byte a : audio) {
                                sum += a;
                            }
                            listenBar.setProgress(sum / 32640.0);
                        }

                        DatagramPacket sendPacket = new DatagramPacket(audio, len, address, port);
                        socket.send(sendPacket);
                    } catch (IOException var6) {
                        var6.printStackTrace();
                    }
                }
            });
            thread.setDaemon(true);
            thread.start();
        } catch (IOException | LineUnavailableException var5) {
            var5.printStackTrace();
        }

    }

    void stop() {
        isListening = false;
        tdl.stop();
        tdl.close();
        if (listenBar != null) listenBar.setDisable(true);
    }

    public void attachBar(ProgressBar listenBar) {
        this.listenBar = listenBar;
    }
}
