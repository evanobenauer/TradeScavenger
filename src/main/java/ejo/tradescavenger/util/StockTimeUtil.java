package ejo.tradescavenger.util;

import com.ejo.util.time.DateTime;

public class StockTimeUtil {

    public static boolean isTradingHours(DateTime currentTime) {
        return !currentTime.isWeekend() && currentTime.getHour() < 16 && currentTime.getHour() >= 9 && (currentTime.getHour() != 9 || currentTime.getMinute() >= 30);
    }

    public static boolean isPreMarket(DateTime currentTime) {
        return !isTradingHours(currentTime) && !currentTime.isWeekend() && currentTime.getHour() >= 4 && currentTime.getHour() < 10;
    }

    public static boolean isPostMarket(DateTime currentTime) {
        return !isTradingHours(currentTime) && !currentTime.isWeekend() && currentTime.getHour() >= 16 && currentTime.getHour() < 20;
    }

    public static boolean isPriceActive(boolean extendedHours, DateTime currentTime) {
        if (extendedHours) return isTradingHours(currentTime) || isPreMarket(currentTime) || isPostMarket(currentTime);
        return isTradingHours(currentTime);
    }

}
