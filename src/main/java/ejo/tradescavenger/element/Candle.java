package ejo.tradescavenger.element;

import com.ejo.ui.Scene;
import com.ejo.ui.element.DrawableElement;
import com.ejo.ui.element.shape.Rectangle;
import com.ejo.util.math.Vector;
import com.ejo.util.time.DateTime;
import ejo.tradescavenger.data.stock.StockLive;
import ejo.tradescavenger.data.stock.Stock;

import java.awt.*;

public class Candle extends DrawableElement {

    private static final Color COLOR_GREEN = new Color(75, 200, 75);
    private static final Color COLOR_RED = new Color(200, 50, 50);
    private static final Color COLOR_NULL = Color.GRAY;

    private float[] data;

    private final Stock stock;
    private final DateTime dateTime;

    private final double x, focusY, focusPrice;

    private final double width;

    private final Vector scale;

    //Historical Candle
    public Candle(Scene scene, Stock stock, DateTime dateTime, double x, double focusY, double focusPrice, double width, Vector scale) {
        super(scene, Vector.NULL());
        this.stock = stock;
        this.dateTime = dateTime;

        this.x = x;
        this.focusY = focusY; //The Y position on the screen of the focus price
        this.focusPrice = focusPrice; //The price at which the chart is centered at

        this.width = width;
        this.scale = scale;

        updateData();
    }

    //Live Candle
    public Candle(Scene scene, StockLive stock, double x, double focusY, double focusPrice, double width, Vector scale) {
        this(scene, stock, stock.getOpenTime(), x, focusY, focusPrice, width, scale);
    }

    @Override
    public void draw(Vector mousePos) {
        updateData();
        float open = data[0];
        float min = data[2];
        float max = data[3];

        //Wicks
        int colorOffset = 100;
        double wickWidth = getBodySize().getX() / 6;
        Vector wickPos = getPos().getAdded((getBodySize().getX() - wickWidth) / 2, 0);
        Color wickColor = new Color(getColor().getRed() - colorOffset, getColor().getGreen() - colorOffset, getColor().getBlue() - colorOffset);
        new Rectangle(getScene(),wickPos,new Vector(wickWidth, -(max - open) * scale.getY()),wickColor).draw();
        new Rectangle(getScene(),wickPos,new Vector(wickWidth, (open - min) * scale.getY()),wickColor).draw();

        //Body
        new Rectangle(getScene(),getPos(),new Vector(getBodySize().getX(), 1),getColor()).draw(); //Base Gray Body
        new Rectangle(getScene(),getPos(),getBodySize(),getColor()).draw(); //Base Gray Body
    }

    @Override
    public boolean getMouseHoveredCalculation(Vector mousePos) {
        boolean mouseOverX = mousePos.getX() >= getPos().getX() && mousePos.getX() <= getPos().getX() + getBodySize().getX();
        boolean mouseOverYDown = mousePos.getY() >= getPos().getY() && mousePos.getY() <= getPos().getY() + getBodySize().getY();
        boolean mouseOverYUp = mousePos.getY() <= getPos().getY() && mousePos.getY() >= getPos().getY() + getBodySize().getY();
        return (mouseOverX && (mouseOverYDown || mouseOverYUp));
    }

    //Data is updated like this so that there are not many calls to stock get methods as those are not as efficient
    private void updateData() {
        data = stock.getData(dateTime);
    }

    public boolean isGreen() {
        float open = data[0];
        float close = data[1];
        return close > open;
    }

    public boolean isRed() {
        float open = data[0];
        float close = data[1];
        return close < open;
    }

    public Color getColor() {
        if (isGreen()) return COLOR_GREEN;
        if (isRed()) return COLOR_RED;
        return COLOR_NULL;
    }

    @Override
    public Vector getPos() {
        float open = data[0];
        return new Vector(x, focusY - (open * scale.getY()) + focusPrice * scale.getY());
    }

    public Vector getBodySize() {
        float open = data[0];
        float close = data[1];
        double candleHeight = -(close - open) * scale.getY();
        return new Vector(width * scale.getX(), candleHeight);
    }

    public DateTime getDateTime() {
        return dateTime;
    }
}