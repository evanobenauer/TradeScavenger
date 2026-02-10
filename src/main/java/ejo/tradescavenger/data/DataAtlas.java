package ejo.tradescavenger.data;

import ejo.tradescavenger.data.indicator.Indicator;
import ejo.tradescavenger.data.stock.Stock;

import java.util.ArrayList;

//This exists to allocate a centralized location to all loaded indicators so they can be swapped between scenes
public class DataAtlas {

    public static Stock LOADED_STOCK = null;

    public static ArrayList<Indicator> LOADED_INDICATORS = new ArrayList<>();

}
