package javax.microedition.lcdui;

public abstract class Canvas extends Displayable {
    public static final int UP = 1;
    public static final int DOWN = 6;
    public static final int LEFT = 2;
    public static final int RIGHT = 5;
    public static final int FIRE = 8;
    public static final int GAME_A = 9;
    public static final int GAME_B = 10;
    public static final int GAME_C = 11;
    public static final int GAME_D = 12;
    public static final int KEY_NUM0 = 48;
    public static final int KEY_NUM1 = 49;
    public static final int KEY_NUM2 = 50;
    public static final int KEY_NUM3 = 51;
    public static final int KEY_NUM4 = 52;
    public static final int KEY_NUM5 = 53;
    public static final int KEY_NUM6 = 54;
    public static final int KEY_NUM7 = 55;
    public static final int KEY_NUM8 = 56;
    public static final int KEY_NUM9 = 57;
    public static final int KEY_STAR = 42;
    public static final int KEY_POUND = 35;

    public int getGameAction(int keyCode) {
        return switch (keyCode) {
            case KEY_NUM2, -1 -> UP;
            case KEY_NUM8, -2 -> DOWN;
            case KEY_NUM4, -3 -> LEFT;
            case KEY_NUM6, -4 -> RIGHT;
            case KEY_NUM5, -5 -> FIRE;
            case KEY_NUM1, -6 -> GAME_A;
            case KEY_NUM3, -7 -> GAME_B;
            default -> 0;
        };
    }

    protected void setFullScreenMode(boolean mode) {}

    protected void keyPressed(int keyCode) {}

    protected void keyReleased(int keyCode) {}

    protected void keyRepeated(int keyCode) {}

    protected void showNotify() {}

    protected void hideNotify() {}

    protected abstract void paint(Graphics g);

    public final void serviceRepaints() {}

    public void dispatchKeyPressed(int keyCode) {
        keyPressed(keyCode);
    }

    public void dispatchKeyReleased(int keyCode) {
        keyReleased(keyCode);
    }

    public void dispatchPaint(Graphics g) {
        paint(g);
    }
}
