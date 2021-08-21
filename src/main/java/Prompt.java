import exceptions.LoginFailedException;
import exceptions.UserNotFoundException;
import models.Account;
import models.Model;
import models.Transaction;
import models.User;
import services.AccountServiceImpl;
import services.UserServiceImpl;
import types.AccountStatus;
import types.AccountType;
import types.Role;
import types.TransactionType;
import utils.Input;
import utils.Log;
import utils.Util;

import java.util.*;
import java.time.Instant;
import java.util.stream.Collectors;

public class Prompt {

    private static final UserServiceImpl userService = new UserServiceImpl();
    private static final AccountServiceImpl accountService = new AccountServiceImpl();

    private <T extends Model<T>> void printModels(final List<T> arr) {
        for (int i = 0; i < arr.size(); i++) {
            System.out.printf("\t%d. %s%n", i + 1, arr.get(i).show());
        }
    }

    private void printTransactions(final Account selected) {
        System.out.println("Transactions for account:");
        System.out.println(selected.show() + ":");
        if (selected.getTransactions().size() > 0) {
            List<Transaction> ts = selected.getTransactions();
            Collections.sort(ts);
            ts.forEach((t) -> System.out.println("\t" + t.show()));
        } else {
            System.out.println("\n\tNO TRANSACTIONS");
        }
    }

    public void loginScreen() {
        System.out.println("Login or Apply for an Account");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("q. Exit");

        final char selection = Input.character("Selection");

        switch (selection) {
            case '1':
                login();
                break;
            case '2':
                register();
                break;
            case 'q':
                exit();
                break;
            default:
                System.err.println("Invalid selection.");
                break;
        }
    }

    public void homeScreen() {
        System.out.printf("Hello, %s!%n", App.user.getFirstName());
        System.out.printf("Role: %s%n", App.user.getRole());

        switch (App.user.getRole()) {
            case CUSTOMER:
                userHome();
                break;
            case EMPLOYEE:
            case ADMIN:
                authorizedHome();
                break;
            default:
                break;
        }
    }

    public void userHome() {
        System.out.println("1. My Accounts");
        System.out.println("2. Apply for an account");
        System.out.println("3. Apply for a joint account");
        System.out.println("4. Logout");
        System.out.println("q. Exit");

        final char selection = Input.character("Selection");

        switch (selection) {
            case '1':
                myAccounts();
                break;
            case '2':
                openAccount();
                break;
            case '3':
                openJointAccount();
                break;
            case '4':
                logout();
                break;
            case 'q':
                exit();
                break;
            default:
                System.err.println("Invalid selection.");
                break;
        }
    }

    private void myAccounts() {
        final List<Account> accounts = accountService.findByUser(App.user.getId());
        if (accounts.size() > 0) {
            printModels(accounts);
            final int idx = Input.fromArray(accounts.size());
            final Account selected = accounts.get(idx);

            if (selected.isApproved()) {
                System.out.println("0. View Transactions");
                System.out.println("1. Deposit");
                System.out.println("2. Withdraw");
                System.out.println("3. Transfer to an internal account");
                System.out.println("4. Transfer between your accounts");
                System.out.println("5. Go back");
                System.out.println("6. Logout");
                System.out.println("q. Exit");

                final char selection = Input.character("Selection");

                switch (selection) {
                    case '0':
                        printTransactions(selected);
                        break;
                    case '1':
                        deposit(selected);
                        break;
                    case '2':
                        withdraw(selected);
                        break;
                    case '3':
                        internalTransfer(selected);
                        break;
                    case '4':
                        transferBetweenAccounts(accounts, idx);
                        break;
                    case '5':
                        accounts.clear();
                        userHome();
                        break;
                    case '6':
                        accounts.clear();
                        logout();
                        break;
                    case 'q':
                        exit();
                        break;
                    default:
                        System.err.println("Invalid selection.");
                        break;
                }
            } else {
                System.err.println("Your account is " + selected.getStatus());
            }
        } else {
            System.out.println("Apply for an account!");
        }
    }

    private void transferBetweenAccounts(List<Account> accounts, int fromIdx) {
        final Account selected = accounts.get(fromIdx);
        accounts.remove(fromIdx);
        accounts = accounts.stream().filter(Account::isApproved).collect(Collectors.toList());

        if (accounts.size() > 0) {
            System.out.println("Select the account to transfer to:");
            printModels(accounts);
            final int toIdx = Input.fromArray(accounts.size());
            final Account to = accounts.get(toIdx);

            final double amount = Input.number("Enter amount to withdraw", (n) -> {
                if (n < 0) {
                    System.err.println("Must be a positive value");
                    return true;
                }

                if ((selected.getAmount() - n) < 0) {
                    System.err.printf("Account does not have enough funds to withdraw $%.2f%n", n);
                    return true;
                }
                return false;
            });

            final String note = String.format(
                    "%s (%s) internal transfer - between accounts: $%.2f from '%s' to '%s'",
                    App.user.getUsername(), App.user.getRole(), amount, selected.getId(), to.getId()
            );

            System.out.printf(
                    "FROM %s: $%.2f -> $%.2f%n",
                    selected.getId(), selected.getAmount(), selected.getAmount() - amount
            );

            System.out.printf(
                    "TO %s: $%.2f -> $%.2f%n",
                    to.getId(), to.getAmount(), to.getAmount() + amount
            );

            final Transaction transaction = new Transaction();
            transaction.setAccount(selected.getId());
            transaction.setType(TransactionType.TRANSFER);
            transaction.setAmount(amount);
            transaction.setTo(to.getId());
            transaction.setDescription(note);

            selected.setAmount(selected.getAmount() - amount);
            selected.getTransactions().add(transaction);

            to.setAmount(to.getAmount() + amount);
            to.getTransactions().add(transaction);

            accountService.update(selected);
            accountService.update(to);
            System.out.println(transaction.show());
        } else {
            System.err.println("You don't have other accounts");
        }
    }

    private void internalTransfer(Account selected) {

        final UUID accountId = UUID.fromString(Input.line("Enter the id of the account to transfer to", (id) -> {
            try {
                if (accountService.findById(UUID.fromString(id)) == null) {
                    System.err.println("This account doesn't seem to exist.");
                    return true;
                }
                return false;
            } catch (Exception e) {
                System.err.println("Invalid input.");
                return true;
            }
        }));

        final Account to = accountService.findById(accountId);

        final double amount = Input.number("Enter amount to withdraw: $", (n) -> {
            if (n < 0) {
                System.err.println("Must be a positive value");
                return true;
            }

            if ((selected.getAmount() - n) < 0) {
                System.err.printf("Account does not have enough funds to withdraw $%.2f%n", n);
                return true;
            }
            return false;
        });

        final String note = String.format(
                "%s (%s) internal transfer: $%.2f from '%s' to '%s'",
                App.user.getUsername(), App.user.getRole(), amount, selected.getId(), to.getId()
        );

        System.out.printf(
                "FROM %s: $%.2f -> $%.2f%n",
                selected.getId(), selected.getAmount(), selected.getAmount() - amount
        );

        System.out.printf(
                "TO %s: $%.2f -> $%.2f%n",
                to.getId(), to.getAmount(), to.getAmount() + amount
        );

        final Transaction transaction = new Transaction();
        transaction.setAccount(selected.getId());
        transaction.setType(TransactionType.TRANSFER);
        transaction.setAmount(amount);
        transaction.setTo(to.getId());
        transaction.setDescription(note);

        selected.setAmount(selected.getAmount() - amount);
        selected.getTransactions().add(transaction);

        to.setAmount(to.getAmount() + amount);
        to.getTransactions().add(transaction);

        accountService.update(selected);
        accountService.update(to);
        System.out.println(transaction.show());
    }

    private void withdraw(Account selected) {
        adminWithdraw(selected);
    }

    private void deposit(Account selected) {
        adminDeposit(selected);
    }

    private void openAccount() {
        System.out.println("Open a:");
        System.out.println("1. Checking account");
        System.out.println("2. Savings account");
        System.out.println("c. Cancel");
        switch (Input.characterLowerCase("Select")) {
            case '1':
                final Account checking = new Account();
                checking.getHolders().add(App.user);
                if (Input.confirm("Open checking account")) {
                    System.out.println("Please wait...");
                    accountService.create(checking);
                    System.out.println(checking.show());
                    System.out.println("The account was created.");
                    System.out.println("Current account status: " + checking.getStatus());
                    System.out.println("Please allow some time until your account is approved");
                } else {
                    System.err.println("Canceled the creation of the checking account");
                }
                break;
            case '2':
                final Account savings = new Account();
                savings.setType(AccountType.SAVING);
                savings.getHolders().add(App.user);
                if (Input.confirm("Open savings account")) {
                    System.out.println("Please wait...");
                    accountService.create(savings);
                    System.out.println(savings.show());
                    System.out.println("The account was created.");
                    System.out.println("Current account status: " + savings.getStatus());
                    System.out.println("Please allow some time until your account is approved");
                } else {
                    System.err.println("Canceled the creation of the savings account");
                }
                break;
            case 'c':
                myAccounts();
                break;
            default:
                break;
        }
    }

    private void openJointAccount() {
        final Account account = new Account();
        final ArrayList<User> holders = new ArrayList<>();
        holders.add(App.user);

        System.out.println("Enter the username or email of the other users in which you wish to open the joint account with:");
        System.out.println("Type the word 'STOP' when you are finished");
        do {
            final String eu = Input.line("Username or Email");

            if (eu.equals("STOP")) {
                break;
            }

            final User found = eu.matches(Util.EMAIL_REGEX)
                    ? userService.findByEmail(eu)
                    : userService.findByUsername(eu);

            if (found != null) {
                if (Input.confirm("Would you like to add " + found.getUsername())) {
                    holders.add(found);
                }
            } else {
                System.err.println(eu + " does not exist.");
            }
        } while (true);

        System.out.println("Number of holders: " + holders.size());
        holders.forEach((u) -> System.out.println("\t- " + u.getUsername()));

        System.out.println("Open a:");
        System.out.println("1. Checking account");
        System.out.println("2. Savings account");
        final char type = Input.characterLowerCase("Select");

        account.setHolders(holders);
        if (type == '2') {
            account.setType(AccountType.SAVING);
        }

        System.out.println("Account type: " + account.getType());

        if (Input.confirm("Open account")) {
            System.out.println("Please wait...");
            accountService.create(account);
            System.out.println(account.show());
            System.out.println("The account was created.");
            System.out.println("Current account status: " + account.getStatus());
            System.out.println("Please allow some time until your account is approved");
        } else {
            System.err.printf("Canceled the creation of the joint %s account", account.getType().toString().toLowerCase());
        }
    }

    public void authorizedHome() {
        System.out.println("1. Pending Accounts");
        System.out.println("2. Accounts");
        System.out.println("3. Logout");
        System.out.println("q. Exit");

        final char selection = Input.characterLowerCase("Selection");

        switch (selection) {
            case '1':
                pendingAccounts();
                break;
            case '2':
                allAccounts();
                break;
            case '3':
                logout();
                break;
            case 'q':
                exit();
                break;
            default:
                System.err.println("Invalid selection.");
                break;
        }
    }

    private void allAccounts() {
        final List<Account> accounts = accountService.findAll();
        printModels(accountService.findAll());

        if (accounts.size() > 0) {
            final int selectedIdx = Input.fromArray(accounts.size());
            final Account selected = accounts.get(selectedIdx);
            System.out.println("Select an action for:");
            System.out.printf("[%s]%n", selected.show());

            System.out.println("0. View Transactions");
            switch (selected.getStatus()) {
                case TERMINATED:
                case REJECTED:
                    if (App.user.isAdmin()) {
                        System.out.println("r. Restore account (account will be set to PENDING)");
                        System.out.println("R. Restore and approve account");
                    } else {
                        System.out.println("-- No Action Available --");
                    }
                    break;
                case PENDING:
                    System.out.println("y. Approve account");
                    System.out.println("n. Reject account");
                    break;
                case APPROVED:
                    if (App.user.isAdmin()) {
                        System.out.println("1. Deposit");
                        System.out.println("2. Withdraw");
                        System.out.println("3. Transfer");
                        System.out.println("x. Terminate account");
                    } else {
                        System.out.println("-- No Action Available --");
                    }
                    break;
                default:
                    System.out.println("-- No Action Available --");
                    break;
            }

            System.out.println("c. Cancel");
            System.out.println("h. Return Home");

            final char action = Input.character("Action");
            switch (action) {
                case '0':
                    printTransactions(selected);
                    break;
                case 'c':
                    accounts.clear();
                    allAccounts();
                    break;
                case 'h':
                    accounts.clear();
                    homeScreen();
                    break;
                default:
                    if (App.user.isAdmin()) {
                        switch (selected.getStatus()) {
                            case TERMINATED:
                            case REJECTED:
                                switch (action) {
                                    case 'r':
                                        selected.setStatus(AccountStatus.PENDING);
                                        accountService.update(selected);
                                        System.out.println("Restored account to pending");
                                        break;
                                    case 'R':
                                        selected.setStatus(AccountStatus.APPROVED);
                                        accountService.update(selected);
                                        System.out.println("Restored and approved account");
                                        break;
                                    default:
                                        System.err.println("Invalid selection.");
                                        break;
                                }
                                break;
                            case PENDING:
                                switch (action) {
                                    case 'y':
                                        selected.setStatus(AccountStatus.APPROVED);
                                        accountService.update(selected);
                                        System.out.println("Approved account");
                                        break;
                                    case 'n':
                                        selected.setStatus(AccountStatus.REJECTED);
                                        accountService.update(selected);
                                        System.out.println("Rejected account");
                                        break;
                                    default:
                                        System.err.println("Invalid selection.");
                                        break;
                                }
                                break;
                            case APPROVED:
                                switch (action) {
                                    case '1':
                                        adminDeposit(selected);
                                        break;
                                    case '2':
                                        adminWithdraw(selected);
                                        break;
                                    case '3':
                                        adminTransfer(accounts, selected);
                                        break;
                                    case 'x':
                                        adminTerminate(selected);
                                        break;
                                    default:
                                        System.err.println("Invalid selection.");
                                        break;
                                }
                                break;
                            default:
                                System.out.println("-- No Action --");
                                break;
                        }
                    } else if (App.user.isEmployee()) {
                        switch (selected.getStatus()) {
                            case PENDING:
                                switch (action) {
                                    case 'y':
                                        selected.setStatus(AccountStatus.APPROVED);
                                        accountService.update(selected);
                                        System.out.println("Approved account");
                                        break;
                                    case 'n':
                                        selected.setStatus(AccountStatus.REJECTED);
                                        accountService.update(selected);
                                        System.out.println("Rejected account");
                                        break;
                                    default:
                                        System.err.println("Invalid selection.");
                                        break;
                                }
                                break;
                            case TERMINATED:
                            case REJECTED:
                            case APPROVED:
                            default:
                                System.out.println("-- No Action --");
                                break;
                        }
                    } else {
                        Log.forbidden(App.user.toString());
                    }
                    break;
            }
        } else {
            System.err.println("There are no accounts.");
        }
    }

    //TODO:
    private void adminTerminate(Account selected) {
        selected.setStatus(AccountStatus.TERMINATED);
        accountService.update(selected);
        System.out.println("Account was terminated.");
    }

    //TODO:
    private void adminTransfer(List<Account> accounts, Account selected) {
        System.out.println("Pick an account to transfer to:");
        printModels(accounts);
        final int selectedIdx = Input.fromArray(accounts.size());
        final Account to = accounts.get(selectedIdx);

        final double amount = Input.number("Enter amount to withdraw", (n) -> {
            if (n < 0) {
                System.err.println("Must be a positive value");
                return true;
            }

            if ((selected.getAmount() - n) < 0) {
                System.err.printf("Account does not have enough funds to withdraw $%.2f%n", n);
                return true;
            }
            return false;
        });

        final String note = String.format(
                "%s (%s) transferred $%.2f from '%s' to '%s'",
                App.user.getUsername(), App.user.getRole(), amount, selected.getId(), to.getId()
        );

        System.out.printf(
                "FROM %s: $%.2f -> $%.2f%n",
                selected.getId(), selected.getAmount(), selected.getAmount() - amount
        );

        System.out.printf(
                "TO %s: $%.2f -> $%.2f%n",
                to.getId(), to.getAmount(), to.getAmount() + amount
        );

        final Transaction transaction = new Transaction();
        transaction.setAccount(selected.getId());
        transaction.setType(TransactionType.TRANSFER);
        transaction.setAmount(amount);
        transaction.setTo(to.getId());
        transaction.setDescription(note);

        selected.setAmount(selected.getAmount() - amount);
        selected.getTransactions().add(transaction);

        to.setAmount(to.getAmount() + amount);
        to.getTransactions().add(transaction);

        accountService.update(selected);
        accountService.update(to);
        System.out.println(transaction.show());
    }

    //TODO:
    private void adminWithdraw(Account selected) {
        final double amount = Input.number("Enter amount to withdraw: $", (n) -> {
            if (n < 0) {
                System.err.println("Must be a positive value");
                return true;
            }

            if ((selected.getAmount() - n) < 0) {
                System.err.printf("You don't have enough funds to withdraw $%.2f%n", n);
                return true;
            }
            return false;
        });
        final String note = String.format(
                "%s (%s) withdrew $%.2f from account id '%s'",
                App.user.getUsername(), App.user.getRole(), amount, selected.getId()
        );

        System.out.printf("Before: $%.2f%n", selected.getAmount());
        final Transaction transaction = new Transaction();
        transaction.setAccount(selected.getId());
        transaction.setType(TransactionType.WITHDRAW);
        transaction.setAmount(amount);
        transaction.setDescription(note);

        selected.setAmount(selected.getAmount() - amount);
        selected.getTransactions().add(transaction);

        accountService.update(selected);
        System.out.printf("After: $%.2f%n", accountService.findById(selected.getId()).getAmount());
        System.out.println(transaction.show());
    }

    //TODO:
    private void adminDeposit(Account selected) {
        final double amount = Input.number("Enter amount to deposit: $", (n) -> n < 0);
        final String note = String.format(
                "%s (%s) deposited $%.2f to account id '%s'",
                App.user.getUsername(), App.user.getRole(), amount, selected.getId()
        );

        System.out.printf("Before: $%.2f%n", selected.getAmount());
        final Transaction transaction = new Transaction();
        transaction.setAccount(selected.getId());
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(amount);
        transaction.setDescription(note);

        selected.setAmount(selected.getAmount() + amount);
        selected.getTransactions().add(transaction);

        accountService.update(selected);
        System.out.printf("After: $%.2f%n", accountService.findById(selected.getId()).getAmount());
        System.out.println(transaction.show());
    }

    private void pendingAccounts() {
        final List<Account> pending = accountService.findAllPendingAccounts();
        printModels(pending);
        if (pending.size() > 0) {
            final int selectedIdx = Input.fromArray(pending.size());
            final Account selected = pending.get(selectedIdx);
            System.out.println("Select an action for:");
            System.out.printf("[%s]%n", selected.show());
            System.out.println("1. Approve");
            System.out.println("2. Reject");
            System.out.println("c. Cancel");
            System.out.println("h. Return Home");

            final char action = Input.characterLowerCase("Action");
            switch (action) {
                case '1':
                    selected.setStatus(AccountStatus.APPROVED);
                    accountService.update(selected);
                    System.out.println("Account approved.");
                    break;
                case '2':
                    selected.setStatus(AccountStatus.REJECTED);
                    accountService.update(selected);
                    System.out.println("Account rejected.");
                    break;
                case 'c':
                    pending.clear();
                    pendingAccounts();
                    break;
                case 'h':
                    pending.clear();
                    homeScreen();
                    break;
                default:
                    System.err.println("Invalid selection.");
                    break;
            }
        } else {
            System.err.println("There are not pending accounts.");
        }
    }

    public void register() {
        while (true) {
            try {
                final String firstName = Input.line("First name");
                final String lastName = Input.line("Last name");
                final String phoneNumber = Input.line("Phone number");
                final String email = Input.line("Email", (e) -> {
                    final boolean exists = userService.emailExists(e);
                    if (exists) {
                        System.out.println("Email already taken.");
                        return true;
                    }

                    if (!e.matches(Util.EMAIL_REGEX)) {
                        System.err.println("Doesn't seem to be an email address.");
                        return true;
                    }
                    return false;
                });
                final String username = Input.line("Username", (u) -> {
                    final boolean exists = userService.usernameExists(u);
                    if (exists) {
                        System.out.println("Username already taken.");
                        return true;
                    }
                    return false;
                });
                final Date dob = Input.date("Date of birth", (date) -> {
                    final int years = Util.getDiffYears(date, Date.from(Instant.now()));
                    System.out.println("Your age is " + years);
                    final boolean valid = years >= 18 && years < 130;
                    if (!valid) {
                        if (years < 18) {
                            System.err.println("You need to apply for a joint account with your guardian.");
                        } else {
                            System.err.println("You exceeded the max age limit. Contact Support.");
                        }
                        return true;
                    }
                    return false;
                });
                final String pwd = Input.password("Password");
                final User user = userService.register(username, email, dob, pwd, firstName, lastName, Role.CUSTOMER,
                        phoneNumber);
                if (user != null) {
                    App.user = user;
                    break;
                } else {
                    System.err.println("Failed to register.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void login() {
        while (true) {
            try {
                final String eu = Input.line("Email or Username");
                final String pwd = Input.password("Password");
                final User user = userService.login(eu, pwd);
                if (user != null) {
                    App.user = user;
                    break;
                } else {
                    System.err.println("Failed to login.");
                }
            } catch (UserNotFoundException | LoginFailedException e) {
                System.err.println(e.getMessage());
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void logout() {
        final boolean logout = Input.confirm("Are you sure you want to logout?");

        if (logout) {
            App.user = null;
            System.out.println("Logged out.");
        } else {
            System.out.println("Canceled logout.");
        }
    }

    public void exit() {
        final boolean exit = Input.confirm("Are you sure you want to exit?");

        if (exit) {
            System.out.print("Goodbye!");
            App.user = null;
            App.run = false;
        } else {
            System.out.println("Canceled exit");
        }
    }
}
