package io.github.zh667.bouncetales.logic;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Loads a campaign chapter from a user JAR via packed index path or a bare entry name.
 */
public final class ChapterLoader {
    private ChapterLoader() {
    }

    public static Optional<RlefLevel> load(Path jar, PackedIndex index, ChapterId chapter) {
        if (jar == null || chapter == null) {
            return Optional.empty();
        }
        PackedIndex packed = index == null ? PackedIndex.empty() : index;
        Optional<PackedIndex.FileRef> ref = packed.findPath(chapter.jarEntry());
        byte[] bytes = ref.flatMap(
                        file -> AssetLocator.readSlice(jar, file.path(), file.skipOffset(), file.readLength()))
                .orElse(null);
        if (bytes == null) {
            bytes = AssetLocator.readEntry(jar, chapter.jarEntry()).orElse(null);
        }
        if (bytes == null || bytes.length == 0) {
            return Optional.empty();
        }
        RlefLevel level = RlefLevel.tryParse(bytes);
        return level.playable() ? Optional.of(level) : Optional.empty();
    }
}
