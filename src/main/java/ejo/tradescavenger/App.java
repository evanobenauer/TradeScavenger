package ejo.tradescavenger;

import com.ejo.ui.Window;
import com.ejo.util.file.FileUtil;
import com.ejo.util.math.Vector;
import com.ejo.util.time.DateTime;
import ejo.tradescavenger.data.HistoricalDataContainer;
import ejo.tradescavenger.scene.MainMenuScene;

//https://firstratedata.com/it/futures <- this has futures data but damn... its expensive AF
// It's in the right format though... Maybe its worth a shot?

//https://www.barchart.com/futures/quotes/GCJ26/historical-download
// This comes with a free trial? Maybe download, then cancel the trial before you become poor?
public class App {

    public static Window WINDOW = new Window("Window",new Vector(1600,1000));

    static void main() {
        FileUtil.createFolderPath("stock_data"); //Create the stock_data folder
        FileUtil.createFolderPath("stock_data/indicator_data"); //Create the stock_data folder
        WINDOW.setScene(new MainMenuScene());
        WINDOW.initAntiAliasingLevel(4);
        WINDOW.init();
        WINDOW.startThreadTickLoop();
        WINDOW.runMainRenderLoop();
    }
}
