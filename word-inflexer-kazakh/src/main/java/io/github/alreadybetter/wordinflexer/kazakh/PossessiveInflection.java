package io.github.alreadybetter.wordinflexer.kazakh;

import io.github.alreadybetter.wordinflexer.kazakh.analysis.FinalSound;
import io.github.alreadybetter.wordinflexer.kazakh.grammar.KazakhPossessive;

import static io.github.alreadybetter.wordinflexer.kazakh.Suffixes.harmonic;
import static io.github.alreadybetter.wordinflexer.kazakh.grammar.KazakhPossessive.NONE;

/**
 * Adds possessive marking after number marking.
 * The caller permits only vowel-final singular stems or newly formed plurals;
 * consonant-final singular stems may need voicing or vowel deletion and are deferred.
 */
final class PossessiveInflection {

    WordForm apply(WordForm form, KazakhPossessive possessive) {
        if (possessive == NONE) return form;
        boolean vowelFinal = form.finalSound() == FinalSound.VOWEL;
        return form.append(possessiveSuffix(form, possessive, vowelFinal));
    }

    private String possessiveSuffix(WordForm form, KazakhPossessive possessive, boolean vowelFinal) {
        switch (possessive) {
            case FIRST_SINGULAR:
                return vowelFinal ? "м" : harmonic(form, "ым", "ім");
            case FIRST_PLURAL:
                return vowelFinal ? harmonic(form, "мыз", "міз") : harmonic(form, "ымыз", "іміз");
            case SECOND_INFORMAL:
                return vowelFinal ? "ң" : harmonic(form, "ың", "ің");
            case SECOND_POLITE:
                return vowelFinal ? harmonic(form, "ңыз", "ңіз") : harmonic(form, "ыңыз", "іңіз");
            case THIRD_PERSON:
                return vowelFinal ? harmonic(form, "сы", "сі") : harmonic(form, "ы", "і");
            default:
                throw new IllegalStateException("Unexpected possessive transformation: " + possessive);
        }
    }
}
