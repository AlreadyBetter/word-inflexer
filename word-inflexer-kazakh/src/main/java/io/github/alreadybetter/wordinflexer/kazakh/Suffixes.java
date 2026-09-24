package io.github.alreadybetter.wordinflexer.kazakh;

import io.github.alreadybetter.wordinflexer.kazakh.analysis.VowelHarmony;

/** Selects a harmonic variant only when the preceding validation established harmony. */
final class Suffixes {

    private Suffixes() {
    }

    static String harmonic(WordForm form, String back, String front) {
        if (form.harmony() == VowelHarmony.BACK) return back;
        if (form.harmony() == VowelHarmony.FRONT) return front;
        throw new IllegalStateException("Harmonic suffix selection requires known vowel harmony");
    }
}
