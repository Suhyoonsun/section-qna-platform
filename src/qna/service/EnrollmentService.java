package qna.service;

import qna.domain.Enrollment;
import qna.domain.Invitation;
import qna.domain.Section;
import qna.domain.Student;
import qna.repository.EnrollmentRepository;
import qna.util.IdGenerator;

import java.time.LocalDateTime;

/**
 * EnrollmentService는 학생의 분반 수강 등록 흐름을 담당한다.
 *
 * SRP:
 *  - 초대 코드 검증 → 중복 등록 확인 → Enrollment 생성 → 코드 사용 처리 순서의 흐름만 책임진다.
 *
 * DIP (핵심 포인트):
 *  - PostService/CommentService는 Enrollment 목록을 직접 조회하지 않고
 *    이 클래스의 isEnrolled()를 호출하여 등록 여부를 확인한다.
 */
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final InvitationService invitationService;

    public EnrollmentService(EnrollmentRepository enrollmentRepository, InvitationService invitationService) {
        this.enrollmentRepository = enrollmentRepository;
        this.invitationService = invitationService;
    }

    // 1. 초대 코드 검증 → 2. 중복 등록 확인 → 3. Enrollment 생성 → 4. 코드 사용 처리
    public Enrollment enrollStudent(Student student, String invitationCode) {
        Invitation invitation = invitationService.validateInvitation(invitationCode);
        Section section = invitation.getSection();

        if (enrollmentRepository.findByStudentAndSection(student, section).isPresent()) {
            throw new IllegalStateException("이미 등록된 분반입니다.");
        }

        String enrollmentId = IdGenerator.generateEnrollmentId();
        Enrollment enrollment = new Enrollment(enrollmentId, student, section, LocalDateTime.now());

        enrollmentRepository.save(enrollment);
        section.addEnrollment(enrollment);

        invitationService.useInvitation(invitationCode);

        return enrollment;
    }

    // PostService/CommentService가 Enrollment 목록을 직접 뒤지지 않고 이 메서드를 호출한다(DIP).
    public boolean isEnrolled(Student student, Section section) {
        return enrollmentRepository.findByStudentAndSection(student, section).isPresent();
    }
}
