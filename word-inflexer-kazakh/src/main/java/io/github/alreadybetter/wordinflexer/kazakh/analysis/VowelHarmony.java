package io.github.alreadybetter.wordinflexer.kazakh.analysis;

/** Vowel series used when choosing a harmonic suffix variant. */
public enum VowelHarmony {
    /** Back-vowel series (zhuan). */
    BACK,
    /** Front-vowel series (zhinishke). */
    FRONT,
    /** The implemented spelling rules cannot establish the series. */
    UNKNOWN
}
