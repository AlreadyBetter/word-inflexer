package io.github.alreadybetter.wordinflexer.kazakh;

import io.github.alreadybetter.wordinflexer.kazakh.analysis.FinalSound;
import io.github.alreadybetter.wordinflexer.kazakh.grammar.KazakhCase;
import io.github.alreadybetter.wordinflexer.kazakh.grammar.KazakhPossessive;

import static io.github.alreadybetter.wordinflexer.kazakh.Suffixes.harmonic;
import static io.github.alreadybetter.wordinflexer.kazakh.grammar.KazakhPossessive.FIRST_SINGULAR;
import static io.github.alreadybetter.wordinflexer.kazakh.grammar.KazakhPossessive.SECOND_INFORMAL;
import static io.github.alreadybetter.wordinflexer.kazakh.grammar.KazakhPossessive.THIRD_PERSON;

/** Adds case marking after number and possession, using the current final sound. */
final class CaseInflection {

    WordForm apply(WordForm form, KazakhCase targetCase, KazakhPossessive possessive) {
        return form.append(caseSuffix(form, targetCase, possessive));
    }

    private String caseSuffix(WordForm form, KazakhCase targetCase, KazakhPossessive possessive) {
        switch (targetCase) {
            case NOMINATIVE:
                return "";
            case GENITIVE:
                return genitive(form);
            case DATIVE:
                return dative(form, possessive);
            case ACCUSATIVE:
                return accusative(form, possessive);
            case LOCATIVE:
                return locative(form, possessive);
            case ABLATIVE:
                return ablative(form, possessive);
            case INSTRUMENTAL:
                return instrumental(form);
            default:
                throw new IllegalStateException("Unexpected case: " + targetCase);
        }
    }

    private String genitive(WordForm form) {
        if (form.endsInHardConsonant()) return harmonic(form, "тың", "тің");
        if (form.finalSound() == FinalSound.VOWEL || form.finalSound() == FinalSound.NASAL)
            return harmonic(form, "ның", "нің");

        return harmonic(form, "дың", "дің");
    }

    private String dative(WordForm form, KazakhPossessive possessive) {
        if (possessive == THIRD_PERSON) return harmonic(form, "на", "не");
        if (possessive == FIRST_SINGULAR || possessive == SECOND_INFORMAL)
            return harmonic(form, "а", "е");
        if (form.endsInHardConsonant()) return harmonic(form, "қа", "ке");
        return harmonic(form, "ға", "ге");
    }

    private String accusative(WordForm form, KazakhPossessive possessive) {
        if (possessive == THIRD_PERSON) return "н";
        if (form.endsInHardConsonant()) return harmonic(form, "ты", "ті");
        if (form.finalSound() == FinalSound.VOWEL) return harmonic(form, "ны", "ні");
        return harmonic(form, "ды", "ді");
    }

    private String locative(WordForm form, KazakhPossessive possessive) {
        if (possessive == THIRD_PERSON) return harmonic(form, "нда", "нде");
        if (form.endsInHardConsonant()) return harmonic(form, "та", "те");
        return harmonic(form, "да", "де");
    }

    private String ablative(WordForm form, KazakhPossessive possessive) {
        if (possessive == THIRD_PERSON || form.finalSound() == FinalSound.NASAL)
            return harmonic(form, "нан", "нен");
        if (form.endsInHardConsonant()) return harmonic(form, "тан", "тен");
        return harmonic(form, "дан", "ден");
    }

    private String instrumental(WordForm form) {
        if (form.endsInHardConsonant()) return "пен";
        if (form.finalSound() == FinalSound.VOICED_ZH_Z) return "бен";
        return "мен";
    }
}
