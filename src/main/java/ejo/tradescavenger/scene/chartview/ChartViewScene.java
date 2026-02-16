package ejo.tradescavenger.scene.chartview;

import com.ejo.ui.Scene;
import com.ejo.util.math.Vector;
import com.ejo.util.time.DateTime;
import com.ejo.util.time.TimeUtil;
import ejo.tradescavenger.data.DataAtlas;
import ejo.tradescavenger.element.CandleCluster;
import ejo.tradescavenger.element.GradientRectangle;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.HashMap;

public class ChartViewScene extends Scene {

    CandleCluster cluster;

    public ChartViewScene() {
        super("Chart View");
        cluster = new CandleCluster(this,new Vector(300,300), new Vector(800,200),Color.RED, DataAtlas.LOADED_STOCK, TimeUtil.getCurrentDateTime(),15,30);

        addElements(cluster);
    }

    int index = 0;

    @Override
    public void draw() {
        //Draw Background
        GradientRectangle background = new GradientRectangle(this, Vector.NULL(),getWindow().getSize(),new Color(0,0,0,255),new Color(128, 128, 128,20), GradientRectangle.Type.VERTICAL);
        background.draw();

        HashMap<Long,float[]> map = DataAtlas.LOADED_STOCK.getLoadedData();

        int i = 0;
        DateTime startTime = null;
        for (long dt : map.keySet()) {
            DateTime d = DateTime.getById(dt);
            startTime = d;
            if (i == index) break;
            i++;
        }

        cluster.setFocusTime(startTime);


        super.draw();
    }

    @Override
    public void onKeyPress(int key, int scancode, int action, int mods) {
        if (action == GLFW.GLFW_RELEASE) return;

        if (key == GLFW.GLFW_KEY_RIGHT) index ++;
        if (key == GLFW.GLFW_KEY_LEFT) index --;

        int x = 10;
        if (key == GLFW.GLFW_KEY_A) cluster.setSize(cluster.getSize().getAdded(-x,0));
        if (key == GLFW.GLFW_KEY_D) cluster.setSize(cluster.getSize().getAdded(x,0));
        if (key == GLFW.GLFW_KEY_W) cluster.setSize(cluster.getSize().getAdded(0,-x));
        if (key == GLFW.GLFW_KEY_S) cluster.setSize(cluster.getSize().getAdded(0,x));


        if (key == GLFW.GLFW_KEY_H) cluster.setCandlesBack(cluster.getCandlesBack() - 1);
        if (key == GLFW.GLFW_KEY_G) cluster.setCandlesBack(cluster.getCandlesBack() + 1);

        if (key == GLFW.GLFW_KEY_T) cluster.setCandlesForward(cluster.getCandlesForward() - 1);
        if (key == GLFW.GLFW_KEY_Y) cluster.setCandlesForward(cluster.getCandlesForward() + 1);

        super.onKeyPress(key, scancode, action, mods);
    }
}
