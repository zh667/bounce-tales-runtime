package javax.microedition.lcdui;

import javax.microedition.midlet.DisplayBridge;
import javax.microedition.midlet.DisplayHost;
import javax.microedition.midlet.MIDlet;

public class Display {
    private static final Display INSTANCE = new Display();
    private Displayable current;

    public static Display getDisplay(MIDlet midlet) {
        return INSTANCE;
    }

    public void setCurrent(Displayable next) {
        Displayable previous = current;
        current = next;
        DisplayBridge bridge = DisplayHost.bridge();
        if (bridge != null && next instanceof Canvas canvas) {
            if (previous != next) {
                canvas.showNotify();
            }
            bridge.attach(canvas);
        }
    }

    public Displayable getCurrent() {
        return current;
    }

    public void flush() {
        DisplayBridge bridge = DisplayHost.bridge();
        if (bridge != null) {
            bridge.flush();
        }
    }

    static int screenWidth() {
        DisplayBridge bridge = DisplayHost.bridge();
        return bridge == null ? 240 : bridge.width();
    }

    static int screenHeight() {
        DisplayBridge bridge = DisplayHost.bridge();
        return bridge == null ? 320 : bridge.height();
    }
}
