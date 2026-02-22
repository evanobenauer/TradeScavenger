package ejo.tradescavenger.util;

import com.ejo.util.time.DateTime;
import ejo.tradescavenger.data.stock.Stock;

public class StockTraversalUtil {


    //Traverse candles between two datetime
    public static void traverseCandles(Stock stock, DateTime start, DateTime end, CandleCode code) {
        if (end.getDateTimeID() < start.getDateTimeID()) return;
        if (start.getDateTimeID() == end.getDateTimeID()) {
            code.run(start,0,0);
            return;
        }

        DateTime currentDateTime = DateTime.getById(start.getDateTimeID());

        int loopIndex = 0;
        int candleIndex = 0;
        int step = stock.getTimeFrame().getSeconds();
        while (currentDateTime.getDateTimeID() < end.getDateTimeID()) {
            currentDateTime = start.getAdded(step * loopIndex);

            //If the next price is not it, iterate the loop, but do not increment the candle
            // so that we try again at the next one forward
            if (!stock.isValidDateTime(currentDateTime)) {
                loopIndex++;
                continue;
            }

            if (code.run(currentDateTime, candleIndex, loopIndex)) break;

            candleIndex++;
            loopIndex++;
        }
    }

    //Traverse a defined number of candles away from a start date. This can be positive or negative
    public static void traverseCandles(Stock stock, DateTime start, int candleCount, CandleCode code) {
        if (candleCount == 0) {
            code.run(start,0,0);
            return;
        }

        //Determine the direction of the traversal depending on the candle count sign
        int traversal = candleCount > 0 ? 1 : -1;

        int loopIndex = 0;
        int candleIndex = 0;
        int step = stock.getTimeFrame().getSeconds();
        while (Math.abs(candleIndex) < Math.abs(candleCount)) {
            DateTime currentDateTime = start.getAdded(step * loopIndex);

            //If the next price is not it, iterate the loop, but do not increment the candle
            // so that we try again at the next one forward
            if (!stock.isValidDateTime(currentDateTime)) {
                loopIndex += traversal;
                continue;
            }

            if (code.run(currentDateTime, candleIndex, loopIndex)) break;

            candleIndex += traversal;
            loopIndex += traversal;
        }
    }

    //IF the lambda returns true, the loop will break
    @FunctionalInterface
    public interface CandleCode {
        boolean run(DateTime currentTime, int candleIndex ,int loopIndex);
    }


}
