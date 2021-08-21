package repositories;

import models.User;

public interface UserRepository extends Crud<User> {
    User findByUsername(String username);
    User findByEmail(String email);
    boolean emailExists(String email);
    boolean usernameExists(String username);
}
