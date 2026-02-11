package ejo.tradescavenger.data.stock;

import com.ejo.util.time.DateTime;
import ejo.tradescavenger.data.HistoricalDataContainer;
import ejo.tradescavenger.util.TimeFrame;

public class Stock extends ejo.tradescavenger.data.HistoricalDataContainer {

    //Stock Information
    protected final String ticker;
    protected final TimeFrame timeFrame;
    protected final boolean extendedHours;

    //Default Constructor
    public Stock(String ticker, TimeFrame timeFrame, boolean extendedHours) {
        super("stock_data", ticker + "_" + timeFrame.getTag());
        this.ticker = ticker;
        this.timeFrame = timeFrame;
        this.extendedHours = extendedHours;
    }

    @Override
    public float[] getNullData() {
        return new float[]{NULL_VAL,NULL_VAL,NULL_VAL,NULL_VAL,NULL_VAL};
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

    @Override
    public String toString() {
        return ticker + "_" + timeFrame + (extendedHours ? "_EH" : "");
    }
}
