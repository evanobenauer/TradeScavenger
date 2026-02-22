package ejo.tradescavenger.element.indicator;

import com.ejo.ui.Scene;
import com.ejo.ui.element.DrawableElement;
import com.ejo.ui.element.Line;
import com.ejo.util.math.Vector;
import ejo.tradescavenger.data.indicator.ma.IndicatorMA;
import ejo.tradescavenger.element.candle.Candle;

import java.awt.*;
import java.util.ArrayList;

public class RenderMA extends DrawableElement {

    private Color color;
    private double width;

    private final IndicatorMA ma;
    private final ArrayList<Candle> candleList;

    public RenderMA(Scene scene, Color color, double width, IndicatorMA ma, ArrayList<Candle> candleList) {
        super(scene, Vector.NULL());

        this.color = color;
        this.width = width;

        this.ma = ma;
        this.candleList = candleList;
    }

    @Override
    public void draw(Vector mousePos) {
        //Create the points for the MA line
        ArrayList<Vector> points = new ArrayList<>();
        for (Candle candle : candleList) {
            float[] data = ma.getData(candle.getDateTime());
            float close = data[1];
            double maY = candle.getFocusY() - (close * candle.getScale().getY()) + candle.getFocusPrice() * candle.getScale().getY();
            if (close != -1)
                points.add(new Vector(candle.getPos().getX() + (candle.getBodySize().getX() / 2), maY));//This is a little buggy with precise positioning of points for some reason
        }

        //Draw the MA line
        if (!points.isEmpty()) {
            Line line = new Line(getScene(), width, Line.Type.PLAIN, color, points.toArray(new Vector[0]));
            line.draw();
        }
    }

    @Override
    public boolean getMouseHoveredCalculation(Vector mousePos) {
        return false;
    }


    public void setColor(Color color) {
        this.color = color;
    }

    public void setWidth(double width) {
        this.width = width;
    }


    public Color getColor() {
        return color;
    }

    public double getWidth() {
        return width;
    }


    public IndicatorMA getMA() {
        return ma;
    }
}
