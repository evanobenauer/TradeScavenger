package ejo.tradescavenger.element.candle;

import com.ejo.ui.Scene;
import com.ejo.ui.element.DrawableElement;
import com.ejo.util.math.Vector;

import java.awt.*;

public class GeneralCandle extends DrawableElement {


    public GeneralCandle(Scene scene, Vector pos, Vector size, int upperWickSize, int lowerWickSize, Color color) {
        super(scene, pos);
    }

    @Override
    public void draw(Vector vector) {

    }

    @Override
    public boolean getMouseHoveredCalculation(Vector vector) {
        return false;
    }


}
