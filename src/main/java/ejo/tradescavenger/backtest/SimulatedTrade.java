package ejo.tradescavenger.backtest;

import com.ejo.util.time.DateTime;
import ejo.tradescavenger.data.stock.Stock;
import ejo.tradescavenger.util.StockTraversalUtil;

//The trade will always enter on the OPEN of the candle
public class SimulatedTrade {

    private final Stock stock;
    private final DateTime entryTime;

    private final Type type;

    private final float tpTicks;
    private final float slTicks;

    private final int maxBarsInTrade;

    //Updated both of these in the updateStatus method
    private boolean win;
    private int barsInTrade;

    public SimulatedTrade(Stock stock, DateTime entryTime, Type type, float tpTicks, float slTicks) {
        this.stock = stock;
        this.entryTime = entryTime;

        this.type = type;

        this.tpTicks = tpTicks;
        this.slTicks = slTicks;

        this.maxBarsInTrade = 100; //Don't loop past 100 bars into the future

        this.win = false;
        this.barsInTrade = 0;
    }

    public boolean updateStatus() {
        return switch (type) {
            case LONG -> updateStatusLong();
            case SHORT -> updateStatusShort();
        };
    }


    private boolean updateStatusLong() {
        float entryPrice = stock.getOpen(entryTime);
        float tp = entryPrice + tpTicks;
        float sl = entryPrice - slTicks;
        StockTraversalUtil.traverseCandles(stock,entryTime,maxBarsInTrade, (dt,c,i) -> {
            float[] data = stock.getData(dt);
            float min = data[2];
            float max = data[1];

            //NOTE: if the candle hits both TP and SL, it counts as a LOSS as it is inconclusive
            if (sl <= min) {
                barsInTrade = c;
                win = false;
                return true; //Break
            }
            if (tp >= max) {
                barsInTrade = c;
                win = true;
                return true; //Break
            }
            return false;
        });
        return win;
    }

    private boolean updateStatusShort() {
        float entryPrice = stock.getOpen(entryTime);
        float tp = entryPrice - tpTicks;
        float sl = entryPrice + slTicks;
        StockTraversalUtil.traverseCandles(stock,entryTime,maxBarsInTrade, (dt,c,i) -> {
            float[] data = stock.getData(dt);
            float min = data[2];
            float max = data[1];

            //NOTE: if the candle hits both TP and SL, it counts as a LOSS as it is inconclusive
            if (sl >= max) {
                barsInTrade = c;
                win = false;
                return true; //Break
            }
            if (tp <= min) {
                barsInTrade = c;
                win = true;
                return true; //Break
            }
            return false;
        });
        return win;
    }

    public boolean isWin() {
        return win;
    }

    public int getBarsInTrade() {
        return barsInTrade;
    }

    public enum Type {
        LONG,
        SHORT
    }

}
