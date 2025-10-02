package application.QA;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.time.Instant;
import java.util.UUID;

public class Question {
	public enum Status { OPEN, RESOLVED, CLOSED }
	
	private final String id;
	private String title;
	private String body;
	private String author;
	private Status status;
	private final List<String> tags;
	private final Instant makeTime;
	private Instant updateTime;
	
	//Constructors
	public Question(String title, String body, String author, List<String> tags) {
		this(UUID.randomUUID().toString(), title, body, author, tags == null ? List.of() : tags, Status.OPEN, Instant.now(), Instant.now());
	}
	
	public Question(String id, String title, String body, String author, List<String> tags, Status status, Instant makeTime, Instant updateTime) {
		this.id = Objects.requireNonNull(id);
		this.tags = new ArrayList<>();
		setTitle(title);
		setBody(body);
		setAuthor(author);
		setStatus(status == null ? Status.OPEN : status);
		if(tags != null) tags.forEach(this::addTag);
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
	public void setTitle(String title) {
		fieldValidation(title != null && title.trim().length() >= 5, "Title must be at least 5 characters.");
		this.title = title.trim();
		touch();
	}
	
	public void setBody(String body) {
		fieldValidation(body != null && !body.trim().isEmpty(), "Body cannot be empty.");
		this.body = body;
		touch();
	}
	
	public void setAuthor(String author) {
		fieldValidation(author != null && !author.trim().isEmpty(), "Author cannot be empty.");
		this.author = author.trim();
		touch();
	}
	
	public void setStatus(Status status) {
		fieldValidation( status != null, "Status is required.");
		this.status = status;
		touch();
	}
	
	public void addTag(String tag) {
		fieldValidation(tag != null && tag.matches("[A-Za-z0-9.-]{1,20}"), "Tag must be 1-20 characters (alphanumeric, periods, dashes)");
		if(!tags.contains(tag)) {
			tags.add(tag);
		}
		touch();
	}
	
	public void removeTag(String tag) {
		tags.remove(tag);
		touch();
	}
	
	
	// Getters
	public String getId() { return id; }
	public String getTitle() { return title; }
	public String getBody() { return body; }
	public String getAuthor() { return author; }
	public Status getStatus() { return status; }
	public List<String> getTags() { return List.copyOf(tags); }
	public Instant getMakeTime() { return makeTime; }
	public Instant getUpdateTime() { return updateTime; }
	
	
	@Override public boolean equals(Object o) {
		return(o instanceof Question q) && id.equals(q.id);
	}
	@Override public int hashCode() {
		return id.hashCode();
	}
	@Override public String toString() {
		return "Question{id=%s, title=%s, status=%s}".formatted(id, title, status);
	}

}
