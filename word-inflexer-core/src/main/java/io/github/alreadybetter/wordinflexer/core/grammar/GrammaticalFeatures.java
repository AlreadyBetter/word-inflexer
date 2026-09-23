package io.github.alreadybetter.wordinflexer.core.grammar;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;

/**
 * An immutable, category-indexed set of grammatical features.
 *
 * <p>At most one distinct value is allowed per {@link FeatureCategory}.
 * Equal duplicates are accepted. Input order has no effect on equality and
 * does not prescribe the order in which a language module transforms a word.
 * An absent category is unspecified; it does not imply a default value.
 *
 * <p>This class validates category conflicts, not linguistic compatibility.
 * Feature implementations must satisfy the immutability contract of
 * {@link GrammaticalFeature}; individual feature objects are not cloned.
 *
 * <p>With a static import for {@link GrammaticalNumber#PLURAL}:
 * <pre>{@code
 * GrammaticalFeatures target = GrammaticalFeatures.of(PLURAL);
 * target.equals(GrammaticalFeatures.of(PLURAL, PLURAL)); // true
 * GrammaticalFeatures.of(PLURAL, PLURAL); // one value
 * GrammaticalFeatures.of(); // empty set
 * }</pre>
 */
public final class GrammaticalFeatures {

    private final Map<FeatureCategory, GrammaticalFeature> features;

    private GrammaticalFeatures(Map<FeatureCategory, GrammaticalFeature> features) {
        this.features = unmodifiableMap(new EnumMap<>(features));
    }

    /**
     * Creates a feature set, rejecting conflicting values in the same category.
     * Subsequent changes to the supplied array do not affect the returned set.
     *
     * @param features features in any order; an empty array is allowed
     * @return a new immutable feature set
     * @throws NullPointerException     if the array, any feature or its category is null
     * @throws IllegalArgumentException if unequal features share a category,
     *                                  such as singular and plural number
     */
    public static GrammaticalFeatures of(GrammaticalFeature... features) {
        requireNonNull(features, "Features must not be null");

        Map<FeatureCategory, GrammaticalFeature> byCategory = new EnumMap<>(FeatureCategory.class);

        for (GrammaticalFeature feature : features)
            addFeature(byCategory, feature);

        return new GrammaticalFeatures(byCategory);
    }

    /**
     * Looks up the explicitly supplied value for a category.
     *
     * @param category the category to look up
     * @return its value, or an empty optional if the category is unspecified
     * @throws NullPointerException if the category is null
     */
    public Optional<GrammaticalFeature> get(FeatureCategory category) {
        requireNonNull(category, "Category must not be null");
        return Optional.ofNullable(features.get(category));
    }

    /**
     * Checks whether an equal value is stored in the feature's category.
     *
     * @param feature the feature to look up
     * @return true if the stored value equals the supplied feature
     * @throws NullPointerException if the feature is null
     */
    public boolean contains(GrammaticalFeature feature) {
        requireNonNull(feature, "Feature must not be null");
        return feature.equals(features.get(feature.category()));
    }

    /**
     * Returns an unmodifiable view containing one value per specified category.
     * Iteration order must not be used as a word-transformation plan.
     *
     * @return the stored features; empty if no categories were specified
     */
    public Collection<GrammaticalFeature> values() {
        return features.values();
    }

    private static void addFeature(Map<FeatureCategory, GrammaticalFeature> features, GrammaticalFeature feature) {
        requireNonNull(feature, "Feature must not be null");

        FeatureCategory category = requireNonNull(feature.category(), "Category must not be null");

        GrammaticalFeature existing = features.putIfAbsent(category, feature);
        if (existing != null && !existing.equals(feature))
            throw new IllegalArgumentException("Conflicting features for " + category + ": " + existing + " and " + feature);
    }

    /**
     * Compares category-to-value mappings independently of input order.
     *
     * @param other the object to compare with, possibly null
     * @return true if the other object is a feature set with the same mapping
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;

        if (!(other instanceof GrammaticalFeatures)) return false;

        GrammaticalFeatures that = (GrammaticalFeatures) other;
        return features.equals(that.features);
    }

    /**
     * Returns a hash consistent with order-independent feature-set equality.
     *
     * @return the hash of the category-to-value mapping
     */
    @Override
    public int hashCode() {
        return features.hashCode();
    }

    /**
     * Returns a diagnostic representation of the stored values.
     * The output is not a serialization format or a transformation plan.
     *
     * @return a human-readable representation of the features
     */
    @Override
    public String toString() {
        return features.values().toString();
    }
}