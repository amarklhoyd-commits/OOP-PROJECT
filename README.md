# Library Management System (Java OOP)

A beginner-friendly desktop application using Java Swing, JDBC, and MySQL.

## Features
- Add, view, search, update, and delete books
- Register and search library members
- Borrow regular books and calculate a 14-day due date
- Return borrowed books
- View borrowing history
- Validate required fields, email format, and numeric IDs
- Prevent borrowing unavailable/reference books and deleting borrowed books

## Requirements
- Java 17+
- Maven
- MySQL Server
- An IDE such as IntelliJ IDEA, NetBeans, or VS Code

## Setup
1. Start your MySQL server.
2. Open MySQL Workbench or a MySQL terminal.
3. Run the full `database.sql` script.
4. Open `src/main/java/com/library/DatabaseConnection.java`.
5. Replace `YOUR_MYSQL_PASSWORD` with your MySQL password. If your MySQL username is not `root`, update `USER` too.
6. Open a terminal in the project folder and run:
   ```bash
   mvn clean compile
   ```
7. Run `com.library.Main` from your IDE. Maven downloads MySQL Connector/J automatically.
8. Add a few books and at least one member before testing borrow/return.

## How to run without an IDE
After Maven has downloaded dependencies, easiest for a beginner is to run `com.library.Main` from the IDE. If using a terminal, you can run:
```bash
mvn dependency:build-classpath -Dmdep.outputFile=classpath.txt
mvn package
java -cp "target/classes;$(cat classpath.txt)" com.library.Main
```
The classpath command above is written for a Unix-like shell; Windows Command Prompt uses `;` and PowerShell may need a different classpath expression. Running through the IDE is recommended.

## Four pillars of OOP
- **Encapsulation:** `LibraryItem` and `Member` use private fields and public accessors/mutators; setters validate data.
- **Inheritance:** `Book` and `ReferenceBook` extend the abstract `LibraryItem` class.
- **Polymorphism:** `LibraryManager.getBooks()` stores objects as `LibraryItem`; `getBorrowingPeriod()` and `getItemType()` are overridden by concrete item types.
- **Abstraction:** `LibraryItem` is abstract and requires each child class to implement `getBorrowingPeriod()` and `getItemType()`.

## Main classes
- `Main`: starts the application on Swing's event dispatch thread.
- `LibraryGUI`: Swing screens, buttons, tables, and dialogs.
- `LibraryManager`: business rules and SQL operations.
- `DatabaseConnection`: opens JDBC connections.
- `LibraryItem`, `Book`, `ReferenceBook`: demonstrate OOP.
- `Member`: member data and validation.
- `BorrowRecord`: borrowing history data.

## Notes
This is a classroom starter project. It does not include login/accounts, fines, or concurrent multi-user administration. The application stores records in MySQL, so data persists after closing it.
