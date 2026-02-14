package ejo.tradescavenger.scene.manager;

import com.ejo.ui.Scene;
import com.ejo.ui.manager.SceneManager;
import com.ejo.ui.render.FontRenderer;
import com.ejo.util.math.Vector;
import com.ejo.util.time.DateTime;
import ejo.tradescavenger.data.DataAtlas;
import ejo.tradescavenger.data.HistoricalDataContainer;
import ejo.tradescavenger.data.indicator.Indicator;

import java.awt.*;
import java.util.ConcurrentModificationException;

//TODO: Make this text kinda nicer lookin. Maybe add a nice background with a red outline??

public class DataDisplayManager extends SceneManager {

    private static final FontRenderer DATA_SET_FONT_RENDERER = new FontRenderer(new Font("Arial",Font.PLAIN,20));

    public DataDisplayManager(Scene scene) {
        super(scene);
    }

    @Override
    public void draw(Vector mousePos) {
        int yIncrement = 10;

        if (DataAtlas.LOADED_STOCK != null) {
            DataSet stockSet = new DataSet(DataAtlas.LOADED_STOCK);
            stockSet.draw(yIncrement);
            yIncrement += stockSet.getIncrement();
        }

        try {
            for (Indicator indicator : DataAtlas.LOADED_INDICATORS.values()) {
                DataSet indicatorSet = new DataSet(indicator);
                indicatorSet.draw(yIncrement);
                yIncrement += indicatorSet.getIncrement();
            }
        } catch (ConcurrentModificationException _) {
            System.out.println("Ran into a concurrent modification while trying to display the Loaded Indicators List");
            System.out.println("The list was probably being rendered while a file was loading");
        }
    }

    class DataSet {

        HistoricalDataContainer container;

        DataSet(HistoricalDataContainer container) {
            this.container = container;
        }

        void draw(int y) {
            String text = getPrefix() + container;
            Vector pos = new Vector(scene.getWindow().getSize().getX() - DATA_SET_FONT_RENDERER.getWidth(text) - 10, y);
            DATA_SET_FONT_RENDERER.drawStaticString(text,pos,Color.GRAY);

            DateTime[] dateRange = container.getLoadedDateRange();
            String text2 = dateRange[0] + "-" + dateRange[1];
            Vector pos2 = new Vector(scene.getWindow().getSize().getX() - DATA_SET_FONT_RENDERER.getWidth(text2) - 10, y + 20);
            DATA_SET_FONT_RENDERER.drawDynamicString(text2,pos2,Color.GRAY);
        }

        String getPrefix() {
            //if (container instanceof Stock) return "Loaded Stock: ";
            //if (container instanceof Indicator) return "Loaded Indicator: ";
            return "";
        }

        int getIncrement() {
            return 50;
        }
    }
}
