package qna.domain;

import java.io.Serializable;

/**
 * User는 모든 사용자의 공통 속성과 행위를 정의하는 추상 클래스
 *
 *  - 사용자 정보와 정보 변경만 담당하도록 하고 인증/권한 검증/저장 같은 책임은 갖지 않도록 하여 SRP만족
 *  - User를 abstract로 두고 Student, Professor가 상속하도록 함
 *    추후 Assistant(조교) 등이 추가되어도 User를 수정하지 않고 새 클래스를 추가할 수 있도록 하여 OCP만족
 *
 *  - DB를 사용하지 않는 대신 Java Serializable을 이용해 데이터를 파일로 저장
 */
public abstract class User implements Serializable {

    private static final long serialVersionUID = 1L;

    // 사용자 식별에 사용하는 고유 ID (UserFactory + IdGenerator로 생성)
    protected String userId;
    protected String name;
    protected String email;
    protected String password;

    // 추상 클래스이지만 자식 생성자에서 super(...)로 호출하기 위해 protected 생성자를 만듦
    protected User(String userId, String name, String email, String password) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // 사용자 본인의 정보 변경
    public void updateInfo(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // 마이페이지/목록 조회 등에서 사용할 수 있는 사용자 요약 정보
    public String getProfile() {
        return "userId=" + userId + ", name=" + name + ", email=" + email;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
}