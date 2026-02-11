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

        HistoricalDataContainer container = new HistoricalDataContainer("stock_data","SPY_1min") {
            @Override
            public float[] getNullData() {
                return new float[]{-1,-1,-1,-1,-1};
            }
        };

        container.load(new DateTime(2022,1,1,0,0,0),new DateTime(2022,11,1,0,0,0));

        for (long str : container.getLoadedData().keySet()) {
            System.out.println(str);
        }

        //WINDOW.setScene(new MainMenuScene());
        //WINDOW.initAntiAliasingLevel(4);
        //WINDOW.init();
        //WINDOW.startThreadTickLoop();
        //WINDOW.runMainRenderLoop();
    }
}
