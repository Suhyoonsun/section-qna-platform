package qna.repository;
import qna.domain.Professor;
import qna.storage.AppData;
import java.util.List;
import java.util.Optional;
/**
 * ProfessorRepository는 교수 데이터 접근 책임만 진다.
 * Repository Pattern을 통해 Service가 AppData에 직접 접근하지 못하도록 한다.
 */
public class ProfessorRepository {
    private final AppData appData;
    public ProfessorRepository(AppData appData) {
        this.appData = appData;
    }
    public void save(Professor professor) {
        if (findById(professor.getUserId()).isPresent()) return;
        appData.getProfessors().add(professor);
    }
    public Optional<Professor> findById(String userId) {
        for (Professor p : appData.getProfessors()) {
            if (p.getUserId().equals(userId)) return Optional.of(p);
        }
        return Optional.empty();
    }
    public List<Professor> findAll() {
        return appData.getProfessors();
    }
}