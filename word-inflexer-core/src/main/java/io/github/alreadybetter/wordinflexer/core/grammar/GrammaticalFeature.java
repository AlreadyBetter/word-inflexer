package io.github.alreadybetter.wordinflexer.core.grammar;

/**
 * A grammatical value belonging to one category, such as plural number or dative case.
 *
 * <p>Implementations must be immutable and expose a stable, non-null category.
 * Equality and hash codes must remain stable while a feature belongs to a
 * {@link GrammaticalFeatures} instance. Enum implementations satisfy these requirements.
 * Language modules decide which values and combinations they support.
 */
public interface GrammaticalFeature {

    /**
     * Returns the category used to detect conflicting values in a feature set.
     *
     * @return the non-null category of this feature
     */
    FeatureCategory category();
}
