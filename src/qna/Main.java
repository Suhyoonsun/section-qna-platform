package qna;

import qna.facade.QnaBoardFacade;
import qna.factory.InvitationFactory;
import qna.factory.UserFactory;
import qna.repository.CommentRepository;
import qna.repository.CourseRepository;
import qna.repository.EnrollmentRepository;
import qna.repository.InvitationRepository;
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
import qna.storage.AppData;
import qna.storage.StorageManager;

import java.util.Scanner;

/**
 * Main
 *  - 시작 시 StorageManager.load()로 AppData 복원하고 Repository, Factory, Service, Facade를 차례로 생성함
 *  - Scanner로 메뉴 입력을 받아 QnaBoardFacade 메서드를 호출함
 *  - 종료 시 StorageManager.save()로 AppData 저장함
 *
 *  - Main은 메뉴 흐름과 입력만 담당하도록 하여 Facade Pattern 만족
 *  - try/catch문을 사용하여 잘못된 입력이나 예외가 발생해도 프로그램이 죽지 않도록 했음
 */

public class Main {

    public static void main(String[] args) {
        // 1) Storage 초기화, 데이터 로드
        StorageManager storageManager = new StorageManager();
        AppData appData = storageManager.load();

        // 2) Repository 계층
        StudentRepository studentRepository = new StudentRepository(appData);
        ProfessorRepository professorRepository = new ProfessorRepository(appData);
        CourseRepository courseRepository = new CourseRepository(appData);
        SectionRepository sectionRepository = new SectionRepository(appData);
        EnrollmentRepository enrollmentRepository = new EnrollmentRepository(appData);
        PostRepository postRepository = new PostRepository(appData);
        CommentRepository commentRepository = new CommentRepository(appData);
        InvitationRepository invitationRepository = new InvitationRepository(appData);

        // 3) Factory 계층
        UserFactory userFactory = new UserFactory();
        InvitationFactory invitationFactory = new InvitationFactory();

        // 4) Service 계층
        SectionService sectionService = new SectionService(sectionRepository, courseRepository);
        InvitationService invitationService = new InvitationService(invitationRepository, invitationFactory);
        EnrollmentService enrollmentService = new EnrollmentService(enrollmentRepository, invitationService);
        PostService postService = new PostService(postRepository, enrollmentService);
        CommentService commentService = new CommentService(commentRepository, postRepository, enrollmentService);
        BookmarkService bookmarkService = new BookmarkService(postRepository);

        // 5) Facade
        QnaBoardFacade facade = new QnaBoardFacade(
                studentRepository, professorRepository, courseRepository,
                sectionRepository, postRepository,
                userFactory,
                sectionService, invitationService, enrollmentService,
                postService, commentService, bookmarkService);

        // 6) 메뉴 루프
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            printMenu();
            String input = readLine(scanner);
            try {
                switch (input) {
                    case "1":
                        facade.createDemoData();
                        break;
                    case "2":
                        handleCreateSection(scanner, facade);
                        break;
                    case "3":
                        handleCreateInvitation(scanner, facade);
                        break;
                    case "4":
                        handleEnroll(scanner, facade);
                        break;
                    case "5":
                        handleCreatePost(scanner, facade);
                        break;
                    case "6":
                        handleShowPostsBySection(scanner, facade);
                        break;
                    case "7":
                        handleShowMyPosts(scanner, facade);
                        break;
                    case "8":
                        handleUpdatePost(scanner, facade);
                        break;
                    case "9":
                        handleDeletePost(scanner, facade);
                        break;
                    case "10":
                        handleCreateComment(scanner, facade);
                        break;
                    case "11":
                        handleCreateReply(scanner, facade);
                        break;
                    case "12":
                        handleUpdateComment(scanner, facade);
                        break;
                    case "13":
                        handleAddBookmark(scanner, facade);
                        break;
                    case "14":
                        handleRemoveBookmark(scanner, facade);
                        break;
                    case "15":
                        handleShowMyPage(scanner, facade);
                        break;
                    case "16":
                        facade.showAllStudents();
                        break;
                    case "17":
                        facade.showAllProfessors();
                        break;
                    case "18":
                        facade.showAllSections();
                        break;
                    case "19":
                        // 저장 후 종료
                        storageManager.save(appData);
                        System.out.println("저장 후 종료합니다.");
                        running = false;
                        break;
                    default:
                        System.out.println("올바른 메뉴 번호를 입력해주세요.");
                }
            } catch (Exception e) {
                // 잘못된 입력을 받더라도 프로그램이 죽지 않도록
                System.out.println("처리 중 오류가 발생했습니다: " + e.getMessage());
            }
            System.out.println();
        }
        scanner.close();
    }

    // 메뉴 출력

    private static void printMenu() {
        System.out.println("===== 분반별 Q&A 게시판 =====");
        System.out.println("1. 초기 데모 데이터 생성");
        System.out.println("2. 교수: 분반 생성");
        System.out.println("3. 교수: 초대 코드 생성");
        System.out.println("4. 학생: 초대 코드로 수강 등록");
        System.out.println("5. 학생: 게시글 작성");
        System.out.println("6. 학생: 분반별 게시글 조회");
        System.out.println("7. 학생: 내 게시글 조회");
        System.out.println("8. 학생: 게시글 수정");
        System.out.println("9. 학생: 게시글 삭제");
        System.out.println("10. 학생: 댓글 작성");
        System.out.println("11. 학생: 대댓글 작성");
        System.out.println("12. 학생: 댓글 수정");
        System.out.println("13. 학생: 게시글 북마크");
        System.out.println("14. 학생: 북마크 해제");
        System.out.println("15. 학생: 마이페이지 조회");
        System.out.println("16. 전체 학생 목록 조회");
        System.out.println("17. 전체 교수 목록 조회");
        System.out.println("18. 전체 분반 목록 조회");
        System.out.println("19. 저장 후 종료");
        System.out.print("선택: ");
    }

    // 입력 헬퍼

    private static String readLine(Scanner scanner) {
        if (!scanner.hasNextLine()) return "";
        return scanner.nextLine().trim();
    }

    private static boolean readBoolean(Scanner scanner, String label) {
        System.out.print(label + " (y/n): ");
        String s = readLine(scanner);
        return s.equalsIgnoreCase("y") || s.equalsIgnoreCase("yes");
    }

    private static int readInt(Scanner scanner, String label, int defaultValue) {
        System.out.print(label + ": ");
        String s = readLine(scanner);
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("숫자가 아닙니다. 기본값 " + defaultValue + "을(를) 사용합니다.");
            return defaultValue;
        }
    }

    // 메뉴 핸들러

    private static void handleCreateSection(Scanner scanner, QnaBoardFacade facade) {
        System.out.print("과목 코드: ");
        String courseCode = readLine(scanner);
        System.out.print("분반 코드: ");
        String sectionCode = readLine(scanner);
        facade.createSection(courseCode, sectionCode);
    }

    private static void handleCreateInvitation(Scanner scanner, QnaBoardFacade facade) {
        System.out.print("교수 ID(userId): ");
        String professorId = readLine(scanner);
        System.out.print("분반 코드: ");
        String sectionCode = readLine(scanner);
        int validMinutes = readInt(scanner, "유효 시간(분)", 60);
        var invitation = facade.createInvitation(professorId, sectionCode, validMinutes);
        if (invitation != null) {
            System.out.println("발급된 초대 코드: " + invitation.getCode());
        }
    }

    private static void handleEnroll(Scanner scanner, QnaBoardFacade facade) {
        System.out.print("학생 ID(userId): ");
        String studentId = readLine(scanner);
        System.out.print("초대 코드: ");
        String code = readLine(scanner);
        var enrollment = facade.enrollStudentByInvitation(studentId, code);
        if (enrollment != null) {
            System.out.println("수강 등록 완료. 분반: "
                    + enrollment.getSection().getSectionCode());
        }
    }

    private static void handleCreatePost(Scanner scanner, QnaBoardFacade facade) {
        System.out.print("학생 ID(userId): ");
        String studentId = readLine(scanner);
        System.out.print("분반 코드: ");
        String sectionCode = readLine(scanner);
        System.out.print("제목: ");
        String title = readLine(scanner);
        System.out.print("내용: ");
        String content = readLine(scanner);
        boolean anonymous = readBoolean(scanner, "익명으로 작성하시겠습니까?");
        boolean professorVisible = readBoolean(scanner, "교수에게도 공개하시겠습니까?");
        var post = facade.createPost(studentId, sectionCode, title, content, anonymous, professorVisible);
        if (post != null) {
            System.out.println("게시글 작성 완료. ID: " + post.getPostId());
        }
    }

    private static void handleShowPostsBySection(Scanner scanner, QnaBoardFacade facade) {
        System.out.print("분반 코드: ");
        String sectionCode = readLine(scanner);
        facade.showPostsBySection(sectionCode);
    }

    private static void handleShowMyPosts(Scanner scanner, QnaBoardFacade facade) {
        System.out.print("학생 ID(userId): ");
        String studentId = readLine(scanner);
        facade.showMyPosts(studentId);
    }

    private static void handleUpdatePost(Scanner scanner, QnaBoardFacade facade) {
        System.out.print("학생 ID(userId): ");
        String studentId = readLine(scanner);
        System.out.print("게시글 ID: ");
        String postId = readLine(scanner);
        System.out.print("새 제목: ");
        String title = readLine(scanner);
        System.out.print("새 내용: ");
        String content = readLine(scanner);
        boolean anonymous = readBoolean(scanner, "익명으로 변경하시겠습니까?");
        boolean professorVisible = readBoolean(scanner, "교수 공개로 변경하시겠습니까?");
        var post = facade.updatePost(studentId, postId, title, content, anonymous, professorVisible);
        if (post != null) {
            System.out.println("게시글 수정 완료.");
        }
    }

    private static void handleDeletePost(Scanner scanner, QnaBoardFacade facade) {
        System.out.print("학생 ID(userId): ");
        String studentId = readLine(scanner);
        System.out.print("게시글 ID: ");
        String postId = readLine(scanner);
        boolean ok = facade.deletePost(studentId, postId);
        if (ok) System.out.println("게시글 삭제 완료.");
    }

    private static void handleCreateComment(Scanner scanner, QnaBoardFacade facade) {
        System.out.print("학생 ID(userId): ");
        String studentId = readLine(scanner);
        System.out.print("게시글 ID: ");
        String postId = readLine(scanner);
        System.out.print("내용: ");
        String content = readLine(scanner);
        boolean anonymous = readBoolean(scanner, "익명으로 작성하시겠습니까?");
        var c = facade.createComment(studentId, postId, content, anonymous);
        if (c != null) {
            System.out.println("댓글 작성 완료. ID: " + c.getCommentId());
        }
    }

    private static void handleCreateReply(Scanner scanner, QnaBoardFacade facade) {
        System.out.print("학생 ID(userId): ");
        String studentId = readLine(scanner);
        System.out.print("부모 댓글 ID: ");
        String parentCommentId = readLine(scanner);
        System.out.print("내용: ");
        String content = readLine(scanner);
        boolean anonymous = readBoolean(scanner, "익명으로 작성하시겠습니까?");
        var c = facade.createReply(studentId, parentCommentId, content, anonymous);
        if (c != null) {
            System.out.println("대댓글 작성 완료. ID: " + c.getCommentId());
        }
    }

    private static void handleUpdateComment(Scanner scanner, QnaBoardFacade facade) {
        System.out.print("학생 ID(userId): ");
        String studentId = readLine(scanner);
        System.out.print("댓글 ID: ");
        String commentId = readLine(scanner);
        System.out.print("새 내용: ");
        String content = readLine(scanner);
        var c = facade.updateComment(studentId, commentId, content);
        if (c != null) {
            System.out.println("댓글 수정 완료. (익명 여부는 작성 시점 그대로 유지됩니다.)");
        }
    }

    private static void handleAddBookmark(Scanner scanner, QnaBoardFacade facade) {
        System.out.print("학생 ID(userId): ");
        String studentId = readLine(scanner);
        System.out.print("게시글 ID: ");
        String postId = readLine(scanner);
        boolean ok = facade.addBookmark(studentId, postId);
        if (ok) System.out.println("북마크 추가 완료.");
    }

    private static void handleRemoveBookmark(Scanner scanner, QnaBoardFacade facade) {
        System.out.print("학생 ID(userId): ");
        String studentId = readLine(scanner);
        System.out.print("게시글 ID: ");
        String postId = readLine(scanner);
        boolean ok = facade.removeBookmark(studentId, postId);
        if (ok) System.out.println("북마크 해제 완료.");
    }

    private static void handleShowMyPage(Scanner scanner, QnaBoardFacade facade) {
        System.out.print("학생 ID(userId): ");
        String studentId = readLine(scanner);
        facade.showMyPage(studentId);
    }
}