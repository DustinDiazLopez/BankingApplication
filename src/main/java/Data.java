import models.Account;
import models.User;
import repositories.AccountRepositoryImpl;
import repositories.UserRepositoryImpl;
import types.Role;
import utils.Hash;
import utils.Persist;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class Data {

    /**
     * Checks to see if the files exists
     */
    public static void check() {
        File users = new File("users");
        File accounts = new File("accounts");

        if (!users.exists()) {
            User u = new User();
            u.setId(UUID.fromString("90d57290-7018-404b-849e-1f4b191945ec"));
            u.setFirstName("Dustin");
            u.setLastName("User");
            u.setEmail("dustin.user@example.com");
            u.setUsername("dustin-user");
            u.setPassword(Hash.text("password"));
            u.setRole(Role.CUSTOMER);
            u.setDob(Date.from(Instant.ofEpochMilli(883612800000L)));
            u.setPhoneNumber("7874782095");

            User e = new User();
            e.setId(UUID.fromString("6894db3d-fe1a-4dbd-b243-8ffcb92e5b12"));
            e.setFirstName("Dustin");
            e.setLastName("Employee");
            e.setEmail("dustin.employee@example.com");
            e.setUsername("dustin-employee");
            e.setPassword(Hash.text("password"));
            e.setRole(Role.EMPLOYEE);
            e.setDob(Date.from(Instant.ofEpochMilli(883612800000L)));
            e.setPhoneNumber("7874782095");


            User a = new User();
            a.setId(UUID.fromString("4c3d77f4-86ee-4d9d-80f8-c44889d2923c"));
            a.setFirstName("Dustin");
            a.setLastName("Admin");
            a.setEmail("dustin.admin@example.com");
            a.setUsername("dustin-admin");
            a.setPassword(Hash.text("password"));
            a.setRole(Role.ADMIN);
            a.setDob(Date.from(Instant.ofEpochMilli(883612800000L)));
            a.setPhoneNumber("7874782095");

            ArrayList<User> _users = new ArrayList<>();
            _users.add(a);
            _users.add(e);
            _users.add(u);
            Persist.write("users", _users);
        }

        if (!accounts.exists()) {
            Persist.write("accounts", new ArrayList<Account>());
        }
    }

    /**
     * Loads the data to their appropriate repositories
     */
    public static void load() {
        UserRepositoryImpl.load();
        AccountRepositoryImpl.load();
    }

    /**
     * Dumps the current state of the objects into a file
     */
    public static void dump() {
        UserRepositoryImpl.dump();
        AccountRepositoryImpl.dump();
    }
}
