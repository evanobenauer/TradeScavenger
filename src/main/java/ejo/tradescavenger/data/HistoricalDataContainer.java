package ejo.tradescavenger.data;

import com.ejo.util.file.FileCSV;
import com.ejo.util.file.FileUtil;
import com.ejo.util.setting.Container;
import com.ejo.util.time.DateTime;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;

//Data Hash Setup: Key = DateTimeID, Value = float array
// (maybe use a linked hashmap for better traversing. Memory is much higher though which for this app is not good)
public abstract class HistoricalDataContainer extends FileCSV<HashMap<Long, float[]>,HashMap<Long, float[]>> {

    protected static final int NULL_VAL = -1;

    //Progress Booleans
    private boolean saving;
    private boolean loading;

    //Progress Containers
    private final Container<Double> saveProgress;
    private final Container<Double> loadProgress;

    //The lowest & Highest dates located in the file
    private DateTime dateLow;
    private DateTime dateHigh;


    public HistoricalDataContainer(String folderPath, String fileName) {
        super(folderPath, fileName);
        this.saving = false;
        this.loading = false;
        this.saveProgress = new Container<>(0d);
        this.loadProgress = new Container<>(0d);

        //Originally, the data is set to null. Here we will always have an available hashmap
        this.data = new HashMap<>();
    }

    // =============================

    // DATA METHODS

    // =============================

    public abstract float[] getNullData();

    public float[] getData(DateTime dateTime) {
        if (dateTime == null) return getNullData();

        float[] rawData = getLoadedData().get(dateTime.getDateTimeID());
        if (rawData == null) return getNullData();

        return rawData;
    }


    @Override
    public void unLoad() {
        this.data = new HashMap<>(); //Overwritten to create a new hashmap rather than set to null
        System.gc(); //Calls the garbage collector to force immediate delete of the data
    }

    // =============================

    // LOAD CSV DATA METHODS

    // =============================

    //This load method directly puts loaded data into the map.
    // It does NOT overwrite the entire map like a normal FileCSV would
    // If you need to overwrite the entire map, call unLoad() first
    public HashMap<Long, float[]> load(DateTime startTime, DateTime endTime) {
        this.loadProgress.set(0d);
        this.loading = true;

        //Init file
        File inputFile = new File(FileUtil.getFilePath(folderPath, fileName));

        //Init start & end time IDs
        boolean runTimeCheck = startTime != null && endTime != null;
        long startTimeID = 0;
        long endTimeID = 0;
        if (runTimeCheck) {
            startTimeID = startTime.getDateTimeID();
            endTimeID = endTime.getDateTimeID();
        }

        //Init date ranges
        long dateHigh = 0;
        long dateLow = (long) Math.pow(10, 15); //Lol I made it big, because every datetime will be lower

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            long fileSize = Files.lines(inputFile.toPath()).count();
            long currentRow = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                String[] row = line.split(",");
                long key = Long.parseLong(row[0]);

                //Execute Time Checker
                if (runTimeCheck && (key < startTimeID || key > endTimeID)) continue;

                //Convert loaded string data to primitive float array
                float[] rowCut = new float[5];
                for (int i = 0; i < row.length - 1; i++) rowCut[i] = (Float.parseFloat(row[i + 1]));

                //Directly put data into the map
                this.data.put(key, rowCut);

                //Update Date Range
                if (key > dateHigh) dateHigh = key;
                if (key < dateLow) dateLow = key;

                //Update progress
                currentRow += 1;
                this.loadProgress.set((double) currentRow / fileSize);
            }
        } catch (IOException e) {
            System.out.println("Error loading data from " + inputFile);
            e.printStackTrace();
        }

        //Update date range
        this.dateLow = DateTime.getById(dateLow);
        this.dateHigh = DateTime.getById(dateHigh);

        this.loadProgress.set(1d);
        this.loading = false;
        return this.data;
    }

    @Override
    public HashMap<Long, float[]> load() {
        return load(null, null);
    }


    // =============================

    // SAVE CSV DATA METHODS

    // =============================


    //The save method will completely overwrite already existing data in the csv.
    // If you want to save and add onto the csv, you must use the general save method
    @Override
    public boolean save(HashMap<Long, float[]> hashMap) {
        this.saving = true;
        this.saveProgress.set(0d);

        //Create the file
        FileUtil.createFolderPath(folderPath); //Creates the folder path if it does not exist
        String outputFile = FileUtil.getFilePath(folderPath, fileName);

        try (FileWriter writer = new FileWriter(outputFile)) {
            long fileSize = hashMap.size();
            long currentRow = 0;
            for (long key : hashMap.keySet()) {
                String values = Arrays.toString(hashMap.get(key)).replace("[", "").replace("]", "").replace(", ", ",");
                writer.write(key + "," + values + "\n");
                currentRow += 1;
                this.saveProgress.set((double) currentRow / fileSize);
            }

            //If the data successfully saves to the file, it will immediately overwrite the loaded data with it
            this.data = hashMap;

            writer.close();
            this.saveProgress.set(1d);
            this.saving = false;
            return true;
        } catch (IOException e) {
            System.out.println("Error writing data to " + outputFile);
            e.printStackTrace();
            this.saving = false;
            return false;
        }
    }

    //This save method does NOT overwrite the previous data stored in the CSV
    //The method will also call upon load(), and the file will be updated accordingly
    //TODO: Look into how this affects progress. It may not function properly or accurately
    public boolean save() {
        //Put all currently data into a new map
        HashMap<Long, float[]> currentMap = (HashMap<Long, float[]>) data.clone();

        //Load all data stored in the file into a new map
        HashMap<Long, float[]> loadedMap = (HashMap<Long, float[]>) load().clone();

        //Apply current data & Overwrite potential repeats in the loaded map clone
        loadedMap.putAll(currentMap);

        //Save the loaded map clone to file
        boolean saved = save(loadedMap);

        //Remove all loaded data stored from the process
        unLoad();

        //Reset the stored file data to the current map, which it started at
        this.data = currentMap;

        return saved;
    }

    // =============================

    // SPECIAL GETTERS/SETTERS

    // =============================

    //This method is specifically used for the general save method which both loads and saves data
    public Container<Double> getSaveLoadProgressCombo() {
        return new Container<>(getSaveProgress().get() / 2 + getLoadProgress().get() / 2);
    }
    public DateTime[] getLoadedDateRange() {
        return new DateTime[]{dateLow, dateHigh};
    }

    // =============================

    // GETTERS/SETTERS

    // =============================


    public boolean isSaving() {
        return saving;
    }

    public boolean isLoading() {
        return loading;
    }

    public Container<Double> getSaveProgress() {
        return saveProgress;
    }

    public Container<Double> getLoadProgress() {
        return loadProgress;
    }

}
