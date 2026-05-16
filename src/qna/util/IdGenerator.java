package qna.util;
import java.util.UUID;
/**
 * IdGenerator는 시스템 전반의 식별자를 한 곳에서 만든다.
 *
 * SRP:
 *  - "ID 생성"이라는 단일 책임만 수행한다.
 *  - 도메인/Service가 직접 UUID를 호출하지 않고, 항상 이 클래스를 통하게 함으로써
 *
 추후 ID 정책(접두어/길이/규칙)을 한 곳에서 변경할 수 있다.
 *
 * 사용처:
 *  - UserFactory, InvitationFactory, PostService, CommentService, EnrollmentService 등
 *
 * 인스턴스화 방지:
 *  - 모든 메서드가 static이므로 외부에서 인스턴스를 만들 필요가 없다.
 */
public final class IdGenerator {
    // 유틸 클래스는 외부 인스턴스화를 막는다.
    private IdGenerator() {}
    public static String generateUserId() {
        return "U-" + shortUuid();
    }
    public static String generatePostId() {
        return "P-" + shortUuid();
    }
    public static String generateCommentId() {
        return "C-" + shortUuid();
    }
    public static String generateEnrollmentId() {
        return "E-" + shortUuid();
    }
    public static String generateInvitationCode() {
        // 콘솔에서 사람이 읽기 쉽도록 대문자로 표기한다.
        return "INV-" + shortUuid().toUpperCase();
    }
    /** UUID의 앞 8자만 잘라 ID 문자열을 짧게 유지한다. */
    private static String shortUuid() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}