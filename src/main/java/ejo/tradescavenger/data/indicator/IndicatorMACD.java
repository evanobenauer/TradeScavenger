package ejo.tradescavenger.data.indicator;

import com.ejo.util.time.DateTime;
import ejo.tradescavenger.data.HistoricalDataContainer;
import ejo.tradescavenger.data.indicator.ma.IndicatorEMA;
import ejo.tradescavenger.data.indicator.ma.IndicatorMA;
import ejo.tradescavenger.data.indicator.ma.IndicatorSMA;
import ejo.tradescavenger.data.stock.Stock;

import java.util.ArrayList;
import java.util.HashMap;

//TODO: Import this from TradeCompanion. It's messy over there... Beware...
public class IndicatorMACD extends Indicator {

    public IndicatorMACD(Stock stock, String name) {
        super(stock, name);
    }

    @Override
    public float[] calculate(DateTime dateTime) {
        return new float[0];
    }

    @Override
    public float[] getNullData() {
        return new float[]{NULL_VAL,NULL_VAL};
    }
}
