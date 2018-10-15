package seedu.address.model.budget;
//@@author winsonhys

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDateTime;
import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import seedu.address.commons.core.ComponentManager;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.model.budget.BudgetRestart;
import seedu.address.model.expense.Expense;
import seedu.address.storage.StorageManager;


/**
 * Represents maximum budget of an expense tracker
 * Guarantees: details are present and not null, field values are validated, mutable.
 */
public class Budget extends ComponentManager {
    public static final String MESSAGE_BUDGET_CONSTRAINTS =
        "Cost should only take values in the following format: {int}.{digit}{digit}";

    public static final String MESSAGE_NEXT_MONTH = String.format("It is the end of the month. Please set a new "
        + "budget");

    public static final String BUDGET_VALIDATION_REGEX = "(\\d+).(\\d)(\\d)";

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);


    private double budgetCap;
    private double currentExpenses;
    private LocalDateTime nextRecurrence;
    private long numberOfSecondsToRecurAgain;



    /**
     * Constructs a {@code Budget}
     *
     * @param budget a valid double
     */
    public Budget(String budget) {
        requireNonNull(budget);
        checkArgument(isValidBudget(budget), BUDGET_VALIDATION_REGEX);
        this.budgetCap = Double.parseDouble(budget);
        this.nextRecurrence = null;
        this.numberOfSecondsToRecurAgain = Long.MAX_VALUE;
        this.currentExpenses = 0.0;
    }

    /**
     * Constructs a {@code Budget} with modified current expenses
     * @param budget
     * @param currentExpenses
     */
    public Budget(double budget, double currentExpenses, LocalDateTime nextRecurrence,
                  long numberOfSecondsToRecurAgain) {
        this.budgetCap = budget;
        this.currentExpenses = currentExpenses;
        this.nextRecurrence = nextRecurrence;
        this.numberOfSecondsToRecurAgain = numberOfSecondsToRecurAgain;
    }

    /**
     * Returns true if a given string is a valid budget.
     */
    public static boolean isValidBudget(String test) {
        return test.matches(BUDGET_VALIDATION_REGEX);
    }


    /**
     * Modifies the current (@code Budget) to have a new value
     *
     * @param budget a valid double
     */
    public void modifyBudget(double budget) {
        this.budgetCap = budget;
    }

    /**
     * Returns the current date in which the budget is created
     *
     * @return a LocalDate object that consists of the most recent timestamp.
     */

    public LocalDateTime getNextRecurrence() {
        return this.nextRecurrence;
    }

    /**
     * Attemps to add expense
     * @param expense a valid expense of type double
     * @return true if expense is successfully added, false if adding expense will result in budget exceeding.
     */

    public boolean addExpense(double expense) {
        this.currentExpenses += expense;
        return this.currentExpenses <= this.budgetCap;
    }


    /**
     * Sets the recurrence frequency
     * @param seconds Number of seconds to recur again
     */
    public void setRecurrenceFrequency(long seconds) {
        this.numberOfSecondsToRecurAgain = seconds;
        if (this.nextRecurrence == null) {
            this.nextRecurrence = LocalDateTime.now().plusSeconds(seconds);
        }
    }

    public void removeExpense(Expense expense) {
        this.currentExpenses -= expense.getCost().getCostValue();
    }

    /**
     * Resets the total expense to 0
     */
    public void clearSpending() {
        this.currentExpenses = 0;
    }

    /**
     * Alters the current total expense
     * @param target valid expense in spending to be removed
     * @param editedExpense new expense to be added
     */
    public void alterSpending(Expense target, Expense editedExpense) {
        this.currentExpenses -= target.getCost().getCostValue();
        this.currentExpenses += editedExpense.getCost().getCostValue();
    }

    public double getBudgetCap() {
        return this.budgetCap;
    }

    public double getCurrentExpenses() {
        return this.currentExpenses;
    }

    @Override
    public boolean equals(Object budget) {
        Budget anotherBudget = (Budget) budget;
        return this.currentExpenses == anotherBudget.currentExpenses
            && this.budgetCap == anotherBudget.budgetCap
            && this.numberOfSecondsToRecurAgain == anotherBudget.numberOfSecondsToRecurAgain
            && this.nextRecurrence.equals(anotherBudget.nextRecurrence);
    }

    @Override
    public String toString() {
        return String.format("$%f", this.budgetCap);
    }

    /**
     * Updates the current budget with the new budget if it is the start of a new month. Does nothing if not
     * @param event an Event that is raised every time the app initializes to check for start of new month.
     */
    @Subscribe
    public void handleBudgetRestartEvent(BudgetRestart event) {
        if (this.nextRecurrence == null) {
            logger.info(LogsCenter.getEventHandlingLogMessage(event, "Budget has not been set."));
            return;
        }
        if (LocalDateTime.now().isAfter(this.nextRecurrence)) {
            this.nextRecurrence = LocalDateTime.now().plusSeconds(this.numberOfSecondsToRecurAgain);
            this.clearSpending();
            logger.info(LogsCenter.getEventHandlingLogMessage(event, "Budget has been restarted"));
        }

    }
}
