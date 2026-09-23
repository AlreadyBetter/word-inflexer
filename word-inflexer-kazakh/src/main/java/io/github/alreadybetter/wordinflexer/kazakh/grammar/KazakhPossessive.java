package io.github.alreadybetter.wordinflexer.kazakh.grammar;

import io.github.alreadybetter.wordinflexer.core.grammar.FeatureCategory;
import io.github.alreadybetter.wordinflexer.core.grammar.GrammaticalFeature;
import io.github.alreadybetter.wordinflexer.core.grammar.GrammaticalNumber;

import static io.github.alreadybetter.wordinflexer.core.grammar.FeatureCategory.POSSESSION;

/**
 * Possessive-marking values for the Kazakh feature model.
 *
 * <p>First-person number describes the possessor, not the possessed items.
 * For example, {@link #FIRST_SINGULAR} can be combined with
 * {@link GrammaticalNumber#PLURAL} to describe multiple items owned by the speaker.
 * Second- and third-person values do not fully specify possessor number or gender.
 * Language modules must not infer distinctions that these values do not express.
 *
 * <p>{@link #NONE} explicitly denotes absence of possessive marking.
 * An omitted possession feature is unspecified, not equivalent to {@code NONE}.
 */
public enum KazakhPossessive implements GrammaticalFeature {
    /** No possessive marking. */
    NONE,
    /** First-person singular possessor: my. */
    FIRST_SINGULAR,
    /** First-person plural possessor: our. */
    FIRST_PLURAL,
    /** Informal second-person possessive form: your. */
    SECOND_INFORMAL,
    /** Polite second-person possessive form: your. */
    SECOND_POLITE,
    /** Third-person possessive form: his, her, its or their. */
    THIRD_PERSON;

    /** {@inheritDoc} */
    @Override
    public FeatureCategory category() {
        return POSSESSION;
    }
}
