package de.dennis_kempf.squidward;

import java.util.*;
import java.util.concurrent.Executor;

/**
 * Implementation of {@link Automaton} whose sets of states and transitions are
 * immutable. This class is not truly immutable as the current state and the
 * enabled flag can change.
 * TODO: Implement this class in a thread safe manner
 */
public class ImmutableAutomaton implements Automaton {
    private final Executor executor;
    private final State initialState;
    private final Set<State> states;
    private final Set<Transition<?>> transitions;
    private State currentState;
    private boolean enabled;

    private ImmutableAutomaton(Builder builder) {
        this.executor = builder.executor;
        this.initialState = builder.initialState;
        this.states = new HashSet<>(builder.states);
        this.transitions = new LinkedHashSet<>(builder.transitions);
        this.currentState = null;
        this.enabled = false;
    }

    @Override
    public State getInitialState() {
        return initialState;
    }

    @Override
    public Optional<State> getCurrentState() {
        return Optional.ofNullable(currentState);
    }

    @Override
    public Set<State> getStates() {
        return Collections.unmodifiableSet(states);
    }

    @Override
    public Set<Transition<?>> getTransitions() {
        return Collections.unmodifiableSet(transitions);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void enable() {
        if (enabled) {
            throw new IllegalStateException("Automaton already enabled!");
        }
        if (!states.contains(initialState)) {
            throw new RuntimeException("Initial state must be in set of automaton states!");
        }
        currentState = initialState;
        enabled = true;
        executor.execute(() -> initialState.getEntryAction().ifPresent(State.Action::execute));
    }

    @Override
    public void post(Object event) {
        if (!enabled) {
            throw new IllegalStateException("Automaton must be enabled first!");
        }
        if (event == null) {
            throw new IllegalArgumentException("Event must not be null!");
        }
        executor.execute(() -> process(event));
    }

    private void process(Object event) {
        if (currentState == null) {
            throw new IllegalStateException("Current state is undefined! Automaton cannot process any events right now.");
        }
        for (Transition transition : transitions) {
            if (!transition.getSourceState().equals(currentState)) {
                continue;
            }
            if (!transition.getEventType().isInstance(event)) {
                continue;
            }
            //noinspection unchecked
            if (process(transition, event)) {
                return;
            }
        }
    }

    private <X> boolean process(Transition<X> transition, X event) {
        Transition.Guard<X> guard = transition.getGuard().orElse(null);
        if (guard != null && !guard.check(event)) {
            return false;
        }
        State destinationState = transition.getDestinationState();
        if (!states.contains(destinationState)) {
            throw new RuntimeException("Destination state must be in set of automaton states!");
        }
        if (destinationState != currentState) {
            currentState.getExitAction().ifPresent(State.Action::execute);
            currentState = null;
            transition.getAction().ifPresent(a -> a.execute(event));
            currentState = destinationState;
            currentState.getEntryAction().ifPresent(State.Action::execute);
        } else {
            transition.getAction().ifPresent(a -> a.execute(event));
        }
        return true;
    }

    /**
     * Builder for construction of an {@link ImmutableAutomaton}.
     */
    public static class Builder {
        private final Set<State> states = new HashSet<>();
        private final Set<Transition<?>> transitions = new LinkedHashSet<>();
        private State initialState;
        private Executor executor;

        /**
         * Adds the provided states to the automaton.
         * @param states to be added
         * @return current builder
         */
        public Builder addStates(Iterable<State> states) {
            for (State state : states) {
                addState(state);
            }
            return this;
        }

        /**
         * Adds the provided states to the automaton.
         * @param states to be added
         * @return current builder
         */
        public Builder addStates(State... states) {
            for (State state : states) {
                addState(state);
            }
            return this;
        }

        /**
         * Adds the provided state to the automaton.
         * @param state to be added
         * @return current builder
         */
        public Builder addState(State state) {
            if (state == null) {
                throw new IllegalArgumentException("State must not be null!");
            }
            this.states.add(state);
            return this;
        }

        /**
         * Adds a new state to the automaton. The state will only be added upon
         * invocation of {@link ImmutableState.Builder#build()}.
         * @return state builder
         */
        public ImmutableState.Builder addState() {
            return new ImmutableState.Builder() {
                @Override
                public ImmutableState build() {
                    ImmutableState state = super.build();
                    addState(state);
                    return state;
                }
            };
        }

        /**
         * Adds the provided transitions to the automaton.
         * @param transitions to be added
         * @return current builder
         */
        public Builder addTransitions(Iterable<Transition<?>> transitions) {
            for (Transition<?> transition : transitions) {
                addTransition(transition);
            }
            return this;
        }

        /**
         * Adds the provided transitions to the automaton.
         * @param transitions to be added
         * @return current builder
         */
        public Builder addTransitions(Transition<?>... transitions) {
            for (Transition transition : transitions) {
                addTransition(transition);
            }
            return this;
        }

        /**
         * Adds the provided transition to the automaton. Source and destination
         * states must have been added previously.
         * @param transition to be added
         * @return current builder
         */
        public Builder addTransition(Transition<?> transition) {
            if (transition == null) {
                throw new IllegalArgumentException("Provided transition must not be null!");
            }
            if (!this.states.contains(transition.getSourceState())) {
                throw new IllegalArgumentException("Source state must be in automaton states!");
            }
            if (!this.states.contains(transition.getDestinationState())) {
                throw new IllegalArgumentException("Destination state must be in automaton states!");
            }
            this.transitions.add(transition);
            return this;
        }

        /**
         * Adds a new transition to the automaton. The transition will only be
         * added upon invocation of {@link ImmutableTransition.Builder#build()}.
         * @return transition builder
         */
        public ImmutableTransition.Builder<?> addTransition() {
            return new ImmutableTransition.Builder<Object>() {
                @Override
                public ImmutableTransition<Object> build() {
                    ImmutableTransition<Object> transition = super.build();
                    addTransition(transition);
                    return transition;
                }
            };
        }

        /**
         * Sets the initial state of the automaton. May only be set once.
         * @param state the automaton will enter first
         * @return current builder
         */
        public Builder initialState(State state) {
            if (this.initialState != null) {
                throw new IllegalStateException("Initial state already set!");
            }
            if (state == null) {
                throw new IllegalArgumentException("Initial state must not be null!");
            }
            if (!this.states.contains(state)) {
                throw new IllegalArgumentException("Initial state must be in automaton states!");
            }
            this.initialState = state;
            return this;
        }

        /**
         * Sets the executor which processes all events. May only be set once.
         * @param executor to be used
         * @return current builder
         */
        public Builder executor(Executor executor) {
            if (this.executor != null) {
                throw new IllegalStateException("Executor already set!");
            }
            this.executor = executor;
            return this;
        }

        /**
         * Builds the automaton with the provided information. Initial state
         * must have been set. If no executor is explicitly specified, the
         * default immediate same-thread executor (Runnable::run) will be used.
         * @return built automaton
         */
        public ImmutableAutomaton build() {
            if (initialState == null) {
                throw new IllegalArgumentException("Initial state must not be null!");
            }
            if (executor == null) {
                executor = Runnable::run;
            }
            return new ImmutableAutomaton(this);
        }
    }
}
