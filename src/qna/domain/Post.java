package qna.domain;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Post implements Serializable {
    private static final long serialVersionUID = 1L;
    private String postId;
    private String title;
    private String content;
    private boolean anonymous;          // 익명 여부
    private boolean professorVisible;   // 교수에게도 공개할지 여부
    private Student author;
    private Section section;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Comment> comments;
    public Post(String postId, String title, String content,
                boolean anonymous, boolean professorVisible,
                Student author, Section section, LocalDateTime createdAt) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.anonymous = anonymous;
        this.professorVisible = professorVisible;
        this.author = author;
        this.section = section;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
        this.comments = new ArrayList<>();
    }
    public void update(String title, String content, boolean anonymous, boolean professorVisible) {
        this.title = title;
        this.content = content;
        this.anonymous = anonymous;
        this.professorVisible = professorVisible;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isAuthor(Student student) {
        if (student == null || this.author == null) return false;
        return this.author.getUserId().equals(student.getUserId());
    }
    public void addComment(Comment comment) { this.comments.add(comment); }
    public void removeComment(Comment comment) { this.comments.remove(comment); }

    public String getDisplayAuthorName() {
        return anonymous ? "익명" : author.getName();
    }
    public String getPostId() { return postId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public boolean isAnonymous() { return anonymous; }
    public boolean isProfessorVisible() { return professorVisible; }
    public Student getAuthor() { return author; }
    public Section getSection() { return section; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public List<Comment> getComments() { return comments; }
}