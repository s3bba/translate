package org.op65n.translate.model;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface Translator {

    @NotNull Optional<String> translate(final @NotNull Language language, final @NotNull String text);

}
