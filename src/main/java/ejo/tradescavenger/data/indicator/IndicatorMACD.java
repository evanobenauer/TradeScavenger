package ejo.tradescavenger.data.indicator;

import com.ejo.util.time.DateTime;
import ejo.tradescavenger.data.stock.Stock;

//TODO: Import this from TradeCompanion. It's messy over there... Beware...
public class IndicatorMACD extends Indicator {

    public IndicatorMACD(Stock stock) {
        super(stock, "MACD" + "_Put the lengths here");
    }

    @Override
    public float[] calculate(DateTime dateTime) {
        return new float[0];
    }

    @Override
    protected float[] getNullData() {
        return new float[]{NULL_VAL,NULL_VAL};
    }
}
