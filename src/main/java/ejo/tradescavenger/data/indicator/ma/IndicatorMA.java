package ejo.tradescavenger.data.indicator.ma;

import com.ejo.util.time.DateTime;
import ejo.tradescavenger.data.indicator.Indicator;
import ejo.tradescavenger.data.stock.Stock;

//TODO: Add a third data entry specifically for the VolumeMA
public abstract class IndicatorMA extends Indicator {

    private final int period;

    public IndicatorMA(Stock stock, String name, int period) {
        super(stock, name + "_" + period);
        this.period = period;
    }

    @Override
    public float[] getNullData() {
        return new float[]{NULL_VAL,NULL_VAL};
    }

    public float getOpen(DateTime dateTime) {
        return getData(dateTime)[0];
    }

    public float getClose(DateTime dateTime) {
        return getData(dateTime)[1];
    }

    public int getPeriod() {
        return period;
    }
}
