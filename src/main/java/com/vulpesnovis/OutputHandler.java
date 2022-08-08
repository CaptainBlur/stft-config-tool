package com.vulpesnovis;

import com.android.sdklib.util.SparseIntArray;
import com.vulpesnovis.StftFilter.NewWindowListener;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;

public class OutputHandler {

    PrintWriter writer;
    public OutputHandler(int outputType) {
        prepareFileCSV();
    }

    private void prepareFileCSV(){

        File CSVFile = new File("CSV.txt");

        try {
            if (CSVFile.createNewFile()){
                System.out.println("File created");
            }
            else {
                CSVFile.delete();
                CSVFile.createNewFile();
                System.out.println("File already exists. Creating new one");
            }
        } catch (IOException e) {
            System.out.println("Cannot create or delete file. Exiting");
            System.exit(1);
        }

        try {
            writer = new PrintWriter(CSVFile);
            writer.println("time,k,val");
        } catch (IOException e) {
            System.out.println("Cannot write to file. Exiting");
            System.exit(1);
        }
    }

    private final NewWindowListener CSVListener = new NewWindowListener() {
        @Override
        public void onWindowComputed(int timestamp, SparseIntArray fftSnapshot) {
            for (int i = 0; i < fftSnapshot.size(); i++) {
                writer.println(timestamp + "," + ((float)fftSnapshot.keyAt(i)/100) + "," + fftSnapshot.valueAt(i));
            }
        }

        @Override
        public void endOfStream() {
            writer.flush();
            writer.close();
        }
    };

    public NewWindowListener getCSVListener() {
        return CSVListener;
    }

    /**
     * This method must be called before requesting Processor to process,
     * because it will force PrintWriter to close the output
     */
    public void printSource(double[] source){
        for (int i = 0; i < source.length; i++) {
            writer.println("," + i + "," + source[i]);
        }
        writer.flush();
        writer.close();
    }
}
