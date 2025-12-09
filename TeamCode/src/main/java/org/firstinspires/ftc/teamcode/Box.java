package org.firstinspires.ftc.teamcode;

/// `Box<T>` wraps another object and provides methods to inspect and mutate it
/// - It is mainly used in `Task` implementations to allow lambdas
/// to effectively mutate local variables
/// - Examples are available in the inner `Examples` class
public class Box<T> {
    private T t;

    private Box(T t) {
        this.t = t;
    }

    public T get() {
        return t;
    }

    public void set(T t) {
        this.t = t;
    }

    /// Static factory method to create a `Box`
    public static <T> Box<T> of(T t) {
        return new Box<>(t);
    }

    @SuppressWarnings("unused")
    public static class Examples {
        static void simple() {
            // Use this to create a `Box` instead of the constructor
            Box<Integer> num = Box.of(3);
            assert num.get() == 3;

            num.set(num.get() + 30);
            assert num.get() == 33;
        }

        static void lambda() {
            Box<Integer> num = Box.of(0);

            Runnable f = () -> {
                System.out.println(num.get());

                // Java does NOT allow directly setting a local variable with `=` in a lambda
                num.set(num.get() + 1);
            };

            f.run(); // prints 0
            f.run(); // prints 1
        }
    }
}
