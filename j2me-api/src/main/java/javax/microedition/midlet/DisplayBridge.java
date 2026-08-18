package javax.microedition.midlet;

/** Desktop window callbacks used by LCDUI {@code Display}. */
public interface DisplayBridge {
    void attach(Object canvas);

    void flush();

    int width();

    int height();

    static void destroyed() {
        DisplayBridgeHolder.destroyed();
    }
}
