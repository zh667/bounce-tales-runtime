package io.github.zh667.bouncetales.pc;

import io.github.zh667.bouncetales.logic.SaveStore;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import javax.microedition.midlet.AppProperties;
import javax.microedition.midlet.DisplayHost;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletBridge;
import javax.swing.SwingUtilities;

/**
 * Loads a user-supplied Bounce Tales JAR and starts its MIDlet. Original game classes stay in the JAR.
 */
public final class MidletHost {
    private MidletHost() {}

    public static void installPlatform(MidletManifest manifest) {
        System.setProperty("microedition.platform", "NokiaN73");
        System.setProperty("microedition.configuration", "CLDC-1.1");
        System.setProperty("microedition.profiles", "MIDP-2.0");
        System.setProperty("microedition.encoding", "ISO-8859-1");
        Locale locale = Locale.getDefault();
        String tag = "en".equalsIgnoreCase(locale.getLanguage()) ? "en" : "zh-CN";
        System.setProperty("microedition.locale", tag);
        AppProperties.clear();
        for (Map.Entry<String, String> entry : manifest.properties().entrySet()) {
            AppProperties.put(entry.getKey(), entry.getValue());
        }
        AppProperties.put("microedition.locale", tag);
        AppProperties.put("Nokia-Platform", manifest.nokiaPlatform());
    }

    public static void launch(Path jar, SaveStore saves) throws Exception {
        MidletManifest manifest = MidletManifest.read(jar)
                .orElseThrow(() -> new IllegalStateException("JAR has no MIDlet-1"));
        installPlatform(manifest);
        System.setProperty("bounce.save.dir", saves.directory().toString());
        URLClassLoader loader = new URLClassLoader(new URL[] {jar.toUri().toURL()}, MidletHost.class.getClassLoader());
        Thread.currentThread().setContextClassLoader(loader);
        Class<?> type = Class.forName(manifest.midletClass(), true, loader);
        Object instance = type.getDeclaredConstructor().newInstance();
        if (!(instance instanceof MIDlet midlet)) {
            throw new IllegalStateException(manifest.midletClass() + " is not a MIDlet");
        }
        MidletWindow window = new MidletWindow(manifest.midletName() + " — " + manifest.version());
        DisplayHost.install(window, window::dispose);
        SwingUtilities.invokeAndWait(window::show);
        Thread.currentThread().setContextClassLoader(loader);
        Thread midletThread = new Thread(
                () -> {
                    Thread.currentThread().setContextClassLoader(loader);
                    try {
                        MIDletBridge.start(midlet);
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                    }
                },
                "bounce-tales-midlet");
        midletThread.setDaemon(false);
        midletThread.start();
        System.out.println(manifest.toLogLine());
    }
}
