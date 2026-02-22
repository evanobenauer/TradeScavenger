package ejo.tradescavenger.scene.datacenter.manager;

import com.ejo.ui.Scene;
import com.ejo.ui.element.ProgressBar;
import com.ejo.ui.element.SideBar;
import com.ejo.ui.element.polygon.RoundedRectangle;
import com.ejo.ui.element.widget.Button;
import com.ejo.ui.manager.SceneManager;
import com.ejo.util.math.Vector;
import com.ejo.util.setting.Container;
import com.ejo.util.time.DateTime;
import com.ejo.util.time.TimeUtil;
import ejo.tradescavenger.data.DataAtlas;
import ejo.tradescavenger.data.indicator.Indicator;
import ejo.tradescavenger.element.CandleCluster;

import java.awt.*;

//TODO: THE INDICATOR CALCULATIONS MANAGER IS CURRENTLY EXTREMELY WIP. IT IS BEING USED FOR DEBUG RN
//TODO: put elements n calculations n shit for indicator calculator in here because data center is too big

public class IndicatorCalculationsManager extends SceneManager {

    //Indicator Calculations Specific Elements
    private final SideBar indicatorCalculationsSideBar;

    private DateTime startTime = DateTime.getById(19930129093000L);//new DateTime(2023,1,1);
    private DateTime endTime = DateTime.getById(20260220093000L);//new DateTime(2024,1,12);

    Indicator indicator;
    CandleCluster cluster;
    ProgressBar<Double> calculationProgressBar;
    RoundedRectangle barBackground;

    public IndicatorCalculationsManager(Scene scene) {
        super(scene);
        this.indicatorCalculationsSideBar = new SideBar(scene, SideBar.Type.RIGHT,400,new Color(50,50,50,250),"Indicator Calculator");
        this.calculationProgressBar = new ProgressBar<>(scene,new Vector(30,0),new Vector(340,30),Color.RED,new Container<>(0d),0,1);
        Button debugButton = new Button(scene,new Vector(30,200),new Vector(340,60),Color.RED,"DEBUG",() -> {
            indicator = getFirst();
            indicator.getLoadedData().clear();
            Thread thread = new Thread(() -> {
                indicator.calculate(startTime,endTime);
                System.out.println("DONE");
                indicatorCalculationsSideBar.removeElement(cluster,true);
            });
            thread.start();
            cluster = new CandleCluster(scene,Vector.NULL(),new Vector(340,200),Color.RED,startTime,200,200,getFirst().getStock(),indicator);
            cluster.setPos(new Vector(30, scene.getWindow().getSize().getY() - cluster.getSize().getY() - 30));
            indicatorCalculationsSideBar.addElements(cluster);
        });
        indicatorCalculationsSideBar.addElements(debugButton);
        barBackground = new RoundedRectangle(scene,calculationProgressBar.getPos(),calculationProgressBar.getSize(),Color.BLACK);
        indicatorCalculationsSideBar.addElements(barBackground);
        indicatorCalculationsSideBar.addElements(calculationProgressBar);


        scene.addElements(indicatorCalculationsSideBar);
    }

    @Override
    public void draw(Vector mousePos) {
        calculationProgressBar.setPos(new Vector(30, scene.getWindow().getSize().getY() - calculationProgressBar.getSize().getY() - 15));
        barBackground.setPos(calculationProgressBar.getPos());

        if (cluster != null) {
            cluster.setPos(new Vector(30, scene.getWindow().getSize().getY() - cluster.getSize().getY() - 60));
            if (indicator.getCurrentCalculationDate() != null) {
                cluster.setFocusTime(indicator.getCurrentCalculationDate());
                calculationProgressBar.getProgressContainer().set(TimeUtil.getDateTimePercent(indicator.getCurrentCalculationDate(),startTime,endTime));
            }
        }

        super.draw(mousePos);
    }

    private Indicator getFirst() {
        for (Indicator ind : DataAtlas.LOADED_INDICATORS.values())
            return ind;
        return null;
    }

}
