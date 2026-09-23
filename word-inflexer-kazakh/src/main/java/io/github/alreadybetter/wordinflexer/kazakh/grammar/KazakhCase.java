package io.github.alreadybetter.wordinflexer.kazakh.grammar;

import io.github.alreadybetter.wordinflexer.core.grammar.FeatureCategory;
import io.github.alreadybetter.wordinflexer.core.grammar.GrammaticalFeature;

import static io.github.alreadybetter.wordinflexer.core.grammar.FeatureCategory.CASE;

/**
 * Grammatical cases supported by the Kazakh feature model.
 * Each value belongs to the shared case category, allowing the core feature set
 * to detect conflicts without depending on Kazakh-specific types.
 */
public enum KazakhCase implements GrammaticalFeature {
    /** Nominative case (Kazakh: atau septik). */
    NOMINATIVE,
    /** Genitive case (Kazakh: ilik septik). */
    GENITIVE,
    /** Dative case (Kazakh: barys septik). */
    DATIVE,
    /** Accusative case (Kazakh: tabys septik). */
    ACCUSATIVE,
    /** Locative case (Kazakh: zhatys septik). */
    LOCATIVE,
    /** Ablative case (Kazakh: shygys septik). */
    ABLATIVE,
    /** Instrumental case (Kazakh: komektes septik). */
    INSTRUMENTAL;

    /** {@inheritDoc} */
    @Override
    public FeatureCategory category() {
        return CASE;
    }
}
