package qna.repository;
import qna.domain.Enrollment;
import qna.domain.Section;
import qna.domain.Student;
import qna.storage.AppData;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// EnrollmentRepository는 수강 등록 데이터 접근만 담당한다.

public class EnrollmentRepository {
    private final AppData appData;
    public EnrollmentRepository(AppData appData) {
        this.appData = appData;
    }
    public void save(Enrollment enrollment) {
        appData.getEnrollments().add(enrollment);
    }
    /**
     * 학생-분반 조합으로 등록 정보를 찾는다.
     *
     존재 여부 판정은 EnrollmentService가 Optional의 isPresent로 수행한다.
     */
    public Optional<Enrollment> findByStudentAndSection(Student student, Section section) {
        for (Enrollment e : appData.getEnrollments()) {
            boolean sameStudent = e.getStudent().getUserId().equals(student.getUserId());
            boolean sameSection = e.getSection().getSectionCode().equals(section.getSectionCode());
            if (sameStudent && sameSection) return Optional.of(e);
        }
        return Optional.empty();
    }
    public List<Enrollment> findByStudent(Student student) {
        List<Enrollment> result = new ArrayList<>();
        for (Enrollment e : appData.getEnrollments()) {
            if (e.getStudent().getUserId().equals(student.getUserId())) {
                result.add(e);
            }
        }
        return result;
    }

    public List<Enrollment> findAll() {
        return appData.getEnrollments();
    }
}