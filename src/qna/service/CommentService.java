package qna.service;

import qna.domain.Comment;
import qna.domain.Post;
import qna.domain.Student;
import qna.repository.CommentRepository;
import qna.repository.PostRepository;
import qna.util.IdGenerator;

import java.time.LocalDateTime;

/**
 * CommentService는 댓글·대댓글 작성·수정 흐름을 담당한다.
 *
 * SRP:
 *  - 댓글/대댓글 CRUD와 작성자 권한 확인만 책임진다.
 *
 * DIP:
 *  - 등록 여부는 EnrollmentService.isEnrolled()를 호출해 확인하며
 *    Enrollment 목록을 직접 뒤지지 않는다.
 *
 * 익명 수정 불가 정책:
 *  - updateComment()는 content만 받는다.
 *    Comment.update(content) 시그니처가 정책을 강제한다.
 */
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final EnrollmentService enrollmentService;

    public CommentService(CommentRepository commentRepository,
                          PostRepository postRepository,
                          EnrollmentService enrollmentService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.enrollmentService = enrollmentService;
    }

    public Comment createComment(Student student, String postId,
                                 String content, boolean anonymous) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다: " + postId));
        if (!enrollmentService.isEnrolled(student, post.getSection())) {
            throw new IllegalStateException("해당 분반에 등록된 학생이 아닙니다.");
        }
        String commentId = IdGenerator.generateCommentId();
        Comment comment = new Comment(commentId, content, student, post,
                anonymous, null, LocalDateTime.now());
        commentRepository.save(comment);
        post.addComment(comment);
        return comment;
    }

    public Comment createReply(Student student, String parentCommentId,
                               String content, boolean anonymous) {
        Comment parent = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다: " + parentCommentId));
        Post post = parent.getPost();
        if (!enrollmentService.isEnrolled(student, post.getSection())) {
            throw new IllegalStateException("해당 분반에 등록된 학생이 아닙니다.");
        }
        String commentId = IdGenerator.generateCommentId();
        Comment reply = new Comment(commentId, content, student, post,
                anonymous, parent, LocalDateTime.now());
        commentRepository.save(reply);
        parent.addReply(reply);
        return reply;
    }

    public Comment updateComment(Student student, String commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다: " + commentId));
        if (!comment.getAuthor().getUserId().equals(student.getUserId())) {
            throw new IllegalStateException("본인이 작성한 댓글만 수정할 수 있습니다.");
        }
        comment.update(content);
        return comment;
    }
}
