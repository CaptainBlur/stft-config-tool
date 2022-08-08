package com.vulpesnovis;

import com.vulpesnovis.StftFilter.Processor;

public class Main {
    public static void main (String[] args){
        Wav_reader reader = new Wav_reader();
        OutputHandler output = new OutputHandler(1);
        Processor processor = new Processor(reader.getSampleRate(),11, 20, output.getCSVListener(), true, true);
        processor.process(reader.getDecodedInput());
        System.exit(0);
    }
}
