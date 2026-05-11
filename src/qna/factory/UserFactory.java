package qna.factory;

import qna.domain.Professor;
import qna.domain.Student;
import qna.util.IdGenerator;

/**
 * UserFactory는 Student/Professor 같은 사용자 객체의 생성 책임을 담당
 *
 *  - 사용자 객체를 만들 때마다 IdGenerator를 직접 호출하지 않고 Factory가 일괄적으로 ID를 부여하여 Factory Pattern 만족
 *  - 어떻게 만드는가가 한 곳에 모여 있어 추후 ID 정책 변경이 쉬워짐
 *  - 검증/저장은 Service/Repository가 담당하고 여기서는 생성만 책임지므로 SRP 만족
 */
public class UserFactory {

    public Student createStudent(String name, String email, String password, String studentNo) {
        String userId = IdGenerator.generateUserId();
        return new Student(userId, name, email, password, studentNo);
    }

    public Professor createProfessor(String name, String email, String password, String professorNo) {
        String userId = IdGenerator.generateUserId();
        return new Professor(userId, name, email, password, professorNo);
    }
}