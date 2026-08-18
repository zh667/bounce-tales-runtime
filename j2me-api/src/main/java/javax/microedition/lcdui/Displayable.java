package javax.microedition.lcdui;

public class Displayable {
    private CommandListener listener;

    public int getWidth() {
        return Display.screenWidth();
    }

    public int getHeight() {
        return Display.screenHeight();
    }

    public void addCommand(Command cmd) {}

    public void removeCommand(Command cmd) {}

    public void setCommandListener(CommandListener l) {
        this.listener = l;
    }

    public void setTitle(String s) {}

    public void repaint() {
        Display.getDisplay(null).flush();
    }

    CommandListener commandListener() {
        return listener;
    }
}
