package application.QA;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.NoSuchElementException;

public class JUnitTesting {
	// Question class Testing
	@Nested
	class QuestionTests {
		
		// Full input validation
		@Test
		void setFieldsValid() {
			Question q = new Question("This is my question", "Please help me do a thing.", "Tyler", List.of("help", "homework"));
			assertNotNull(q.getId());
			assertEquals("This is my question", q.getTitle());
			assertEquals(Question.Status.OPEN, q.getStatus());
			assertNotNull(q.getMakeTime());
			assertNotNull(q.getUpdateTime());
		}
		
		// Invalid title
		@Test
		void invalidTitleThrows() {
			assertThrows(IllegalArgumentException.class, () -> new Question("bad", "ok body", "Tyler", List.of()));
		}
		
		// Invalid body
		@Test
		void invalidBodyThrows() {
			assertThrows(IllegalArgumentException.class, () -> new Question("Valid title", "   ", "Tyler", List.of()));
		}
		
		// Time updates properly
		@Test
		void updateTime() {
			Question q = new Question("Valid title", "This is a body", "Tyler", List.of());
			var initial = q.getUpdateTime();
			q.setTitle("New valid title");
			q.setBody("This is still a body");
			assertTrue(!q.getUpdateTime().isBefore(initial));
		}
		
		// Tag validation
		@Test
		void addTagThrows() {
			Question q = new Question("Valid title", "Also valid body", "Still Tyler", List.of());
			assertThrows(IllegalArgumentException.class, () -> q.addTag("this-tag-is-much-too-long-to-be-accepted-and-it-should-definitely-cause-an-error"));
		}
	}
	
	@Nested
	// Questions class Testing
	class QuestionsTests {
		Questions qList;
		
		@BeforeEach
		void setUp() { qList = new Questions(); }
		
		@Test
		void correctCount() {
			var q1 = new Question("Title one", "Body one", "Tyler", List.of("first"));
			var q2 = new Question("Title two", "Body two", "Still Tyler", List.of("second"));
			qList.create(q1);
			qList.create(q2);
			assertEquals(2, qList.readAll().size());
			assertTrue(qList.exists(q1.getId()));
			assertTrue(qList.read(q2.getId()).isPresent());
		}
		
		@Test
		void missingThrows() {
			var missing = new Question("Missing", "Body", "Tyler", List.of());
			assertThrows(NoSuchElementException.class, () -> qList.update(missing));
		}
		
		@Test
		void deleteRemoves() {
			var q = new Question("Delete me", "Body", "Tyler", List.of());
			qList.create(q);
			qList.delete(q.getId());
			assertFalse(qList.read(q.getId()).isPresent());
		}
		
		@Test
		void deleteThrows() {
			assertThrows(NoSuchElementException.class, () -> qList.delete("Nope"));
		}
		
		@Test
		void filterByStatusAndSearch() {
			var a = new Question("Open question", "This is a body", "Tyler", List.of("help"));
			var b = new Question("Testing another question", "A different body", "Still Tyler", List.of("testing"));
			qList.create(a);
			qList.create(b);
			b.setStatus(Question.Status.RESOLVED);
			qList.update(b);
			
			assertEquals(1, qList.filterByStatus(Question.Status.OPEN).size());
			assertEquals(1, qList.search("testing").size());
		}
	}
	
	
	@Nested
	// Answer class testing
	class AnswerTests {
		Question q;
		
		@BeforeEach
		void setUp() { q = new Question("Answer test question", "Body for answer tests", "Tyler", List.of("tag")); }
		
		@Test
		void setFieldsValid() {
			Answer a = new Answer(q.getId(), "Insert helpful feedback here", "Not Tyler");
			
			assertNotNull(a.getId());
			assertEquals(q.getId(), a.getQuestionId());
			assertEquals("Not Tyler", a.getAuthor());
			assertNotNull(a.getMakeTime());
			assertNotNull(a.getUpdateTime());
		}
		
		@Test
		void update() {
			Answer a = new Answer(q.getId(), "Original body", "Not Tyler");
			var initial = a.getUpdateTime();
			a.setBody("New and more original body");
			assertTrue(!a.getUpdateTime().isBefore(initial));
		}
		
		@Test
		void accepted() {
			Answer a = new Answer(q.getId(), "Wise words to go by", "Not Tyler");
			assertFalse(a.isAccepted());
			a.setAccepted(true);
			assertTrue(a.isAccepted());
			a.setAccepted(false);
			assertFalse(a.isAccepted());
		}
	}
	
	@Nested
	// Answers class testing
	class AnswersTests {
		Questions qList;
		Answers aList;
		Question q;
		
		@BeforeEach
		void setUp() {
			qList = new Questions();
			aList = new Answers(qList);
			q = new Question("Test Title", "Test body", "Tyler", List.of("send", "help"));
			qList.create(q);
		}
		
		@Test
		void validAnswerCreation() {
			var a1 = new Answer(q.getId(), "Here is a solution to your problem", "Not Tyler");
			var save = aList.create(a1);
			assertNotNull(save.getId());
			assertEquals(1, aList.byQuestion(q.getId()).size());
		}
		
		@Test
		void createRequiresQuestion() {
			var a = new Answer("theres-no-question", "Something", "Not Tyler");
			var except = assertThrows(IllegalArgumentException.class, () -> aList.create(a));
			assertTrue(except.getMessage().toLowerCase().contains("does not exist"));
		}
		
		@Test
		void updateAndDelete() {
			var a = new Answer(q.getId(), "Original body", "Not Tyler");
			aList.create(a);
			
			a.setBody("New and improved body");
			aList.update(a);
			assertEquals("New and improved body", aList.read(a.getId()).orElseThrow().getBody());
			
			aList.delete(a.getId());
			assertFalse(aList.read(a.getId()).isPresent());
		}
		
		@Test
		void updateMissingThrows() {
			var missing = new Answer(q.getId(), "Missing", "Not Tyler");
			assertThrows(NoSuchElementException.class, () -> aList.update(missing));
		}
		
		@Test
		void deleteMissingThrows() {
			assertThrows(NoSuchElementException.class, () -> aList.delete("nope"));
		}
		
		@Test
		void searchQuestionFilterKeyword() {
			aList.create(new Answer(q.getId(), "This is the first answer", "Not Tyler"));
			aList.create(new Answer(q.getId(), "Something different", "relyT"));
			assertEquals(1, aList.searchInQuestion(q.getId(), "something").size());
			assertEquals(0, aList.searchInQuestion(q.getId(), "nothing").size());
		}
	}

}
