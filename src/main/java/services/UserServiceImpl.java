package services;

import exceptions.LoginFailedException;
import exceptions.UserNotFoundException;
import types.Role;
import models.User;
import repositories.UserRepositoryImpl;
import utils.Util;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class UserServiceImpl implements UserService {
    private static final UserRepositoryImpl repository = new UserRepositoryImpl();

    public User login(String eu, String password) throws UserNotFoundException, LoginFailedException {
        final User user = eu.matches(Util.EMAIL_REGEX) ? repository.findByEmail(eu) : repository.findByUsername(eu);

        if (user == null) {
            throw new UserNotFoundException("Couldn't find user with specified credentials.");
        }

        if (!user.getPassword().equals(password)) {
            throw new LoginFailedException("Invalid credentials.");
        }

        return user;
    }

    public User register(String username, String email, Date dob, String password, String firstName, String lastName,
                         Role role, String phoneNumber) {
        final User user = new User();
        user.setRole(role);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setDob(dob);
        user.setPhoneNumber(phoneNumber);
        final boolean created = repository.create(user);
        return created ? user : null;
    }

    @Override
    public boolean create(User o) {
        return repository.create(o);
    }

    @Override
    public User findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    public boolean update(User o) {
        return repository.update(o);
    }

    @Override
    public boolean delete(User o) {
        return repository.delete(o);
    }

    @Override
    public User findByUsername(String username) {
        return repository.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public boolean emailExists(String email) {
        return repository.emailExists(email);
    }

    @Override
    public boolean usernameExists(String username) {
        return repository.usernameExists(username);
    }
}
