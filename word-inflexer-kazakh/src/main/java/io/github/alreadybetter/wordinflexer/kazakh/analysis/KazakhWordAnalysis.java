package io.github.alreadybetter.wordinflexer.kazakh.analysis;

import java.util.EnumSet;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;

/**
 * Immutable orthographic observations, not a complete morphological analysis.
 * No grammatical case, plurality or possession is inferred here.
 * Empty diagnostics do not prove that the input is a valid word or a regular noun.
 */
public final class KazakhWordAnalysis {

    private final String word;
    private final String normalizedWord;
    private final VowelHarmony harmony;
    private final FinalSound finalSound;
    private final Set<AnalysisIssue> issues;

    KazakhWordAnalysis(String word, String normalizedWord, VowelHarmony harmony,
                       FinalSound finalSound, Set<AnalysisIssue> issues) {
        this.word = word;
        this.normalizedWord = normalizedWord;
        this.harmony = harmony;
        this.finalSound = finalSound;
        EnumSet<AnalysisIssue> copy = EnumSet.noneOf(AnalysisIssue.class);
        copy.addAll(issues);
        this.issues = unmodifiableSet(copy);
    }

    /** @return the original input, unchanged */
    public String word() {
        return word;
    }

    /** @return an NFC-normalized, lowercase copy used only for analysis */
    public String normalizedWord() {
        return normalizedWord;
    }

    /** @return the regular-spelling harmony, or UNKNOWN when unresolved */
    public VowelHarmony harmony() {
        return harmony;
    }

    /** @return the final-sound group, which may be known even when harmony is unknown */
    public FinalSound finalSound() {
        return finalSound;
    }

    /**
     * Returns unresolved aspects of the analysis.
     * Consumers must determine which observations a particular rule requires;
     * missing harmony need not prevent a non-harmonic suffix from being selected.
     *
     * @return an immutable diagnostic set, possibly empty
     */
    public Set<AnalysisIssue> issues() {
        return issues;
    }
}
