package ejo.tradescavenger.data.indicator.ma;

import com.ejo.util.math.MathUtil;
import com.ejo.util.time.DateTime;
import ejo.tradescavenger.data.stock.Stock;
import ejo.tradescavenger.util.StockTimeUtil;

import java.util.ArrayList;

public class IndicatorSMA extends IndicatorMA {

    public IndicatorSMA(Stock stock, int period) {
        super(stock, "SMA", period);
    }

    @Override
    public float[] calculate(DateTime dateTime) {
        if (!StockTimeUtil.isPriceActive(getStock().isExtendedHours(), dateTime)) return getNullData();

        //Define all candle average lists
        ArrayList<Float> openAvgList = new ArrayList<>();
        ArrayList<Float> closeAvgList = new ArrayList<>();

        //Adds the period of candles behind our goal candle together to the avg list
        int candleCount = 0;
        int loopCount = 0;
        int step = getStock().getTimeFrame().getSeconds();
        while (candleCount < getPeriod()) {
            DateTime nextDate = dateTime.getAdded(-step * loopCount);

            //If the previous price is not it, iterate the loop, but do not increment the candle
            // so that we try again at the next one back
            if (!StockTimeUtil.isPriceActive(getStock().isExtendedHours(), nextDate)) {
                loopCount++;
                continue;
            }

            //Add candle to the average list
            float[] data = getStock().getData(nextDate);
            float open = data[0];
            float close = data[1];
            if (open != NULL_VAL) openAvgList.add(open);
            if (close != NULL_VAL) closeAvgList.add(close);

            //Increment values
            candleCount++;
            loopCount++;
        }

        //Generate SMA value
        double openAvg = MathUtil.roundDouble(calculateAverage(openAvgList), 4);
        double closeAvg = MathUtil.roundDouble(calculateAverage(closeAvgList), 4);
        float[] result = new float[]{(float)openAvg, (float)closeAvg};
        this.data.put(dateTime.getDateTimeID(), result);
        return result;
    }

    public static <T extends Number> double calculateAverage(ArrayList<T> values) {
        double avg = 0;
        for (T val : values) avg += val.doubleValue();
        avg /= values.size();
        return avg;
    }

}
