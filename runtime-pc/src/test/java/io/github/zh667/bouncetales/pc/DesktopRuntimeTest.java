package io.github.zh667.bouncetales.pc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zh667.bouncetales.logic.AssetInventory;
import io.github.zh667.bouncetales.logic.GameAction;
import io.github.zh667.bouncetales.logic.HostTarget;
import java.awt.event.KeyEvent;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

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
    void startHeadlessReportsEmptyAssets(@TempDir Path temp) throws Exception {
        Files.writeString(temp.resolve("README.md"), "keep", StandardCharsets.UTF_8);
        AssetInventory inventory = DesktopRuntime.start(true, temp);
        assertEquals(AssetInventory.Status.EMPTY, inventory.status());
        assertFalse(inventory.ready());
    }

    @Test
    void discoverHonorsSystemProperty(@TempDir Path temp) {
        String previous = System.getProperty("bounce.assets.dir");
        System.setProperty("bounce.assets.dir", temp.toString());
        try {
            assertEquals(temp.toAbsolutePath().normalize(), AssetPaths.discover());
        } finally {
            if (previous == null) {
                System.clearProperty("bounce.assets.dir");
            } else {
                System.setProperty("bounce.assets.dir", previous);
            }
        }
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
        assertTrue(zh.assetsStatus(AssetInventory.empty(Path.of("assets"))).contains("Bounce Tales"));
        assertEquals("资源", zh.assetsHeading());
    }

    @Test
    void englishBundleExists() {
        UiText en = new UiText(Locale.ENGLISH);
        assertEquals("Up (jump)", en.label(GameAction.UP));
        assertEquals("Star (change bounce form)", en.label(GameAction.STAR));
        assertEquals("Back", en.label(GameAction.BACK));
    }
}
