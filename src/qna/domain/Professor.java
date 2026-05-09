package qna.domain;

/**
 * Professor는 교수 사용자 도메인
 *
 *  - 교수의 식별 정보(professorNo)만 갖도록 하고 분반 생성/교수 배정은 SectionService가 담당하도록 하여 SRP만족
 *  - User 상속을 통해 다른 역할(조교)을 추가해도 기존 구조가 바뀌지 않도록하여 OCP만족
 */
public class Professor extends User {

    private static final long serialVersionUID = 1L;

    // 교수 식별 번호
    private String professorNo;

    public Professor(String userId, String name, String email, String password, String professorNo) {
        super(userId, name, email, password);
        this.professorNo = professorNo;
    }

    public String getProfessorNo() { return professorNo; }
}