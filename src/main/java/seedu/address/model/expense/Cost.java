package seedu.address.model.expense;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's address in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidCost(String)}
 */
public class Cost {

    public static final String MESSAGE_ADDRESS_CONSTRAINTS =
            "Cost should only take values in the following format: {int}.{digit}{digit}";

    /*
     * The first character of the address must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String COST_VALIDATION_REGEX = "(\\d+).(\\d)(\\d)";

    public final String value;

    /**
     * Constructs an {@code Cost}.
     *
     * @param address A valid address.
     */
    public Cost(String address) {
        requireNonNull(address);
        checkArgument(isValidCost(address), MESSAGE_ADDRESS_CONSTRAINTS);
        value = address;
    }

    /**
     * Returns true if a given string is a valid email.
     */
    public static boolean isValidCost(String test) {
        return test.matches(COST_VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Cost // instanceof handles nulls
                && value.equals(((Cost) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
