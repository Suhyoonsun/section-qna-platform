package qna.domain;
import java.io.Serializable;
import java.time.LocalDateTime;
/**
 * Invitation은 교수가 분반에 발급하는 1회성 초대 코드를 표현한다.
 *
 * SRP:
 *  - 코드 자체의 유효성 판단(isValid)과 사용 처리(use)만 담당한다.
 *  - 누가 어떤 분반에 발급할 수 있는지 같은 비즈니스 흐름은 InvitationService가 담당한다.
 *
 * 1회성 보장 (발표 핵심 포인트):
 *  - usedAt이 null이 아니면 이미 사용된 코드.
 *  - now가 expiredAt 이후이면 만료된 코드.
 *  - isValid는 "사용되지 않았고 + 만료되지 않았을 때"만 true.
 *  - 이 단순한 두 필드 조합만으로 "1회성"과 "유효시간" 정책이 모두 표현된다.
 */
public class Invitation implements Serializable {
    private static final long serialVersionUID = 1L;
    private String code;                 // 초대 코드 문자열 (IdGenerator로 발급)
    private Section section;             // 어느 분반에 대한 초대인지
    private LocalDateTime createdAt;     // 발급 시각
    private LocalDateTime expiredAt;     // 만료 시각
    // usedAt이 null이 아니면 이미 사용된 초대 코드이므로 재사용할 수 없다.
    private LocalDateTime usedAt;
    public Invitation(String code, Section section,
                      LocalDateTime createdAt, LocalDateTime expiredAt) {
        this.code = code;
        this.section = section;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
        this.usedAt = null;
    }
    /**
     *
     현재 시각 기준으로 사용 가능한지 판단.
     * 이 메서드는 외부 데이터에 의존하지 않고 자기 상태만으로 결론낸다(SRP).
     */
    public boolean isValid(LocalDateTime now) {
        if (usedAt != null) return false;        // 이미 사용됨
        if (now.isAfter(expiredAt)) return false; // 만료됨
        return true;
    }
    /**
     * 초대 코드를 사용 처리한다.
     * usedAt 한 번만 설정함으로써 "1회성" 정책이 자연스럽게 강제된다.
     */
    public void use(LocalDateTime now) {
        this.usedAt = now;
    }

    public boolean isUsed() {
        return usedAt != null;
    }
    public String getCode() { return code; }
    public Section getSection() { return section; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getExpiredAt() { return expiredAt; }
    public LocalDateTime getUsedAt() { return usedAt; }
}