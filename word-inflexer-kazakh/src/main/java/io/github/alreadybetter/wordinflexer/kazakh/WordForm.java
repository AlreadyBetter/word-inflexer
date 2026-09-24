package io.github.alreadybetter.wordinflexer.kazakh;

import io.github.alreadybetter.wordinflexer.kazakh.analysis.FinalSound;
import io.github.alreadybetter.wordinflexer.kazakh.analysis.KazakhWordAnalysis;
import io.github.alreadybetter.wordinflexer.kazakh.analysis.KazakhWordAnalyzer;
import io.github.alreadybetter.wordinflexer.kazakh.analysis.VowelHarmony;

/** Immutable working form shared by the ordered suffix stages. */
final class WordForm {

    private static final KazakhWordAnalyzer ANALYZER = new KazakhWordAnalyzer();

    private final String text;
    private final KazakhWordAnalysis analysis;

    WordForm(String text) {
        this.text = text;
        this.analysis = ANALYZER.analyze(text);
    }

    String text() {
        return text;
    }

    VowelHarmony harmony() {
        return analysis.harmony();
    }

    FinalSound finalSound() {
        return analysis.finalSound();
    }

    char lastLetter() {
        String normalized = analysis.normalizedWord();
        return normalized.charAt(normalized.length() - 1);
    }

    WordForm append(String suffix) {
        return suffix.isEmpty() ? this : new WordForm(text + suffix);
    }

    boolean endsInHardConsonant() {
        return finalSound() == FinalSound.VOICELESS || finalSound() == FinalSound.FINAL_B_V_G_D;
    }
}
