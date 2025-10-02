package application.QA;

import application.QA.Question;
import application.QA.Question.Status;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Questions {
	private final Map<String, Question> byId = new LinkedHashMap<>();
	
	// CRUD
	public Question create(Question q) {
		Objects.requireNonNull(q, "Question is required.");
		if(byId.containsKey(q.getId())) {
			throw new IllegalArgumentException("Question id already exists: " + q.getId());
		}
		byId.put(q.getId(), q);
		return q;
	}
	
	public Optional<Question> read(String id) {
		return Optional.ofNullable(byId.get(id));
	}
	
	public List<Question> readAll() {
		return new ArrayList<>(byId.values());
	}
	
	public Question update(Question updated) {
		Objects.requireNonNull(updated);
		String id = updated.getId();
		if(!byId.containsKey(id)) {
			throw new NoSuchElementException("Question not found: " + id);
		}
		byId.put(id, updated);
		return updated;
	}
	
	public void delete(String id) {
		if(byId.remove(id) == null) {
			throw new NoSuchElementException("Question not found: " + id);
		}
	}
	
	
	// Subsets
	public List<Question> filterByStatus(Status status) {
		return subset(q -> q.getStatus() == status);
	}
	
	public List<Question> search(String keyword) {
		if(keyword == null || keyword.isBlank()) {
			return List.of();
		}
		String k = keyword.toLowerCase();
		return subset(q -> q.getTitle().toLowerCase().contains(k) || q.getBody().toLowerCase().contains(k) || q.getTags().stream().anyMatch(t -> t.equalsIgnoreCase(k)));
	}
	
	public List<Question> subset(Predicate<Question> predicate) {
		return byId.values().stream().filter(predicate).collect(Collectors.toList());
	}
	
	public boolean exists(String id) {
		return byId.containsKey(id);
	}

}
