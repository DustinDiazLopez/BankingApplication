package repositories;

import models.Account;
import types.AccountStatus;
import utils.Persist;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AccountRepositoryImpl implements AccountRepository {

    private static ArrayList<Account> accounts = new ArrayList<>();

    public static void load() {
        accounts = Persist.read("accounts", accounts.getClass());
    }

    public static void dump() {
        final boolean saved = Persist.write("accounts", accounts);
        if (!saved) System.err.println("Failed to save accounts");
    }

    @Override
    public List<Account> findByUser(UUID userId) {
        return accounts.stream()
                .filter((a) -> a.getHolders().stream().anyMatch((u) -> u.getId().equals(userId)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Account> findAllPendingAccounts() {
        return accounts.stream()
                .filter((a) -> a.getStatus().equals(AccountStatus.PENDING))
                .collect(Collectors.toList());
    }

    @Override
    public List<Account> finalAllJointAccounts() {
        return accounts.stream()
                .filter(Account::isJointAccount)
                .collect(Collectors.toList());
    }


    @Override
    public boolean create(Account o) {
        accounts.add(o);
        return true;
    }

    @Override
    public Account findById(UUID id) {
        Account result = null;
        for (Account u : accounts) {
            if (u.getId().equals(id)) {
                result = u;
                break;
            }
        }

        return result;
    }

    @Override
    public List<Account> findAll() {
        return new ArrayList<>(accounts);
    }

    @Override
    public boolean update(Account o) {
        int idx = -1;
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getId().equals(o.getId())) {
                idx = i;
                break;
            }
        }

        if (idx >= 0) {
            accounts.set(idx, o);
            return true;
        }

        return false;
    }

    @Override
    public boolean delete(Account o) {
        int idx = -1;
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getId().equals(o.getId())) {
                idx = i;
                break;
            }
        }

        if (idx >= 0) {
            final int prev = accounts.size();
            accounts.remove(idx);
            return prev != accounts.size();
        }

        return false;
    }
}
