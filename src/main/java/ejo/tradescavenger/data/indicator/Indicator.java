package ejo.tradescavenger.data.indicator;

import com.ejo.util.setting.Container;
import com.ejo.util.time.DateTime;
import com.ejo.util.time.TimeUtil;
import ejo.tradescavenger.data.HistoricalDataContainer;
import ejo.tradescavenger.data.stock.Stock;
import ejo.tradescavenger.setting.ProgressiveFileCSVMap;
import ejo.tradescavenger.util.StockTimeUtil;

public abstract class Indicator extends HistoricalDataContainer {

    private final Stock stock;
    private final String name;

    //This will be used for calculation progress
    protected boolean calculating;
    protected Container<Double> calculationProgress;
    protected DateTime currentCalculationDate;

    public Indicator(Stock stock, String name) {
        super(new ProgressiveFileCSVMap("stock_data/indicators",name + "_" + stock.getFileCSVMap().getFileName()));
        this.stock = stock;
        this.name = name;

        this.calculating = false;
        this.calculationProgress = new Container<>(0d);
        this.currentCalculationDate = null;
    }


    public abstract float[] calculate(DateTime dateTime);


    public void calculate(DateTime start, DateTime end) {
        this.calculationProgress.set(0d);
        this.currentCalculationDate = null;
        this.calculating = true;

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

            //If the next price is not it, iterate the loop, but do not increment the candle
            // so that we try again at the next one forward
            if (!StockTimeUtil.isPriceActive(stock.isExtendedHours(), currentDateTime)) {
                loopCount++;
                continue;
            }

            //Update progression variables
            this.currentCalculationDate = currentDateTime;
            this.calculationProgress.set(TimeUtil.getDateTimePercent(start,currentDateTime,end));

            calculate(currentDateTime);
            loopCount++;
        }

        this.calculationProgress.set(1d);
        this.currentCalculationDate = null;
        this.calculating = false;
    }


    public Stock getStock() {
        return stock;
    }

    public String getName() {
        return name;
    }

}
