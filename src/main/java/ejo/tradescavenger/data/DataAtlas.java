package ejo.tradescavenger.data;

import com.ejo.util.action.OnChange;
import ejo.tradescavenger.data.indicator.Indicator;
import ejo.tradescavenger.data.stock.Stock;

import java.util.ArrayList;
import java.util.HashMap;

//This exists to allocate a centralized location to all loaded indicators so they can be swapped between scenes
public class DataAtlas {

    public static Stock LOADED_STOCK = null;

    //K: File Name, V: Indicator
    public static HashMap<String,Indicator> LOADED_INDICATORS = new HashMap<>();

}
