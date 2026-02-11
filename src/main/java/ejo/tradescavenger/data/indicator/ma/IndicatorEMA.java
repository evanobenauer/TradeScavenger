package ejo.tradescavenger.data.indicator.ma;

import com.ejo.util.math.MathUtil;
import com.ejo.util.time.DateTime;
import ejo.tradescavenger.data.stock.Stock;
import ejo.tradescavenger.util.StockTimeUtil;

public class IndicatorEMA extends IndicatorMA {

    private final IndicatorSMA equivalentSMA;

    public IndicatorEMA(Stock stock, int period) {
        super(stock, "EMA", period);
        this.equivalentSMA = new IndicatorSMA(getStock(),period);
    }

    @Override
    public float[] calculate(DateTime dateTime) {
        if (!StockTimeUtil.isPriceActive(getStock().isExtendedHours(), dateTime)) return getNullData();

        //Get Current Stock Data
        float[] data = getStock().getData(dateTime);
        float open = data[0];
        float close = data[1];

        //Get previous candle time
        int loopCount = 1;
        int step = getStock().getTimeFrame().getSeconds();
        DateTime lastCandleTime = dateTime.getAdded(-step * loopCount);
        while (!StockTimeUtil.isPriceActive(getStock().isExtendedHours(), lastCandleTime)) {
            loopCount++;
            lastCandleTime = dateTime.getAdded(-step * loopCount);
        }

        //Get previous EMA value
        float[] prevEMA = getData(lastCandleTime);
        double prevOpenEMA = prevEMA[0];
        double prevCloseEMA = prevEMA[1];
        //If the previous EMA does not exist, set this value to the current SMA
        if (prevOpenEMA == NULL_VAL || Double.isNaN(prevOpenEMA)) {
            prevOpenEMA = equivalentSMA.calculate(dateTime)[0];
            prevCloseEMA = equivalentSMA.calculate(dateTime)[1];
        }

        //Generate next EMA value
        double weight = (double) 2 / (getPeriod() + 1);
        double openEMA = MathUtil.roundDouble(open == NULL_VAL ? prevOpenEMA : open * weight + prevOpenEMA * (1 - weight), 4);
        double closeEMA = MathUtil.roundDouble(close == NULL_VAL ? prevCloseEMA : close * weight + prevCloseEMA * (1 - weight), 4);
        float[] result = new float[]{(float)openEMA, (float)closeEMA};
        this.data.put(dateTime.getDateTimeID(), result);
        return result;
    }

}
