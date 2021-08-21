package models;

import lombok.Data;
import types.AccountStatus;
import types.AccountType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class Account implements Serializable, Model<Account> {
    UUID id = UUID.randomUUID();
    ArrayList<User> holders = new ArrayList<>();
    ArrayList<Transaction> transactions = new ArrayList<>();
    AccountStatus status = AccountStatus.PENDING;
    AccountType type = AccountType.CHECKING;
    Double amount = 0d;

    public boolean isJointAccount() {
        return holders.size() > 1;
    }

    public boolean isApproved() {
        return status.equals(AccountStatus.APPROVED);
    }
    public boolean isTerminated() {
        return status.equals(AccountStatus.TERMINATED);
    }

    public boolean isPending() {
        return status.equals(AccountStatus.PENDING);
    }

    public boolean isRejected() {
        return status.equals(AccountStatus.REJECTED);
    }

    @Override
    public String show() {
        final StringBuilder holders = new StringBuilder();
        for (final User u : this.holders) {
            holders.append("\t- ");
            holders.append(u.show());
            holders.append("\n");
        }
        return String.format(
                "[%s - %s] $%.2f %s\n%s",
                type,
                status,
                amount,
                id,
                holders
        ).trim();
    }

    @Override
    public int compareTo(Account o) {
        return Double.compare(amount, o.amount);
    }

}
