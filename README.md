# HW2
CSE360 HW2

Project Structure
  - The initial assignment description said to copy our groups first turn in, so I have placed this new package alongside that. 
  - src/application/QA
    - Question.java # Single Question
    - Questions.java # Manages and stores all questions
    - Answer.java # Single Answer
    - Answers.java # manages and stores all answers
    - HW2.java # Demo of each CRUD step
    - JUnitTesting.java # Unit tests covering CRUD and validation

**Questions**
  - Create with validation (title, body, author, tags)
  - Update title, body, status, tags
  - Filter by status (OPEN, RESOLVED, CLOSED)
  - Search by keyword/tags
**Answers**
  - Create with referential integrity (must link to an existing question)
  - Update body, toggle accepted flag
  - Search answers by question/keyword
**Validation**
  - Reject invalid titles/tags
  - Ensure timestamps update properly
  - Provide meaningful error messages
**Collections**
  - Support storing all current questions/answers
  - Support subsets (search results, filtered lists)

My setup
  - JDK24
  - JUnit 5

Run HW2.java for the console demo
