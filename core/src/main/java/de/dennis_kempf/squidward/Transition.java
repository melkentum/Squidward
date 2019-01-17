package de.dennis_kempf.squidward;

import java.util.Optional;

/**
 * Transition from one {@link State} of an {@link Automaton} to another.
 * @param <T> event type that triggers this transition
 */
public interface Transition<T> {
    /**
     * Returns the source state. The transition can only be taken if the source
     * state matches the current state of the automaton.
     * @return source state
     */
    State getSourceState();

    /**
     * Returns the destination state. If the transition is taken, the current
     * state of the automaton will be set to the destination state.
     * @return destination state
     */
    State getDestinationState();

    /**
     * Returns the type of events that may trigger this transition. Any event
     * which is an instance of this type may trigger the transition.
     * @return event type
     */
    Class<T> getEventType();

    /**
     * Returns optional guard that must be satisfied for this transition to be
     * taken.
     * @return optional guard
     */
    default Optional<Guard<T>> getGuard() {
        return Optional.empty();
    }

    /**
     * Returns optional action that will be executed if this transition is
     * taken. The action will be called after the current state is left but
     * before the destination state is entered. Thus the current state will be
     * undefined during the execution of this action.
     * @return optional action
     */
    default Optional<Action<T>> getAction() {
        return Optional.empty();
    }

    /**
     * Guard which has to be satisfied for an transition to be taken.
     * @param <T> event type that may be checked
     */
    interface Guard<T> {
        /**
         * Returns a guard that always answers with the same value.
         * @param result to be used as an answer
         * @return guard with a static answer
         */
        static Guard<Object> always(boolean result) {
            return o -> result;
        }

        /**
         * Returns a guard that will always pass.
         * @return always passing guard
         */
        static Guard<Object> pass() {
            return always(true);
        }

        /**
         * Returns a guard that will always fail.
         * @return always failing guard
         */
        static Guard<Object> fail() {
            return always(false);
        }

        /**
         * Checks whether the provided event satisfies the guard.
         * @param event to be checked
         * @return true, if satisfied, otherwise false
         */
        boolean check(T event);
    }

    /**
     * Action which will be executed if an transaction is taken.
     * @param <T> event type that may be consumed
     */
    interface Action<T> {
        /**
         * Executes the action. This method will be called after the current
         * state is left but before the destination state is entered. Thus the
         * current state will be undefined during the execution of this method.
         * @param event that triggered the transition
         */
        void execute(T event);
    }
}
