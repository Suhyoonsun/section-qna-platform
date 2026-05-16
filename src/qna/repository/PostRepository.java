package qna.repository;
import qna.domain.Post;
import qna.domain.Section;
import qna.domain.Student;
import qna.storage.AppData;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//  PostRepository는 게시글 데이터 접근만 담당한다.
 /**
 * Repository Pattern:
 *  - PostService는 어떤 자료구조에 게시글이 저장되는지 알 필요가 없다.
 *  - Repository 인터페이스만 알면 되므로 데이터 저장 방식이 바뀌어도 Service를 수정할 일이 줄어든다.
 */
public class PostRepository {
    private final AppData appData;
    public PostRepository(AppData appData) {
        this.appData = appData;
    }
    public void save(Post post) {
        if (findById(post.getPostId()).isPresent()) return;
        appData.getPosts().add(post);
    }
    public void delete(Post post) {
        appData.getPosts().remove(post);
    }
    public Optional<Post> findById(String postId) {
        for (Post p : appData.getPosts()) {
            if (p.getPostId().equals(postId)) return Optional.of(p);
        }
        return Optional.empty();
    }
    public List<Post> findBySection(Section section) {
        List<Post> result = new ArrayList<>();
        for (Post p : appData.getPosts()) {
            if (p.getSection().getSectionCode().equals(section.getSectionCode())) {
                result.add(p);
            }
        }
        return result;
    }
    public List<Post> findByStudent(Student student) {
        List<Post> result = new ArrayList<>();
        for (Post p : appData.getPosts()) {
            if (p.getAuthor().getUserId().equals(student.getUserId())) {
                result.add(p);
            }
        }
        return result;
    }
    public List<Post> findAll() {
        return appData.getPosts();
    }
}