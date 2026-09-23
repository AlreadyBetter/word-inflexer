# Word Inflexer

A Java library in early development for rule-based word inflection, starting with Kazakh and designed to support additional languages through separate modules.

The planned API lets callers specify the desired grammatical features, such as plural number, possession and case. Each language module will determine the order of transformations and report ambiguity when more information is needed.

## Modules

- **word-inflexer-core** — language-independent feature contracts, grammatical number and an immutable feature set with conflict validation.
- **word-inflexer-kazakh** — Kazakh case and possessive features; word analysis and inflection rules are planned.

## Status

The initial grammatical feature model is implemented. Word analysis and inflection are not implemented yet.

Targets Java 8 or later and uses Maven. When building with a newer JDK, the compiler release is set to 8 to preserve Java 8 API and bytecode compatibility.
