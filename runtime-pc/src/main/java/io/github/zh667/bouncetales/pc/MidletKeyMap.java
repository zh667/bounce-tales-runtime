package io.github.zh667.bouncetales.pc;

import com.nokia.mid.ui.FullCanvas;
import java.awt.event.KeyEvent;
import javax.microedition.lcdui.Canvas;

/** Maps AWT keys onto Nokia FullCanvas / MIDP game keys. */
public final class MidletKeyMap {
    private MidletKeyMap() {}

    public static int keyCode(int awtKeyCode) {
        return switch (awtKeyCode) {
            case KeyEvent.VK_UP, KeyEvent.VK_W -> FullCanvas.KEY_UP_ARROW;
            case KeyEvent.VK_DOWN, KeyEvent.VK_S -> FullCanvas.KEY_DOWN_ARROW;
            case KeyEvent.VK_LEFT, KeyEvent.VK_A -> FullCanvas.KEY_LEFT_ARROW;
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> FullCanvas.KEY_RIGHT_ARROW;
            case KeyEvent.VK_ENTER -> FullCanvas.KEY_SOFTKEY3;
            case KeyEvent.VK_BACK_SPACE, KeyEvent.VK_ESCAPE -> FullCanvas.KEY_SOFTKEY2;
            case KeyEvent.VK_Q -> Canvas.KEY_STAR;
            case KeyEvent.VK_E -> Canvas.KEY_POUND;
            default -> 0;
        };
    }
}
