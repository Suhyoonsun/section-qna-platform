package qna.service;

import qna.domain.Post;
import qna.domain.Section;
import qna.domain.Student;
import qna.repository.PostRepository;
import qna.util.IdGenerator;

import java.time.LocalDateTime;
import java.util.List;

/**
 * PostService는 게시글 작성·수정·삭제 흐름을 담당한다.
 *
 * SRP:
 *  - "게시글 CRUD"와 "작성자 권한 확인"만 책임진다.
 *
 * DIP:
 *  - 등록 여부는 EnrollmentService.isEnrolled()를 호출해 확인하며
 *    Enrollment 목록을 직접 뒤지지 않는다.
 */
public class PostService {

    private final PostRepository postRepository;
    private final EnrollmentService enrollmentService;

    public PostService(PostRepository postRepository, EnrollmentService enrollmentService) {
        this.postRepository = postRepository;
        this.enrollmentService = enrollmentService;
    }

    public Post createPost(Student student, Section section,
                           String title, String content,
                           boolean anonymous, boolean professorVisible) {
        if (!enrollmentService.isEnrolled(student, section)) {
            throw new IllegalStateException("해당 분반에 등록된 학생이 아닙니다.");
        }
        String postId = IdGenerator.generatePostId();
        Post post = new Post(postId, title, content, student, section,
                anonymous, professorVisible, LocalDateTime.now());
        postRepository.save(post);
        section.addPost(post);
        return post;
    }

    public Post updatePost(Student student, String postId,
                           String title, String content,
                           boolean anonymous, boolean professorVisible) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다: " + postId));
        if (!post.getAuthor().getUserId().equals(student.getUserId())) {
            throw new IllegalStateException("본인이 작성한 게시글만 수정할 수 있습니다.");
        }
        post.update(title, content, anonymous, professorVisible);
        return post;
    }

    public boolean deletePost(Student student, String postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다: " + postId));
        if (!post.getAuthor().getUserId().equals(student.getUserId())) {
            throw new IllegalStateException("본인이 작성한 게시글만 삭제할 수 있습니다.");
        }
        postRepository.delete(post);
        post.getSection().removePost(post);
        return true;
    }

    public List<Post> getPostsBySection(Section section) {
        return postRepository.findBySection(section);
    }

    public List<Post> getMyPosts(Student student) {
        return postRepository.findByStudent(student);
    }
}
