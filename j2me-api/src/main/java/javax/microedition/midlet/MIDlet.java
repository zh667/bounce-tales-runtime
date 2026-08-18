package javax.microedition.midlet;

/**
 * MIDP MIDlet lifecycle. Original host type; game classes come from the user JAR.
 */
public abstract class MIDlet {
    protected MIDlet() {}

    protected abstract void startApp() throws MIDletStateChangeException;

    protected abstract void pauseApp();

    protected abstract void destroyApp(boolean unconditional) throws MIDletStateChangeException;

    public final void notifyDestroyed() {
        DisplayBridge.destroyed();
    }

    public final void notifyPaused() {}

    public final String getAppProperty(String key) {
        return AppProperties.get(key);
    }

    public final boolean platformRequest(String url) {
        return false;
    }
}
