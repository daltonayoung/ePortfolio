# CS-499 ePortfolio

Dalton Young's Computer Science Capstone ePortfolio for Southern New Hampshire University, published via GitHub Pages.

The site includes a professional self-assessment, a code review, and three enhancements to a single artifact: a Java contact management service originally from CS-320. It covers:

- **Enhancement One:** Software Design & Engineering (CLI layer)
- **Enhancement Two:** Algorithms & Data Structures (HashMap + sorted lookup)
- **Enhancement Three:** Databases (SQLite via JDBC)

Original and enhanced source code for each milestone lives in this repository alongside the narratives and site content.

## Repository layout

- `CS-320 Original Artifact/` - the pre-enhancement artifact (plain `.java` files, included for comparison only, not meant to be built)
- `Enhancement 1 - Software Design and Engineering/` - standalone Maven project
- `Enhancement 2 - Algorithms and Data Structures/` - standalone Maven project
- `Enhancement 3 - Databases/` - standalone Maven project
- `*.md` files at the repository root - the Jekyll site content (narratives, self-assessment, code review)

Each enhancement folder is its own independent Maven project. They aren't cumulative modules. Enhancement 3, for example, contains the full, already-integrated code for all three enhancements combined, not just the database layer on its own.

## Building and running

**Requirements:** JDK 17+ and Maven.

From inside any of the three `Enhancement * - *` folders:

```
mvn test        # compiles and runs the JUnit test suite
mvn compile      # compiles only
```

To run the command-line app itself, the easiest path is importing the folder as a Maven project into an IDE (Eclipse, IntelliJ, VS Code) and running `contactService.ContactApp`'s `main` method directly.

To run it from the command line instead:

```
mvn compile
mvn dependency:copy-dependencies

# Windows
java -cp "target/classes;target/dependency/*" contactService.ContactApp

# macOS/Linux
java -cp "target/classes:target/dependency/*" contactService.ContactApp
```

(`dependency:copy-dependencies` is only strictly necessary for Enhancement 3, which depends on the SQLite JDBC driver, but running it is harmless for Enhancements 1 and 2 as well.)

Enhancement 3's `ContactApp` creates `contacts.db` in the current working directory on first run and reuses it on subsequent runs, so contacts persist across restarts.
