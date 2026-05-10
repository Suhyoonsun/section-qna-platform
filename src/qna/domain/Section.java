package qna.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Section은 분반을 나타낸다.
 * "분반별 Q&A 게시판"의 핵심 단위로, 같은 Section에 등록된 학생만 글을 쓸 수 있다.
 *
 * SRP:
 * - 분반의 상태(담당 교수, 등록자, 게시글, 초대 코드)만 보유한다.
 * - 권한 검증은 EnrollmentService가, 게시글 작성/수정은 PostService가 담당한다.
 *
 * 분반별 공간 분리 설명:
 * - posts, enrollments, invitations를 모두 Section이 보유함으로써,
 * "분반별로 격리된 Q&A 공간"이 도메인 모델 수준에서 자연스럽게 표현된다.
 */
public class Section implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sectionCode;          // 분반 코드 (예: 501)
    private Course course;               // 어느 과목 소속인지
    private Professor professor;         // 담당 교수 (배정 전에는 null 가능)
    private List<Enrollment> enrollments;
    private List<Post> posts;
    private List<Invitation> invitations;

    public Section(String sectionCode, Course course) {
        this.sectionCode = sectionCode;
        this.course = course;
        this.enrollments = new ArrayList<>();
        this.posts = new ArrayList<>();
        this.invitations = new ArrayList<>();
    }

    /**
     * 분반의 담당 교수를 변경한다.
     * 실제로 누가 호출 가능한지(권한 검증)는 SectionService가 책임진다.
     */
    public void changeProfessor(Professor professor) {
        this.professor = professor;
    }

    public void addEnrollment(Enrollment enrollment) { this.enrollments.add(enrollment); }
    public void addPost(Post post) { this.posts.add(post); }
    public void removePost(Post post) { this.posts.remove(post); }
    public void addInvitation(Invitation invitation) { this.invitations.add(invitation); }

    public String getSectionCode() { return sectionCode; }
    public Course getCourse() { return course; }
    public Professor getProfessor() { return professor; }
    public List<Enrollment> getEnrollments() { return enrollments; }
    public List<Post> getPosts() { return posts; }
    public List<Invitation> getInvitations() { return invitations; }
}
