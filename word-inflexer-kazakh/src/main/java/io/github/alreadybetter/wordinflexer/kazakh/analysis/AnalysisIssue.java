package io.github.alreadybetter.wordinflexer.kazakh.analysis;

/** Reasons why the initial orthographic analyzer cannot establish a characteristic. */
public enum AnalysisIssue {
    /** Input contains something other than a Kazakh Cyrillic alphabet letter. */
    UNSUPPORTED_CHARACTERS,
    /** Internal capitals or all-capital spelling may indicate an abbreviation. */
    POSSIBLE_ABBREVIATION,
    /** The spelling includes и, у or letters needing loanword-specific handling. */
    CONTEXT_DEPENDENT_SPELLING,
    /** No unambiguous harmony-bearing vowel was found. */
    NO_HARMONY_VOWEL,
    /** A final harmony-bearing ә outside the first vowel position needs special handling. */
    NONINITIAL_AE,
    /** The last letter is not covered by the current final-sound rules. */
    UNSUPPORTED_FINAL_SOUND
}
