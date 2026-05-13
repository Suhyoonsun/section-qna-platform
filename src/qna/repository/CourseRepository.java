package qna.repository;
import qna.domain.Course;
import qna.storage.AppData;
import java.util.List;
import java.util.Optional;

//  CourseRepository는 과목 데이터 접근만 담당한다.

public class CourseRepository {
    private final AppData appData;
    public CourseRepository(AppData appData) {
        this.appData = appData;
    }
    public void save(Course course) {
        if (findByCode(course.getCourseCode()).isPresent()) return;
        appData.getCourses().add(course);
    }
    public Optional<Course> findByCode(String courseCode) {
        for (Course c : appData.getCourses()) {
            if (c.getCourseCode().equals(courseCode)) return Optional.of(c);
        }
        return Optional.empty();
    }
    public List<Course> findAll() {
        return appData.getCourses();
    }
}