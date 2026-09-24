package io.github.alreadybetter.wordinflexer.kazakh.analysis;

import java.util.EnumSet;
import java.util.Set;

import static io.github.alreadybetter.wordinflexer.kazakh.analysis.AnalysisIssue.CONTEXT_DEPENDENT_SPELLING;
import static io.github.alreadybetter.wordinflexer.kazakh.analysis.AnalysisIssue.NONINITIAL_AE;
import static io.github.alreadybetter.wordinflexer.kazakh.analysis.AnalysisIssue.NO_HARMONY_VOWEL;
import static io.github.alreadybetter.wordinflexer.kazakh.analysis.AnalysisIssue.POSSIBLE_ABBREVIATION;
import static io.github.alreadybetter.wordinflexer.kazakh.analysis.AnalysisIssue.UNSUPPORTED_CHARACTERS;
import static io.github.alreadybetter.wordinflexer.kazakh.analysis.AnalysisIssue.UNSUPPORTED_FINAL_SOUND;
import static java.lang.Character.isSpaceChar;
import static java.lang.Character.isUpperCase;
import static java.lang.Character.isWhitespace;
import static java.text.Normalizer.Form.NFC;
import static java.text.Normalizer.normalize;
import static java.util.Locale.ROOT;
import static java.util.Objects.requireNonNull;

/**
 * Stateless, thread-safe analyzer of regular Kazakh Cyrillic word spellings.
 *
 * <p>Determines the last unambiguous vowel series and the final-sound group.
 * For example, кітап yields BACK/VOICELESS, мектеп FRONT/VOICELESS,
 * қала BACK/VOWEL and қалам BACK/NASAL. This does not distinguish
 * қалам as a lexical noun from a possessive form.
 *
 * <p>This initial implementation is deliberately conservative. The letters
 * и, у, ё, э, ю, я, ь, ъ, в, ф, ц, ч and щ prevent automatic harmony selection.
 * Final и and у are also unresolved. Noninitial ә as the last harmony vowel
 * needs additional rules. Ordinary lowercase and initial-capital words are
 * supported; internal capitals are treated as possible abbreviations.
 *
 * <p>No dictionary validation, loanword exception lookup, affix segmentation
 * or part-of-speech detection is performed. Observations describe the regular
 * spelling model and must not be treated as proof of grammatical validity.
 */
public final class KazakhWordAnalyzer {

    private static final String ALPHABET = "аәбвгғдеёжзийкқлмнңоөпрстуұүфхһцчшщъыіьэюя";
    private static final String BACK_VOWELS = "аоұы";
    private static final String FRONT_VOWELS = "әөүіе";
    private static final String CONTEXT_DEPENDENT_LETTERS = "иуёэюяьъвфцчщ";

    /**
     * Analyzes a copy of the word without modifying the original spelling.
     * Unsupported characters produce diagnostics rather than being removed.
     * A word may have a known final sound but unknown harmony.
     *
     * @param word a non-null, non-blank input word
     * @return immutable observations and diagnostics
     * @throws NullPointerException if the word is null
     * @throws IllegalArgumentException if the word is empty or entirely whitespace
     */
    public KazakhWordAnalysis analyze(String word) {
        validateWord(word);
        String normalizedWord = normalize(word, NFC).toLowerCase(ROOT);
        Set<AnalysisIssue> issues = EnumSet.noneOf(AnalysisIssue.class);

        if (!normalizedWord.codePoints().allMatch(letter -> ALPHABET.indexOf(letter) >= 0)) {
            issues.add(UNSUPPORTED_CHARACTERS);
            return unresolved(word, normalizedWord, issues);
        }
        if (word.codePoints().skip(1).anyMatch(Character::isUpperCase)) {
            issues.add(POSSIBLE_ABBREVIATION);
            return unresolved(word, normalizedWord, issues);
        }

        VowelHarmony harmony = determineHarmony(normalizedWord, issues);
        FinalSound finalSound = determineFinalSound(normalizedWord);
        if (finalSound == FinalSound.UNKNOWN) issues.add(UNSUPPORTED_FINAL_SOUND);

        return new KazakhWordAnalysis(word, normalizedWord, harmony, finalSound, issues);
    }

    private static VowelHarmony determineHarmony(String word, Set<AnalysisIssue> issues) {
        if (word.codePoints().anyMatch(letter -> CONTEXT_DEPENDENT_LETTERS.indexOf(letter) >= 0)) {
            issues.add(CONTEXT_DEPENDENT_SPELLING);
            return VowelHarmony.UNKNOWN;
        }

        for (int index = word.length() - 1; index >= 0; index--) {
            char letter = word.charAt(index);
            if (BACK_VOWELS.indexOf(letter) >= 0) return VowelHarmony.BACK;
            if (FRONT_VOWELS.indexOf(letter) < 0) continue;
            if (letter == 'ә' && hasEarlierVowel(word, index)) {
                issues.add(NONINITIAL_AE);
                return VowelHarmony.UNKNOWN;
            }
            return VowelHarmony.FRONT;
        }

        issues.add(NO_HARMONY_VOWEL);
        return VowelHarmony.UNKNOWN;
    }

    private static boolean hasEarlierVowel(String word, int end) {
        for (int index = 0; index < end; index++) {
            char letter = word.charAt(index);
            if (BACK_VOWELS.indexOf(letter) >= 0 || FRONT_VOWELS.indexOf(letter) >= 0) return true;
        }
        return false;
    }

    private static FinalSound determineFinalSound(String word) {
        char letter = word.charAt(word.length() - 1);
        if (BACK_VOWELS.indexOf(letter) >= 0 || FRONT_VOWELS.indexOf(letter) >= 0) return FinalSound.VOWEL;
        if ("мнң".indexOf(letter) >= 0) return FinalSound.NASAL;
        if ("лрй".indexOf(letter) >= 0) return FinalSound.OTHER_SONORANT;
        if ("жз".indexOf(letter) >= 0) return FinalSound.VOICED_ZH_Z;
        if ("пфкқтсшщхһцч".indexOf(letter) >= 0) return FinalSound.VOICELESS;
        if ("бвгд".indexOf(letter) >= 0) return FinalSound.FINAL_B_V_G_D;
        return FinalSound.UNKNOWN;
    }

    private static KazakhWordAnalysis unresolved(String word, String normalizedWord, Set<AnalysisIssue> issues) {
        return new KazakhWordAnalysis(word, normalizedWord, VowelHarmony.UNKNOWN, FinalSound.UNKNOWN, issues);
    }

    private static void validateWord(String word) {
        requireNonNull(word, "Word must not be null");
        if (word.codePoints().allMatch(codePoint -> isWhitespace(codePoint) || isSpaceChar(codePoint)))
            throw new IllegalArgumentException("Word must not be blank");
    }
}
