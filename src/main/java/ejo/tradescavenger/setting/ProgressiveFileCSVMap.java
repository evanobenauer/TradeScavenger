package ejo.tradescavenger.setting;

import com.ejo.util.file.FileCSVMap;
import com.ejo.util.file.FileUtil;
import com.ejo.util.setting.Container;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;

//This class is the same as FileCSVMap, but it has Saving and Loading progress bars built in
// I would make this the standard version in AppNexus, but it's slower and IDK if that's what I want
public class ProgressiveFileCSVMap extends FileCSVMap {

    private boolean saving;
    private boolean loading;

    private final Container<Double> saveProgress;
    private final Container<Double> loadProgress;

    public ProgressiveFileCSVMap(String folderPath, String fileName) {
        super(folderPath, fileName);
        this.saving = false;
        this.loading = false;
        this.saveProgress = new Container<>(0d);
        this.loadProgress = new Container<>(0d);
    }

    @Override
    public HashMap<String, String[]> load() {
        loadProgress.set(0d);
        loading = true;

        File file = new File(FileUtil.getFilePath(folderPath,fileName));
        HashMap<String, String[]> rawDataHashMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            long fileSize = Files.lines(file.toPath()).count();
            long currentRow = 0;
            while ((line = reader.readLine()) != null) {
                String[] row = line.split(",");
                String key = row[0];
                String[] rowCut = line.replace(key + ",", "").split(",");
                rawDataHashMap.put(row[0], rowCut);
                currentRow += 1;
                loadProgress.set((double) currentRow / fileSize);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        data = rawDataHashMap;
        loading = false;
        return rawDataHashMap;
    }

    @Override
    public <K, V> boolean save(HashMap<K, V[]> hashMap) {
        this.saving = true;
        this.saveProgress.set(0d);

        FileUtil.createFolderPath(folderPath); //Creates the folder path if it does not exist
        String outputFile = FileUtil.getFilePath(folderPath,fileName);
        try {
            FileWriter writer = new FileWriter(outputFile);
            HashMap<String, String[]> tempMap = new HashMap<>();
            long fileSize = hashMap.size();
            long currentRow = 0;
            for (K key : hashMap.keySet()) {
                tempMap.put(key.toString(), (String[]) hashMap.get(key));
                writer.write(key + "," + Arrays.toString(hashMap.get(key)).replace("[", "").replace("]", "").replace(", ",",") + "\n");
                currentRow += 1;
                saveProgress.set((double) currentRow / fileSize);
            }
            data = tempMap;
            writer.close();
            this.saving = false;
            return true;
        } catch (IOException e) {
            System.out.println("Error writing data to " + outputFile);
            e.printStackTrace();
            this.saving = false;
            return false;
        }
    }

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
