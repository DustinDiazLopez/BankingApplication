package services;

import models.Account;
import repositories.AccountRepositoryImpl;

import java.util.List;
import java.util.UUID;

public class AccountServiceImpl implements AccountService {
    private static final AccountRepositoryImpl repository = new AccountRepositoryImpl();

    @Override
    public boolean create(Account o) {
        return repository.create(o);
    }

    @Override
    public Account findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Account> findAll() {
        return repository.findAll();
    }

    @Override
    public boolean update(Account o) {
        return repository.update(o);
    }

    @Override
    public boolean delete(Account o) {
        return repository.delete(o);
    }

    @Override
    public List<Account> findByUser(UUID userId) {
        return repository.findByUser(userId);
    }

    @Override
    public List<Account> findAllPendingAccounts() {
        return repository.findAllPendingAccounts();
    }

    @Override
    public List<Account> finalAllJointAccounts() {
        return repository.finalAllJointAccounts();
    }
}
