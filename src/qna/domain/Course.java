package qna.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Course는 강의 과목을 나타낸다. 하나의 Course는 여러 Section(분반)을 가질 수 있다.
 *
 * SRP:
 * - 과목 자체의 식별 정보와 자신이 가진 분반 목록만 보유한다.
 * - 분반 생성 로직은 SectionService가 담당한다.
 */
public class Course implements Serializable {
    private static final long serialVersionUID = 1L;

    private String courseCode; // 과목 코드 (예: 704828)
    private String courseName; // 과목명 (예: 디자인패턴프로그래밍및실습)
    private List<Section> sections;

    public Course(String courseCode, String courseName) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.sections = new ArrayList<>();
    }

    public void addSection(Section section) {
        this.sections.add(section);
    }

    public List<Section> getSections() { return sections; }
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
}
