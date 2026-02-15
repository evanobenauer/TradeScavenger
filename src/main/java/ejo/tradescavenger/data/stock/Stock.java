package ejo.tradescavenger.data.stock;

import com.ejo.util.time.DateTime;
import ejo.tradescavenger.data.HistoricalDataContainer;
import ejo.tradescavenger.util.StockTimeUtil;
import ejo.tradescavenger.util.TimeFrame;

public class Stock extends HistoricalDataContainer {

    //Stock Information
    protected final String ticker;
    protected final TimeFrame timeFrame;
    protected final boolean extendedHours;

    //Default Constructor
    public Stock(String ticker, TimeFrame timeFrame, boolean extendedHours) {
        super("stock_data", ticker + "_" + timeFrame.getTag() + (extendedHours ? "_EH" : ""));
        this.ticker = ticker;
        this.timeFrame = timeFrame;
        this.extendedHours = extendedHours;
    }

    @Override
    protected float[] getNullData() {
        return new float[]{NULL_VAL,NULL_VAL,NULL_VAL,NULL_VAL,NULL_VAL};
    }

    @Override
    public boolean isValidDateTime(DateTime dateTime) {
        return StockTimeUtil.isPriceActive(extendedHours, dateTime);
    }

    public float getOpen(DateTime dateTime) {
        return getData(dateTime)[0];
    }

    public float getClose(DateTime dateTime) {
        return getData(dateTime)[1];
    }

    public float getMin(DateTime dateTime) {
        return getData(dateTime)[2];
    }

    public float getMax(DateTime dateTime) {
        return getData(dateTime)[3];
    }

    public float getVolume(DateTime dateTime) {
        return getData(dateTime)[4];
    }


    public String getTicker() {
        return ticker;
    }

    public TimeFrame getTimeFrame() {
        return timeFrame;
    }

    public boolean isExtendedHours() {
        return extendedHours;
    }


    @Override
    public String toString() {
        return fileName;
    }
}
