import models.Account;
import models.User;
import utils.Persist;

import java.util.ArrayList;

public class ShowData {
    public static void main(String[] args) {
        ArrayList<User> users = new ArrayList<>();
        users =  Persist.read("users", users.getClass());
        ArrayList<Account> accounts = new ArrayList<>();
        accounts =  Persist.read("accounts", accounts.getClass());

        if (users != null) {
            users.forEach(System.out::println);
            System.out.printf("Number of users: %d%n", users.size());
        } else {
            System.err.println("Users was null!!!");
        }

        if (accounts != null) {
            accounts.forEach(System.out::println);
            System.out.printf("Number of accounts: %d%n", accounts.size());
        } else {
            System.err.println("Accounts was null!!!");
        }
    }
}
