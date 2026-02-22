package ejo.tradescavenger.util;


import com.ejo.util.file.FileCSVMap;
import com.ejo.util.time.DateTime;

import java.io.*;
import java.util.TreeMap;

//CSV files in TradeScavenger run on the format: DATE, OPEN, HIGH, LOW, CLOSE, VOLUME

public class CSVConverter {

    private final FileCSVMap<String,String> inputFile;
    private final FileCSVMap<String,String> outputFile;

    public CSVConverter(FileCSVMap<String, String> inputFile, FileCSVMap<String,String> outputFile) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    public void convertFile(TimeFrame timeFrame, int dateIndex, int openIndex, int maxIndex, int minIndex, int closeIndex, int volIndex) throws IOException {
        File file = new File(inputFile.getFolderPath() + "/" + inputFile.getFileName());
        FileReader fileReader = new FileReader(file);
        BufferedReader reader = new BufferedReader(fileReader);

        TreeMap<String,String[]> convertedData = new TreeMap<>();

        String line;
        while ((line = reader.readLine()) != null) {
            try {
                String[] data = line.split(",");
                String id = convertDateToDateTimeIDString(data[dateIndex], timeFrame);

                String open = data[openIndex];
                String max = data[maxIndex];
                String min = data[minIndex];
                String close = data[closeIndex];
                String volume = data[volIndex];

                convertedData.put(id, new String[]{open, max, min, close, volume});
            } catch (Exception _) {
                System.out.println("Could not convert line: " + line);
            }
        }
        reader.close();
        fileReader.close();

        outputFile.save(convertedData);
        outputFile.unLoad();

    }

    // TODO: make an enum selector of multiple functions in the DataCenter for DateFormat.
    //  List specific formats like: YYYY-MM-DD-HH-MM-SS or YYYY-MM-DD or something else
    private static String convertDateToDateTimeIDString(String dateString, TimeFrame timeFrame) {
        //if (true) return dateString; //If you want to keep the current id, use this
        switch (timeFrame) {
            case ONE_DAY -> {
                String[] split = dateString.split("-");
                String year = split[0];
                String month = split[1];
                String day = split[2];
                String hour = "09";
                String minute = "30";
                String second = "00";

                return year + month + day + hour + minute + second;
            }
            default -> {
                String[] split = dateString.split(" ");
                String[] date = split[0].split("-");
                String[] time = split[1].split(":");
                String year = date[0];
                String month =  date[1];
                String day =  date[2];
                String hour = time[0];
                String minute = time[1];
                String second = time[2];

                return year + month + day + hour + minute + second;
            }
        }
    }

    static void main() throws IOException {
        CSVConverter converter = new CSVConverter(new FileCSVMap<>("stock_data","SPY_30sec"),new FileCSVMap<>("stock_data","SPY_30sec_N"));
        converter.convertFile(TimeFrame.ONE_MINUTE,0,1,2,3,4,5);
    }


}
