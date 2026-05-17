package qna.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Post는 분반 Q&A 게시글을 나타낸다.
 *
 * SRP:
 *  - 게시글 자체의 상태(제목·내용·공개 여부·댓글 목록)와 자기 정보 변경만 담당한다.
 *  - "누가 쓸 수 있는가"(등록 여부 검증)는 PostService가, 저장은 PostRepository가 담당한다.
 *
 * 익명 정책:
 *  - anonymous 플래그는 작성 시점에 결정되며, update()로 변경할 수 있다.
 *  - professorVisible: false이면 교수에게 내용이 노출되지 않는다(표시 정책은 Facade/UI 레이어 책임).
 */
public class Post implements Serializable {
    private static final long serialVersionUID = 1L;

    private String postId;
    private String title;
    private String content;
    private Student author;
    private Section section;
    private boolean anonymous;
    private boolean professorVisible;
    private List<Comment> comments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Post(String postId, String title, String content,
                Student author, Section section,
                boolean anonymous, boolean professorVisible,
                LocalDateTime createdAt) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.author = author;
        this.section = section;
        this.anonymous = anonymous;
        this.professorVisible = professorVisible;
        this.comments = new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public void update(String title, String content, boolean anonymous, boolean professorVisible) {
        this.title = title;
        this.content = content;
        this.anonymous = anonymous;
        this.professorVisible = professorVisible;
        this.updatedAt = LocalDateTime.now();
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public String getPostId() { return postId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public Student getAuthor() { return author; }
    public Section getSection() { return section; }
    public boolean isAnonymous() { return anonymous; }
    public boolean isProfessorVisible() { return professorVisible; }
    public List<Comment> getComments() { return comments; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
