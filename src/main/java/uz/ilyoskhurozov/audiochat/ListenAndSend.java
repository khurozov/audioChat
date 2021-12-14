package uz.ilyoskhurozov.audiochat;

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

    public ListenAndSend(InetAddress address, int port) throws LineUnavailableException {
        this.address = address;
        this.port = port;
        Info info = new Info(TargetDataLine.class, this.getAudioFormat());
        if (!AudioSystem.isLineSupported(info)) {
            System.out.println("Line not supported");
            System.exit(0);
        }

        this.tdl = (TargetDataLine)AudioSystem.getLine(info);
    }

    private AudioFormat getAudioFormat() {
        float sampleRate = 96000.0F;
        int sampleSizeInBits = 16;
        int channels = 2;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, true, true);
    }

    void start() {
        try {
            this.tdl.open(this.getAudioFormat());
            this.tdl.start();
            AudioInputStream ais = new AudioInputStream(this.tdl);
            DatagramSocket socket = new DatagramSocket();
            byte[] audio = new byte[256];
            Thread thread = new Thread(() -> {
                while(true) {
                    try {
                        int len = ais.read(audio);
                        if (len == -1) {
                            return;
                        }

                        DatagramPacket sendPacket = new DatagramPacket(audio, len, this.address, this.port);
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
        this.tdl.stop();
        this.tdl.close();
    }
}
