package qna.repository;
import qna.domain.Comment;
import qna.storage.AppData;
import java.util.List;
import java.util.Optional;

//  CommentRepository는 댓글/대댓글 데이터 접근만 담당
// 자기참조 구조이므로, 단일 List에 모든 댓글을 평탄하게 저장

public class CommentRepository {
    private final AppData appData;
    public CommentRepository(AppData appData) {
        this.appData = appData;
    }
    public void save(Comment comment) {
        if (findById(comment.getCommentId()).isPresent()) return;
        appData.getComments().add(comment);
    }
    public Optional<Comment> findById(String commentId) {
        for (Comment c : appData.getComments()) {
            if (c.getCommentId().equals(commentId)) return Optional.of(c);
        }
        return Optional.empty();
    }
    public List<Comment> findAll() {
        return appData.getComments();
    }
}