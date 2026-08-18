package io.github.zh667.bouncetales.pc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;

/**
 * Plays MIDI bytes from a user JAR via Java Sound. Original host code, not an MMAPI copy.
 */
final class MidiPlayer implements AutoCloseable {
    enum Status {
        IDLE,
        READY,
        PLAYING,
        UNAVAILABLE,
        FAILED
    }

    private Sequencer sequencer;
    private Status status = Status.IDLE;
    private String detail = "";

    synchronized void load(byte[] midiBytes) {
        closeQuietly();
        if (midiBytes == null || midiBytes.length == 0) {
            status = Status.IDLE;
            detail = "";
            return;
        }
        try {
            Sequencer next = MidiSystem.getSequencer();
            if (next == null) {
                status = Status.UNAVAILABLE;
                detail = "no sequencer";
                return;
            }
            next.open();
            next.setSequence(new ByteArrayInputStream(midiBytes));
            next.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            sequencer = next;
            status = Status.READY;
            detail = "";
        } catch (MidiUnavailableException ex) {
            status = Status.UNAVAILABLE;
            detail = safeMessage(ex);
        } catch (InvalidMidiDataException | IOException ex) {
            status = Status.FAILED;
            detail = safeMessage(ex);
        }
    }

    synchronized void toggle() {
        if (sequencer == null) {
            return;
        }
        if (status == Status.PLAYING) {
            sequencer.stop();
            status = Status.READY;
            return;
        }
        if (status == Status.READY) {
            sequencer.start();
            status = Status.PLAYING;
        }
    }

    synchronized void stop() {
        if (sequencer == null) {
            return;
        }
        sequencer.stop();
        sequencer.setTickPosition(0);
        if (status == Status.PLAYING) {
            status = Status.READY;
        }
    }

    synchronized void poll() {
        if (status == Status.PLAYING && sequencer != null && !sequencer.isRunning()) {
            status = Status.READY;
        }
    }

    synchronized Status status() {
        return status;
    }

    synchronized String detail() {
        return detail;
    }

    @Override
    public synchronized void close() {
        closeQuietly();
        status = Status.IDLE;
        detail = "";
    }

    private void closeQuietly() {
        if (sequencer == null) {
            return;
        }
        try {
            sequencer.stop();
        } catch (RuntimeException ignored) {
            // closing anyway
        }
        try {
            sequencer.close();
        } catch (RuntimeException ignored) {
            // closing anyway
        }
        sequencer = null;
    }

    private static String safeMessage(Exception ex) {
        String message = ex.getMessage();
        return message == null ? ex.getClass().getSimpleName() : message;
    }
}
