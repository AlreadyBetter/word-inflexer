package io.github.alreadybetter.wordinflexer.core.grammar;

import static io.github.alreadybetter.wordinflexer.core.grammar.FeatureCategory.NUMBER;

/**
 * Initial grammatical number values for an inflected word.
 * These values do not describe the number of possessors.
 * The selected language module determines whether a value is supported.
 */
public enum GrammaticalNumber implements GrammaticalFeature {
    /** Singular number. */
    SINGULAR,
    /** Plural number. */
    PLURAL;

    /** {@inheritDoc} */
    @Override
    public FeatureCategory category() {
        return NUMBER;
    }
}