package ejo.tradescavenger.data.indicator;

import com.ejo.util.setting.Container;
import com.ejo.util.time.DateTime;
import com.ejo.util.time.TimeUtil;
import ejo.tradescavenger.data.stock.Stock;
import ejo.tradescavenger.data.HistoricalDataContainer;
import ejo.tradescavenger.util.StockTraversalUtil;

public abstract class Indicator extends HistoricalDataContainer {

    private final Stock stock;
    private final String name;

    //This will be used for calculation progress
    protected boolean calculating;
    protected Container<Double> calculationProgress;
    protected DateTime currentCalculationDate;

    public Indicator(Stock stock, String name) {
        super("stock_data/indicator_data",name + "_" + stock.getFileName());
        this.stock = stock;
        this.name = name;

        this.calculating = false;
        this.calculationProgress = new Container<>(0d);
        this.currentCalculationDate = null;
    }

    @Override
    public boolean isValidDateTime(DateTime dateTime) {
        return stock.isValidDateTime(dateTime);
    }

    public abstract float[] calculate(DateTime dateTime);


    public void calculate(DateTime start, DateTime end) {
        this.calculationProgress.set(0d);
        this.currentCalculationDate = null;
        this.calculating = true;

        StockTraversalUtil.traverseCandles(stock,start,end,(currentDateTime, i) -> {
            //Update progression variables
            this.currentCalculationDate = currentDateTime;
            this.calculationProgress.set(TimeUtil.getDateTimePercent(start,currentDateTime,end));

            calculate(currentDateTime);
        });

        this.calculationProgress.set(1d);
        this.currentCalculationDate = null;
        this.calculating = false;
    }

    @Deprecated
    private void oldTraversal(DateTime start, DateTime end) {
        if (end.getDateTimeID() < start.getDateTimeID()) return;
        if (start.getDateTimeID() == end.getDateTimeID()) {
            calculate(start);
            return;
        }

        DateTime currentDateTime = DateTime.getById(start.getDateTimeID());

        int loopCount = 0;
        int step = stock.getTimeFrame().getSeconds();
        while (currentDateTime.getDateTimeID() < end.getDateTimeID()) {
            currentDateTime = start.getAdded(step * loopCount);
            //If the next price is not valid, iterate the loop, but do not increment the candle
            // so that we try again at the next one forward
            if (!isValidDateTime(currentDateTime)) {
                loopCount++;
                continue;
            }

            //Update progression variables
            this.currentCalculationDate = currentDateTime;
            this.calculationProgress.set(TimeUtil.getDateTimePercent(start,currentDateTime,end));

            calculate(currentDateTime);
            loopCount++;
        }
    }


    public Stock getStock() {
        return stock;
    }

    public String getIndicatorName() {
        return name;
    }

    @Override
    public String toString() {
        return getFileName();
    }
}
