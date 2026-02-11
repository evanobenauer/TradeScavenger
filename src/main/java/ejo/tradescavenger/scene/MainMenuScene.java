package ejo.tradescavenger.scene;

import com.ejo.ui.Scene;
import com.ejo.ui.element.Text;
import com.ejo.ui.element.widget.Button;
import com.ejo.util.math.Vector;
import ejo.tradescavenger.data.DataAtlas;
import ejo.tradescavenger.data.indicator.Indicator;
import ejo.tradescavenger.element.GradientRectangle;
import ejo.tradescavenger.scene.manager.LoadedDataManager;
import ejo.tradescavenger.util.TitleBounceHandler;

import java.awt.*;

public class MainMenuScene extends Scene {

    private final TitleBounceHandler titleBounceHandler;

    private int buttonX;
    private Button startButtonMiddleBackTest;
    private Button startButtonUpperDataCenter;
    private Button startButtonLowerChartView;

    public MainMenuScene() {
        super("Title Scene");

        //Create Title
        Text title = new Text(this,Vector.NULL(),"Trade Scavenger", new Font("Arial Black",Font.PLAIN,100),Color.WHITE, Text.Type.STATIC);
        this.titleBounceHandler = new TitleBounceHandler(title,title.getPos());

        //Init Start Buttons
        this.buttonX = -200;
        initStartButtons();

        //Add Elements
        addElements(title);
        addElements(startButtonMiddleBackTest, startButtonLowerChartView, startButtonUpperDataCenter);

        addSceneManagers(new LoadedDataManager(this));
    }


    @Override
    public void draw() {
        //Update Positions
        updateElementPositions();

        //Draw Background
        GradientRectangle background = new GradientRectangle(this,Vector.NULL(),getWindow().getSize(),new Color(0,0,0,255),new Color(255,0,0,20), GradientRectangle.Type.VERTICAL);
        background.draw();

        //Draw Elements
        super.draw();
    }

    @Override
    public void updateAnimation() {
        //Button Roll-In Animation
        int maxX = 100;
        if (buttonX < maxX) buttonX += 10;
        super.updateAnimation();
    }

    public void initStartButtons() {
        Color widgetColor = new Color(255,0,0);
        this.startButtonUpperDataCenter = new Button(this, Vector.NULL(),new Vector(200,200), widgetColor,"D",() -> {
            getWindow().setScene(new DataCenterScene());
        });

        this.startButtonMiddleBackTest = new Button(this, Vector.NULL(),new Vector(200,200), widgetColor,"B",() -> {
            System.out.println("This is the BackTest Button");
        });

        this.startButtonLowerChartView = new Button(this, Vector.NULL(),new Vector(200,200), widgetColor,"C",() -> {
            System.out.println("This is the ChartView Button");
        });
    }

    public void updateElementPositions() {
        //Update Title Position
        this.titleBounceHandler.setPos((getWindow().getSize().getScaled(1.25,1).getSubtracted(titleBounceHandler.getText().getSize())).getMultiplied(.5).getSubtracted(0,titleBounceHandler.getText().getSize().getY() / 4));
        this.titleBounceHandler.updatePos();

        //Update Start Buttons
        int sep = 50;
        startButtonMiddleBackTest.setPos(new Vector(buttonX,(getWindow().getSize().getY() - startButtonMiddleBackTest.getSize().getY()) / 2));
        startButtonUpperDataCenter.setPos(new Vector(buttonX,(getWindow().getSize().getY() - startButtonMiddleBackTest.getSize().getY()) / 2).getSubtracted(0, startButtonMiddleBackTest.getSize().getY() + sep));
        startButtonLowerChartView.setPos(new Vector(buttonX,(getWindow().getSize().getY() - startButtonMiddleBackTest.getSize().getY()) / 2).getAdded(0, startButtonMiddleBackTest.getSize().getY() + sep));
    }
}
