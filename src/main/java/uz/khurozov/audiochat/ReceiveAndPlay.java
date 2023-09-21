package uz.khurozov.audiochat;

import javafx.scene.control.ProgressBar;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

public class ReceiveAndPlay {
    boolean isReceiving = true;
    final int port;
    final SourceDataLine sdl;
    private ProgressBar playBar;

    public ReceiveAndPlay(int port) throws LineUnavailableException {
        this.port = port;
        Info info = new Info(SourceDataLine.class, getAudioFormat());
        sdl = (SourceDataLine)AudioSystem.getLine(info);
    }

    private AudioFormat getAudioFormat() {
        float sampleRate = 96000.0F;
        int sampleSizeInBits = 16;
        int channels = 2;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, true, true);
    }

    public void start() {
        isReceiving = true;

        try {
            sdl.open(getAudioFormat());
            sdl.start();
            if (playBar != null) playBar.setDisable(false);

            MulticastSocket socket = new MulticastSocket(port);
            byte[] audio = new byte[128];
            DatagramPacket receivePacket = new DatagramPacket(audio, audio.length);
            Thread thread = new Thread(() -> {
                while(isReceiving) {
                    try {
                        socket.receive(receivePacket);

                        if (playBar != null){
                            int sum = 16384; //midpoint
                            for (byte a : audio) {
                                sum += a;
                            }
                            playBar.setProgress(sum / 32640.0);
                        }

                        sdl.write(audio, 0, receivePacket.getLength());
                    } catch (IOException var5) {
                        var5.printStackTrace();
                    }
                }

            });
            thread.setDaemon(true);
            thread.start();
        } catch (IOException | LineUnavailableException var5) {
            var5.printStackTrace();
        }

    }

    public void stop() {
        isReceiving = false;
        sdl.stop();
        sdl.close();
        if (playBar != null) playBar.setDisable(true);
    }

    public void attachBar(ProgressBar playBar) {
        this.playBar = playBar;
    }
}