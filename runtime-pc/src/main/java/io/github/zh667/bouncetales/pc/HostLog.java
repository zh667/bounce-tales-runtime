package io.github.zh667.bouncetales.pc;

import java.lang.management.ManagementFactory;
import java.util.concurrent.atomic.AtomicInteger;

/** Low-rate host stats so ``:runtime-pc:run`` shows whether paint is racing or leaking threads. */
final class HostLog {
    private static final boolean ENABLED =
            !"false".equalsIgnoreCase(System.getProperty("bounce.debug.host", "true"));
    private static final AtomicInteger flushes = new AtomicInteger();
    private static final AtomicInteger paints = new AtomicInteger();
    private static final Object lock = new Object();
    private static volatile long windowStartNs;
    private static volatile long lastReportNs;

    private HostLog() {}

    static void windowOpened(int logicalWidth, int logicalHeight, int scale) {
        if (!ENABLED) {
            return;
        }
        windowStartNs = System.nanoTime();
        lastReportNs = windowStartNs;
        System.out.println(
                "host: window "
                        + (logicalWidth * scale)
                        + "x"
                        + (logicalHeight * scale)
                        + " ("
                        + logicalWidth
                        + "x"
                        + logicalHeight
                        + " @ "
                        + scale
                        + "x). Gradle stays on :runtime-pc:run until you close the window.");
    }

    static void flush() {
        flushes.incrementAndGet();
        maybeReport();
    }

    static void paint() {
        paints.incrementAndGet();
    }

    private static void maybeReport() {
        if (!ENABLED) {
            return;
        }
        long now = System.nanoTime();
        if (now - lastReportNs < 5_000_000_000L) {
            return;
        }
        synchronized (lock) {
            if (now - lastReportNs < 5_000_000_000L) {
                return;
            }
            double seconds = (now - lastReportNs) / 1_000_000_000.0;
            int flushCount = flushes.getAndSet(0);
            int paintCount = paints.getAndSet(0);
            lastReportNs = now;
            int threads = ManagementFactory.getThreadMXBean().getThreadCount();
            System.out.printf(
                    "host: flush/s=%.0f paint/s=%.0f threads=%d uptime=%.0fs%n",
                    flushCount / seconds,
                    paintCount / seconds,
                    threads,
                    (now - windowStartNs) / 1_000_000_000.0);
        }
    }
}
