package io.github.zh667.bouncetales.logic;

/**
 * Shared game-logic identity. Media parsers and a ball preview live here; hosts stay in runtimes.
 */
public final class GameLogic {
    public static final String MODULE_NAME = "game-logic";

    private GameLogic() {
    }

    public static String describe() {
        return MODULE_NAME + "/chapter";
    }
}
