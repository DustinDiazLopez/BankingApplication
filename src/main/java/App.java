import models.User;

public class App {
    public static User user;
    public static boolean run = true;

    public static void main(String[] args) {
        final Prompt prompt = new Prompt();
        Data.check();
        Data.load();
        while (run) {
            System.out.println();
            if (App.user == null) {
                prompt.loginScreen();
            } else {
                prompt.homeScreen();
            }
            System.out.println();
        }
        Data.dump();
    }
}
