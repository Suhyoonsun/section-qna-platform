package qna.repository;
import qna.domain.Student;
import qna.storage.AppData;
import java.util.List;
import java.util.Optional;

// StudentRepository는 학생 데이터에 대한 접근 책임만 진다.


public class StudentRepository {
    private final AppData appData;
    public StudentRepository(AppData appData) {
        this.appData = appData;
    }
//    같은 userId가 이미 존재하면 추가하지 않는다(중복 방지).
    public void save(Student student) {
        if (findById(student.getUserId()).isPresent()) return;
        appData.getStudents().add(student);
    }
    public Optional<Student> findById(String userId) {
        for (Student s : appData.getStudents()) {
            if (s.getUserId().equals(userId)) return Optional.of(s);
        }
        return Optional.empty();
    }
    public List<Student> findAll() {
        return appData.getStudents();
    }
}