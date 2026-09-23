package io.github.alreadybetter.wordinflexer.core;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static io.github.alreadybetter.wordinflexer.core.InflectionStatus.AMBIGUOUS;
import static io.github.alreadybetter.wordinflexer.core.InflectionStatus.SUCCESS;
import static java.lang.Character.isSpaceChar;
import static java.lang.Character.isWhitespace;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

/**
 * An immutable outcome of inflection, without dependencies on language-specific types.
 *
 * <p>A successful result contains exactly one form and no diagnostic message.
 * An ambiguous result contains at least two distinct possible forms and an
 * explanation of the ambiguity. Other outcomes contain no forms and require
 * an explanation. Conditional alternatives must not be treated as confirmed output.
 *
 * <p>Factories enforce structural consistency only. A language processor must
 * complete all applicable checks before reporting success; a single remaining
 * candidate is not sufficient if its conditions have not been established.
 * Words and messages are preserved without normalization.
 */
public final class InflectionResult {

    private final InflectionStatus status;
    private final Set<String> forms;
    private final String message;

    private InflectionResult(InflectionStatus status, Set<String> forms, String message) {
        this.status = status;
        this.forms = unmodifiableSet(new LinkedHashSet<>(forms));
        this.message = message;
    }

    /**
     * Creates a successful result after the processor has confirmed the output.
     *
     * @param word the confirmed output form
     * @return a successful result containing exactly this form
     * @throws NullPointerException if the word is null
     * @throws IllegalArgumentException if the word is blank
     */
    public static InflectionResult success(String word) {
        return new InflectionResult(SUCCESS, singleton(requireText(word, "Word")), null);
    }

    /**
     * Creates an ambiguous result from distinct possible output forms.
     * Equal duplicates are collapsed; at least two distinct forms must remain.
     * Iteration follows first occurrence in the input, without implying a ranking.
     *
     * @param alternatives possible forms under different analyses
     * @param message an explanation, including useful clarification when known
     * @return an ambiguous result with a defensive copy of the alternatives
     * @throws NullPointerException if the collection, a form or the message is null
     * @throws IllegalArgumentException if a form or message is blank, or fewer
     *                                  than two distinct forms remain
     */
    public static InflectionResult ambiguous(Collection<String> alternatives, String message) {
        Set<String> forms = distinctForms(alternatives);
        if (forms.size() < 2)
            throw new IllegalArgumentException("Ambiguity requires at least two distinct forms");

        return new InflectionResult(AMBIGUOUS, forms, requireText(message, "Message"));
    }

    /**
     * Creates an outcome with no established output forms.
     * Use the dedicated factories for success and ambiguity.
     *
     * @param status a status other than success or ambiguity
     * @param message the reason processing could not establish an output
     * @return a failure result with an empty set of forms
     * @throws NullPointerException if the status or message is null
     * @throws IllegalArgumentException if the status is success or ambiguity,
     *                                  or the message is blank
     */
    public static InflectionResult failure(InflectionStatus status, String message) {
        requireNonNull(status, "Status must not be null");
        if (status == SUCCESS || status == AMBIGUOUS)
            throw new IllegalArgumentException("Use the dedicated factory for " + status);

        return new InflectionResult(status, emptySet(), requireText(message, "Message"));
    }

    /** @return the non-null processing outcome */
    public InflectionStatus status() {
        return status;
    }

    /**
     * Returns an output only when processing succeeded.
     * Never selects an arbitrary alternative from an ambiguous result.
     *
     * @return the confirmed form, or an empty optional for every other status
     */
    public Optional<String> word() {
        return status == SUCCESS ? Optional.of(forms.iterator().next()) : Optional.empty();
    }

    /**
     * Returns the confirmed form for success or conditional alternatives for ambiguity.
     *
     * @return an unmodifiable set; empty for all other outcomes
     */
    public Set<String> forms() {
        return forms;
    }

    /**
     * Returns a human-readable diagnostic, not a machine-readable error code.
     * Callers should branch on {@link #status()} rather than parse this text.
     *
     * @return an explanation for non-success outcomes; empty for success
     */
    public Optional<String> message() {
        return Optional.ofNullable(message);
    }

    private static Set<String> distinctForms(Collection<String> alternatives) {
        requireNonNull(alternatives, "Alternatives must not be null");
        Set<String> forms = new LinkedHashSet<>();
        for (String alternative : alternatives)
            forms.add(requireText(alternative, "Alternative"));

        return forms;
    }

    private static String requireText(String text, String name) {
        requireNonNull(text, name + " must not be null");
        if (text.codePoints().allMatch(codePoint -> isWhitespace(codePoint) || isSpaceChar(codePoint)))
            throw new IllegalArgumentException(name + " must not be blank");

        return text;
    }

    /**
     * Compares status, forms and diagnostic text. Form order is ignored.
     *
     * @param other the object to compare with, possibly null
     * @return true if the outcome data is equal
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof InflectionResult)) return false;

        InflectionResult that = (InflectionResult) other;
        return status == that.status
                && forms.equals(that.forms)
                && message().equals(that.message());
    }

    /** @return a hash consistent with order-independent form equality */
    @Override
    public int hashCode() {
        return hash(status, forms, message);
    }

    /** @return a diagnostic representation, not a serialization format */
    @Override
    public String toString() {
        return "InflectionResult{status=" + status + ", forms=" + forms + ", message='" + message + "'}";
    }
}
