package io.github.wysohn.triggerreactor.core.manager.trigger.share;

/**
 * Example class used for testing reflection methods
 */
public class ExampleClass {
    public int marker;
    public ExampleClass(int x) {
        this.marker = 0;
    }

    public ExampleClass(Integer x) {
        this.marker = 1;
    }

    public ExampleClass(Number x) {
        this.marker = 2;
    }

    public ExampleClass(Object x) {
        this.marker = 3;
    }
    public static int foo;
    public static boolean bar;

    public static void reset() {
        foo = 42;
        bar = false;
    }

    public static int baz(int merp) {
        return merp * 2;
    }

    public static String add(String a, String b) {
        return a + b;
    }
}