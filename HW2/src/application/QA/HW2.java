package application.QA;

import java.util.List;
import java.util.NoSuchElementException;

public class HW2 {
	
	public static void main(String[] args) {
		System.out.println("HW2 Demo: CRUD, Validation, Subsets\n");
		
		Questions questions = new Questions();
		Answers answers = new Answers(questions);
		
		// Questions: Create and Read
		System.out.println("1) Create Questions(valid) and list-all");
		Question q1 = new Question("This is my first question", "This is my completely normal body explaining more details on the question", "Tyler", List.of("first"));
		Question q2 = new Question("This is my second question", "Can I pet that dog", "Not Tyler", List.of("dog"));
		questions.create(q1);
		questions.create(q2);
		printQuestion("Created q1", q1);
		printQuestion("Created q2", q2);
		
		System.out.println("\nAll Questions:");
		questions.readAll().forEach(HW2::printQuestionQuick);
		
		// Answers: Create(valid), Read byQuestion
		System.out.println("\n\n\n2) Create Answers(valid) and list by question");
		Answer a1 = new Answer(q1.getId(), "Congratulations on your first question, here is my solution", "NewUser");
		Answer a2 = new Answer(q1.getId(), "While that other solution might work, mine is better", "BetterUser");
		answers.create(a1);
		answers.create(a2);
		printAnswer("Created a1", a1);
		printAnswer("Created a2", a2);
		
		System.out.println("\nAnswers for q1:");
		answers.byQuestion(q1.getId()).forEach(HW2::printAnswerQuick);
		
		// Subsets: Search, Unresolved
		System.out.println("\n\n\n3) Subsets: search questions, filter unresolved, search answers in a question");
		System.out.println("Search questions for 'first':");
		questions.search("first").forEach(HW2::printQuestionQuick);
		
		System.out.println("\nUnresolved (OPEN) questions:");
		questions.filterByStatus(Question.Status.OPEN).forEach(HW2::printQuestionQuick);
		
		System.out.println("\nSearch answers in q1 for 'better':");
		answers.searchInQuestion(q1.getId(), "better").forEach(HW2::printAnswerQuick);
		
		// Update: mark q1 RESOLVED, edit answer
		System.out.println("\n\n\n4) Update: mark question RESOLVED and edit answer");
		q1.setStatus(Question.Status.RESOLVED);
		questions.update(q1);
		printQuestion("Updated q1 to RESOLVED", q1);
		a2.setBody("Here is an improvement on my previous solution");
		answers.update(a2);
		printAnswer("Updated aw body", a2);
		
		// Validation and Negatives
		System.out.println();
		System.out.println();
		System.out.println();
		// Invalid title
		try { 
			System.out.println("Creating invalid question title...");
			new Question("bad", "Accepted body", "Tyler", List.of());
			System.out.println("Error: expected IllegalArgumentException not thrown.");
		} catch(IllegalArgumentException e) {
			System.out.println(" OK (Invalid title rejected): " + e.getMessage());
		}
		
		// Answer must point to existing question
		try {
			System.out.println("Creating answer for a question that does not exist...");
			answers.create(new Answer("missing-id", "Tip body", "Not Tyler"));
			System.out.println("ERROR: expected IllegalArgumentException not thrown.");
		} catch(IllegalArgumentException e) {
			System.out.println(" OK : " + e.getMessage());
		}
		
		// Update and Delete errors
		try {
			System.out.println("Update missing question...");
			questions.update(new Question("Random title", "Random body", "NoName", List.of()));
		} catch(NoSuchElementException e) {
			System.out.println(" OK (update missing question rejected): " + e.getMessage());
		}
		
		try {
			System.out.println("Delete missing answer id...");
			answers.delete("does-not-exist");
		} catch (NoSuchElementException e) {
			System.out.println(" OK (delete missing answer rejected): " + e.getMessage());
		}
		
		// Delete case: question has answers
		System.out.println("\n\n\n6) Delete: remove answers then delete question");
		System.out.println("Current answers for q1: " + answers.byQuestion(q1.getId()).size());
		answers.byQuestion(q1.getId()).forEach(a -> { 
			answers.delete(a.getId());
			System.out.println("Delete answer id=" + a.getId());
		});
		System.out.println("Answers for q1 after deletions: " + answers.byQuestion(q1.getId()).size());
		
		questions.delete(q1.getId());
		System.out.println("Deleted q1. Does it still exist? " + questions.read(q1.getId()).isPresent());
		
		// Final State
		System.out.println("\n\n\n7) Final State");
		questions.readAll().forEach(HW2::printQuestionQuick);
		
		System.out.println("\nDemo complete.");
		
		
	}
	
	
	private static void printQuestion(String header, Question q) {
		System.out.println("\n" + header);
		System.out.println("  id:         " + q.getId());
		System.out.println("  title:      " + q.getTitle());
		System.out.println("  author:     " + q.getAuthor());
		System.out.println("  status:     " + q.getStatus());
		System.out.println("  tags:       " + q.getTags());
		System.out.println("  makeTime:   " + q.getMakeTime());
		System.out.println("  updateTime: " + q.getUpdateTime());
		System.out.println("  body:       " + q.getBody());
	}
	
	private static void printAnswer(String header, Answer a) {
		System.out.println("\n" + header);
		System.out.println("  id:         " + a.getId());
		System.out.println("  questionId: " + a.getQuestionId());
		System.out.println("  author:     " + a.getAuthor());
		System.out.println("  accepted:   " + a.isAccepted());
		System.out.println("  makeTime:   " + a.getMakeTime());
		System.out.println("  updateTime: " + a.getUpdateTime());
		System.out.println("  body:       " + a.getBody());
	}
	
	private static void printQuestionQuick(Question q) {
		System.out.println("- [" + q.getStatus() + "] " + q.getTitle() + " (id=" + q.getId() + ", tags=" + q.getTags() + ")");
	}
	
	private static void printAnswerQuick(Answer a) {
		System.out.println("- " + a.getAuthor() + ": " + a.getBody() + " (id=" + a.getId() + ", accepted=" + a.isAccepted() + ")");
	}


}