package ejo.tradescavenger.setting;

import com.ejo.util.file.FileCSVMap;
import com.ejo.util.setting.Setting;
import com.ejo.util.setting.SettingManager;
import ejo.tradescavenger.util.TimeFrame;

public class SettingAtlas {

    //Manager
    private static final FileCSVMap CSV_FILE = new FileCSVMap("setting","stockSettings");
    public static final SettingManager SETTING_MANAGER = new SettingManager(CSV_FILE) {
        //TODO: Override the load function here. Make it so it can load the TIMEFRAME from its string
        // You'll also need to override the settings getType method
    };

    private static final Setting<String> STOCK_TICKER = new Setting<>("stockTicker", "");
    private static final Setting<TimeFrame> TIME_FRAME = new Setting<>("timeFrame", TimeFrame.ONE_MINUTE);
    private static final Setting<Boolean> EXTENDED_HOURS = new Setting<>("extendedHours", false);
    public static final Setting<Integer> LIVE_SECOND_OFFSET = new Setting<>(SETTING_MANAGER,"live_secondOffset", 0);

}
