package ejo.tradescavenger.scene.manager;

import com.ejo.ui.Scene;
import com.ejo.ui.manager.SceneManager;
import com.ejo.ui.render.FontRenderer;
import com.ejo.util.math.Vector;
import com.ejo.util.time.DateTime;
import ejo.tradescavenger.data.DataAtlas;
import ejo.tradescavenger.data.HistoricalDataContainer;
import ejo.tradescavenger.data.indicator.Indicator;
import ejo.tradescavenger.data.indicator.ma.IndicatorEMA;
import ejo.tradescavenger.data.stock.Stock;
import ejo.tradescavenger.util.TimeFrame;

import java.awt.*;

public class LoadedDataManager extends SceneManager {

    public LoadedDataManager(Scene scene) {
        super(scene);
        DataAtlas.LOADED_STOCK = new Stock("SPY", TimeFrame.ONE_MINUTE,true);
        DataAtlas.LOADED_INDICATORS.add(new IndicatorEMA(DataAtlas.LOADED_STOCK,30));
        DataAtlas.LOADED_INDICATORS.add(new IndicatorEMA(DataAtlas.LOADED_STOCK,31));
        DataAtlas.LOADED_INDICATORS.add(new IndicatorEMA(DataAtlas.LOADED_STOCK,32));
    }


    //TODO: Include the loaded date range in this method as well!!!
    @Override
    public void draw(Vector mousePos) {
        int yIncrement = 10;
        DataSet stockSet = new DataSet(DataAtlas.LOADED_STOCK);
        stockSet.draw(yIncrement);
        yIncrement += stockSet.getIncrement();

        for (Indicator indicator: DataAtlas.LOADED_INDICATORS) {
            DataSet indicatorSet = new DataSet(indicator);
            indicatorSet.draw(yIncrement);
            yIncrement += indicatorSet.getIncrement();
        }

    }


    public class DataSet {

        HistoricalDataContainer container;

        FontRenderer fontRenderer;

        public DataSet(HistoricalDataContainer container) {
            this.container = container;
            this.fontRenderer = new FontRenderer(new Font("Arial",Font.PLAIN,20));
        }

        public void draw(int y) {
            String text = getPrefix() + container;
            Vector pos = new Vector(scene.getWindow().getSize().getX() - fontRenderer.getWidth(text) - 10, y);
            fontRenderer.drawStaticString(text,pos,Color.GRAY);

            DateTime[] dateRange = container.getLoadedDateRange();
            String text2 = dateRange[0] + "-" + dateRange[1];
            Vector pos2 = new Vector(scene.getWindow().getSize().getX() - fontRenderer.getWidth(text2) - 10, y + 20);
            fontRenderer.drawDynamicString(text2,pos2,Color.GRAY);
        }

        public String getPrefix() {
            if (container instanceof Stock) return "Loaded Stock: ";
            if (container instanceof Indicator) return "Loaded Indicator: ";
            return "";
        }

        public int getIncrement() {
            return 50;
        }
    }
}
