package ejo.tradescavenger;

import com.ejo.ui.Window;
import com.ejo.util.file.FileUtil;
import com.ejo.util.math.Vector;
import com.ejo.util.time.DateTime;
import ejo.tradescavenger.data.HistoricalDataContainer;
import ejo.tradescavenger.scene.MainMenuScene;

public class App {

    public static Window WINDOW = new Window("Window",new Vector(1600,1000));

    static void main() {
        FileUtil.createFolderPath("stock_data"); //Create the stock_data folder
        WINDOW.setScene(new MainMenuScene());
        WINDOW.initAntiAliasingLevel(4);
        WINDOW.init();
        WINDOW.startThreadTickLoop();
        WINDOW.runMainRenderLoop();
    }
}
