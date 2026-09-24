package io.github.alreadybetter.wordinflexer.kazakh;

import io.github.alreadybetter.wordinflexer.core.InflectionRequest;
import io.github.alreadybetter.wordinflexer.core.InflectionResult;
import io.github.alreadybetter.wordinflexer.core.InflectionStatus;
import io.github.alreadybetter.wordinflexer.core.WordInflector;
import io.github.alreadybetter.wordinflexer.core.grammar.GrammaticalFeature;
import io.github.alreadybetter.wordinflexer.core.grammar.GrammaticalFeatures;
import io.github.alreadybetter.wordinflexer.kazakh.grammar.KazakhCase;

import static io.github.alreadybetter.wordinflexer.core.InflectionStatus.INSUFFICIENT_INFORMATION;
import static io.github.alreadybetter.wordinflexer.core.InflectionStatus.SUCCESS;
import static io.github.alreadybetter.wordinflexer.core.InflectionStatus.UNSUPPORTED_FEATURES;
import static io.github.alreadybetter.wordinflexer.core.InflectionStatus.UNSUPPORTED_WORD;
import static io.github.alreadybetter.wordinflexer.core.grammar.FeatureCategory.CASE;
import static io.github.alreadybetter.wordinflexer.core.grammar.GrammaticalFeatures.of;
import static io.github.alreadybetter.wordinflexer.core.grammar.GrammaticalNumber.PLURAL;
import static io.github.alreadybetter.wordinflexer.core.grammar.GrammaticalNumber.SINGULAR;
import static io.github.alreadybetter.wordinflexer.kazakh.grammar.KazakhCase.ABLATIVE;
import static io.github.alreadybetter.wordinflexer.kazakh.grammar.KazakhCase.ACCUSATIVE;
import static io.github.alreadybetter.wordinflexer.kazakh.grammar.KazakhCase.DATIVE;
import static io.github.alreadybetter.wordinflexer.kazakh.grammar.KazakhCase.GENITIVE;
import static io.github.alreadybetter.wordinflexer.kazakh.grammar.KazakhCase.INSTRUMENTAL;
import static io.github.alreadybetter.wordinflexer.kazakh.grammar.KazakhCase.LOCATIVE;
import static io.github.alreadybetter.wordinflexer.kazakh.grammar.KazakhCase.NOMINATIVE;
import static io.github.alreadybetter.wordinflexer.kazakh.grammar.KazakhPossessive.FIRST_PLURAL;
import static io.github.alreadybetter.wordinflexer.kazakh.grammar.KazakhPossessive.FIRST_SINGULAR;
import static io.github.alreadybetter.wordinflexer.kazakh.grammar.KazakhPossessive.NONE;
import static io.github.alreadybetter.wordinflexer.kazakh.grammar.KazakhPossessive.SECOND_INFORMAL;
import static io.github.alreadybetter.wordinflexer.kazakh.grammar.KazakhPossessive.SECOND_POLITE;
import static io.github.alreadybetter.wordinflexer.kazakh.grammar.KazakhPossessive.THIRD_PERSON;

/**
 * Standalone regression checks runnable directly through main in an IDE.
 * No testing framework, consuming project or JVM assertion flag is required.
 * Failures throw AssertionError and make the process exit unsuccessfully.
 * Maven Surefire does not automatically execute this main-based runner.
 */
public final class KazakhWordInflectorSmokeTest {

    private static final GrammaticalFeatures BASE_FORM = of(NOMINATIVE, SINGULAR, NONE);
    private final WordInflector inflector = new KazakhWordInflector();
    private int passed;

    /**
     * Runs fixed expected forms and error-contract checks, printing each success.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        KazakhWordInflectorSmokeTest checks = new KazakhWordInflectorSmokeTest();
        checks.verifyCases();
        checks.verifyCombinedTransformations();
        checks.verifyFailures();
        checks.verifyNoChange();
        System.out.println("All " + checks.passed + " checks passed.");
    }

    private void verifyCases() {
        verifyParadigm("кітап", "кітап", "кітаптың", "кітапқа", "кітапты", "кітапта", "кітаптан", "кітаппен");
        verifyParadigm("мектеп", "мектеп", "мектептің", "мектепке", "мектепті", "мектепте", "мектептен", "мектеппен");
        verifyParadigm("қала", "қала", "қаланың", "қалаға", "қаланы", "қалада", "қаладан", "қаламен");
        verifyParadigm("қалам", "қалам", "қаламның", "қаламға", "қаламды", "қаламда", "қаламнан", "қаламмен");
        verifyParadigm("жол", "жол", "жолдың", "жолға", "жолды", "жолда", "жолдан", "жолмен");
        verifyParadigm("қыз", "қыз", "қыздың", "қызға", "қызды", "қызда", "қыздан", "қызбен");
    }

    private void verifyCombinedTransformations() {
        expectForm("жол", "жолдар", PLURAL);
        expectForm("жер", "жерлер", PLURAL);
        expectForm("кітап", "кітаптар", PLURAL);
        expectForm("қалам", "қаламдар", PLURAL);
        expectForm("астана", "астаналарға", PLURAL, DATIVE);
        expectForm("астана", "астаналарына", PLURAL, THIRD_PERSON, DATIVE);
        expectForm("астана", "астаналарына", DATIVE, THIRD_PERSON, PLURAL);
        expectForm("қала", "қалама", FIRST_SINGULAR, DATIVE);
        expectForm("қала", "қаламызға", FIRST_PLURAL, DATIVE);
        expectForm("қала", "қалаңа", SECOND_INFORMAL, DATIVE);
        expectForm("қала", "қалаңызға", SECOND_POLITE, DATIVE);
        expectForm("қала", "қаласына", THIRD_PERSON, DATIVE);
        expectForm("қала", "қаласын", THIRD_PERSON, ACCUSATIVE);
        expectForm("қала", "қаласынан", THIRD_PERSON, ABLATIVE);
        expectForm("қала", "қаламызбен", FIRST_PLURAL, INSTRUMENTAL);
        expectForm("кеме", "кемесінде", THIRD_PERSON, LOCATIVE);
        expectForm("кеме", "кемеме", FIRST_SINGULAR, DATIVE);
        expectForm("кітап", "кітаптарын", PLURAL, THIRD_PERSON, ACCUSATIVE);
        expectForm("Астана", "Астанаға", DATIVE);
        expectForm("автор", "автормен", INSTRUMENTAL);
    }

    private void verifyFailures() {
        expectStatus("unknown source", new InflectionRequest("қалам", of(DATIVE)), INSUFFICIENT_INFORMATION);
        expectStatus("partial source", new InflectionRequest("қала", of(NOMINATIVE), of(DATIVE)), INSUFFICIENT_INFORMATION);
        expectStatus("existing possession", new InflectionRequest("қаласы", of(NOMINATIVE, SINGULAR, THIRD_PERSON), of(DATIVE)), UNSUPPORTED_WORD);
        expectStatus("stem alternation", request("кітап", FIRST_SINGULAR), UNSUPPORTED_WORD);
        expectStatus("unsupported harmony spelling", request("автор", DATIVE), INSUFFICIENT_INFORMATION);
        expectStatus("Latin input", request("Astana", DATIVE), UNSUPPORTED_WORD);
        expectStatus("phrase input", request("жаңа қала", DATIVE), UNSUPPORTED_WORD);
        expectStatus("abbreviation", request("ҚР", DATIVE), UNSUPPORTED_WORD);
        expectStatus("pronoun", request("мен", DATIVE), UNSUPPORTED_WORD);
        GrammaticalFeature foreignCase = () -> CASE;
        expectStatus("foreign case type", request("қала", foreignCase), UNSUPPORTED_FEATURES);
    }

    private void verifyNoChange() {
        InflectionResult result = inflector.inflect(new InflectionRequest("Астана", of()));
        require(result.status() == SUCCESS, "Empty target should succeed without source declarations");
        require("Астана".equals(result.word().orElse(null)), "Empty target must preserve the original spelling");
        report("Empty target preserves original word");
    }

    private void verifyParadigm(String word, String... expected) {
        KazakhCase[] cases = {NOMINATIVE, GENITIVE, DATIVE, ACCUSATIVE, LOCATIVE, ABLATIVE, INSTRUMENTAL};
        require(expected.length == cases.length, "A paradigm must specify all seven expected forms");
        for (int index = 0; index < cases.length; index++)
            expectForm(word, expected[index], cases[index]);
    }

    private void expectForm(String word, String expected, GrammaticalFeature... target) {
        InflectionResult result = inflector.inflect(request(word, target));
        String label = word + " + " + of(target);
        require(result.status() == SUCCESS, label + ": expected SUCCESS, got " + result);
        require(expected.equals(result.word().orElse(null)), label + ": expected " + expected + ", got " + result);
        require(result.forms().size() == 1 && result.forms().contains(expected), label + ": inconsistent forms");
        require(!result.message().isPresent(), label + ": success must not include an error message");
        report(label + " -> " + expected);
    }

    private void expectStatus(String label, InflectionRequest request, InflectionStatus expected) {
        InflectionResult result = inflector.inflect(request);
        require(result.status() == expected, label + ": expected " + expected + ", got " + result);
        require(!result.word().isPresent() && result.forms().isEmpty(), label + ": failure must not expose an output");
        require(result.message().isPresent(), label + ": failure must explain its cause");
        report(label + " -> " + expected);
    }

    private InflectionRequest request(String word, GrammaticalFeature... target) {
        return new InflectionRequest(word, BASE_FORM, of(target));
    }

    private void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private void report(String message) {
        passed++;
        System.out.println("PASS: " + message);
    }
}
