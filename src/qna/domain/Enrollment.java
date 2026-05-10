package qna.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Enrollment는 "어떤 학생이 어떤 분반에 언제 등록되었는가"를 표현하는 도메인이다.
 *
 * SRP:
 * - 등록 자체의 식별 정보와 시간만 보유한다.
 * - "이미 등록되어 있는지"를 판단하는 로직은 EnrollmentService가 담당한다.
 *
 * 발표 포인트:
 * - PostService/CommentService는 Enrollment 목록을 직접 뒤지지 않고
 * EnrollmentService.isEnrolled()를 호출해서 검증한다(DIP).
 */
public class Enrollment implements Serializable {
    private static final long serialVersionUID = 1L;

    private String enrollmentId; // 등록 식별자
    private Student student;     // 등록한 학생
    private Section section;     // 등록 대상 분반
    private LocalDateTime enrolledAt; // 등록 시각

    public Enrollment(String enrollmentId, Student student, Section section, LocalDateTime enrolledAt) {
        this.enrollmentId = enrollmentId;
        this.student = student;
        this.section = section;
        this.enrolledAt = enrolledAt;
    }

    public String getEnrollmentId() { return enrollmentId; }
    public Student getStudent() { return student; }
    public Section getSection() { return section; }
    public LocalDateTime getEnrolledAt() { return enrolledAt; }
}
