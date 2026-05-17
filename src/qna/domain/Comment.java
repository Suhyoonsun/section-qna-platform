package qna.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Comment는 게시글에 달리는 댓글·대댓글을 나타낸다.
 *
 * SRP:
 *  - 댓글 자체의 상태(내용·익명 여부)와 자기 정보 변경만 담당한다.
 *  - 등록 여부 검증은 CommentService가, 저장은 CommentRepository가 담당한다.
 *
 * 자기참조 구조 (대댓글):
 *  - parentComment가 null이면 최상위 댓글, null이 아니면 대댓글이다.
 *  - 단일 CommentRepository에 평탄하게 저장하고 replies 리스트로 계층을 표현한다.
 *
 * 익명 수정 불가 정책:
 *  - update()는 content만 받는다. 익명 여부는 작성 시점에 고정된다.
 *  - 메서드 시그니처 자체가 정책을 강제하므로 별도 검증 코드가 필요 없다.
 */
public class Comment implements Serializable {
    private static final long serialVersionUID = 1L;

    private String commentId;
    private String content;
    private Student author;
    private Post post;
    private boolean anonymous;
    private Comment parentComment;   // null이면 최상위 댓글
    private List<Comment> replies;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Comment(String commentId, String content,
                   Student author, Post post,
                   boolean anonymous, Comment parentComment,
                   LocalDateTime createdAt) {
        this.commentId = commentId;
        this.content = content;
        this.author = author;
        this.post = post;
        this.anonymous = anonymous;
        this.parentComment = parentComment;
        this.replies = new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    // 익명 여부는 변경하지 않는다(메서드 시그니처로 정책 강제).
    public void update(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public void addReply(Comment reply) {
        this.replies.add(reply);
    }

    public String getCommentId() { return commentId; }
    public String getContent() { return content; }
    public Student getAuthor() { return author; }
    public Post getPost() { return post; }
    public boolean isAnonymous() { return anonymous; }
    public Comment getParentComment() { return parentComment; }
    public List<Comment> getReplies() { return replies; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
