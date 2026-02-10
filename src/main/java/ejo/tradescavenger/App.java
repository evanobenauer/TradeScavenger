package ejo.tradescavenger;

import com.ejo.ui.Window;
import com.ejo.util.math.Vector;
import ejo.tradescavenger.scene.TitleScene;

public class App {

    public static Window WINDOW = new Window("Window",new Vector(1600,1000));

    static void main() {
        WINDOW.setScene(new TitleScene());
        WINDOW.initAntiAliasingLevel(4);
        WINDOW.init();
        WINDOW.startThreadTickLoop();
        WINDOW.runMainRenderLoop();
    }
}
