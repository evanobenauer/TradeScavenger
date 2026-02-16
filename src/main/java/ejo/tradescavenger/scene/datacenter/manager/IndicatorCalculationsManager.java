package ejo.tradescavenger.scene.datacenter.manager;

import com.ejo.ui.Scene;
import com.ejo.ui.element.SideBar;
import com.ejo.ui.element.widget.Button;
import com.ejo.ui.manager.SceneManager;
import com.ejo.util.math.Vector;
import com.ejo.util.time.DateTime;
import ejo.tradescavenger.data.DataAtlas;
import ejo.tradescavenger.data.indicator.Indicator;
import ejo.tradescavenger.element.CandleCluster;

import java.awt.*;

//TODO: THE INDICATOR CALCULATIONS MANAGER IS CURRENTLY EXTREMELY WIP. IT IS BEING USED FOR DEBUG RN
//TODO: put elements n calculations n shit for indicator calculator in here because data center is too big

public class IndicatorCalculationsManager extends SceneManager {

    //Indicator Calculations Specific Elements
    private final SideBar indicatorCalculationsSideBar;

    private DateTime startTime = new DateTime(2023,1,1);
    private DateTime endTime = new DateTime(2024,1,12);

    Indicator indicator;
    CandleCluster cluster;

    public IndicatorCalculationsManager(Scene scene) {
        super(scene);
        this.indicatorCalculationsSideBar = new SideBar(scene, SideBar.Type.RIGHT,400,new Color(50,50,50,250),"Indicator Calculator");


        Button debugButton = new Button(scene,new Vector(30,200),new Vector(340,60),Color.RED,"DEBUG",() -> {
            indicator = getFirst();
            Thread thread = new Thread(() -> {
                indicator.calculate(startTime,endTime);
                System.out.println("DONE");
                indicatorCalculationsSideBar.removeElement(cluster,true);
            });
            thread.start();
            cluster = new CandleCluster(scene,Vector.NULL(),new Vector(340,200),Color.RED,getFirst().getStock(),startTime,150,150);
            cluster.setPos(new Vector(30, scene.getWindow().getSize().getY() - cluster.getSize().getY() - 30));
            indicatorCalculationsSideBar.addElements(cluster);
        });
        indicatorCalculationsSideBar.addElements(debugButton);


        scene.addElements(indicatorCalculationsSideBar);
    }

    @Override
    public void draw(Vector mousePos) {

        if (cluster != null) {
            cluster.setPos(new Vector(30, scene.getWindow().getSize().getY() - cluster.getSize().getY() - 30));
            if (indicator.getCurrentCalculationDate() != null)
                cluster.setFocusTime(indicator.getCurrentCalculationDate());
        }

        super.draw(mousePos);
    }

    private Indicator getFirst() {
        for (Indicator ind : DataAtlas.LOADED_INDICATORS.values())
            return ind;
        return null;
    }

}
