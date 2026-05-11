package qna.factory;

import qna.domain.Invitation;
import qna.domain.Section;
import qna.util.IdGenerator;

import java.time.LocalDateTime;

/**
 * InvitationFactory는 초대 코드 객체의 생성 책임을 담당
 *
 *  - 초대 코드 문자열, 발급 시각, 만료 시각 계산을 한곳에 모아 Factory Pattern 만족
 *  - InvitationService는 "몇 분짜리 초대인가"만 전달하면 됨
 *  - "이 코드가 유효한가" 같은 판단은 Invitation 도메인이 "누가 발급할 수 있는가"는 InvitationService가 생성은 Factory가 담당하도록 하여 SRP 만족
 */
public class InvitationFactory {

    public Invitation createInvitation(Section section, int validMinutes) {
        String code = IdGenerator.generateInvitationCode();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredAt = now.plusMinutes(validMinutes);
        return new Invitation(code, section, now, expiredAt);
    }
}