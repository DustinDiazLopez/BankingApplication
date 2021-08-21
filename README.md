# Banking Application

## Technologies Used

* Maven
* Lombok
* Java 8
  * Annotations
  * Lambdas
  * Interfaces
  * Generics
  * Stream
  * Comparable
  * Serialization

## Features

- [x] Customers of the bank should be able to register with a username and password
- [x] Customers can apply to open an account.
- [x] Customers should be able to apply for joint accounts
- [x] Once the account is open, customers should be able to withdraw, deposit, and transfer funds between accounts
- [x] All basic validation should be done, such as trying to input negative amounts, overdrawing from accounts etc.
- [x] Employees of the bank should be able to view all of their customers information.
    - This includes:
        - [x] Account information
        - [x] Account balances
        - [x] Personal information
- [x] Employees should be able to approve/deny open applications for accounts
- [x] Bank admins should be able to view and edit all accounts
    - This includes:
        - [x] Approving/denying accounts
        - [x] withdrawing, depositing, transferring from all accounts
        - [x] canceling accounts
- [x] All information should be persisted using text files and serialization
- [x] All transactions should be logged

## Getting Started

Clone the repository:
```bash
git clone https://github.com/DustinDiazLopez/BankingApplication.git
```

Open the project in an IDE (preferably IntelliJ), and run the `App.java` file found in:
`src/main/java/App.java`

## License

This project uses the following license: [GNU General Public License v3.0](./LICENSE).
