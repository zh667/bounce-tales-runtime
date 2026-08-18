package io.github.zh667.bouncetales.pc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zh667.bouncetales.logic.GameAction;
import io.github.zh667.bouncetales.logic.HostTarget;
import java.awt.event.KeyEvent;
import java.util.Locale;
import org.junit.jupiter.api.Test;

class DesktopRuntimeTest {
    @Test
    void targetsDesktop() {
        assertEquals(HostTarget.DESKTOP, DesktopRuntime.target());
    }

    @Test
    void bannerNamesSharedLogic() {
        assertTrue(DesktopRuntime.banner().contains("game-logic/skeleton"));
    }

    @Test
    void startHeadlessDoesNotOpenWindow() {
        DesktopRuntime.start(true);
    }
}

class KeyMapTest {
    @Test
    void arrowsAndWasdShareMovement() {
        assertEquals(GameAction.UP, KeyMap.actionFor(KeyEvent.VK_UP).orElseThrow());
        assertEquals(GameAction.UP, KeyMap.actionFor(KeyEvent.VK_W).orElseThrow());
        assertEquals(GameAction.DOWN, KeyMap.actionFor(KeyEvent.VK_DOWN).orElseThrow());
        assertEquals(GameAction.DOWN, KeyMap.actionFor(KeyEvent.VK_S).orElseThrow());
        assertEquals(GameAction.LEFT, KeyMap.actionFor(KeyEvent.VK_LEFT).orElseThrow());
        assertEquals(GameAction.LEFT, KeyMap.actionFor(KeyEvent.VK_A).orElseThrow());
        assertEquals(GameAction.RIGHT, KeyMap.actionFor(KeyEvent.VK_RIGHT).orElseThrow());
        assertEquals(GameAction.RIGHT, KeyMap.actionFor(KeyEvent.VK_D).orElseThrow());
    }

    @Test
    void confirmBackAndStar() {
        assertEquals(GameAction.FIRE, KeyMap.actionFor(KeyEvent.VK_ENTER).orElseThrow());
        assertEquals(GameAction.BACK, KeyMap.actionFor(KeyEvent.VK_BACK_SPACE).orElseThrow());
        assertEquals(GameAction.STAR, KeyMap.actionFor(KeyEvent.VK_Q).orElseThrow());
    }

    @Test
    void digitTwoIsNotBack() {
        assertTrue(KeyMap.actionFor(KeyEvent.VK_2).isEmpty());
    }
}

class UiTextTest {
    @Test
    void chineseLabelsCoverRequestedActions() {
        UiText zh = new UiText(Locale.SIMPLIFIED_CHINESE);
        assertEquals("蹦球传说运行时（非官方）", zh.title());
        assertEquals("上（跳跃）", zh.label(GameAction.UP));
        assertEquals("下", zh.label(GameAction.DOWN));
        assertEquals("左", zh.label(GameAction.LEFT));
        assertEquals("右", zh.label(GameAction.RIGHT));
        assertEquals("确认（跳跃）", zh.label(GameAction.FIRE));
        assertEquals("返回", zh.label(GameAction.BACK));
        assertEquals("星号（切换球形态）", zh.label(GameAction.STAR));
        assertTrue(zh.binding(GameAction.UP).contains("W"));
        assertTrue(zh.binding(GameAction.BACK).contains("Backspace"));
    }

    @Test
    void englishBundleExists() {
        UiText en = new UiText(Locale.ENGLISH);
        assertEquals("Up (jump)", en.label(GameAction.UP));
        assertEquals("Star (change bounce form)", en.label(GameAction.STAR));
        assertEquals("Back", en.label(GameAction.BACK));
    }
}
