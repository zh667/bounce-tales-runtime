package io.github.zh667.bouncetales.pc;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import org.junit.jupiter.api.Test;

class MidiPlayerTest {
    @Test
    void loadsSyntheticMidiOrReportsUnavailable() throws Exception {
        byte[] midi = oneNote();
        MidiPlayer player = new MidiPlayer();
        player.load(midi);
        MidiPlayer.Status status = player.status();
        assertTrue(
                status == MidiPlayer.Status.READY
                        || status == MidiPlayer.Status.UNAVAILABLE
                        || status == MidiPlayer.Status.FAILED,
                "unexpected status " + status + " " + player.detail());
        assertNotEquals(MidiPlayer.Status.PLAYING, status);
        if (status == MidiPlayer.Status.READY) {
            player.toggle();
            assertTrue(player.status() == MidiPlayer.Status.PLAYING || player.status() == MidiPlayer.Status.READY);
            player.stop();
        }
        player.close();
        assertTrue(player.status() == MidiPlayer.Status.IDLE);
    }

    private static byte[] oneNote() throws InvalidMidiDataException, IOException {
        Sequence sequence = new Sequence(Sequence.PPQ, 24);
        Track track = sequence.createTrack();
        ShortMessage on = new ShortMessage();
        on.setMessage(ShortMessage.NOTE_ON, 0, 60, 80);
        track.add(new MidiEvent(on, 0));
        ShortMessage off = new ShortMessage();
        off.setMessage(ShortMessage.NOTE_OFF, 0, 60, 0);
        track.add(new MidiEvent(off, 24));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MidiSystem.write(sequence, 0, out);
        return out.toByteArray();
    }
}
