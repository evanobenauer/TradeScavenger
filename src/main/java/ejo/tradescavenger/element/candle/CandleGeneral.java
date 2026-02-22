package ejo.tradescavenger.element.candle;

import com.ejo.ui.Scene;
import com.ejo.ui.element.DrawableElement;
import com.ejo.ui.element.polygon.Rectangle;
import com.ejo.util.math.Vector;

import java.awt.*;

public class CandleGeneral extends DrawableElement {

    private Vector size;
    private float upperWickSize;
    private float lowerWickSize;
    private Color color;

    public CandleGeneral(Scene scene, Vector pos, Vector size, float upperWickSize, float lowerWickSize, Color color) {
        super(scene, pos);
        this.size = size;
        this.upperWickSize = upperWickSize;
        this.lowerWickSize = lowerWickSize;
        this.color = color;
    }

    @Override
    public void draw(Vector vector) {
        //Draw Wicks
        int colorOffset = 100;
        int r = Math.clamp(color.getRed() - colorOffset,0,255);
        int g = Math.clamp(color.getGreen() - colorOffset,0,255);
        int b = Math.clamp(color.getBlue() - colorOffset,0,255);
        int a;

        Color wickColor = new Color(r,g,b);

        double wickWidth = size.getX() / 6;
        Vector wickPos = getPos().getAdded((size.getX() - wickWidth) / 2, 0);

        new Rectangle(getScene(),wickPos,new Vector(wickWidth, upperWickSize),wickColor).draw();
        new Rectangle(getScene(),wickPos,new Vector(wickWidth, lowerWickSize),wickColor).draw();

        //Draw Body
        new Rectangle(getScene(),getPos(),new Vector(size.getX(), 1),color).draw(); //Base Gray Body
        new Rectangle(getScene(),getPos(),size,color).draw(); //Base Gray Body
    }

    @Override
    public boolean getMouseHoveredCalculation(Vector mousePos) {
        return Rectangle.isInRectangleBoundingBox(getPos(),size, mousePos);
    }


}
