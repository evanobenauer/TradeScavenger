package ejo.tradescavenger.data.indicator.ma;

import com.ejo.util.math.MathUtil;
import com.ejo.util.misc.LambdaUtil;
import com.ejo.util.time.DateTime;
import ejo.tradescavenger.data.stock.Stock;
import ejo.tradescavenger.util.StockTimeUtil;
import ejo.tradescavenger.util.StockTraversalUtil;

public class IndicatorEMA extends IndicatorMA {

    private final IndicatorSMA equivalentSMA;

    public IndicatorEMA(Stock stock, int period) {
        super(stock, "EMA", period);
        this.equivalentSMA = new IndicatorSMA(getStock(),period);
    }

    @Override
    public float[] calculate(DateTime dateTime) {
        if (!isValidDateTime(dateTime)) return getNullData();

        //Get Current Stock Data
        float[] data = getStock().getData(dateTime);
        float open = data[0];
        float close = data[1];

        //Get previous candle time
        int loopCount = 1;
        int step = getStock().getTimeFrame().getSeconds();
        DateTime lastCandleTime = dateTime.getAdded(-step * loopCount);
        while (!isValidDateTime(lastCandleTime)) {
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

        double weight = (double) 2 / (getPeriod() + 1);

        LambdaUtil.action<Double> calculation = (args) -> {
            float val = (float) args[0];
            double prevEMAVal = (double) args[1];
            return MathUtil.roundDouble(val == NULL_VAL ? prevEMAVal : val * weight + prevEMAVal * (1 - weight), 4);
        };

        //Generate next EMA value
        double openEMA = calculation.run(open,prevOpenEMA);
        double closeEMA = calculation.run(close,prevCloseEMA);
        float[] result = new float[]{(float)openEMA, (float)closeEMA};
        this.data.put(dateTime.getDateTimeID(), result);
        return result;
    }

}
