package de.dennis_kempf.squidward;

import java.util.Optional;

/**
 * State of an {@link Automaton}.
 */
public interface State {
    /**
     * Returns the optional entry action. This action will be executed when any
     * {@link Automaton} enters this state.
     * @return optional entry action
     */
    default Optional<Action> getEntryAction() {
        return Optional.empty();
    }

    /**
     * Returns the optional exit action. This action will be executed when any
     * {@link Automaton} leaves this state.
     * @return optional exit action
     */
    default Optional<Action> getExitAction() {
        return Optional.empty();
    }

    /**
     * Action that will be executed upon state entry or exit.
     */
    interface Action {
        /**
         * Executes the action. Called upon state entry or exit by an automaton.
         */
        void execute();
    }
}
