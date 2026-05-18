<!-- docs/development-process.md -->
# 개발 과정 기록
이 문서는 팀이 분반별 Q&A 게시판 시스템을 설계하고 구현하면서 거친 의사결정과 어려웠던 점, 해결 과정을 정리한 자료입니다. 발표
시 "결과물"이 아닌 "과정"을 보여주기 위해 작성했습니다.
## 1. 요구사항 분석 단계
교수님 피드백을 정리하면 다음과 같았습니다.

- 실제 웹사이트로 만들 필요 없음
- DB까지 만들 필요 없음
- 콘솔 출력으로 충분
- 핵심은 "객체지향 설계와 디자인 패턴을 어떻게 적용했는가"
이를 통해 팀은 다음 원칙을 세웠습니다.

- UI/DB 같은 외부 의존을 의도적으로 제거하고, **클래스 분리와 책임 분배에만 집중**한다.
- 데이터 보관은 `Serializable` 기반 파일 저장으로 단순화하지만, 그 자체도 별도 계층(`StorageManager`)으로 분리한다.
## 2. 도메인 모델 설계 단계
처음에는 모든 기능을 담은 큰 클래스(예: `User`, `Board`)를 만들 뻔했지만, 팀 토론을 통해 다음 분리를 채택했습니다.

- `User`를 추상 클래스로 두고 `Student`/`Professor`로 분기 → OCP를 의식한 결정
- 분반(`Section`)이 게시글, 초대 코드, 등록 학생을 모두 들고 있게 → 분반별 격리된 Q&A 공간을 도메인 모델 자체로 표현
- 댓글은 `parentComment + replies` 자기참조 구조 → 단일 클래스로 댓글/대댓글을 모두 표현
## 3. 권한 검증 위치를 결정한 과정
"수강 등록되지 않은 학생은 글을 못 쓴다"라는 규칙을 어디서 검사할지 토론했습니다.

- 후보 1: `Post`/`Comment` 도메인이 직접 검증 → 도메인이 외부 데이터(`Enrollment` 목록)를 알아야 해서 SRP 위반
- 후보 2: `PostService`가 `AppData`에서 등록 정보를 직접 뒤짐 → DIP 위반, 데이터 구조 변경 시 영향 받음
- 최종: `EnrollmentService.isEnrolled(student, section)`을 만들고, 다른 Service들이 이 메서드만 호출하게 함 → DIP 만족
이 결정 덕분에 권한 정책이 한 군데로 모이고, 이후 권한 종류가 늘어도 다른 Service를 손대지 않게 됐습니다.
## 4. 1회성 초대 코드 정책을 표현한 과정
"초대 코드를 한 번만 쓸 수 있게 하라"는 요구사항을 어떻게 표현할지 고민했습니다.

- 별도 `boolean used` 플래그를 둘 수도 있었지만,
- `LocalDateTime usedAt` 한 필드로 "사용 여부 + 사용 시점"을 모두 표현하는 쪽이 더 단순했고,
- `isValid(now)`가 자기 상태만으로 결론낼 수 있어 SRP에 맞았습니다.
발표 때 강조할 포인트는 "정책을 boolean이 아니라 도메인 의미가 있는 시간 필드로 표현했다"는 점입니다.
## 5. 디자인 패턴 적용 결정

- **Repository Pattern**: DB가 없으니 처음에는 Service가 List에 직접 접근해도 된다는 의견이 있었지만, 추후 DB 도입을 가정한 발표 흐름을 만들기 위해 도입.
- **Factory Pattern**: `Student/Professor/Invitation`은 ID와 시간 계산이 필요한데, 이를 Service에 흩어놓으면 정책 변경이 어려워 Factory에 모음.
- **Facade Pattern**: 메뉴 19개를 Main에 그대로 작성하면 Service 6개를 Main이 모두 알아야 했음. Facade를 둠으로써 Main이 ID 입력만 받고 Facade에 위임하는 구조로 단순화.
## 6. 익명 정책을 코드로 강제한 과정
"댓글의 익명 여부는 작성 시점에 고정"이라는 정책을 잘못 구현하면 `update`에서 anonymous를 같이 받게 됩니다. 팀은 의도적으로
`Comment.update(String content)`만 두고, 메서드 시그니처 자체로 정책을 강제했습니다. 이후 누군가 anonymous를 받도록
시그니처를 바꾸려고 하면, 정책 위반이 컴파일 단계나 코드 리뷰 단계에서 잡힙니다.
## 7. 어려웠던 점과 해결 과정

- **순환 참조에 대한 우려**: `Student ↔ Enrollment ↔ Section`이 서로를 가리키는 구조이지만, Java 직렬화는 순환을 처리할 수 있고 객체 그래프가 깨지지 않으므로 그대로 채택. 단, equals/hashCode를 도메인에서 임의로 구현하지 않고 ID 비교(`getUserId().equals(...)`)로 통일하여 컬렉션 동작이 예측 가능하도록 함.
- **데이터 무결성 유지**: `Section.removePost`처럼 양방향 일관성을 유지해주는 메서드를 항상 짝지어 호출하도록 Service에서 책임짐.
- **콘솔 입력의 안정성**: `Scanner.nextLine`만 사용하고, 숫자 입력은 `try/catch`로 감싸 잘못된 입력에도 프로그램이 죽지 않도록 함.
- **저장 실패 시 복구**: `StorageManager.load`가 직렬화 오류 시 빈 `AppData`를 돌려주도록 함으로써, 손상된 파일이 있어도 프로그램이 계속 동작.
## 8. 만약 다음 단계가 있다면

- Repository를 인터페이스로 추출하고 InMemory/JDBC 구현체로 갈아끼우는 실험 (DIP 강화)
- `User`에 `Assistant` 등 새 역할을 추가하여 OCP가 실제로 잘 작동하는지 확인
- 댓글 트리 깊이 제한, 게시글 검색, 페이지네이션 등 기능 확장
- 콘솔 UI를 분리해 별도 `ConsoleUI` 클래스로 추출하면 Main이 더 단순해짐
## 9. Git Commit History 기준 개발 과정
1. `chore: initialize java console project structure` — 프로젝트 골격
2. `feat: add user domain model` — User/Student/Professor (OCP)
3. `feat: add course section and enrollment model` — 분반 공간 분리
4. `feat: add post and comment domain model` — 자기참조 구조, 익명 정책
5. `feat: add invitation domain and id generator` — 1회성 초대 코드
6. `feat: add file based persistence storage` — Serializable 저장
7. `feat: add in memory repositories` — Repository Pattern
8. `feat: add factories for users and invitations` — Factory Pattern
9. `feat: implement section invitation and enrollment services`
10. `feat: implement post service with enrollment validation` — DIP의 핵심 적용
11. `feat: implement comment and reply service`
12. `feat: implement bookmark service` — 상태/행위 분리(SRP)
13. `feat: add qna board facade` — Facade Pattern
14. `feat: implement console menu application`
15. `docs: add project documentation for final presentation`

이 순서를 그대로 따라가면, 도메인부터 저장까지의 의존 관계가 자연스럽게 형성되고, GitHub History 자체가 발표 자료가 됩니다.