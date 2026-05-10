package qna.domain;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Comment implements Serializable {
    private static final long serialVersionUID = 1L;
    private String commentId;
    private String content;
    private boolean anonymous;        // 작성 시점에만 설정. update에서는 건드리지 않는다.
    private Student author;
    private Post post;
    private Comment parentComment;    // null이면 루트 댓글
    private List<Comment> replies;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    public Comment(String commentId, String content, boolean anonymous,
                   Student author, Post post, Comment parentComment, LocalDateTime createdAt) {
        this.commentId = commentId;
        this.content = content;
        this.anonymous = anonymous;
        this.author = author;
        this.post = post;
        this.parentComment = parentComment;
        this.replies = new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }
    /**
     * 댓글 본문만 수정. anonymous는 일부러 파라미터로 받지 않는다.
     * (요구사항: 댓글 수정 시 익명 여부 변경 불가)
     */
    public void update(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }
    public boolean isAuthor(Student student) {
        if (student == null || this.author == null) return false;
        return this.author.getUserId().equals(student.getUserId());
    }
    /**
     * 대댓글을 자식 목록에 추가한다.
     *
     누가 추가할 수 있는지(작성 권한)는 CommentService가 검증한다.
     */
    public void addReply(Comment reply) {
        this.replies.add(reply);
    }
    /** 대댓글 여부. parentComment가 null이 아닐 때 true. */
    public boolean isReply() {
        return parentComment != null;
    }
    public String getDisplayAuthorName() {
        return anonymous ? "익명" : author.getName();
    }
    public String getCommentId() { return commentId; }
    public String getContent() { return content; }
    public boolean isAnonymous() { return anonymous; }
    public Student getAuthor() { return author; }
    public Post getPost() { return post; }
    public Comment getParentComment() { return parentComment; }
    public List<Comment> getReplies() { return replies; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}