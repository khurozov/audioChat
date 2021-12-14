package uz.ilyoskhurozov.audiochat;

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

    public ReceiveAndPlay(int port) throws LineUnavailableException {
        this.port = port;
        Info info = new Info(SourceDataLine.class, this.getAudioFormat());
        this.sdl = (SourceDataLine)AudioSystem.getLine(info);
    }

    private AudioFormat getAudioFormat() {
        float sampleRate = 96000.0F;
        int sampleSizeInBits = 16;
        int channels = 2;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, true, true);
    }

    public void start() {
        this.isReceiving = true;

        try {
            this.sdl.open(this.getAudioFormat());
            this.sdl.start();
            MulticastSocket socket = new MulticastSocket(this.port);
            byte[] audio = new byte[256];
            DatagramPacket receivePacket = new DatagramPacket(audio, audio.length);
            Thread thread = new Thread(() -> {
                while(this.isReceiving) {
                    try {
                        socket.receive(receivePacket);
                        this.sdl.write(audio, 0, receivePacket.getLength());
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
        this.isReceiving = false;
        this.sdl.stop();
        this.sdl.close();
    }
}