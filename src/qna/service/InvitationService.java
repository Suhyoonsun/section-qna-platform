package qna.service;

import qna.domain.Invitation;
import qna.domain.Professor;
import qna.domain.Section;
import qna.factory.InvitationFactory;
import qna.repository.InvitationRepository;

import java.time.LocalDateTime;

/**
 * InvitationService는 초대 코드의 발급·검증·사용 흐름을 담당한다.
 *
 * SRP:
 *  - "누가 발급할 수 있는가"(교수만 가능)와 "1회성 유효성 검사" 흐름만 책임진다.
 *  - 코드 생성은 InvitationFactory가, 상태 변경은 Invitation 도메인이 담당한다.
 */
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final InvitationFactory invitationFactory;

    public InvitationService(InvitationRepository invitationRepository, InvitationFactory invitationFactory) {
        this.invitationRepository = invitationRepository;
        this.invitationFactory = invitationFactory;
    }

    public Invitation createInvitation(Professor professor, Section section, int validMinutes) {
        Invitation invitation = invitationFactory.createInvitation(section, validMinutes);
        invitationRepository.save(invitation);
        section.addInvitation(invitation);
        return invitation;
    }

    // 코드가 존재하고 아직 유효한지 확인한다.
    public Invitation validateInvitation(String code) {
        Invitation invitation = invitationRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 초대 코드입니다: " + code));
        if (!invitation.isValid(LocalDateTime.now())) {
            throw new IllegalStateException("이미 사용되었거나 만료된 초대 코드입니다: " + code);
        }
        return invitation;
    }

    // 초대 코드를 사용 처리한다(1회성 보장).
    public void useInvitation(String code) {
        Invitation invitation = validateInvitation(code);
        invitation.use(LocalDateTime.now());
    }
}
