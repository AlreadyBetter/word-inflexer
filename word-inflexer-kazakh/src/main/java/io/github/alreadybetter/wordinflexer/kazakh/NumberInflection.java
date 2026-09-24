package io.github.alreadybetter.wordinflexer.kazakh;

import io.github.alreadybetter.wordinflexer.core.grammar.GrammaticalNumber;
import io.github.alreadybetter.wordinflexer.kazakh.analysis.FinalSound;

import static io.github.alreadybetter.wordinflexer.core.grammar.GrammaticalNumber.SINGULAR;
import static io.github.alreadybetter.wordinflexer.kazakh.Suffixes.harmonic;

/** Adds plural marking to a declared singular source form. */
final class NumberInflection {

    WordForm apply(WordForm form, GrammaticalNumber number) {
        if (number == SINGULAR) return form;
        return form.append(pluralSuffix(form));
    }

    private String pluralSuffix(WordForm form) {
        if (form.endsInHardConsonant()) return harmonic(form, "тар", "тер");
        if (form.finalSound() == FinalSound.VOWEL || "рй".indexOf(form.lastLetter()) >= 0)
            return harmonic(form, "лар", "лер");

        return harmonic(form, "дар", "дер");
    }
}
