package models;

import lombok.Data;
import types.Role;
import utils.Util;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Data
public class User implements Serializable, Model<User> {
    private UUID id = UUID.randomUUID();
    private String username;
    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String password;
    private Date dob;
    private Role role;

    public int age() {
        return Util.getDiffYears(dob, Date.from(Instant.now()));
    }

    public boolean isAdmin() {
        return role.equals(Role.ADMIN);
    }

    public boolean isEmployee() {
        return role.equals(Role.EMPLOYEE);
    }

    public boolean isCustomer() {
        return role.equals(Role.CUSTOMER);
    }

    @Override
    public String show() {
        return String.format(
                "%s, %s is %d old (%s, %s, %s)",
                firstName,
                lastName,
                age(),
                username,
                email,
                phoneNumber
        );
    }

    @Override
    public int compareTo(User o) {
        return this.username.compareToIgnoreCase(o.username);
    }
}
