package io.github.alreadybetter.wordinflexer.core;

/**
 * Processes word-inflection requests using a language-specific implementation.
 *
 * <p>The caller selects the language by choosing an implementation. The core
 * does not detect languages, define a closed language list or prescribe an
 * affix order. Implementations analyze input forms, validate supported features
 * and determine the transformations needed to produce the requested form.
 *
 * <p>Implementations must respect the distinction between unknown source
 * features and unchanged target categories described by {@link InflectionRequest}.
 * Feature iteration order must not determine transformation order.
 * Implementations must document their supported input forms and thread safety.
 */
@FunctionalInterface
public interface WordInflector {

    /**
     * Produces a word form or explains why a form could not be established.
     *
     * <p>Both source and target features must be checked for language support.
     * Sharing a category with a supported feature does not make a foreign or
     * custom feature supported. Such values must not be silently ignored.
     * Supplied source information must also be checked against the input form.
     *
     * <p>A successful result requires all applicable checks to be complete.
     * Missing information must not be replaced with an arbitrary assumption,
     * and multiple possible output forms must not be reduced to an arbitrary
     * first choice. Diagnostics should describe the problem and, when known,
     * the information needed to resolve it.
     *
     * <p>An empty target set requests no changes. After validating support and
     * supplied source information, return the original word unchanged; do not
     * require analysis of unknown features that cannot affect this outcome.
     *
     * <p>Expected linguistic outcomes are represented by {@link InflectionStatus}:
     * ambiguity, insufficient information, unsupported words or features,
     * contradictory source metadata, and detected rule conflicts. Implementations
     * must not catch arbitrary programming exceptions and relabel them as
     * unsupported words or rule conflicts.
     *
     * @param request a non-null request containing the input word and feature sets
     * @return a non-null result with a diagnostic for every non-success outcome
     * @throws NullPointerException if the request is null
     */
    InflectionResult inflect(InflectionRequest request);
}
