# Word Inflexer

A Java 8-compatible library for rule-based word inflection. Language-independent contracts live in the core module; each language module owns its grammatical values, analysis and transformation order.

The initial Kazakh implementation generates regular noun forms by applying **number → possession → case**, regardless of the order of requested features.

## Modules

- **word-inflexer-core** — grammatical feature sets, immutable requests and results, and the `WordInflector` contract.
- **word-inflexer-kazakh** — Kazakh grammatical values, conservative Cyrillic spelling analysis and regular noun generation.

## Usage

The example uses the Kazakh module and its transitive core dependency. The language is selected by the implementation, not inferred from the input string.

```java
import io.github.alreadybetter.wordinflexer.core.InflectionRequest;
import io.github.alreadybetter.wordinflexer.core.InflectionResult;
import io.github.alreadybetter.wordinflexer.core.WordInflector;
import io.github.alreadybetter.wordinflexer.core.grammar.GrammaticalFeatures;
import io.github.alreadybetter.wordinflexer.kazakh.KazakhWordInflector;

import static io.github.alreadybetter.wordinflexer.core.grammar.GrammaticalFeatures.of;
import static io.github.alreadybetter.wordinflexer.core.grammar.GrammaticalNumber.PLURAL;
import static io.github.alreadybetter.wordinflexer.core.grammar.GrammaticalNumber.SINGULAR;
import static io.github.alreadybetter.wordinflexer.kazakh.grammar.KazakhCase.DATIVE;
import static io.github.alreadybetter.wordinflexer.kazakh.grammar.KazakhCase.NOMINATIVE;
import static io.github.alreadybetter.wordinflexer.kazakh.grammar.KazakhPossessive.NONE;
import static io.github.alreadybetter.wordinflexer.kazakh.grammar.KazakhPossessive.THIRD_PERSON;

public class Example {
    static void main(String[] args) {
        WordInflector inflector = new KazakhWordInflector();
        GrammaticalFeatures source = of(NOMINATIVE, SINGULAR, NONE);

        InflectionResult result = inflector.inflect(
                new InflectionRequest("астана", source, of(PLURAL, THIRD_PERSON, DATIVE))
        );

        System.out.println(result.status()); // SUCCESS
        System.out.println(result.word().orElse("No confirmed form")); // астаналарына
        result.message().ifPresent(System.out::println);
    }
}
```

Source features are declarations about the input, not additional transformations. The first implementation requires `NOMINATIVE`, `SINGULAR` and `NONE` for requests that change a word. Omitted source categories remain unknown and return `INSUFFICIENT_INFORMATION`; they are not silently filled in. Omitted target categories preserve the declared source values.

Examples below assume those three source declarations. These are documented expected outputs, not an automated test report.

| Input | Target features | Expected form |
| --- | --- | --- |
| кітап | DATIVE | кітапқа |
| мектеп | DATIVE | мектепке |
| қала | GENITIVE | қаланың |
| қалам | ABLATIVE | қаламнан |
| жол | PLURAL | жолдар |
| жер | PLURAL | жерлер |
| астана | PLURAL, DATIVE | астаналарға |
| астана | PLURAL, THIRD_PERSON, DATIVE | астаналарына |
| қала | FIRST_SINGULAR, DATIVE | қалама |
| қала | FIRST_PLURAL, DATIVE | қаламызға |
| кеме | THIRD_PERSON, LOCATIVE | кемесінде |
| кітап | PLURAL, THIRD_PERSON, ACCUSATIVE | кітаптарын |

`word()` is populated only for `SUCCESS`. Other outcomes include an explanatory `message()`. The shared result model can represent ambiguity, but this first processor requests explicit source declarations instead of guessing competing morphological analyses.

## Current scope

- All seven Kazakh cases, plural marking and possessive suffixes in their grammatical order.
- Possessive suffixes on vowel-final singular stems and on newly generated plural stems.
- Lowercase and initial-capital Kazakh Cyrillic nouns; original spelling is preserved and suffixes are lowercase.
- Immutable request/result objects and a stateless, thread-safe Kazakh processor.
- No external runtime dependencies.

## Limitations

- Already-inflected input cannot yet be analyzed and rebuilt. For example, supply `астана` with target possession instead of supplying `астанасы` as the source.
- Possession on consonant-final singular stems is deferred: forms such as `кітап → кітабым` need stem-alternation rules. Plural possession, such as `кітап → кітаптарым`, is supported.
- Harmony detection is conservative. Spellings containing `и`, `у`, `ё`, `э`, `ю`, `я`, `ь`, `ъ`, `в`, `ф`, `ц`, `ч` or `щ`, and a final harmony-bearing noninitial `ә`, need additional handling. Harmony-independent instrumental marking may still be possible when the final sound is known.
- Latin spelling, mixed scripts, phrases, punctuation and internal/all-capital spellings are not supported. The last group may represent abbreviations whose pronunciation cannot be inferred safely.
- There is no dictionary, part-of-speech detection or general validation of lexical exceptions. Callers must supply regular nouns and accurate source declarations. Pronouns are outside the supported scope.
- An empty target set requests no change. The processor still validates supported input and supplied features, but does not require missing source categories merely to return the original word.
- The processor generates forms; it does not decide whether a word can meaningfully be pluralized in a particular context.

## Tests

Run from the repository root:

```shell
mvn test
```

To select the Kazakh module and build its core dependency in the same reactor:

```shell
mvn -pl word-inflexer-kazakh -am test
```

Maven Surefire runs four JUnit test groups covering case paradigms, combined transformations, failure outcomes and unchanged input. JUnit 4.13.2 is a test-scoped dependency and is not required by applications using the library. Test reports are written to `word-inflexer-kazakh/target/surefire-reports`.

In IntelliJ IDEA, reload Maven dependencies and run `KazakhWordInflectorSmokeTest` as a JUnit test. Its `main` method also remains available for manual execution. No separate consuming project or `-ea` JVM flag is needed.

Add an `expectForm` call in `verifyCombinedTransformations` to check another word and its expected output. Checks throw `AssertionError` on a mismatch, causing the JUnit group and Maven build to fail.

## Build target

Uses Maven and targets Java 8. When compiling with a newer JDK, `release=8` preserves Java 8 API and bytecode compatibility.

## Grammar references

The regular suffix rules and the conservative spelling analyzer are based on these references; the implementation intentionally supports only the scope documented above:

- [Kazakh case suffix table — East Kazakhstan Technical University](https://ektu.kz/files/feim/KRYD/textbook/Грамматика/Грамматика%206.htm)
- [Word formation and grammatical suffixes — iTest](https://itest.kz/kz/ent/qazaq-tili/sozzhasam/lecture/soz-tulhasy-tuyndy-soz-zhasaluy-tubir-men-qosymsha-qosymshanyng-turleri)
- [A Grammar of Kazakh — Zura Dotton and John Doyle Wagner](https://slaviccenters.duke.edu/sites/slaviccenters.duke.edu/files/file-attachments/kazakh-grammar.pdf)

## Publication

Maven Central publication is configured through the optional central-release profile. No release has been published by this setup. See [the publishing guide](publishing/README.md) for credentials, signing and manual release steps.

## License

MIT License. Copyright (c) 2026 Timur Kokishev (AlreadyBetter).
