package qna.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Student는 학생 사용자 도메인
 *
 *  - 학생의 식별 정보(studentNo)와 학생이 보유한 상태(enrollments, bookmarks)만 갖도록 하여 SRP 만족
 *  - 북마크 추가/삭제 같은 행위는 BookmarkService가 담당하도록 하여 SRP 만족
 *  - 수강 등록 행위는 EnrollmentService가 담당하도록 하여 SRP 만족
 *  - User를 상속하여 새로운 역할을 추가하기 쉬운 구조를 유지하도록 하여 OCP 만족
 */
public class Student extends User {

    private static final long serialVersionUID = 1L;

    // 학번. userId와는 별개의 도메인 식별자
    private String studentNo;

    // 학생이 등록한 분반 목록. Section 양방향 관계를 위해 Student도 보관함
    private List<Enrollment> enrollments;

    // 북마크한 게시글 목록. 상태만 보유하고 추가/삭제는 BookmarkService가 수행
    private List<Post> bookmarks;

    public Student(String userId, String name, String email, String password, String studentNo) {
        super(userId, name, email, password);
        this.studentNo = studentNo;
        this.enrollments = new ArrayList<>();
        this.bookmarks = new ArrayList<>();
    }

    public String getStudentNo() { return studentNo; }
    public List<Enrollment> getEnrollments() { return enrollments; }
    public List<Post> getBookmarks() { return bookmarks; }

    // Enrollment를 학생 측 목록에 추가하여 실제 등록 검증/생성은 EnrollmentService가 수행하고 도메인은 단순 상태 반영만 책임짐
    public void addEnrollment(Enrollment enrollment) {
        this.enrollments.add(enrollment);
    }
}