package javax.microedition.midlet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MIDletBridgeTest {
    @Test
    void startCallsStartAppWithoutSwing() throws Exception {
        ProbeMidlet midlet = new ProbeMidlet();
        MIDletBridge.start(midlet);
        assertTrue(midlet.started);
    }

    @Test
    void getAppPropertyReadsInstalledManifestKeys() {
        AppProperties.clear();
        AppProperties.put("MIDlet-Name", "Probe");
        ProbeMidlet midlet = new ProbeMidlet();
        assertEquals("Probe", midlet.getAppProperty("MIDlet-Name"));
        AppProperties.clear();
    }

    @Test
    void playerGetControlUsesMediaControlType() throws Exception {
        assertEquals(
                "javax.microedition.media.Control",
                javax.microedition.media.Player.class
                        .getMethod("getControl", String.class)
                        .getReturnType()
                        .getName());
    }
}
