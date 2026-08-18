package javax.microedition.midlet;

final class ProbeMidlet extends MIDlet {
    boolean started;

    @Override
    protected void startApp() {
        started = true;
    }

    @Override
    protected void pauseApp() {}

    @Override
    protected void destroyApp(boolean unconditional) {}
}
