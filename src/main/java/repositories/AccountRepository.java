package repositories;

import models.Account;

import java.util.List;
import java.util.UUID;

public interface AccountRepository extends Crud<Account> {
    List<Account> findByUser(UUID userId);
    List<Account> findAllPendingAccounts();
    List<Account> finalAllJointAccounts();

}
