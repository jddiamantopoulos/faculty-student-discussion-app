# JavaFX Faculty–Student Discussion Application (CSE360 Team Project)

A desktop application developed in **Java** and **JavaFX** as part of CSE360 (Software Engineering).
The system supports structured communication between students, staff, instructors, and administrators,
including messaging, administrative workflows, and database-backed persistence.

This project was developed collaboratively in a multi-person team environment.

---

## Tech Stack

- Java  
- JavaFX  
- JUnit (Automated Testing)  
- JDBC / SQL (Database Integration)  
- IDE: Eclipse  

---

## Key Features

- User account management with role-based access  
- Discussion and messaging workflows  
- Administrative request and resolution system  
- Database-backed persistence  
- Automated testing with JUnit  

---

## Project Structure

- `src/` — Application source code  
- `test/` — JUnit automated tests  
- `bin/` — Compiled output (excluded via `.gitignore`)  
- `doc/` — Javadoc HTML documentation  

---

## My Contributions (Jonathan Diamantopoulos)

I was a major contributor to this project and the second-highest contributor by commit count. My work focused on core feature implementation, testing, and system integration.

### Administrative Task Messaging System (Primary Owner)

I designed and fully implemented the **Task Messaging System**, which enables structured communication between staff/instructors and administrators.

This system allows:

- Staff and instructors to open administrative request threads  
- All instructors, staff, and admins to view request threads  
- Administrators to respond, manage, and close requests  
- Staff and instructors to reopen threads for related issues  
- Controlled messaging permissions based on role  

Once a request is opened, staff/instructors cannot continue posting in the thread, while administrators can respond. Threads may be closed by admins and reopened if needed.

I independently developed the following core files:

- `TaskMessage.java`  
- `TaskMessages.java`  
- `TaskMessagePage.java`  
- `TaskMessageListPage.java`  
- `TaskMessageValidator.java`  
- `TaskMessageTests.java`  

This subsystem represents a major functional component of the application.

---

### Automated Testing (JUnit)

I authored and maintained multiple JUnit test suites to ensure database reliability and system correctness, including:

- `QuestionTests.java`  
- `AnswerTests.java`  
- `ReviewTests.java`  
- `ReviewerRequestTests.java`  
- `ReviewerStringTests.java`  
- `MessageTests.java`  
- `TaskMessageTests.java`  

These tests validate database interactions, data integrity, and edge-case behavior.

---

### Database & Integration Work

I made significant contributions to backend functionality, particularly within:

- `DatabaseHelper.java`  

This included implementing SQL methods for the Task Messaging system and contributing to broader database functionality.

---

### UI Integration

To integrate the task messaging feature into the application workflow, I contributed to:

- `AdminHomePage.java`  
- `UserHomePage.java`  

This enabled seamless access to administrative requests from core navigation pages.

---

### Summary of Contributions

- Primary developer of the Task Messaging subsystem  
- Major contributor to database integration  
- Author of multiple JUnit test suites  
- Contributor to UI integration and system workflows  
- Participated in debugging, refactoring, and documentation  

---

## Team & Credits

This project was developed collaboratively as part of CSE360 in early 2025.

Contributors include:

- @ETSells  
- @JonathanDiamantopoulos  
- @A23N3TH  
- @hsing117 
- @bnsalcid  
- @Kush402  

Contributors are listed based on GitHub history. Responsibilities evolved throughout development.

This repository was later migrated to my personal GitHub in early 2026 for portfolio purposes. Original authorship and commit history have been preserved.

---

## Installation & Setup

### Requirements

- Java (compatible with JavaFX)  
- JavaFX SDK  
- Database setup (as provided by course template)  
- JUnit 4 (required for running tests)  

---

### Setup Instructions

1. Clone the repository:

   ```bash
   git clone https://github.com/jddiamantopoulos/faculty-student-discussion-app.git
   ```

2. Open the project in Eclipse.

3. Configure JavaFX libraries and VM arguments.

4. Ensure your module/classpath matches the provided course template.

5. Set the application entry point to:

   ```
   StartCSE360.java
   ```

6. If your database contains previous data, clear it before running.

7. Run the application.

---

## Using the Application

### Initial Setup

1. Begin by creating an administrator account.  
2. Follow the on-screen validation messages to create a valid username and password.  

---

### Creating User Accounts

1. An administrator generates an invite code.  
2. The invite code is assigned a role (`user`, `reviewer`, `instructor`, `staff`, or `admin`).  
3. New users enter:
   - Name  
   - Username  
   - Password  
   - Email  
   - Invite code  

---

### Navigating the System

- Access the question list from the user or admin home page  
- Use the help button for guidance on new features  
- Access messaging and reviewer tools from the home page  
- Write and submit reviews from the Question Page  

---

### Administrative Task Messaging

- Staff/instructors create administrative requests  
- Requests appear in a shared list  
- Administrators respond and resolve issues  
- Threads may be reopened for related follow-up  

This system enables structured, auditable communication between instructional staff and administrators.

---

## Running Tests

JUnit tests are located in the `test/` directory.

Run tests through your IDE or test runner with JUnit 4 configured.

---

## License

Educational / portfolio use only.
