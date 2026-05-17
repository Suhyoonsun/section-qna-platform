package qna.facade;
import qna.domain.Comment;
import qna.domain.Course;
import qna.domain.Enrollment;
import qna.domain.Invitation;
import qna.domain.Post;
import qna.domain.Professor;
import qna.domain.Section;
import qna.domain.Student;
import qna.factory.UserFactory;
import qna.repository.CourseRepository;
import qna.repository.PostRepository;
import qna.repository.ProfessorRepository;
import qna.repository.SectionRepository;
import qna.repository.StudentRepository;
import qna.service.BookmarkService;
import qna.service.CommentService;
import qna.service.EnrollmentService;
import qna.service.InvitationService;
import qna.service.PostService;
import qna.service.SectionService;
import java.util.List;
import java.util.Optional;

public class QnaBoardFacade {
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    private final PostRepository postRepository;
    private final UserFactory userFactory;
    private final SectionService sectionService;
    private final InvitationService invitationService;
    private final EnrollmentService enrollmentService;
    private final PostService postService;
    private final CommentService commentService;
    private final BookmarkService bookmarkService;

    public QnaBoardFacade(StudentRepository studentRepository,
                          ProfessorRepository professorRepository,
                          CourseRepository courseRepository,
                          SectionRepository sectionRepository,
                          PostRepository postRepository,
                          UserFactory userFactory,
                          SectionService sectionService,
                          InvitationService invitationService,
                          EnrollmentService enrollmentService,
                          PostService postService,
                          CommentService commentService,
                          BookmarkService bookmarkService) {
        this.studentRepository = studentRepository;
        this.professorRepository = professorRepository;
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
        this.postRepository = postRepository;
        this.userFactory = userFactory;
        this.sectionService = sectionService;
        this.invitationService = invitationService;
        this.enrollmentService = enrollmentService;
        this.postService = postService;
        this.commentService = commentService;
        this.bookmarkService = bookmarkService;
    }

    public void createDemoData() {
        boolean hasDemoCourse = courseRepository.findByCode("704828").isPresent();
        boolean hasDemoSection = sectionRepository.findByCode("501").isPresent();
        if (hasDemoCourse && hasDemoSection) {
            System.out.println("이미 데모 데이터가 존재합니다.");
            return;
        }
        // 교수 1명
        Professor professor = userFactory.createProfessor(
                "조혜진", "hjcho@example.ac.kr", "pw1234", "P001");
        professorRepository.save(professor);
        // 학생 4명
        Student s1 = userFactory.createStudent("서윤선", "yoonsun@example.ac.kr", "pw1234", "C389033");
        Student s2 = userFactory.createStudent("김현진", "hyunjin@example.ac.kr", "pw1234", "C289013");
        Student s3 = userFactory.createStudent("유승환", "seunghwan@example.ac.kr", "pw1234", "C289039");
        Student s4 = userFactory.createStudent("민은채", "eunchae@example.ac.kr", "pw1234", "C389074");
        studentRepository.save(s1);
        studentRepository.save(s2);
        studentRepository.save(s3);
        studentRepository.save(s4);
        // 과목, 분반, 담당 교수 배정
        Course course = courseRepository.findByCode("704828")
                .orElseGet(() -> {
                    Course c = new Course("704828", "디자인패턴프로그래밍및실습");
                    courseRepository.save(c);
                    return c;
                });
        Section section = sectionService.createSection(course, "501");
        sectionService.assignProfessor(section, professor);
        //초대 코드 1개 생성
        Invitation invitation = invitationService.createInvitation(professor, section, 60);
        if (invitation == null) {
            System.out.println("데모용 초대 코드 생성에 실패했습니다.");
            return;
        }
        // 서윤선만 초대 코드로 등록 → 김현진은 미등록 상태
        Enrollment e = enrollmentService.enrollStudent(s1, invitation.getCode());
        if (e == null) {
            6
            System.out.println("데모용 수강 등록에 실패했습니다.");
            section-qna-board 구현 문서 | Checkpoint 11-15
            return;
        }
        // 서윤선이 게시글 1개 작성
        Post post = postService.createPost(s1, section,
                "디자인패턴 첫 번째 질문",
                "팩토리 패턴은 언제 쓰는 게 좋을까요?",
                false, true);
        if (post != null) {
            System.out.println("데모 데이터 생성 완료. 게시글 ID: " + post.getPostId());
        }
        System.out.println("- 미등록 학생(김현진)으로 게시글 작성을 시도하면 실패하는 시나리오를 확인할 수 있습니다.");
    }
    // 분반/초대/등록
    public Section createSection(String courseCode, String sectionCode) {
        Optional<Course> opt = courseRepository.findByCode(courseCode);
        if (opt.isEmpty()) {
            System.out.println("존재하지 않는 과목 코드입니다.");
            return null;
        }
        return sectionService.createSection(opt.get(), sectionCode);
    }
    public Invitation createInvitation(String professorId, String sectionCode, int validMinutes) {
        Optional<Professor> p = professorRepository.findById(professorId);
        if (p.isEmpty()) {
            System.out.println("존재하지 않는 교수 ID입니다.");
            return null;
        }
        Optional<Section> s = sectionRepository.findByCode(sectionCode);
        if (s.isEmpty()) {
            System.out.println("존재하지 않는 분반 코드입니다.");
            return null;
        }
        return invitationService.createInvitation(p.get(), s.get(), validMinutes);
    }
    public Enrollment enrollStudentByInvitation(String studentId, String invitationCode) {
        Optional<Student> opt = studentRepository.findById(studentId);
        if (opt.isEmpty()) {
            System.out.println("존재하지 않는 학생 ID입니다.");
            return null;
        }
        return enrollmentService.enrollStudent(opt.get(), invitationCode);
    }
    //게시글
    public Post createPost(String studentId, String sectionCode,
                           String title, String content,
                           boolean anonymous, boolean professorVisible) {
        Optional<Student> opt = studentRepository.findById(studentId);
        if (opt.isEmpty()) {
            System.out.println("존재하지 않는 학생 ID입니다.");
            return null;
        }
        Optional<Section> s = sectionRepository.findByCode(sectionCode);
        if (s.isEmpty()) {
            System.out.println("존재하지 않는 분반 코드입니다.");
            return null;
        }
        return postService.createPost(opt.get(), s.get(), title, content, anonymous, professorVisible);
    }
    public void showPostsBySection(String sectionCode) {
        Optional<Section> s = sectionRepository.findByCode(sectionCode);
        if (s.isEmpty()) {
            System.out.println("존재하지 않는 분반 코드입니다.");
            return;
        }
        List<Post> posts = postService.getPostsBySection(s.get());
        if (posts.isEmpty()) {
            System.out.println("[" + sectionCode + " 분반] 게시글이 없습니다.");
            return;
        }
        System.out.println("===== [" + sectionCode + " 분반] 게시글 목록 =====");
        for (Post p : posts) {
            printPost(p);
        }
    }
    public void showMyPosts(String studentId) {
        Optional<Student> opt = studentRepository.findById(studentId);
        if (opt.isEmpty()) {
            System.out.println("존재하지 않는 학생 ID입니다.");
            return;
        }
        List<Post> posts = postService.getPostsByStudent(opt.get());
        if (posts.isEmpty()) {
            System.out.println("작성한 게시글이 없습니다.");
            return;
        }
        System.out.println("===== 내 게시글 목록 =====");
        for (Post p : posts) {
            printPost(p);
        }
    }
    public Post updatePost(String studentId, String postId,
                           String title, String content,
                           boolean anonymous, boolean professorVisible) {
        Optional<Student> opt = studentRepository.findById(studentId);
        if (opt.isEmpty()) {
            System.out.println("존재하지 않는 학생 ID입니다.");
            return null;
        }
        return postService.updatePost(opt.get(), postId, title, content, anonymous, professorVisible);
    }
    public boolean deletePost(String studentId, String postId) {
        Optional<Student> opt = studentRepository.findById(studentId);
        if (opt.isEmpty()) {
            System.out.println("존재하지 않는 학생 ID입니다.");
            return false;
        }
        return postService.deletePost(opt.get(), postId);
    }
    //댓글/대댓글
    public Comment createComment(String studentId, String postId,
                                 String content, boolean anonymous) {
        Optional<Student> opt = studentRepository.findById(studentId);
        if (opt.isEmpty()) {
            System.out.println("존재하지 않는 학생 ID입니다.");
            return null;
        }
        return commentService.createComment(opt.get(), postId, content, anonymous);
    }
    public Comment createReply(String studentId, String parentCommentId,
                               String content, boolean anonymous) {
        Optional<Student> opt = studentRepository.findById(studentId);
        if (opt.isEmpty()) {
            System.out.println("존재하지 않는 학생 ID입니다.");
            return null;
        }
        return commentService.createReply(opt.get(), parentCommentId, content, anonymous);
    }
    public Comment updateComment(String studentId, String commentId, String content) {
        Optional<Student> opt = studentRepository.findById(studentId);
        if (opt.isEmpty()) {
            System.out.println("존재하지 않는 학생 ID입니다.");
            return null;
        }
        return commentService.updateComment(opt.get(), commentId, content);
    }
    // 북마크

    public boolean addBookmark(String studentId, String postId) {
        Optional<Student> opt = studentRepository.findById(studentId);
        if (opt.isEmpty()) {
            System.out.println("존재하지 않는 학생 ID입니다.");
            return false;
        }
        return bookmarkService.addBookmark(opt.get(), postId);
    }
    public boolean removeBookmark(String studentId, String postId) {
        Optional<Student> opt = studentRepository.findById(studentId);
        if (opt.isEmpty()) {
            System.out.println("존재하지 않는 학생 ID입니다.");
            return false;
        }
        return bookmarkService.removeBookmark(opt.get(), postId);
    }
    //  마이페이지/조회
    public void showMyPage(String studentId) {
        Optional<Student> opt = studentRepository.findById(studentId);
        if (opt.isEmpty()) {
            System.out.println("존재하지 않는 학생 ID입니다.");
            return;
        }
        Student student = opt.get();
        System.out.println("===== 마이페이지 =====");
        System.out.println("이름: " + student.getName() + " (학번: " + student.getStudentNo() + ")");
        System.out.println("이메일: " + student.getEmail());
        System.out.println("- 등록한 분반:");
        if (student.getEnrollments().isEmpty()) {
            System.out.println("  (없음)");
        } else {
            for (Enrollment e : student.getEnrollments()) {
                Section s = e.getSection();
                System.out.println("· " + s.getCourse().getCourseName() + " / " + s.getSectionCode() + " 분반");
            }
        }
        System.out.println("- 작성한 게시글:");
        List<Post> myPosts = postService.getPostsByStudent(student);
        if (myPosts.isEmpty()) {
            System.out.println(" (없음)");
        } else {
            for (Post p : myPosts) {
                System.out.println("· [" + p.getPostId() + "] " + p.getTitle());
            }
        }

        System.out.println("- 북마크한 게시글:");
        List<Post> bookmarks = bookmarkService.getBookmarks(student);
        if (bookmarks.isEmpty()) {
            System.out.println(" (없음)");
        } else {
            for (Post p : bookmarks) {
                System.out.println("· [" + p.getPostId() + "] " + p.getTitle());
            }
        }
    }
    public void showAllStudents() {
        List<Student> all = studentRepository.findAll();
        if (all.isEmpty()) {
            System.out.println("등록된 학생이 없습니다.");
            return;
        }
        System.out.println("===== 전체 학생 목록 =====");
        for (Student s : all) {
            System.out.println(s.getUserId() + " | " + s.getName() + " | 학번: " + s.getStudentNo());
        }
    }
    public void showAllProfessors() {

        List<Professor> all = professorRepository.findAll();
        if (all.isEmpty()) {
            System.out.println("등록된 교수가 없습니다.");
            return;
        }
        System.out.println("===== 전체 교수 목록 =====");
        for (Professor p : all) {
            System.out.println(p.getUserId() + " | " + p.getName() + " | 교수번호: " + p.getProfessorNo());
        }
    }
    public void showAllSections() {
        List<Section> all = sectionRepository.findAll();
        if (all.isEmpty()) {
            System.out.println("등록된 분반이 없습니다.");
            return;
        }
        System.out.println("===== 전체 분반 목록 =====");
        for (Section s : all) {
            String prof = (s.getProfessor() == null) ? "(미배정)" : s.getProfessor().getName();
            String course = (s.getCourse() == null) ? "(과목없음)" : s.getCourse().getCourseName();
            System.out.println(course + " / " + s.getSectionCode() + " 분반 / 담당: " + prof);
        }
    }

    private void printPost(Post p) {
        System.out.println("[" + p.getPostId() + "] "
                + p.getTitle()
                + " (작성자: " + p.getDisplayAuthorName()
                + ", 교수공개: " + (p.isProfessorVisible() ? "Y" : "N") + ")");
        System.out.println("    " + p.getContent());
        if (!p.getComments().isEmpty()) {
            System.out.println("    -- 댓글 --");
            for (Comment c : p.getComments()) {
                String prefix = c.isReply() ? " ->" : "  · ";
                System.out.println(prefix + "[" + c.getCommentId() + "] " + c.getDisplayAuthorName() + ": " + c.getContent());
            }
        }
    }
}