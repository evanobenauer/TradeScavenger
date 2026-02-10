package ejo.tradescavenger.data;

import com.ejo.util.setting.Container;
import com.ejo.util.time.DateTime;
import ejo.tradescavenger.setting.ProgressiveFileCSVMap;

import java.util.ArrayList;
import java.util.HashMap;

//TODO: There are a few issues I have with this class
// 1. Loading and saving do not accurately update their progress containers by the way I do it
// 2. Start/End time loading is not compatible with fileCSVmap
// 3. Needing to convert between a float[] and Float[] takes extra time?
// Take inspiration from the old HistoricalDataContainer, the isProgressActive and getProgress is very versatile??
public abstract class HistoricalDataContainer {

    protected static final int NULL_VAL = -1;

    //Historical Data HashMap
    protected final ProgressiveFileCSVMap fileCSVMap;

    //Data Hash Setup: Key = DateTimeID, Value = float array
    protected final HashMap<Long, float[]> historicalDataHash;


    public HistoricalDataContainer(ProgressiveFileCSVMap file) {
        this.fileCSVMap = file;
        this.historicalDataHash = new HashMap<>();
    }

    public abstract float[] getNullData();

    public float[] getData(DateTime dateTime) {
        if (dateTime == null) return getNullData();

        float[] rawData = historicalDataHash.get(dateTime.getDateTimeID());
        if (rawData == null) return getNullData();

        return rawData;
    }

    //The save method will save all current data to the file without removing previously saved data
    //The save method works as follows for all historical data:
    // 1. Load all data currently saved in the map into a new map
    // 2. Put all current data from the historical map into the new map
    // 3. Save the combined map to the file
    public void saveHistoricalData() {
        //Load all file data into a map
        fileCSVMap.load();
        HashMap<String,String[]> map = (HashMap<String, String[]>) fileCSVMap.getLoadedData().clone();

        //Load all currently data into the same map
        for (Long key : historicalDataHash.keySet()) {
            ArrayList<String> stringValues = new ArrayList<>();
            float[] values = historicalDataHash.get(key);
            for (float val : values) stringValues.add(Float.toString(val));
            map.put(Long.toString(key),stringValues.toArray(new String[0]));
        }

        fileCSVMap.save(map); //Save map to file
        fileCSVMap.unLoad(); //Fully unload data
    }

    //The load method will load all file data into a new hashmap and convert it into the correct format
    //If a data entry exists in the currently loaded data, all newly loaded data will overwrite it
    public void loadHistoricalData() {
        HashMap<Long,float[]> map = new HashMap<>();

        //Load all file data into a map
        fileCSVMap.load();

        for (String str : fileCSVMap.getLoadedData().keySet()) {
            ArrayList<Float> floatList = new ArrayList<>();
            Long key = Long.parseLong(str);
            for (String val : fileCSVMap.getLoadedData().get(str)) floatList.add(Float.parseFloat(val));

            // Convert to primitive float array
            float[] primitiveFloatList = new float[floatList.size()];
            for (int i = 0; i < floatList.size(); i++) primitiveFloatList[i] = floatList.get(i);

            map.put(key,primitiveFloatList);
        }
        historicalDataHash.putAll(map);
        fileCSVMap.unLoad();
    }

    //TODO: Create a "Load Historical Data" with timeframe parameters as not to load the whole thing so memory doesn't explode
    public void loadHistoricalData(DateTime startTime, DateTime endTime) {
        loadHistoricalData();
    }

    public Container<Double> getFileSaveProgress() {
        return new Container<>(fileCSVMap.getSaveProgress().get() / 2 + fileCSVMap.getLoadProgress().get() / 2);
    }

    public Container<Double> getFileLoadProgress() {
        return fileCSVMap.getLoadProgress();
    }

    public ProgressiveFileCSVMap getFileCSVMap() {
        return fileCSVMap;
    }

    public HashMap<Long, float[]> getHistoricalData() {
        return historicalDataHash;
    }


}
