package ejo.tradescavenger.data.stock;

import com.ejo.util.action.DoOnce;
import com.ejo.util.setting.Container;
import com.ejo.util.time.DateTime;
import com.ejo.util.time.StopWatch;
import com.ejo.util.time.TimeUtil;
import ejo.tradescavenger.setting.SettingAtlas;
import ejo.tradescavenger.util.StockTimeUtil;
import ejo.tradescavenger.util.TimeFrame;

//TODO: I do NOT plan to use this class in TradeScavenger.
// The program will strictly be offline with historical data. It is here because
// I really like the class and don't want to lose it

/**
 * The stock class is a multi use class. It encompasses both loading historical data and adding new data to said history. The live data is updated
 * by a method whenever it is called
 * Historical data is saved in the folder: stock_data/ticker_timeframe.csv. Place data into this location for it to be
 * saved and loaded with the stock
 */
public class StockLive extends Stock {

    //Live price source
    //private PriceSource priceSource;

    //Live Price Variables
    private float price;
    private float open;
    private float min;
    private float max;

    //Live Open Time
    private DateTime openTime;

    //Open-Close Percentage
    private final Container<Double> candleProgress;

    //Live Price Update Variables
    private boolean shouldStartUpdates;
    private final StopWatch livePriceUpdateTimer;

    //Do Once Definitions
    private final DoOnce doLivePriceUpdate;
    private final DoOnce doOpen;
    private final DoOnce doClose;

    //Default Constructor
    public StockLive(String ticker, TimeFrame timeFrame, boolean extendedHours) {
        super(ticker, timeFrame, extendedHours);
        //this.priceSource = priceSource;

        //Init Live Data Variables
        this.setAllData(NULL_VAL);
        this.openTime = null;
        this.candleProgress = new Container<>(0d);

        //Init Live Price Update Variables
        this.shouldStartUpdates = false;
        this.livePriceUpdateTimer = new StopWatch();

        //Init Do Once
        this.doLivePriceUpdate = new DoOnce();
        this.doOpen = new DoOnce();
        this.doClose = new DoOnce();
        this.doLivePriceUpdate.reset();
        this.doOpen.reset();
        this.doClose.reset();
    }


    /**
     * This method updates the live price of the stock as well as the min and max. Depending on the timeframe, the stock will save data to the dataList periodically with this method
     * **METHOD PROCESS: Waits... [Time to close: Updates the close, updates the price, updates the open], Waits...**
     */
    public void update(double liveDelayS, boolean includePriceUpdate) {
        //Updates the progress bar of each segmentation
        if (StockTimeUtil.isPriceActive(extendedHours, getAdjustedCurrentTime()))
            updateCandleProgress();

        //Check if the stock should update. If not, don't run the method
        if (!shouldUpdate()) return;

        //Close the previous segment
        updateClose();

        //Update live price every provided delay second or update the live price on the start of every open
        if (includePriceUpdate) updatePrice(liveDelayS);

        //Open the next segment
        updateOpen();

        //Updates the minimum/maximum values of the stock price over the time frame
        updateMinMax();
    }

    /**
     * Retrieves and sets the live price data gathered for the stock from web scraping.
     */
    public void updatePrice() {
        try {
            float livePrice = NULL_VAL;
            /*switch (priceSource) {
                case MARKETWATCH -> {
                    String url = "https://www.marketwatch.com/investing/fund/" + ticker;
                    livePrice = StockScrapeUtil.getWebScrapePriceCSS(url, "bg-quote.value", 0);
                }
                case YAHOOFINANCE -> {
                    String url = "https://finance.yahoo.com/quote/" + ticker + "?p=" + getTicker();
                    livePrice = StockScrapeUtil.getWebScrapePriceHTML(url, "data-testid", "qsp-price", 0);
                }
                case INVESTING -> {
                    String url = "https://www.investing.com/etfs/spdr-s-p-500";
                    livePrice = StockScrapeUtil.getWebScrapePriceHTML(url, "data-test", "instrument-price-last", 0);
                }
                case DEBUG -> {
                    String url = "https://www.google.com/finance/quote/SPY:NYSEARCA";
                    livePrice = StockScrapeUtil.getWebScrapePriceHTML(url, "jsname", "ip75Cb", 10);
                }
                default -> livePrice = NULL_VAL;
            }*/
            if (livePrice != NULL_VAL) this.price = livePrice;
        } catch (Exception e) {
            System.out.println("Live Data: Timed Out");
        }
    }


    /**
     * Updates the live price data every timeframe specified in the liveDelay in seconds. The method will also force an update at the beginning of every open to make sure the stock
     * is up-to-date.
     * It is best to include this update in a parallel thread as the price scraping from the internet may cause lag
     * @param liveDelayS
     */
    public void updatePrice(double liveDelayS) {
        livePriceUpdateTimer.start();
        if (livePriceUpdateTimer.hasTimePassedS(liveDelayS) || shouldClose()) {
            doLivePriceUpdate.run(() -> {
                livePriceUpdateTimer.restart();
                updatePrice();
            });
        }

        //Have live price updates reset if the stock should not close to continue with the liveDelay. This is so the stock will FORCE an update each open. Shown above
        if (!shouldClose()) doLivePriceUpdate.reset();
    }


    /**
     * Sets the stock's open, min, and max to the current price value only when doOpen is set to reset
     */
    private void updateOpen() {
        this.doOpen.run(() -> {
            this.openTime = getAdjustedCurrentTime();
            setAllData(price);
        });
    }


    /**
     * Updates the splitting of the stock into candles based on the TimeFrame of the stock selected. This method adds an entry to the historical data HashMap and then resets the livedata to the current price
     */
    private void updateClose() {
        if (!shouldClose()) {
            doClose.reset();
            return;
        }
        this.doClose.run(() -> {
            DateTime ct = getAdjustedCurrentTime();
            //Save Live Data as Historical [Data is stored as (DATETIME,OPEN,CLOSE,MIN,MAX)]
            float[] timeFrameData = {open, price, min, max};
            DateTime openTime = new DateTime(ct.getYear(), ct.getMonth(), ct.getDay(), ct.getHour(), ct.getMinute(), ct.getSecond() - timeFrame.getSeconds());
            if (openTime != null) data.put(openTime.getDateTimeID(), timeFrameData);

            //Set stock ready for open
            doOpen.reset();
        });
    }


    /**
     * Updates the minimum/maximum values of the stock over the time frame period. This is reset upon open
     */
    private void updateMinMax() {
        if (openTime == null) return;
        if (price < min) this.min = price;
        if (price > max) this.max = price;
    }


    /**
     * Updates the percentage complete for the current stock candle
     */
    private void updateCandleProgress() {
        DateTime ct = getAdjustedCurrentTime();
        double totalPercent = 0;

        //Second Percent
        double secPercent = (double) ct.getSecond() / timeFrame.getSeconds();
        totalPercent += secPercent;

        //Minute Percent
        double minPercent = ct.getMinute() / ((double) timeFrame.getSeconds() / 60);
        totalPercent += minPercent;

        //Hour Percent
        double hrPercent = ct.getHour() / ((double) timeFrame.getSeconds() / 60 / 60);
        totalPercent += hrPercent;

        totalPercent -= Math.floor(totalPercent);
        candleProgress.set(totalPercent);
    }

    /**
     * Checks if the stock should update live data. This method has the main purpose of stopping the update method if returned false
     * @return
     */
    public boolean shouldUpdate() {
        //Wait until the start of the candle timeframe to allow updates
        if (shouldClose()) this.shouldStartUpdates = true;
        if (!this.shouldStartUpdates) return false;

        //Only allows for data collection during trading hours
        return StockTimeUtil.isPriceActive(extendedHours, getAdjustedCurrentTime());
    }

    /**
     * This method will return true if the stock is at a place to go through with a split depending on the current TimeFrame
     * @return
     */
    public boolean shouldClose() {
        DateTime ct = getAdjustedCurrentTime();
        return switch (timeFrame) {
            case ONE_SECOND -> true;
            case FIVE_SECONDS -> ct.getSecond() % 5 == 0;
            case FIFTEEN_SECONDS -> ct.getSecond() % 15 == 0;
            case THIRTY_SECONDS -> ct.getSecond() % 30 == 0;
            case ONE_MINUTE -> ct.getSecond() == 0;
            case FIVE_MINUTES -> ct.getMinute() % 5 == 0 && ct.getSecond() == 0;
            case FIFTEEN_MINUTES -> ct.getMinute() % 15 == 0 && ct.getSecond() == 0;
            case THIRTY_MINUTES -> ct.getMinute() % 30 == 0 && ct.getSecond() == 0;
            case ONE_HOUR -> ct.getHour() == 0 && ct.getMinute() == 0 && ct.getSecond() == 0;
            case TWO_HOUR -> ct.getHour() % 2 == 0 && ct.getMinute() == 0 && ct.getSecond() == 0;
            case FOUR_HOUR -> ct.getHour() % 4 == 0 && ct.getMinute() == 0 && ct.getSecond() == 0;
            case ONE_DAY -> ct.getHour() % 8 == 0 && ct.getMinute() == 0 && ct.getSecond() == 0;
        };
    }

    /**
     * Sets all the data pertaining to the stock to a single value. This includes the price, open, min, and max
     *
     * @param value
     */
    private void setAllData(float value) {
        this.price = value;
        this.open = value;
        this.min = value;
        this.max = value;
    }

    @Override
    public float[] getData(DateTime dateTime) {
        if (dateTime == null) return getNullData();
        if (dateTime.equals(openTime)) return new float[]{open,price,min,max};
        return super.getData(dateTime);
    }

    public float getOpen() {
        return open;
    }

    public float getPrice() {
        return price;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }


    public DateTime getOpenTime() {
        return openTime;
    }

    public Container<Double> getCandleProgress() {
        return candleProgress;
    }


    public DateTime getAdjustedCurrentTime() {
        int secondOffset = SettingAtlas.LIVE_SECOND_OFFSET.get();
        return TimeUtil.getCurrentDateTime().getAdded(secondOffset);
    }

}
