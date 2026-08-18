package javax.microedition.media;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.media.control.VolumeControl;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;

public final class Manager {
    private static final Object MIDI = new Object();
    private static Sequencer sharedSequencer;
    private static Synthesizer sharedSynth;

    private Manager() {}

    public static Player createPlayer(InputStream stream, String type) throws IOException, MediaException {
        byte[] data = stream.readAllBytes();
        return new MidiPlayer(data);
    }

    private static Sequencer sequencer() throws MidiUnavailableException {
        synchronized (MIDI) {
            if (sharedSequencer == null) {
                Sequencer sequencer = MidiSystem.getSequencer(false);
                if (sequencer == null) {
                    throw new MidiUnavailableException("no sequencer");
                }
                Synthesizer synth = MidiSystem.getSynthesizer();
                synth.open();
                sequencer.open();
                sequencer.getTransmitter().setReceiver(synth.getReceiver());
                sharedSequencer = sequencer;
                sharedSynth = synth;
            }
            return sharedSequencer;
        }
    }

    private static final class MidiPlayer implements Player, VolumeControl {
        private final byte[] data;
        private Sequencer sequencer;
        private int state = UNREALIZED;
        private int loopCount = 1;
        private int volume = 100;

        MidiPlayer(byte[] data) {
            this.data = data;
        }

        @Override
        public void prefetch() throws MediaException {
            try {
                synchronized (MIDI) {
                    sequencer = sequencer();
                    sequencer.stop();
                    sequencer.setSequence(new ByteArrayInputStream(data));
                    applyLoop();
                    state = PREFETCHED;
                }
            } catch (MidiUnavailableException | InvalidMidiDataException | IOException ex) {
                throw new MediaException(ex.getMessage());
            }
        }

        @Override
        public void start() throws MediaException {
            if (state < PREFETCHED) {
                prefetch();
            }
            sequencer.start();
            state = STARTED;
        }

        @Override
        public void stop() {
            if (sequencer != null && sequencer.isOpen()) {
                sequencer.stop();
            }
            if (state == STARTED) {
                state = PREFETCHED;
            }
        }

        @Override
        public void deallocate() {
            stop();
        }

        @Override
        public void close() {
            stop();
            sequencer = null;
            state = CLOSED;
        }

        @Override
        public int getState() {
            return state;
        }

        @Override
        public void setLoopCount(int count) {
            loopCount = count;
            applyLoop();
        }

        @Override
        public Control getControl(String controlType) {
            if (controlType != null
                    && (controlType.equals("VolumeControl")
                            || controlType.endsWith("VolumeControl"))) {
                return this;
            }
            return null;
        }

        @Override
        public int setLevel(int level) {
            volume = Math.max(0, Math.min(100, level));
            return volume;
        }

        @Override
        public int getLevel() {
            return volume;
        }

        @Override
        public void setMute(boolean mute) {}

        @Override
        public boolean isMuted() {
            return false;
        }

        private void applyLoop() {
            if (sequencer == null) {
                return;
            }
            if (loopCount == -1) {
                sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            } else {
                sequencer.setLoopCount(Math.max(0, loopCount - 1));
            }
        }
    }
}
