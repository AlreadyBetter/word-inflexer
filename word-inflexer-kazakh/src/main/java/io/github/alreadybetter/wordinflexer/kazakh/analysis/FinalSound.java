package io.github.alreadybetter.wordinflexer.kazakh.analysis;

/**
 * Final-sound groups used by Kazakh suffix rules.
 * A group is not itself a suffix-selection rule: different cases combine groups differently.
 */
public enum FinalSound {
    /** An unambiguous vowel letter. */
    VOWEL,
    /** Nasal sonorants: м, н, ң. */
    NASAL,
    /** Other supported sonorants: л, р, й. Context-sensitive у is not inferred yet. */
    OTHER_SONORANT,
    /** Voiced consonants ж and з. */
    VOICED_ZH_Z,
    /** Voiceless consonants. */
    VOICELESS,
    /** Word-final б, в, г, д, handled separately by suffix rules. */
    FINAL_B_V_G_D,
    /** No supported final-sound interpretation is available. */
    UNKNOWN
}
