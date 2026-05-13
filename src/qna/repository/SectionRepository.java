package qna.repository;
import qna.domain.Section;
import qna.storage.AppData;
import java.util.List;
import java.util.Optional;

public class SectionRepository {
    private final AppData appData;
    public SectionRepository(AppData appData) {
        this.appData = appData;
    }
    public void save(Section section) {
        if (findByCode(section.getSectionCode()).isPresent()) return;
        appData.getSections().add(section);
    }
    public Optional<Section> findByCode(String sectionCode) {
        for (Section s : appData.getSections()) {
            if (s.getSectionCode().equals(sectionCode)) return Optional.of(s);
        }
        return Optional.empty();
    }
    public List<Section> findAll() {
        return appData.getSections();
    }
}