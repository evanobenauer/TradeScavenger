package ejo.tradescavenger.element;

import com.ejo.ui.Scene;
import com.ejo.ui.element.polygon.Rectangle;
import com.ejo.ui.render.GLUtil;
import com.ejo.util.math.Vector;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GradientRectangle extends Rectangle {

    private final Color color1;
    private final Color color2;
    private final Type type;

    public GradientRectangle(Scene scene, Vector pos, Vector size, Color color1, Color color2, Type type) {
        super(scene,pos,size,color1);
        this.color1 = color1;
        this.color2 = color2;
        this.type = type;
    }

    public void draw(Vector mousePos) {
        updateVertices();
        GLUtil.color(getColor().getRed(),getColor().getGreen(),getColor().getBlue(),getColor().getAlpha());
        GL11.glBegin(GL11.GL_POLYGON);
        boolean cycle = true;
        boolean swap = false;
        for (Vector vert : vertices) {
            Color col = cycle ? getColor2() : getColor1();
            GLUtil.color(col.getRed(),col.getGreen(),col.getBlue(),col.getAlpha());
            GL11.glVertex2f(getPos().getXf() + vert.getXf(), getPos().getYf() + vert.getYf());
            if (type.equals(Type.VERTICAL)) swap = !swap;
            if (swap) cycle = !cycle;
            if (type.equals(Type.HORIZONTAL)) swap = !swap;
        }
        GL11.glEnd();
        GLUtil.color(1,1,1,1);
    }

    public Color getColor() {
        return getColor1();
    }

    public Color getColor1() {
        return this.color1;
    }

    public Color getColor2() {
        return this.color2;
    }

    public Type getType() {
        return this.type;
    }

    public enum Type {
        VERTICAL,
        HORIZONTAL;
    }
}
