package qna.service;
import qna.domain.Post;
import qna.domain.Section;
import qna.domain.Student;
import qna.repository.PostRepository;
import qna.util.IdGenerator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
/**
 * PostService는 게시글 생성, 수정, 삭제, 조회를 담당하는 서비스 클래스이다.
 *
 * SRP:
 *  - 게시글 관련 비즈니스 로직만 담당한다.
 *  - 데이터 접근은 PostRepository에, 권한 검증은 EnrollmentService에 위임한다.
 *
 * DIP (발표 핵심):
 *  - PostService는 Enrollment 목록을 직접 조회하지 않는다.
 *  - 반드시 EnrollmentService.isEnrolled(student, section)를 호출해 검증한다.
 *  - 덕분에 등록 정보의 저장 방식이 바뀌어도 PostService는 영향을 받지 않는다.
 */
public class PostService {
    private final PostRepository postRepository;
    private final EnrollmentService enrollmentService;
    public PostService(PostRepository postRepository, EnrollmentService enrollmentService) {
        this.postRepository = postRepository;
        this.enrollmentService = enrollmentService;
    }
    /**
     * 게시글 작성 전 수강 여부를 검증한다.
     * PostService가 Enrollment 목록을 직접 조회하지 않고
     * EnrollmentService에 위임하기 때문에 DIP를 만족한다.
     */
    public Post createPost(Student student, Section section,
                           String title, String content,
                           boolean anonymous, boolean professorVisible) {
        if (student == null) {
            System.out.println("존재하지 않는 학생 ID입니다.");
            return null;
        }
        if (section == null) {
            System.out.println("존재하지 않는 분반 코드입니다.");
            return null;
        }
        // DIP의 핵심 호출 지점.
        if (!enrollmentService.isEnrolled(student, section)) {
            System.out.println("수강 등록되지 않은 학생은 게시글을 작성할 수 없습니다.");
            return null;
        }
        String postId = IdGenerator.generatePostId();
        Post post = new Post(postId, title, content, anonymous, professorVisible,
                분반별 Q&A 게시판 구현 문서
                33
                student, section, LocalDateTime.now());
        postRepository.save(post);
        section.addPost(post);
        return post;
    }
    /**
     * 게시글 수정. 작성자 본인 검증 후 도메인의 update를 호출한다.
     * "수정 가능 여부"는 Service가, "상태 변경"은 도메인이 담당한다(SRP).
     */
    public Post updatePost(Student student, String postId,
                           String title, String content,
                           boolean anonymous, boolean professorVisible) {
        Optional<Post> opt = postRepository.findById(postId);
        if (opt.isEmpty()) {
            System.out.println("존재하지 않는 게시글 ID입니다.");
            return null;
        }
        Post post = opt.get();
        if (!post.isAuthor(student)) {
            System.out.println("작성자 본인만 수정할 수 있습니다.");
            return null;
        }
        post.update(title, content, anonymous, professorVisible);
        return post;
    }
    /**
     * 게시글 삭제. 작성자 본인 검증 후 Repository와 Section 양쪽에서 제거한다.
     */
    public boolean deletePost(Student student, String postId) {
        Optional<Post> opt = postRepository.findById(postId);
        if (opt.isEmpty()) {
            System.out.println("존재하지 않는 게시글 ID입니다.");
            return false;
        }
        Post post = opt.get();
        if (!post.isAuthor(student)) {
            System.out.println("작성자 본인만 삭제할 수 있습니다.");
            return false;
        }
        postRepository.delete(post);
        post.getSection().removePost(post);
        return true;
    }
    /**
     * 분반의 모든 게시글을 조회한다.
     * 분반별로 격리된 공간이라는 도메인 의미를 그대로 반영한다.
     */
    public List<Post> getPostsBySection(Section section) {
        if (section == null) return List.of();
        return postRepository.findBySection(section);
    }
    /** 학생 본인이 작성한 모든 게시글을 조회한다. */
    public List<Post> getPostsByStudent(Student student) {
        if (student == null) return List.of();
        return postRepository.findByStudent(student);
    }
}