package com.vulpesnovis;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import com.vulpesnovis.WavFile.WavFile;
import com.vulpesnovis.WavFile.WavFileException;

public class Wav_reader {
    WavFile wavFile;
    File path = null;

    public Wav_reader(){
        readDefault();
    }

    private void readFile(){
        FileDialog fd = new FileDialog(new JFrame());
        fd.setDirectory("~/");
        fd.setVisible(true);

        try {
            path = new File(fd.getFile());
        } catch (NullPointerException e) {
            System.out.println("File not selected");
            System.exit(1);
        }

        System.out.println(path.getAbsolutePath());

        try {
            wavFile = WavFile.openWavFile(path);
        } catch (IOException | WavFileException e) {
            System.out.println("Please choose .wav file");
            System.exit(1);}
        wavFile.display();
    }

    public void readDefault(){
        path = new File("audio.wav");

        try {
            wavFile = WavFile.openWavFile(path);
        } catch (IOException | WavFileException e) {
            System.out.println("Please choose .wav file");
            System.exit(1);}
        wavFile.display();
    }

    public double[] getDecodedInput(){
        int numFrames = (int)wavFile.getNumFrames();
        double[] buffer = new double[numFrames];
        try {
            wavFile.readFrames(buffer, numFrames);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (WavFileException e) {
            throw new RuntimeException(e);
        }
        return buffer;
    }

    public int getSampleRate(){
        return (int)wavFile.getSampleRate();
    }
}
