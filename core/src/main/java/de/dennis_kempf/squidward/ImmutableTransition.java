package de.dennis_kempf.squidward;

import java.util.Optional;

/**
 * Immutable implementation of {@link Transition}.
 */
public class ImmutableTransition<T> implements Transition<T> {
    private final State sourceState;
    private final State destinationState;
    private final Class<T> eventType;
    private final Guard<T> guard;
    private final Action<T> action;

    private ImmutableTransition(Builder<T> builder) {
        this.sourceState = builder.sourceState;
        this.destinationState = builder.destinationState;
        this.eventType = builder.eventType;
        this.guard = builder.guard;
        this.action = builder.action;
    }

    @Override
    public State getSourceState() {
        return sourceState;
    }

    @Override
    public State getDestinationState() {
        return destinationState;
    }

    @Override
    public Class<T> getEventType() {
        return eventType;
    }

    @Override
    public Optional<Guard<T>> getGuard() {
        return Optional.ofNullable(guard);
    }

    @Override
    public Optional<Action<T>> getAction() {
        return Optional.ofNullable(action);
    }

    /**
     * Builder for construction of an {@link ImmutableTransition}.
     */
    public static class Builder<T> {
        private State sourceState;
        private State destinationState;
        private Class<T> eventType;
        private Guard<T> guard;
        private Action<T> action;

        /**
         * Sets the source state of the transition. May only be set once.
         * @see Transition#getSourceState()
         * @param state source state
         * @return current builder
         */
        public Builder<T> from(State state) {
            if (this.sourceState != null) {
                throw new IllegalStateException("Source state already set!");
            }
            if (state == null) {
                throw new IllegalArgumentException("Source state must not be null!");
            }
            this.sourceState = state;
            return this;
        }

        /**
         * Sets the destination state of the transition. May only be set once.
         * @see Transition#getDestinationState()
         * @param state destination state
         * @return current builder
         */
        public Builder<T> to(State state) {
            if (this.destinationState != null) {
                throw new IllegalStateException("Destination state already set!");
            }
            if (state == null) {
                throw new IllegalArgumentException("Source state must not be null!");
            }
            this.destinationState = state;
            return this;
        }

        /**
         * Sets the event type that triggers this transition. May only be set
         * once.
         * @see Transition#getEventType()
         * @param type that triggers this transition
         * @param <X> event type that triggers this transition
         * @return current builder
         */
        public <X> Builder<X> on(Class<X> type) {
            if (this.eventType != null) {
                throw new IllegalStateException("Event type already set!");
            }
            if (type == null) {
                throw new IllegalArgumentException("Event type must not be null!");
            }
            Builder<X> builder = (Builder<X>) this;
            builder.eventType = type;
            return builder;
        }

        /**
         * Sets the guard of the transition. May only be set once.
         * @see Transition#getGuard()
         * @param guard that must be satisfied
         * @return current builder
         */
        public Builder<T> check(Guard<T> guard) {
            if (this.guard != null) {
                throw new IllegalStateException("Guard already set!");
            }
            this.guard = guard;
            return this;
        }

        /**
         * Sets the action of the transition. May only be set once.
         * @see Transition#getAction()
         * @param action that will be executed
         * @return current builder
         */
        public Builder<T> execute(Action<T> action) {
            if (this.action != null) {
                throw new IllegalStateException("Action already set!");
            }
            this.action = action;
            return this;
        }

        /**
         * Builds the transition with the provided information. Source and
         * destination state must be set. If the event type has not been
         * explicitly specified, {@link Object#getClass()} will be assumed.
         * @return built transition
         */
        public ImmutableTransition<T> build() {
            if (sourceState == null) {
                throw new IllegalArgumentException("Source state must not be null!");
            }
            if (destinationState == null) {
                throw new IllegalArgumentException("Destination state must not be null!");
            }
            if (eventType == null) {
                //noinspection unchecked
                eventType = (Class<T>)Object.class;
            }
            return new ImmutableTransition<>(this);
        }
    }
}
