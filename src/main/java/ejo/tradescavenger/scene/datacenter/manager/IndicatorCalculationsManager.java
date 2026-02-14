package ejo.tradescavenger.scene.datacenter.manager;

import com.ejo.ui.Scene;
import com.ejo.ui.element.SideBar;
import com.ejo.ui.manager.SceneManager;

import java.awt.*;

public class IndicatorCalculationsManager extends SceneManager {

    //TODO: put elements n calculations n shit for indicator calculator in here because data center is too big

    //Indicator Calculations Specific Elements
    private final SideBar indicatorCalculationsSideBar;

    public IndicatorCalculationsManager(Scene scene) {
        super(scene);
        this.indicatorCalculationsSideBar = new SideBar(scene, SideBar.Type.RIGHT,400,new Color(50,50,50,250),"Indicator Calculator");
        scene.addElements(indicatorCalculationsSideBar);
    }

}
