package ejo.tradescavenger.scene;

import com.ejo.ui.Scene;
import com.ejo.ui.element.ProgressBar;
import com.ejo.ui.element.Text;
import com.ejo.ui.element.widget.Button;
import com.ejo.ui.element.widget.settingwidget.Cycle;
import com.ejo.ui.element.widget.settingwidget.DropDown;
import com.ejo.util.file.CSVUtil;
import com.ejo.util.math.Angle;
import com.ejo.util.math.Vector;
import com.ejo.util.setting.Container;
import ejo.tradescavenger.data.DataAtlas;
import ejo.tradescavenger.data.stock.Stock;
import ejo.tradescavenger.element.GradientRectangle;
import ejo.tradescavenger.scene.manager.LoadedDataManager;
import ejo.tradescavenger.util.TimeFrame;
import ejo.tradescavenger.util.TitleBounceHandler;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

//TODO:
// have 2 columns under the indicator tab. One for calculations & one for loading

public class DataCenterScene extends Scene  {

    private final TitleBounceHandler stockBounceHandler;
    private final TitleBounceHandler indicatorBounceHandler;

    private final ProgressBar<Double> progressBar;

    public DataCenterScene() {
        super("Data Center");
        Color widgetColor = new Color(150,15,15);

        //Init Titles
        Text stockTitle = new Text(this,Vector.NULL(),"Stocks", new Font("Arial Black",Font.PLAIN,100),Color.WHITE, Text.Type.STATIC);
        this.stockBounceHandler = new TitleBounceHandler(stockTitle,stockTitle.getPos());

        Text indicatorTitle = new Text(this,Vector.NULL(),"Indicators", new Font("Arial Black",Font.PLAIN,100),Color.WHITE, Text.Type.STATIC);
        this.indicatorBounceHandler = new TitleBounceHandler(indicatorTitle,indicatorTitle.getPos());
        this.indicatorBounceHandler.getAngle().add(new Angle(-1));

        //Init Main Progress Bar
        this.progressBar = new ProgressBar<>(this,Vector.NULL(),Vector.NULL(),widgetColor,new Container<>(0d),0d,1d);
        //this.progressBar.setPercentageShown(true);

        //Init main menu button
        Button mainMenuButton = new Button(this,new Vector(2,2),new Vector(300,60),widgetColor,"Main Menu",() -> {
           getWindow().setSceneTransitioned(new MainMenuScene());
        });


        addElements(stockTitle,indicatorTitle);
        addElements(mainMenuButton);

        addSceneManagers(new LoadedDataManager(this));

        //======================================

        // DEBUG FEATURES

        //======================================

        //TODO: Make sure to have it save the current file to the SettingAtlas
        DropDown<String> setStockDropDown = new DropDown<>(this, new Vector(100,300),new Vector(600,60),widgetColor,"Stock", "", new Container<>("NONE"), CSVUtil.getCSVFilesInDirectory("stock_data").toArray(new String[0]));
        setStockDropDown.setAction(() -> {
            String fileName = setStockDropDown.getContainer().get().replace(".csv","");
            String[] datas = fileName.split("_");
            if (datas.length <= 1) return;
            TimeFrame timeFrame = TimeFrame.getFromTag(datas[1]);
            if (timeFrame == null) return;
            //Add a check here. If the current stock == the selected one, don't make a new one. return
            DataAtlas.LOADED_STOCK = new Stock(datas[0], timeFrame, fileName.contains("_EH"));
        });

        Button loadStockButton = new Button(this, new Vector(100, 550), new Vector(400, 60), widgetColor, "Load Stock", () -> {
            Thread thread = new Thread(() -> {
                if (DataAtlas.LOADED_STOCK != null) {
                    if (!DataAtlas.LOADED_STOCK.isLoading()) {
                        DataAtlas.LOADED_STOCK.load();
                    }
                } else {
                    System.out.println("Stock Not Loaded");
                }
            });
            thread.start();
        });

        addElements(setStockDropDown, loadStockButton);
    }


    @Override
    public void draw() {
        //Draw Background
        GradientRectangle background = new GradientRectangle(this, Vector.NULL(),getWindow().getSize(),new Color(0,0,0,255),new Color(128, 128, 128,20), GradientRectangle.Type.VERTICAL);
        background.draw();

        //Bounce Titles
        int xThird = getWindow().getSize().getXi() / 4;
        stockBounceHandler.setPos(new Vector(xThird - stockBounceHandler.getText().getSize().getX() / 2,100));
        indicatorBounceHandler.setPos(new Vector(xThird * 3 - indicatorBounceHandler.getText().getSize().getX() / 2,100));
        indicatorBounceHandler.updatePos();
        stockBounceHandler.updatePos();

        //Draw Progress Bar
        this.progressBar.setPos(new Vector(2, getWindow().getSize().getY() - progressBar.getSize().getY() - 2));
        this.progressBar.setSize(new Vector(getWindow().getSize().getX() - 4,60));
        if (DataAtlas.LOADED_STOCK != null && DataAtlas.LOADED_STOCK.isLoading()) {
            this.progressBar.setProgressContainer(DataAtlas.LOADED_STOCK.getLoadProgress());
            this.progressBar.draw();
        }

        super.draw();
    }

    @Override
    public void onKeyPress(int key, int scancode, int action, int mods) {
        super.onKeyPress(key, scancode, action, mods);
        if (key == GLFW.GLFW_KEY_ESCAPE)
            getWindow().setSceneTransitioned(new MainMenuScene());
    }
}
