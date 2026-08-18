package javax.microedition.midlet;

/** Same-package hook so the desktop host can call protected lifecycle methods. */
public final class MIDletBridge {
    private MIDletBridge() {}

    public static void start(MIDlet midlet) throws MIDletStateChangeException {
        midlet.startApp();
    }

    public static void pause(MIDlet midlet) {
        midlet.pauseApp();
    }

    public static void destroy(MIDlet midlet, boolean unconditional) throws MIDletStateChangeException {
        midlet.destroyApp(unconditional);
    }
}
