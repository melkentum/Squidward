package de.dennis_kempf.squidward;

import java.util.Optional;

/**
 * Immutable implementation of {@link State}.
 */
public class ImmutableState implements State {
    private final Action entryAction;
    private final Action exitAction;

    private ImmutableState(Builder builder) {
        this.entryAction = builder.entryAction;
        this.exitAction = builder.exitAction;
    }

    @Override
    public Optional<Action> getEntryAction() {
        return Optional.ofNullable(entryAction);
    }

    @Override
    public Optional<Action> getExitAction() {
        return Optional.ofNullable(exitAction);
    }

    /**
     * Builder for construction of an {@link ImmutableState}.
     */
    public static class Builder {
        private Action entryAction;
        private Action exitAction;

        /**
         * Sets the entry action of the state. May only be set once.
         * @param action that will be executed upon entry
         * @return current builder
         */
        public Builder whenEntered(Action action) {
            if (this.entryAction != null) {
                throw new IllegalStateException("Entry action already set!");
            }
            this.entryAction = action;
            return this;
        }

        /**
         * Sets the exit action of the state. May only be set once.
         * @param action that will be executed upon exit
         * @return current builder
         */
        public Builder whenExited(Action action) {
            if (this.exitAction != null) {
                throw new IllegalStateException("Exit action already set!");
            }
            this.exitAction = action;
            return this;
        }

        /**
         * Builds the transition with the provided information.
         * @return built state
         */
        public ImmutableState build() {
            return new ImmutableState(this);
        }
    }
}
