package de.dennis_kempf.squidward.examples;

import de.dennis_kempf.squidward.Automaton;
import de.dennis_kempf.squidward.ImmutableAutomaton;
import de.dennis_kempf.squidward.State;

import static de.dennis_kempf.squidward.examples.ExampleUtils.delay;
import static de.dennis_kempf.squidward.examples.ExampleUtils.readLine;

/**
 * This example showcases the basic usage of enumeration values as states and
 * events. All reactions are modelled as transition actions. After entering
 * "start" the engine will start to crank. This takes a couple of seconds.
 * While the engine is running it can be stopped by typing "stop".
 */
public class Engine {
    // Make automaton accessible to transition actions
    private static Automaton automaton;

    public static void main(String[] args) {
        ImmutableAutomaton.Builder builder = new ImmutableAutomaton.Builder();

        // Add all enum values as states
        builder.addStates(EngineState.values());

        // Transition OFF -> CRANKING
        builder.addTransition()
                .from(EngineState.OFF).to(EngineState.CRANKING)
                .check("start"::equals)
                .execute(e -> {
                    System.out.println("Turning engine on...");
                    new Thread(() -> {
                        delay(2500); // Realistic simulation of engine cranking
                        automaton.post(EngineEvent.STARTED);
                    }).start();
                })
                .build();

        // Transition CRANKING -> RUNNING
        builder.addTransition()
                .from(EngineState.CRANKING).to(EngineState.RUNNING)
                .check(EngineEvent.STARTED::equals)
                .execute(e -> System.out.println("Engine is now running!"))
                .build();

        // Transition RUNNING -> OFF
        builder.addTransition()
                .from(EngineState.RUNNING).to(EngineState.OFF)
                .check("stop"::equals)
                .execute(e -> System.out.println("Engine stopped!"))
                .build();

        automaton = builder.initialState(EngineState.OFF).build();
        automaton.enable();

        while (true) {
            String line = readLine();
            automaton.post(line);
        }
    }

    private enum EngineState implements State {
        OFF, CRANKING, RUNNING
    }

    private enum EngineEvent {
        STARTED
    }
}
