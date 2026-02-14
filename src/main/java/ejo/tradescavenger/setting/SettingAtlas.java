package ejo.tradescavenger.setting;

import com.ejo.util.file.FileCSVMap;
import com.ejo.util.setting.Setting;
import com.ejo.util.setting.SettingManager;
import ejo.tradescavenger.util.TimeFrame;

public class SettingAtlas {

    //Manager
    private static final FileCSVMap<String,String> CSV_FILE = new FileCSVMap<>("setting","stockSettings");
    public static final SettingManager SETTING_MANAGER = new SettingManager(CSV_FILE);

    //TODO: ADD ALL SETTINGS IN HERE

    @Deprecated
    public static final Setting<Integer> LIVE_SECOND_OFFSET = new Setting<>(SETTING_MANAGER,"live_secondOffset", 0);


    public static final Setting<String> DATA_SELECTED_STOCK_FILE = new Setting<>(SETTING_MANAGER,"data_selectedStockFile", "NONE");
    public static final Setting<String> DATA_SELECTED_INDICATOR_FILE = new Setting<>(SETTING_MANAGER,"data_selectedIndicatorFile", "NONE");

}
