package io.github.alreadybetter.wordinflexer.core.grammar;

/**
 * Categories of grammatical values understood by the initial core API.
 * A feature set can contain at most one distinct value per category.
 * Declaration order does not define the order of word transformations.
 */
public enum FeatureCategory {
    /** Grammatical number of the inflected word, not the number of possessors. */
    NUMBER,
    /** Grammatical case of the inflected word. */
    CASE,
    /** Possessive marking, including its explicit absence. */
    POSSESSION
}
