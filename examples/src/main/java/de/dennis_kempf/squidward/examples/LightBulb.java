package de.dennis_kempf.squidward.examples;

import de.dennis_kempf.squidward.Automaton;
import de.dennis_kempf.squidward.ImmutableAutomaton;
import de.dennis_kempf.squidward.State;

import java.util.Scanner;

/**
 * Very simple example controlling a two-state light bulb using the console.
 * The light bulb will turn on when "on" is entered while in "off" state and
 * turn off when "off" is entered while in "on" state.
 */
public class LightBulb {
    public static void main(String[] args) {
        ImmutableAutomaton.Builder builder = new ImmutableAutomaton.Builder();

        // Off-State
        State off = builder.addState().whenEntered(() -> {
            System.out.println("The light bulb has been turned off!");
        }).build();

        // On-State
        State on = builder.addState().whenEntered(() -> {
            System.out.println("The light bulb has been turned on!");
        }).build();

        // Transition Off -> On
        builder.addTransition()
                .from(off).to(on)
                .check("on"::equals) // Checking whether event equals "on"
                .build();

        // Transition On -> Off
        builder.addTransition()
                .from(on).to(off)
                .check("off"::equals) // Checking whether event equals "off"
                .build();

        // Build automaton
        Automaton automaton = builder.initialState(off).build();

        // Enable automaton
        automaton.enable();

        // Read console input and control light bulb
        while (true) {
            String line = readLine(); // Read single line from console
            automaton.post(line); // Forward read line to automaton
        }
    }

    private static String readLine() {
        return new Scanner(System.in).nextLine();
    }
}
