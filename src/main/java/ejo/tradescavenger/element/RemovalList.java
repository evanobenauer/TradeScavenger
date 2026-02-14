package ejo.tradescavenger.element;

import com.ejo.ui.Scene;
import com.ejo.ui.element.DrawableElement;
import com.ejo.ui.element.Text;
import com.ejo.ui.element.base.Interactable;
import com.ejo.ui.element.polygon.Rectangle;
import com.ejo.ui.element.polygon.RoundedRectangle;
import com.ejo.ui.render.FontRenderer;
import com.ejo.ui.render.GLUtil;
import com.ejo.util.math.Vector;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RemovalList<V> extends DrawableElement implements Interactable {

    private final HashMap<String,V> map;

    private double width;
    private Color color;

    private ArrayList<Text> textList;

    private final FontRenderer fontGeneral;
    private final FontRenderer fontHovered;

    public RemovalList(Scene scene, Vector pos, double width, Color color, HashMap<String,V> map) {
        super(scene, pos);
        this.map = map;
        this.width = width;
        this.color = color;

        this.textList = new ArrayList<>();

        //Setup font renderers
        int fontSize = 40;

        this.fontGeneral = new FontRenderer(new Font("Arial", Font.PLAIN, fontSize));

        Font fontHovered = new Font("Arial", Font.ITALIC, fontSize);
        Map attributes = fontHovered.getAttributes();
        attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
        this.fontHovered = new FontRenderer(new Font(attributes));
    }

    @Override
    public boolean getMouseHoveredCalculation(Vector mousePos) {
        return Rectangle.isInRectangleBoundingBox(getPos(),getSize(),mousePos);
    }

    @Override
    public void draw(Vector mousePos) {
        if (!map.isEmpty()) drawBackground();

        updateTextList();

        drawTextList(mousePos);
    }

    @Override
    public void onMouseClick(int button, int action, int mods, Vector mousePos) {
        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT || action != GLFW.GLFW_PRESS) return;

        //Remove the item in the list if it's corresponding text is hovered
        for (Text text : textList) {
            if (!text.getMouseHoveredCalculation(mousePos) || !isMouseHovered()) continue;
            map.remove(text.getText());
            System.gc();
            break;
        }
    }

    @Override
    public void onKeyPress(int key, int scancode, int action, int mods) {
        //NA
    }

    @Override
    public void onMouseScroll(double scroll, Vector mousePos) {
        //NA
    }

    // ===========================

    // DRAW/UPDATE METHODS

    // ===========================

    //I stole this from my DropDown menu :)
    private void drawBackground() {
        Vector size = new Vector(getSize().getX(),getHeight());
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

    private void drawTextList(Vector mousePos) {
        double border = fontGeneral.getHeight() / 5;
        double add = -border;
        for (Text text : textList) {
            //Update Pos
            Vector pos = getPos().getAdded(0,add);
            text.setPos(pos);

            //Update Scaling
            double scale = 1;
            double textWidth = text.getSize().getX();
            double widgetWidth = getSize().getX() - border * 2;
            if (textWidth > widgetWidth) scale = widgetWidth / textWidth;
            Vector scaleVector = new Vector(scale,scale);
            GLUtil.textureScale(new Vector(scaleVector.getX(),scaleVector.getY(),1));
            text.setPos(pos.getAdded(border + 2, fontGeneral.getHeight() / 2 * scale));

            //Update Color
            boolean hovered = text.getMouseHoveredCalculation(mousePos) && isMouseHovered();
            if (hovered) text.setColor(Color.GRAY);

            //Update Font
            FontRenderer font = hovered ? fontHovered : fontGeneral;
            text.setFontRenderer(font);

            //Draw Text
            text.draw();
            GLUtil.textureScale(1);

            //Increment to next text
            add += text.getSize().getY() + 5;
        }
    }


    private void updateTextList() {
        ArrayList<Text> texts = new ArrayList<>();
        for (String key : getMap().keySet()) {
            Text text = new Text(getScene(), Vector.NULL(), key, fontGeneral.getFont(), Color.WHITE, Text.Type.STATIC);
            texts.add(text);
        }
        this.textList = texts;
    }

    // ===========================

    // GETTERS/SETTERS

    // ===========================

    public void setWidth(double width) {
        this.width = width;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    private double getHeight() {
        double height = 4;
        for (Text text : textList) height += text.getSize().getY() + 5;
        return height;
    }

    public Vector getSize() {
        return new Vector(width,getHeight());
    }

    public Color getColor() {
        return color;
    }

    public HashMap<String,V> getMap() {
        return map;
    }
}
