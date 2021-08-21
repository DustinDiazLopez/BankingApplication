package repositories;

import models.User;
import utils.Persist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserRepositoryImpl implements UserRepository {

    private static ArrayList<User> users = new ArrayList<>();

    public static void load() {
        users = Persist.read("users", ArrayList.class);
    }

    public static void dump() {
        final boolean saved = Persist.write("users", users);
        if (!saved) System.err.println("Failed to save users");
    }

    @Override
    public User findByUsername(String username) {
        User result = null;
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                result = u;
                break;
            }
        }

        return result;
    }

    @Override
    public User findByEmail(String email) {
        User result = null;
        for (User u : users) {
            if (u.getEmail().equals(email)) {
                result = u;
                break;
            }
        }

        return result;
    }

    @Override
    public boolean emailExists(String email) {
        boolean result = false;
        for (User u : users) {
            if (u.getEmail().equals(email)) {
                result = true;
                break;
            }
        }

        return result;
    }

    @Override
    public boolean usernameExists(String username) {
        boolean result = false;
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                result = true;
                break;
            }
        }

        return result;
    }

    @Override
    public boolean create(User o) {
        users.add(o);
        return true;
    }

    @Override
    public User findById(UUID id) {
        User result = null;
        for (User u : users) {
            if (u.getId().equals(id)) {
                result = u;
                break;
            }
        }

        return result;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    @Override
    public boolean update(User o) {
        int idx = -1;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(o.getId())) {
                idx = i;
                break;
            }
        }

        if (idx >= 0) {
            users.set(idx, o);
            return true;
        }

        return false;
    }

    @Override
    public boolean delete(User o) {
        int idx = -1;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(o.getId())) {
                idx = i;
                break;
            }
        }

        if (idx >= 0) {
            final int prev = users.size();
            users.remove(idx);
            return prev != users.size();
        }

        return false;
    }
}
