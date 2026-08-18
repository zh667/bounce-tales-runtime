package javax.microedition.midlet;

/** Installs the desktop window behind LCDUI. */
public final class DisplayHost {
    private DisplayHost() {}

    public static void install(DisplayBridge bridge, Runnable onDestroyed) {
        DisplayBridgeHolder.bridge = bridge;
        DisplayBridgeHolder.onDestroyed = onDestroyed;
    }

    public static DisplayBridge bridge() {
        return DisplayBridgeHolder.bridge;
    }
}
