package qna.service;

import qna.domain.Course;
import qna.domain.Professor;
import qna.domain.Section;
import qna.repository.CourseRepository;
import qna.repository.SectionRepository;

/**
 * SectionService는 분반 생성과 교수 배정 흐름을 담당한다.
 *
 * SRP:
 *  - "분반을 만들고 교수를 배정한다"는 한 가지 흐름만 처리한다.
 *  - Section 도메인은 상태 보유만, SectionRepository는 저장만 담당한다.
 */
public class SectionService {

    private final SectionRepository sectionRepository;
    private final CourseRepository courseRepository;

    public SectionService(SectionRepository sectionRepository, CourseRepository courseRepository) {
        this.sectionRepository = sectionRepository;
        this.courseRepository = courseRepository;
    }

    public Section createSection(Course course, String sectionCode) {
        if (sectionRepository.findByCode(sectionCode).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 분반 코드입니다: " + sectionCode);
        }
        Section section = new Section(sectionCode, course);
        course.addSection(section);
        sectionRepository.save(section);
        return section;
    }

    public void assignProfessor(Section section, Professor professor) {
        section.changeProfessor(professor);
    }
}
