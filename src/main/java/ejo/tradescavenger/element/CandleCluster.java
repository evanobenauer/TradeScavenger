package ejo.tradescavenger.element;

import com.ejo.ui.Scene;
import com.ejo.ui.element.DrawableElement;
import com.ejo.ui.element.polygon.Rectangle;
import com.ejo.ui.element.polygon.RoundedRectangle;
import com.ejo.util.math.Vector;
import com.ejo.util.setting.Container;
import com.ejo.util.time.DateTime;
import ejo.tradescavenger.data.stock.Stock;
import ejo.tradescavenger.util.StockTraversalUtil;

import java.awt.*;
import java.util.ArrayList;

public class CandleCluster extends DrawableElement {

    private Vector size;
    private Color color;

    private final Stock stock;
    private DateTime startTime;

    private int candlesBack;
    private int candlesForward;

    public CandleCluster(Scene scene, Vector pos, Vector size, Color color, Stock stock, DateTime startTime, int candlesBack, int candlesForward) {
        super(scene, pos);
        this.size = size;
        this.color = color;

        this.stock = stock;
        this.startTime = startTime;

        this.candlesBack = candlesBack;
        this.candlesForward = candlesForward;
    }

    @Override
    public void draw(Vector mousePos) {
        drawBackground(color);

        //General
        int step = stock.getTimeFrame().getSeconds();
        int candleCount = candlesBack + candlesForward + 1;

        //Candle Dimensions
        int candleWidth = 10;
        int candleSep = 5;

        //Position Variables
        double focusY = getPos().getY() + size.getY() / 2;
        int startX = getPos().getXi() + candleSep;

        //ScaleX Definition
        double scaleX = (size.getX() - candleSep) / (candleCount * (candleWidth + candleSep));

        //Draw highlight column
        Rectangle rect = new Rectangle(getScene(),new Vector(startX + (candleWidth + candleSep) * scaleX * candlesBack,getPos().getY()),new Vector(candleWidth * scaleX,size.getY()),new Color(0,100,0,100));
        rect.draw();

        //Define price Min/Max containers
        Container<Double> priceDiffHighMax = new Container<>(0d);
        Container<Double> priceDiffLowMin = new Container<>(Math.pow(10,10));

        //Traverse all candles, add usable candles to the arrayList, update MinMax from the stock
        ArrayList<Candle> candles = new ArrayList<>();
        float focusPrice = stock.getOpen(startTime);
        StockTraversalUtil.traverseCandles(stock,startTime.getAdded(-step * candlesBack),startTime.getAdded(step * candlesForward),(d, l) -> {
            float[] data = stock.getData(d);

            // =============
            // Create a candle at the DateTime
            // =============
            Candle candle = new Candle(getScene(),stock,d,startX + (candleWidth + candleSep) * scaleX * l,focusY, focusPrice,candleWidth,new Vector(1,1));
            candles.add(candle);

            // =============
            // Update MinMax
            // =============

            float min = data[2];
            float max = data[3];

            //Update Min/Max price containers
            if (min != -1 && max != -1) {
                double priceDiffHigh = max - focusPrice;
                double priceDiffLow = min - focusPrice;
                if (priceDiffHigh > priceDiffHighMax.get()) priceDiffHighMax.set(priceDiffHigh);
                if (priceDiffLow < priceDiffLowMin.get()) priceDiffLowMin.set(priceDiffLow);
            }
        });

        //Set ScaleY
        double high = Math.abs(priceDiffHighMax.get());
        double low = Math.abs(priceDiffLowMin.get());
        double maxStretch = Math.max(high,low);
        double scaleY = (size.getY() / 2) / maxStretch;

        //Draw Candles
        for (Candle candle : candles) {
            candle.setScale(new Vector(scaleX,scaleY));
            candle.draw();
        }
    }

    @Override
    public boolean getMouseHoveredCalculation(Vector mousePos) {
        return Rectangle.isInRectangleBoundingBox(getPos(),size,mousePos);
    }


    //I stole this from my DropDown menu :)
    private void drawBackground(Color color) {
        //Draw Drop-Down
        RoundedRectangle dropDown = new RoundedRectangle(getScene(),getPos(),size,new Color(25,25,25,150));
        dropDown.setCornerRadius(5);
        dropDown.draw();

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

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
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

    public DateTime getStartTime() {
        return startTime;
    }

    public int getCandlesBack() {
        return candlesBack;
    }

    public int getCandlesForward() {
        return candlesForward;
    }
}
