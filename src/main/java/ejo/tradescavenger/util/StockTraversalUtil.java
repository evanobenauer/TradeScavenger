package ejo.tradescavenger.util;

import com.ejo.util.time.DateTime;
import ejo.tradescavenger.data.stock.Stock;

public class StockTraversalUtil {


    public static void traverseCandles(Stock stock, DateTime start, DateTime end, CandleCode code) {
        if (end.getDateTimeID() < start.getDateTimeID()) return;
        if (start.getDateTimeID() == end.getDateTimeID()) {
            code.run(start,0);
            return;
        }

        DateTime currentDateTime = DateTime.getById(start.getDateTimeID());

        int loopCount = 0;
        int step = stock.getTimeFrame().getSeconds();
        while (currentDateTime.getDateTimeID() < end.getDateTimeID()) {
            currentDateTime = start.getAdded(step * loopCount);

            //If the next price is not it, iterate the loop, but do not increment the candle
            // so that we try again at the next one forward
            if (!stock.isValidDateTime(currentDateTime)) {
                loopCount++;
                continue;
            }

            code.run(currentDateTime, loopCount);

            loopCount++;
        }
    }

    @FunctionalInterface
    public interface CandleCode {
        void run(DateTime currentTime, int loopCount);
    }


}
