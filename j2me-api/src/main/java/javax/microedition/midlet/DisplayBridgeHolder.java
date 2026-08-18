package javax.microedition.midlet;

final class DisplayBridgeHolder {
    static volatile DisplayBridge bridge;
    static volatile Runnable onDestroyed;

    private DisplayBridgeHolder() {}

    static void destroyed() {
        Runnable hook = onDestroyed;
        if (hook != null) {
            hook.run();
        }
    }
}
