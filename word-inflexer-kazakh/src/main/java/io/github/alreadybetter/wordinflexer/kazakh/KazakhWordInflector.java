package io.github.alreadybetter.wordinflexer.kazakh;

import io.github.alreadybetter.wordinflexer.core.InflectionRequest;
import io.github.alreadybetter.wordinflexer.core.InflectionResult;
import io.github.alreadybetter.wordinflexer.core.WordInflector;
import io.github.alreadybetter.wordinflexer.core.grammar.GrammaticalFeature;
import io.github.alreadybetter.wordinflexer.core.grammar.GrammaticalFeatures;
import io.github.alreadybetter.wordinflexer.core.grammar.GrammaticalNumber;
import io.github.alreadybetter.wordinflexer.kazakh.analysis.FinalSound;
import io.github.alreadybetter.wordinflexer.kazakh.analysis.KazakhWordAnalysis;
import io.github.alreadybetter.wordinflexer.kazakh.analysis.KazakhWordAnalyzer;
import io.github.alreadybetter.wordinflexer.kazakh.analysis.VowelHarmony;
import io.github.alreadybetter.wordinflexer.kazakh.grammar.KazakhCase;
import io.github.alreadybetter.wordinflexer.kazakh.grammar.KazakhPossessive;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static io.github.alreadybetter.wordinflexer.core.InflectionResult.failure;
import static io.github.alreadybetter.wordinflexer.core.InflectionResult.success;
import static io.github.alreadybetter.wordinflexer.core.InflectionStatus.INSUFFICIENT_INFORMATION;
import static io.github.alreadybetter.wordinflexer.core.InflectionStatus.UNSUPPORTED_FEATURES;
import static io.github.alreadybetter.wordinflexer.core.InflectionStatus.UNSUPPORTED_WORD;
import static io.github.alreadybetter.wordinflexer.core.grammar.FeatureCategory.CASE;
import static io.github.alreadybetter.wordinflexer.core.grammar.FeatureCategory.NUMBER;
import static io.github.alreadybetter.wordinflexer.core.grammar.FeatureCategory.POSSESSION;
import static io.github.alreadybetter.wordinflexer.core.grammar.GrammaticalNumber.SINGULAR;
import static io.github.alreadybetter.wordinflexer.kazakh.analysis.AnalysisIssue.POSSIBLE_ABBREVIATION;
import static io.github.alreadybetter.wordinflexer.kazakh.analysis.AnalysisIssue.UNSUPPORTED_CHARACTERS;
import static io.github.alreadybetter.wordinflexer.kazakh.grammar.KazakhCase.INSTRUMENTAL;
import static io.github.alreadybetter.wordinflexer.kazakh.grammar.KazakhCase.NOMINATIVE;
import static io.github.alreadybetter.wordinflexer.kazakh.grammar.KazakhPossessive.NONE;
import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;

/**
 * Stateless, thread-safe generator for regular Kazakh Cyrillic noun forms.
 *
 * <p>This first implementation requires the caller to declare the input as
 * NOMINATIVE, SINGULAR and NONE (no possession) before requesting changes.
 * Missing source categories remain unknown and yield insufficient information.
 * Already-inflected input needs morphological analysis and is not supported yet.
 * Source declarations are authoritative: without a dictionary the processor
 * cannot prove that a noun is a lemma or identify all incorrect declarations.
 *
 * <p>Target number, possession and case are applied in that order, independently
 * of feature insertion order. Omitted targets preserve the declared source values.
 * All seven cases, plural formation and the six possessive values are supported.
 * Possession on singular consonant-final stems is deferred because it can require
 * lexical stem changes; possession after generated plural marking is supported.
 *
 * <p>Spelling restrictions follow {@link KazakhWordAnalyzer}. Pronouns and known
 * irregular demonstratives are excluded. This is a form generator, not a
 * dictionary or semantic validator; callers must supply regular nouns.
 * Original spelling is preserved and lowercase suffixes are appended.
 */
public final class KazakhWordInflector implements WordInflector {

    private static final Set<String> EXCLUDED_PRONOUNS = unmodifiableSet(new HashSet<>(Arrays.asList(
            "мен", "сен", "ол", "біз", "сіз", "олар", "сендер", "сіздер",
            "бұл", "сол", "осы", "мынау", "анау", "сонау", "өзі", "кім", "не")));

    private final KazakhWordAnalyzer analyzer = new KazakhWordAnalyzer();
    private final NumberInflection numberInflection = new NumberInflection();
    private final PossessiveInflection possessiveInflection = new PossessiveInflection();
    private final CaseInflection caseInflection = new CaseInflection();

    /**
     * {@inheritDoc}
     *
     * <p>Unsupported source forms are rejected rather than stripped heuristically.
     * If no changes are requested and no contradictory source forms are declared,
     * the original supported word is returned without requiring missing categories.
     */
    @Override
    public InflectionResult inflect(InflectionRequest request) {
        requireNonNull(request, "Request must not be null");
        Optional<InflectionResult> featureProblem = validateFeatures(request);
        if (featureProblem.isPresent()) return featureProblem.get();

        KazakhWordAnalysis analysis = analyzer.analyze(request.word());
        Optional<InflectionResult> wordProblem = validateWord(analysis);
        if (wordProblem.isPresent()) return wordProblem.get();
        if (request.targetFeatures().values().isEmpty()) return success(request.word());

        Optional<InflectionResult> sourceProblem = requireBaseForm(request.sourceFeatures());
        if (sourceProblem.isPresent()) return sourceProblem.get();

        GrammaticalFeatures target = request.targetFeatures();
        GrammaticalNumber number = (GrammaticalNumber) target.get(NUMBER).orElse(SINGULAR);
        KazakhPossessive possessive = (KazakhPossessive) target.get(POSSESSION).orElse(NONE);
        KazakhCase targetCase = (KazakhCase) target.get(CASE).orElse(NOMINATIVE);

        Optional<InflectionResult> transformationProblem = validateTransformation(analysis, number, possessive, targetCase);
        if (transformationProblem.isPresent()) return transformationProblem.get();

        WordForm form = new WordForm(request.word());
        form = numberInflection.apply(form, number);
        form = possessiveInflection.apply(form, possessive);
        form = caseInflection.apply(form, targetCase, possessive);
        return success(form.text());
    }

    private Optional<InflectionResult> validateFeatures(InflectionRequest request) {
        if (!supportedFeatures(request.sourceFeatures()) || !supportedFeatures(request.targetFeatures()))
            return Optional.of(failure(UNSUPPORTED_FEATURES,
                    "Use GrammaticalNumber, KazakhPossessive and KazakhCase features only."));

        GrammaticalFeatures source = request.sourceFeatures();
        if (source.get(CASE).filter(value -> value != NOMINATIVE).isPresent()
                || source.get(NUMBER).filter(value -> value != SINGULAR).isPresent()
                || source.get(POSSESSION).filter(value -> value != NONE).isPresent())
            return Optional.of(failure(UNSUPPORTED_WORD,
                    "Already-inflected source forms are not supported. Supply a singular nominative noun without possession."));

        return Optional.empty();
    }

    private boolean supportedFeatures(GrammaticalFeatures features) {
        for (GrammaticalFeature feature : features.values())
            if (!(feature instanceof GrammaticalNumber)
                    && !(feature instanceof KazakhPossessive)
                    && !(feature instanceof KazakhCase)) return false;
        return true;
    }

    private Optional<InflectionResult> validateWord(KazakhWordAnalysis analysis) {
        if (analysis.issues().contains(UNSUPPORTED_CHARACTERS) || analysis.issues().contains(POSSIBLE_ABBREVIATION))
            return Optional.of(failure(UNSUPPORTED_WORD,
                    "Use one lowercase or initial-capital Kazakh Cyrillic word: " + analysis.issues()));
        if (EXCLUDED_PRONOUNS.contains(analysis.normalizedWord()))
            return Optional.of(failure(UNSUPPORTED_WORD, "Pronoun inflection is not implemented; supply a regular noun."));
        return Optional.empty();
    }

    private Optional<InflectionResult> requireBaseForm(GrammaticalFeatures source) {
        if (!source.contains(NOMINATIVE) || !source.contains(SINGULAR) || !source.contains(NONE))
            return Optional.of(failure(INSUFFICIENT_INFORMATION,
                    "Declare source features NOMINATIVE, SINGULAR and NONE to confirm a base noun form."
                            + " Missing categories are not inferred from spelling."));
        return Optional.empty();
    }

    private Optional<InflectionResult> validateTransformation(KazakhWordAnalysis analysis, GrammaticalNumber number,
                                                            KazakhPossessive possessive, KazakhCase targetCase) {
        boolean unchanged = number == SINGULAR && possessive == NONE && targetCase == NOMINATIVE;
        if (unchanged) return Optional.empty();
        if (analysis.finalSound() == FinalSound.UNKNOWN)
            return Optional.of(failure(UNSUPPORTED_WORD, "The final sound requires additional spelling rules: " + analysis.issues()));

        boolean harmonicSuffixNeeded = number != SINGULAR || possessive != NONE || targetCase != INSTRUMENTAL;
        if (harmonicSuffixNeeded && analysis.harmony() == VowelHarmony.UNKNOWN)
            return Optional.of(failure(INSUFFICIENT_INFORMATION,
                    "Vowel harmony cannot be established by the current spelling rules: " + analysis.issues()));
        if (possessive != NONE && number == SINGULAR && analysis.finalSound() != FinalSound.VOWEL)
            return Optional.of(failure(UNSUPPORTED_WORD,
                    "Possession on a consonant-final singular stem requires stem-alternation rules that are not implemented yet."));
        return Optional.empty();
    }
}
