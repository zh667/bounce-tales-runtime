package io.github.zh667.bouncetales.pc;

import io.github.zh667.bouncetales.logic.GameAction;
import java.awt.event.KeyEvent;
import java.util.Optional;

/**
 * Hangar-style PC keymap. Arrows and WASD share movement; Back is Backspace, not 2.
 */
public final class KeyMap {
    private KeyMap() {
    }

    public static Optional<GameAction> actionFor(int keyCode) {
        return switch (keyCode) {
            case KeyEvent.VK_UP, KeyEvent.VK_W -> Optional.of(GameAction.UP);
            case KeyEvent.VK_DOWN, KeyEvent.VK_S -> Optional.of(GameAction.DOWN);
            case KeyEvent.VK_LEFT, KeyEvent.VK_A -> Optional.of(GameAction.LEFT);
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> Optional.of(GameAction.RIGHT);
            case KeyEvent.VK_ENTER -> Optional.of(GameAction.FIRE);
            case KeyEvent.VK_BACK_SPACE -> Optional.of(GameAction.BACK);
            case KeyEvent.VK_Q -> Optional.of(GameAction.STAR);
            default -> Optional.empty();
        };
    }
}
