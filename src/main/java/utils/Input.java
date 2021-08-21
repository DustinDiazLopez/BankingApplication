package utils;

import java.io.Console;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Input {

    @FunctionalInterface
    public interface Validate<T> {
        boolean run(T o);
    }

    public static String line(final String message) {
        final Scanner scanner = new Scanner(System.in);
        String input = null;
        while (input == null || input.length() == 0) {
            System.out.printf("%s: ", message);
            input = scanner.nextLine().trim();
        }
        return input;
    }

    /**
     *
     * @param validate lambda function; return whether to keep asking for input
     */
    public static String line(final String message, final Validate<String> validate) {
        String input;
        do {
            input = line(message);
        } while (validate.run(input));
        return input;
    }

    public static String password(final String message) {
        try {
            final Console cons = System.console();
            if (cons != null) {
                final char[] password = cons.readPassword("[%s]", String.format("%s:", message));
                if (password != null) {
                    final String hashed = Hash.text(password);
                    Arrays.fill(password, ' ');
                    return hashed;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.err.println("WARNING: password will be visible!");
        return Hash.text(line(message));
    }

    public static Double number(final String message) {
        final Scanner scanner = new Scanner(System.in);
        String input = null;
        Double result = null;
        while (input == null) {
            System.out.printf("%s: ", message);
            input = scanner.nextLine().trim();

            if (input.length() > 0) {
                try {
                    result = Double.parseDouble(input);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    result = null;
                    input = null;
                }
            } else {
                result = null;
                input = null;
            }
        }

        return result;
    }

    public static Double number(final String message, final Validate<Double> validate) {
        Double input;
        do {
            input = number(message);
        } while (validate.run(input));
        return input;
    }

    public static Integer integer(final String message) {
        return Math.toIntExact(Math.round(Input.number(message)));
    }

    public static Character character(final String message) {
        return Input.line(message).trim().charAt(0);
    }

    public static Character characterLowerCase(final String message) {
        return Character.toLowerCase(character(message));
    }

    public static boolean confirm(final String message) {
        final char selection = Input.characterLowerCase(String.format("%s (y/n)", message));
        return selection == 'y';
    }

    public static Date date(final String message) {
        Date date;
        final DateFormat formatter = new SimpleDateFormat("d-M-y");

        while (true) {
            try {
                System.out.printf("%s:%n", message);
                final int month = integer("\tMonth");
                final int day = integer("\tDay");
                final int year = integer("\tyear");
                final String str = String.format("%d-%d-%d", day, month, year);
                date = formatter.parse(str);
                break;
            } catch (Exception e) {
                System.err.println("Try again -> " + e.getMessage());
            }
        }

        return date;
    }

    public static Date date(final String message, final Validate<Date> validate) {
        Date input;
        do {
            input = date(message);
        } while (validate.run(input));
        return input;
    }

    /**
     * This is a wrapper to the {@link #fromArray(String[])}
     * Ex:
     * size = 3
     * resulting selections: [1, 2, 3]
     * @param size size of the selection array to generate
     * @return returns the index location of the selected string
     */
    public static int fromArray(int size) {
        final String[] selections = new String[size];
        for (int i = 0; i < selections.length; i++) selections[i] = (i + 1) + "";
        return fromArray(selections);
    }

    /**
     *
     * @param selections options to select from
     * @return returns the index location of the selected string
     */
    public static int fromArray(String[] selections) {
        if (selections.length == 0) {
            throw new IllegalArgumentException("size of selections must be greater than zero (0)");
        }

        int idx = -1;
        while (idx < 0) {
            final String selection = line("Pick");
            for (int i = 0; i < selections.length; i++) {
                if (selections[i].equals(selection)) {
                    idx = i;
                    break;
                }
            }

            if (idx == -1) {
                System.err.println("Invalid selection");
            }
        }
        return idx;
    }
}
