package io.github.zh667.bouncetales.logic;

/**
 * Shared game-logic identity. Gameplay systems land here later; hosts stay in runtimes.
 */
public final class GameLogic {
    public static final String MODULE_NAME = "game-logic";

    private GameLogic() {
    }

    public static String describe() {
        return MODULE_NAME + "/skeleton";
    }
}
