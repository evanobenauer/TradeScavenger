package ejo.tradescavenger.element;

import com.ejo.ui.Scene;
import com.ejo.ui.element.DrawableElement;
import com.ejo.ui.element.polygon.Rectangle;
import com.ejo.ui.element.polygon.RoundedRectangle;
import com.ejo.util.math.Vector;
import com.ejo.util.misc.LambdaUtil;
import com.ejo.util.setting.Container;
import com.ejo.util.time.DateTime;
import ejo.tradescavenger.data.indicator.Indicator;
import ejo.tradescavenger.data.indicator.ma.IndicatorMA;
import ejo.tradescavenger.data.stock.Stock;
import ejo.tradescavenger.element.candle.Candle;
import ejo.tradescavenger.element.indicator.RenderMA;
import ejo.tradescavenger.util.StockTraversalUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public class CandleCluster extends DrawableElement {

    private Vector size;
    private Color color;

    private DateTime focusTime;
    private int candlesBack;
    private int candlesForward;

    private final Stock stock;
    private final Indicator[] indicators;

    public CandleCluster(Scene scene, Vector pos, Vector size, Color color, DateTime focusTime, int candlesBack, int candlesForward, Stock stock, Indicator... indicators) {
        super(scene, pos);
        this.size = size;
        this.color = color;

        this.focusTime = focusTime;
        this.candlesBack = candlesBack;
        this.candlesForward = candlesForward;

        this.stock = stock;
        this.indicators = indicators;
    }

    public void draw(Vector mousePos) {
        DateTime focusTime = this.focusTime.clone(); //Clone the focus time so it doesnt change mid method from calculations
        //==========================
        // UPDATE DATA
        //==========================

        //General
        int candleCount = candlesBack + candlesForward + 1;
        int step = stock.getTimeFrame().getSeconds();

        //Candle Dimensions
        int candleWidth = 10;
        int candleSep = 5;

        //Position Variables
        double focusY = getPos().getY() + size.getY() / 2;
        int startX = getPos().getXi() + candleSep;

        float focusPrice = stock.getOpen(focusTime);

        //ScaleX Definition
        double scaleX = (size.getX() - candleSep) / (candleCount * (candleWidth + candleSep));

        //Define price Min/Max containers
        Container<Double> priceDiffHighMax = new Container<>(0d);
        Container<Double> priceDiffLowMin = new Container<>(Math.pow(10,10));
        LambdaUtil.actionVoid updateMinMax = (args) -> {
            float min = (float) args[0];
            float max = (float) args[1];
            //Update Min/Max price containers
            if (min != -1 && max != -1) {
                double priceDiffHigh = max - focusPrice;
                double priceDiffLow = min - focusPrice;
                if (priceDiffHigh > priceDiffHighMax.get()) priceDiffHighMax.set(priceDiffHigh);
                if (priceDiffLow < priceDiffLowMin.get()) priceDiffLowMin.set(priceDiffLow);
            }
        };

        //Traverse all candles, add usable candles to the arrayList, update MinMax from the stock
        ArrayList<Candle> candles = new ArrayList<>();

        //Traverse all Pre Candles from midpoint(Does NOT include the target candle
        StockTraversalUtil.traverseCandles(stock,focusTime.getAdded(-step),-candlesBack,(d, c,l) -> {
            float[] data = stock.getData(d);
            updateMinMax.run(data[2],data[3]);

            Candle candle = new Candle(getScene(),stock,d,startX + (candleWidth + candleSep) * scaleX * (c + candlesBack - 1),focusY, focusPrice,candleWidth,new Vector(1,1));
            candles.add(candle);
        });

        //Reverse array list to keep it in order
        Collections.reverse(candles);

        //Traverse all Post candles from midpoint(Includes the target candle)
        StockTraversalUtil.traverseCandles(stock,focusTime,candlesForward + 1,(d, c,l) -> {
            float[] data = stock.getData(d);
            updateMinMax.run(data[2],data[3]);

            Candle candle = new Candle(getScene(),stock,d,startX + (candleWidth + candleSep) * scaleX * (c + candlesBack),focusY, focusPrice,candleWidth,new Vector(1,1));
            candles.add(candle);
        });

        //Set ScaleY
        double high = Math.abs(priceDiffHighMax.get());
        double low = Math.abs(priceDiffLowMin.get());
        double maxStretch = Math.max(high,low);
        double scaleY = (size.getY() / 2) / maxStretch;

        //==========================
        // DRAW DATA
        //==========================

        drawBackground();

        //Draw highlight column
        Rectangle rect = new Rectangle(getScene(),new Vector(startX + (candleWidth + candleSep) * scaleX * candlesBack,getPos().getY()),new Vector(candleWidth * scaleX,size.getY()),new Color(0,100,0,100));
        rect.draw();

        //Draw Candles
        for (Candle candle : candles) {
            candle.setScale(new Vector(scaleX,scaleY));
            candle.draw();
        }

        //Draw indicators
        for (Indicator indicator : indicators) {
            String name = indicator.getIndicatorName().split("_")[0];
            switch (name) {
                case "EMA", "SMA" -> {
                    Color color = new Color(0,125,255);
                    RenderMA ma = new RenderMA(getScene(),color,1,(IndicatorMA) indicator,candles);
                    ma.draw();
                }
                case "MACD" -> {
                    //UNIMPLEMENTED
                }
            }
        }

        //Draw Outline
        drawOutline();
    }

    @Override
    public boolean getMouseHoveredCalculation(Vector mousePos) {
        return Rectangle.isInRectangleBoundingBox(getPos(),size,mousePos);
    }


    //I stole this from my DropDown menu :)
    private void drawBackground() {
        //Draw Drop-Down
        RoundedRectangle dropDown = new RoundedRectangle(getScene(),getPos(),size,new Color(25,25,25,150));
        dropDown.setCornerRadius(5);
        dropDown.draw();
    }

    private void drawOutline() {
        //Draw Drop-Down Outline
        int offset = 1;
        RoundedRectangle dropDownOutline = new RoundedRectangle(getScene(),getPos().getAdded(offset,offset),size.getSubtracted(new Vector(offset,offset).getMultiplied(2)),color,5,true,2);
        dropDownOutline.setCornerRadius(5);
        dropDownOutline.draw();
    }


    public void setSize(Vector size) {
        this.size = size;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setFocusTime(DateTime focusTime) {
        this.focusTime = focusTime;
    }

    public void setCandlesBack(int candlesBack) {
        this.candlesBack = candlesBack;
    }

    public void setCandlesForward(int candlesForward) {
        this.candlesForward = candlesForward;
    }



    public Vector getSize() {
        return size;
    }

    public Color getColor() {
        return color;
    }

    public DateTime getFocusTime() {
        return focusTime;
    }

    public int getCandlesBack() {
        return candlesBack;
    }

    public int getCandlesForward() {
        return candlesForward;
    }
}
