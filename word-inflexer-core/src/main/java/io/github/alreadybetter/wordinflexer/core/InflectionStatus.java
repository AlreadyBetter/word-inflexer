package io.github.alreadybetter.wordinflexer.core;

/**
 * Language-independent outcomes of word inflection.
 * Invalid API arguments are rejected with exceptions before processing;
 * these statuses describe outcomes produced by a language processor.
 */
public enum InflectionStatus {
    /** One output form is established after all applicable checks. */
    SUCCESS,
    /** Multiple distinct output forms remain possible under different analyses. */
    AMBIGUOUS,
    /** Required information is missing, so an output cannot be established. */
    INSUFFICIENT_INFORMATION,
    /** The word's spelling or morphological construction is not supported. */
    UNSUPPORTED_WORD,
    /** A requested feature or combination is not supported by the language processor. */
    UNSUPPORTED_FEATURES,
    /** Supplied source features contradict the input word or each other. */
    INVALID_METADATA,
    /** Supported, consistent input encounters missing or contradictory processing rules. */
    RULE_CONFLICT
}
