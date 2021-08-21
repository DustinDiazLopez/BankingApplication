package models;

import lombok.Data;
import types.TransactionType;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Data
public class Transaction implements Serializable, Model<Transaction> {
    UUID id = UUID.randomUUID();
    Date date = Date.from(Instant.now());
    /** Account that made the transaction */
    UUID account;
    TransactionType type;
    /** Only available for {@link TransactionType#TRANSFER} */
    UUID to = null;
    Double amount;
    String description = null;

    @Override
    public String show() {
        return String.format(
                "[%s] $%.2f (%s) %s -> %s %s",
                type,
                amount,
                date,
                account,
                to == null ? account : to,
                description == null ? "" : String.format("(Note: %s)", description)
        ).trim();
    }

    @Override
    public int compareTo(Transaction o) {
        return o.date.compareTo(date);
    }
}
