package application.QA;

import application.QA.Answer;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Answers {
	private final Map<String, Answer> byId = new LinkedHashMap<>();
	private final Questions questions;
	
	public Answers(Questions questions) {
		this.questions = Objects.requireNonNull(questions);
	}
	
	// CRUD
	public Answer create(Answer a) {
		Objects.requireNonNull(a, "Answer is required.");
		if(!questions.exists(a.getQuestionId())) {
			throw new IllegalArgumentException("Question does not exist: " + a.getQuestionId());
		}
		if(byId.containsKey(a.getId())) {
			throw new IllegalArgumentException("Asnwer id already exists: " + a.getId());
		}
		byId.put(a.getId(), a);
		return a;
	}
	
	public Optional<Answer> read(String id) {
		return Optional.ofNullable(byId.get(id));
	}
	
	public List<Answer> readAll() {
		return new ArrayList<>(byId.values());
	}
	
	public Answer update(Answer updated) {
		Objects.requireNonNull(updated);
		String id = updated.getId();
		if(!byId.containsKey(id)) {
			throw new NoSuchElementException("Answer not found: " + id);
		}
		byId.put(id, updated);
		return updated;
	}
	
	public void delete(String id) {
		if(byId.remove(id) == null) {
			throw new NoSuchElementException("Answer not found: " + id);
		}
	}
	
	
	// Subsets
	public List<Answer> byQuestion(String questionId) {
		return subset(a -> a.getQuestionId().equals(questionId));
	}
	
	public List<Answer> searchInQuestion(String questionId, String keyword) {
		if(keyword == null || keyword.isBlank()) {
			return List.of();
		}
		String k = keyword.toLowerCase();
		return subset(a -> a.getQuestionId().equals(questionId) && a.getBody().toLowerCase().contains(k));
	}
	
	public List<Answer> subset(Predicate<Answer> predicate) {
		return byId.values().stream().filter(predicate).collect(Collectors.toList());
	}

}
