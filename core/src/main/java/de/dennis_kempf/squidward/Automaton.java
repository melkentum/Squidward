package de.dennis_kempf.squidward;

import java.util.Optional;
import java.util.Set;

/**
 * Automaton with a finite set of {@link State}s and {@link Transition}s.
 */
public interface Automaton {
    /**
     * Returns the initial state of the automaton. When {@link #enable()} is
     * first called, this will be the new state and its entry action will be
     * invoked.
     * @return initial state
     */
    State getInitialState();

    /**
     * Returns the current state of the automaton. The current state may be
     * undefined (optional will be empty) if the automaton has not been enabled
     * yet or the automaton is currently executing a transition.
     * @return current state
     */
    Optional<State> getCurrentState();

    /**
     * Returns the finite set of states this automaton may be in.
     * @return set of available states
     */
    Set<State> getStates();

    /**
     * Return this finite set of transitions this automaton may take.
     * @return set of available transitions
     */
    Set<Transition<?>> getTransitions();

    /**
     * Returns whether the automaton has been enabled. This method may return
     * true, even if the initial state's entry action has not been executed yet.
     * @return true, if enabled, otherwise false
     */
    boolean isEnabled();

    /**
     * Enables the automaton. The current state will be set to the initial state
     * and the initial state's entry action will be queued for execution. This
     * method may only be called, if the automaton has not been enabled yet.
     */
    void enable();

    /**
     * Adds the provided event to the processing queue. This method may only
     * be called once the automaton has been enabled.
     * TODO: Add documentation on transition mechanism
     * @param event non-null
     */
    void post(Object event);
}
