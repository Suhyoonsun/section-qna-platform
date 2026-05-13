package qna.repository;
import qna.domain.Invitation;
import qna.storage.AppData;
import java.util.List;
import java.util.Optional;

//  InvitationRepository는 초대 코드 데이터 접근만 담당
//  코드 문자열로 조회하는 일이 잦으므로 findByCode를 제공

public class InvitationRepository {
    private final AppData appData;
    public InvitationRepository(AppData appData) {
        this.appData = appData;
    }
    public void save(Invitation invitation) {
        if (findByCode(invitation.getCode()).isPresent()) return;
        appData.getInvitations().add(invitation);
    }
    public Optional<Invitation> findByCode(String code) {
        for (Invitation i : appData.getInvitations()) {
            if (i.getCode().equals(code)) return Optional.of(i);
        }
        return Optional.empty();
    }
    public List<Invitation> findAll() {
        return appData.getInvitations();
    }
}