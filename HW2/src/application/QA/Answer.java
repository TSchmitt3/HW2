package application.QA;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Answer {
	private final String id;
	private final String questionId;
	private String body;
	private String author;
	private boolean isAccepted;
	private final Instant makeTime;
	private Instant updateTime;
	
	public Answer(String questionId, String body, String author) {
		this(UUID.randomUUID().toString(), questionId, body, author, false, Instant.now(), Instant.now());
	}
	
	public Answer(String id, String questionId, String body, String author, boolean isAccepted, Instant makeTime, Instant updateTime) {
		this.id = Objects.requireNonNull(id);
		this.questionId = Objects.requireNonNull(questionId);
		setBody(body);
		setAuthor(author);
		this.isAccepted = isAccepted;
		this.makeTime = makeTime == null ? Instant.now() : makeTime;
		this.updateTime = updateTime == null ? this.makeTime : updateTime;
	}
	
	
	// Input validation helper
	private void fieldValidation(boolean condition, String message) {
		if(!condition) throw new IllegalArgumentException(message);
	}
		
	private void touch() {
		this.updateTime = Instant.now();
	}
	
	
	// Setters
	public void setBody(String body) {
		fieldValidation(body != null && !body.trim().isEmpty(), "Answer body cannot be empty.");
		this.body = body;
		touch();
	}
	
	public void setAuthor(String author) {
		fieldValidation(author != null && !author.trim().isEmpty(), "Author cannot be empty.");
		this.author = author.trim();
		touch();
	}
	
	public void setAccepted(boolean accepted) {
		this.isAccepted = accepted;
		touch();
	}
	
	
	// Getters
	public String getId() { return id; }
	public String getQuestionId() { return questionId; }
	public String getBody() { return body; }
	public String getAuthor() { return author; }
	public boolean isAccepted() { return isAccepted; }
	public Instant getMakeTime() { return makeTime; }
	public Instant getUpdateTime() { return updateTime; }
	
	@Override public boolean equals(Object o) {
		return(o instanceof Answer a) && id.equals(a.id);
	}
	@Override public int hashCode() { return id.hashCode(); }
	@Override public String toString() {
		return "Answer{id=%s, qid=%s, accepted=%s}".formatted(id, questionId, isAccepted);
	}

}
