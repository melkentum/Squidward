package de.dennis_kempf.squidward.examples;

import de.dennis_kempf.squidward.Automaton;
import de.dennis_kempf.squidward.ImmutableAutomaton;
import de.dennis_kempf.squidward.State;

import static de.dennis_kempf.squidward.examples.ExampleUtils.readLine;

/**
 * Very simple example using only one state. Any non-empty input on the console
 * will be added to the phrase "Hello, (...)!".
 */
public class Greeter {
    public static void main(String[] args) {
        ImmutableAutomaton.Builder builder = new ImmutableAutomaton.Builder();

        // This automaton has only a single state
        State state = builder.addState().build();

        // This transition will process all incoming events
        builder.addTransition()
                .from(state).to(state) // Stay inside state
                .on(String.class) // Only care about events of type "String"
                .check(e -> !e.isEmpty()) // Only care about non-empty strings
                .execute(e -> System.out.println("Hello, " + e + "!"))
                .build();

        // Build automaton
        Automaton automaton = builder.initialState(state).build();

        // Enable automaton
        automaton.enable();

        while (true) {
            String line = readLine(); // Read single line from console
            automaton.post(line); // Forward read line to automaton
        }
    }
}
