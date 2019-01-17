package de.dennis_kempf.squidward.examples;

import java.util.Scanner;

public class ExampleUtils {
    private ExampleUtils() {

    }

    public static void delay(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e1) {
            Thread.currentThread().interrupt();
        }
    }

    public static String readLine() {
        return new Scanner(System.in).nextLine();
    }
}
