package ejo.tradescavenger.scene.datacenter;

import com.ejo.ui.Scene;
import com.ejo.ui.element.ProgressBar;
import com.ejo.ui.element.SideBar;
import com.ejo.ui.element.Text;
import com.ejo.ui.element.widget.Button;
import com.ejo.ui.element.widget.settingwidget.DropDown;
import com.ejo.ui.manager.NotificationManager;
import com.ejo.util.file.CSVUtil;
import com.ejo.util.math.Angle;
import com.ejo.util.math.Vector;
import com.ejo.util.setting.Container;
import ejo.tradescavenger.data.DataAtlas;
import ejo.tradescavenger.data.indicator.Indicator;
import ejo.tradescavenger.data.indicator.ma.IndicatorEMA;
import ejo.tradescavenger.data.indicator.ma.IndicatorSMA;
import ejo.tradescavenger.data.stock.Stock;
import ejo.tradescavenger.element.GradientRectangle;
import ejo.tradescavenger.element.RemovalList;
import ejo.tradescavenger.scene.MainMenuScene;
import ejo.tradescavenger.scene.datacenter.manager.IndicatorCalculationsManager;
import ejo.tradescavenger.scene.manager.DataDisplayManager;
import ejo.tradescavenger.setting.SettingAtlas;
import ejo.tradescavenger.util.TimeFrame;
import ejo.tradescavenger.util.TitleBounceHandler;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;

public class DataCenterScene extends Scene  {

    private static Indicator CACHED_INDICATOR;

    private final NotificationManager notificationManager;


    //Title Elements
    private final TitleBounceHandler stockBounceHandler;
    private final TitleBounceHandler indicatorBounceHandler;

    //General Progress Bar
    private final ProgressBar<Double> progressBar;


    //Stock Specific Elements
    private final Button loadStockButton;
    private final DropDown<String> setStockDropDown;


    //Indicator Specific Elements
    private final Button loadIndicatorButton;
    private final DropDown<String> addIndicatorDropDown;
    private final RemovalList<Indicator> indicatorRemovalList;


    public DataCenterScene() {
        super("Data Center");
        Color widgetColor = new Color(150,15,15);

        //======================================
        // GENERAL FEATURES
        //======================================

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
            SettingAtlas.SETTING_MANAGER.saveAll();
            getWindow().setSceneTransitioned(new MainMenuScene());
        });

        addElements(stockTitle,indicatorTitle);
        addElements(mainMenuButton);

        //======================================
        // INIT STOCK FEATURES
        //======================================

        //Create a list of all stock files in the directory
        ArrayList<String> stockFiles = new ArrayList<>();
        for (String name : CSVUtil.getCSVFilesInDirectory("stock_data"))
            if (isValidStockFileName(name)) stockFiles.add(name);

        //Init dropdown
        this.setStockDropDown = new DropDown<>(this, Vector.NULL(),new Vector(1,60),widgetColor, SettingAtlas.DATA_SELECTED_STOCK_FILE, stockFiles.toArray(new String[0]));
        this.setStockDropDown.setOpen(true);

        //Init load button
        this.loadStockButton = new Button(this, Vector.NULL(), new Vector(1,60), widgetColor, "Load Stock", this::loadStock);

        //Add stock elements
        addElements(setStockDropDown,loadStockButton);

        //======================================
        // INIT INDICATOR FEATURES
        //======================================

        //Create a list of all indicator files in the directory
        ArrayList<String> indicatorFiles = new ArrayList<>();
        for (String name : CSVUtil.getCSVFilesInDirectory("stock_data/indicator_data"))
            if (isValidIndicatorFileName(name)) indicatorFiles.add(name);

        //Init dropdown
        this.addIndicatorDropDown = new DropDown<>(this, Vector.NULL(),new Vector(1,60),widgetColor, SettingAtlas.DATA_SELECTED_INDICATOR_FILE, indicatorFiles.toArray(new String[0]));
        this.addIndicatorDropDown.setOpen(true);

        //Init load button
        this.loadIndicatorButton = new Button(this, Vector.NULL(), new Vector(1,60), widgetColor, "Load Indicator", this::loadIndicator);

        //Init removal list
        this.indicatorRemovalList = new RemovalList<>(this, Vector.NULL(),1,widgetColor,DataAtlas.LOADED_INDICATORS);

        //Add indicator elements
        addElements(loadIndicatorButton, addIndicatorDropDown, indicatorRemovalList);

        //======================================
        // INIT SCENE MANAGERS
        //======================================

        addSceneManagers(new DataDisplayManager(this));

        this.notificationManager = new NotificationManager(this,30,4);
        addSceneManagers(notificationManager);

        addSceneManagers(new IndicatorCalculationsManager(this));
    }

    @Override
    public void draw() {
        //Draw Background
        GradientRectangle background = new GradientRectangle(this, Vector.NULL(),getWindow().getSize(),new Color(0,0,0,255),new Color(128, 128, 128,20), GradientRectangle.Type.VERTICAL);
        background.draw();

        //Update element positions
        updateElementPositions();

        //Draw all elements
        super.draw();

        //Set progress bar activity
        updateProgressBar();
    }

    @Override
    public void onKeyPress(int key, int scancode, int action, int mods) {
        super.onKeyPress(key, scancode, action, mods);
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            SettingAtlas.SETTING_MANAGER.saveAll();
            getWindow().setSceneTransitioned(new MainMenuScene());
        }
    }

    // =============================

    // UPDATE METHODS

    // =============================

    private void updateElementPositions() {
        //Bounce Titles
        int xThird = getWindow().getSize().getXi() / 4;
        stockBounceHandler.setPos(new Vector(xThird - stockBounceHandler.getText().getSize().getX() / 2,100));
        indicatorBounceHandler.setPos(new Vector(xThird * 3 - indicatorBounceHandler.getText().getSize().getX() / 2,100));
        indicatorBounceHandler.updatePos();
        stockBounceHandler.updatePos();

        //Update Progress Bar
        this.progressBar.setPos(new Vector(2, getWindow().getSize().getY() - progressBar.getSize().getY() - 2));
        this.progressBar.setSize(new Vector(getWindow().getSize().getX() - 4,60));

        //Update Stock Load Button
        int extraWidth = 100;
        this.loadStockButton.setPos(stockBounceHandler.getPos().getAdded(-extraWidth,200));
        this.loadStockButton.setSize(new Vector(stockBounceHandler.getText().getSize().getX() + extraWidth*2,loadStockButton.getSize().getY()));

        //Update Stock DropDown
        this.setStockDropDown.setPos(stockBounceHandler.getPos().getAdded(-extraWidth,300));
        this.setStockDropDown.setSize(new Vector(stockBounceHandler.getText().getSize().getX() + extraWidth*2,setStockDropDown.getHeadSize().getY()));

        //Update Indicator Load Button
        this.loadIndicatorButton.setPos(indicatorBounceHandler.getPos().getAdded(-extraWidth,200));
        this.loadIndicatorButton.setSize(new Vector(indicatorBounceHandler.getText().getSize().getX() + extraWidth*2,loadStockButton.getSize().getY()));

        //Update Indicator DropDown
        int sep = 30;
        this.addIndicatorDropDown.setPos(indicatorBounceHandler.getPos().getAdded(-extraWidth,300));
        this.addIndicatorDropDown.setSize(new Vector(indicatorBounceHandler.getText().getSize().getX() + extraWidth*2,setStockDropDown.getHeadSize().getY()).getScaled(0.5,1).getSubtracted(sep / 2,0));

        //Update Indicator List
        this.indicatorRemovalList.setPos(addIndicatorDropDown.getPos().getAdded(sep + extraWidth + indicatorBounceHandler.getText().getSize().getX() / 2,0));
        this.indicatorRemovalList.setWidth(addIndicatorDropDown.getSize().getX() - sep/2);

    }

    private void updateProgressBar() {
        //Stock Loading
        if (DataAtlas.LOADED_STOCK != null && DataAtlas.LOADED_STOCK.isLoading()) {
            this.progressBar.setProgressContainer(DataAtlas.LOADED_STOCK.getLoadProgress());
            this.progressBar.draw();
        }

        //Indicator Loading
        if (CACHED_INDICATOR != null && CACHED_INDICATOR.isLoading()) {
            this.progressBar.setProgressContainer(CACHED_INDICATOR.getLoadProgress());
            this.progressBar.draw();
        }

        //Indicator Calculating
        //TODO: add more if-draw statements here for the progress bar
    }


    // =============================

    // LOAD METHODS

    // =============================


    private void loadStock() {
        Thread thread = new Thread(() -> {
            String fileName = SettingAtlas.DATA_SELECTED_STOCK_FILE.get();
            String[] data = fileName.replace(".csv","").split("_");

            boolean stockProgressActive = DataAtlas.LOADED_STOCK != null && (DataAtlas.LOADED_STOCK.isLoading() || DataAtlas.LOADED_STOCK.isSaving());

            //Instantiate Stock Object
            String ticker = data[0];
            TimeFrame timeFrame = TimeFrame.getFromTag(data[1]); //This cannot be null btw...
            boolean extendedHours = fileName.contains("_EH");
            if (DataAtlas.LOADED_STOCK == null || !stockProgressActive)
                DataAtlas.LOADED_STOCK = new Stock(ticker, timeFrame, extendedHours);

            //Load stock data
            if (!stockProgressActive) {
                DataAtlas.LOADED_STOCK.load();
                notificationManager.sendNotification("Loaded: " + DataAtlas.LOADED_STOCK.getFileName(),Color.GREEN);
            }

            //Delete all indicators IF the stock file is changed
            if (!stockProgressActive && !DataAtlas.LOADED_INDICATORS.isEmpty()) {
                Indicator indicator = (Indicator) DataAtlas.LOADED_INDICATORS.values().toArray()[0];
                if (!indicator.getStock().getFileName().equals(DataAtlas.LOADED_STOCK.getFileName())) {
                    DataAtlas.LOADED_INDICATORS.clear();
                    System.gc(); //Call the garbage collector to free all the memory
                    notificationManager.sendNotification("Removed all indicators as they do not belong to the loaded stock",Color.RED);
                }
            }
        });
        thread.start();
    }

    private void loadIndicator() {
        Thread thread = new Thread(() -> {
            String fileName = SettingAtlas.DATA_SELECTED_INDICATOR_FILE.get();
            String[] data = fileName.replace(".csv","").split("_");

            //Don't load indicators if the stock isn't existent
            if (DataAtlas.LOADED_STOCK == null) {
                notificationManager.sendNotification("Chose not to load indicator as there is no loaded stock",Color.RED);
                return;
            }

            //Define the indicator name (The first element in the file name array)
            String indicatorName = data[0];

            //Instantiate the Dummy Stock to double-check the file name of the loaded stock
            Stock dummyStock;
            switch (indicatorName) {
                case "EMA","SMA" -> {
                    String stockTicker = data[2];
                    TimeFrame stockTimeFrame = TimeFrame.getFromTag(data[3]);
                    boolean extendedHours = fileName.contains("_EH");
                    dummyStock = new Stock(stockTicker,stockTimeFrame,extendedHours);
                }
                case "MACD" -> {
                    //Currently Unimplemented
                    dummyStock = null;
                }
                default -> dummyStock = null;
            };

            //Instantiate the indicator
            switch (indicatorName) {
                case "EMA" -> {
                    String period = data[1];
                    CACHED_INDICATOR = new IndicatorEMA(DataAtlas.LOADED_STOCK,Integer.parseInt(period));
                }
                case "SMA" -> {
                    String period = data[1];
                    CACHED_INDICATOR = new IndicatorSMA(DataAtlas.LOADED_STOCK,Integer.parseInt(period));
                }
                case "MACD" -> {
                    //NOT YET IMPLEMENTED LOL POGGERS
                }
            }

            //Load cached indicator into memory & apply it to the stored list
            if (DataAtlas.LOADED_STOCK.getFileName().equals(dummyStock.getFileName())) {
                CACHED_INDICATOR.load();
                DataAtlas.LOADED_INDICATORS.put(CACHED_INDICATOR.getFileName(),CACHED_INDICATOR);
                notificationManager.sendNotification("Loaded: " + CACHED_INDICATOR.getFileName(),Color.GREEN);
                CACHED_INDICATOR = null;
            } else {
                //If the loaded stock does not match the indicator file, do not load the indicator
                CACHED_INDICATOR = null;
                notificationManager.sendNotification("Incorrect stock for currently loaded version",Color.RED);
            }

        });
        thread.start();
    }

    // =============================

    // FILE NAME VERIFICATION METHODS

    // =============================

    public boolean isValidStockFileName(String fileName) {
        String name = fileName.replace(".csv","");
        String[] data = name.split("_");

        if (name.contains(" ")) return false;
        if (data.length < 2) return false;

        String ticker = data[0];
        if (ticker.isEmpty()) return false;

        TimeFrame timeFrame = TimeFrame.getFromTag(data[1]);
        if (timeFrame == null) return false;

        return true;
    }

    public boolean isValidIndicatorFileName(String fileName) {
        String name = fileName.replace(".csv","");
        String[] data = name.split("_");

        if (name.contains(" ")) return false;
        if (data.length < 3) return false;

        boolean isIndicator = switch (data[0]) {
            case "EMA", "SMA", "MACD" -> true;
            default -> false;
        };

        //Add stock checking here. You may need to specify the indicator type to get the correct data index

        return isIndicator;
    }
}