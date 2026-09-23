package io.github.alreadybetter.wordinflexer.core;

import io.github.alreadybetter.wordinflexer.core.grammar.GrammaticalFeatures;

import static java.lang.Character.isSpaceChar;
import static java.lang.Character.isWhitespace;
import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

/**
 * An immutable request to produce a word form with the specified target features.
 *
 * <p>Source features describe what is known about the supplied word. An absent
 * source category is unknown, not an instruction to assume a default value.
 * Target features describe the desired result. An absent target category means
 * to preserve the corresponding source characteristic, once it is determined.
 * An empty target set requests no grammatical changes.
 *
 * <p>Different source and target values in the same category are allowed: they
 * express a requested change. This object does not merge feature sets, analyze
 * the word, select a language or validate linguistic compatibility. Those tasks
 * belong to the language processor.
 *
 * <p>The word is preserved exactly as supplied, without trimming, case folding
 * or Unicode normalization. Only null and blank input are rejected here;
 * language-specific word syntax is validated by the language processor.
 */
public final class InflectionRequest {

    private final String word;
    private final GrammaticalFeatures sourceFeatures;
    private final GrammaticalFeatures targetFeatures;

    /**
     * Creates a request with explicit source information and target features.
     *
     * @param word the input word, preserved exactly as supplied
     * @param sourceFeatures known source features; an empty set is allowed
     * @param targetFeatures desired features; an empty set requests no changes
     * @throws NullPointerException if any argument is null
     * @throws IllegalArgumentException if the word is empty or consists entirely
     *                                  of Unicode whitespace or space characters
     */
    public InflectionRequest(String word, GrammaticalFeatures sourceFeatures, GrammaticalFeatures targetFeatures) {
        this.word = validateWord(word);
        this.sourceFeatures = requireNonNull(sourceFeatures, "Source features must not be null");
        this.targetFeatures = requireNonNull(targetFeatures, "Target features must not be null");
    }

    /**
     * Creates a request without supplied source information.
     * The source feature set is empty; source characteristics remain unknown.
     *
     * @param word the input word, preserved exactly as supplied
     * @param targetFeatures desired features; an empty set requests no changes
     * @throws NullPointerException if either argument is null
     * @throws IllegalArgumentException if the word is empty or consists entirely
     *                                  of Unicode whitespace or space characters
     */
    public InflectionRequest(String word, GrammaticalFeatures targetFeatures) {
        this(word, GrammaticalFeatures.of(), targetFeatures);
    }

    /**
     * Returns the original word without normalization.
     *
     * @return the non-null, non-blank input word
     */
    public String word() {
        return word;
    }

    /**
     * Returns the known characteristics of the input form.
     *
     * @return the immutable source feature set, possibly empty
     */
    public GrammaticalFeatures sourceFeatures() {
        return sourceFeatures;
    }

    /**
     * Returns the requested characteristics of the resulting form.
     * Unspecified categories are to be preserved, not reset to defaults.
     *
     * @return the immutable target feature set, possibly empty
     */
    public GrammaticalFeatures targetFeatures() {
        return targetFeatures;
    }

    private static String validateWord(String word) {
        requireNonNull(word, "Word must not be null");
        if (word.codePoints().allMatch(codePoint -> isWhitespace(codePoint) || isSpaceChar(codePoint)))
            throw new IllegalArgumentException("Word must not be blank");

        return word;
    }

    /**
     * Compares the exact input word and both feature sets.
     *
     * @param other the object to compare with, possibly null
     * @return true if all three request components are equal
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof InflectionRequest)) return false;

        InflectionRequest that = (InflectionRequest) other;
        return word.equals(that.word)
                && sourceFeatures.equals(that.sourceFeatures)
                && targetFeatures.equals(that.targetFeatures);
    }

    /**
     * Returns a hash consistent with request equality.
     *
     * @return a hash of the word and both feature sets
     */
    @Override
    public int hashCode() {
        return hash(word, sourceFeatures, targetFeatures);
    }

    /**
     * Returns a diagnostic representation, not a serialization format.
     *
     * @return a human-readable representation of the request
     */
    @Override
    public String toString() {
        return "InflectionRequest{word='" + word + "', sourceFeatures=" + sourceFeatures
                + ", targetFeatures=" + targetFeatures + "}";
    }
}
