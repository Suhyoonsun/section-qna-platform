# section-qna-board
분반별 Q&A 게시판 시스템 (디자인 패턴 팀 프로젝트)
## 1. 프로젝트 개요
교수가 발급한 1회성 초대 코드를 통해 인증된 학생만 특정 분반의 Q&A 게시판에 참여할 수 있는 콘솔 기반 강의 Q&A 플랫폼입니다.
실제 웹/DB 없이도 객체지향 설계와 디자인 패턴이 어떻게 적용되는지 보여주는 것이 목표입니다.
## 2. 기술 스택과 제약

- Java 17
- Console I/O (Scanner)
- Java Serializable 기반 파일 저장 (`data/qna-data.ser`)
- 외부 라이브러리 없음 (Spring/Web/DB/HTML/Lombok 미사용)
## 3. 실행 방법
```bash
# 컴파일
javac -d out $(find src -name "*.java")
# 실행
java -cp out qna.Main
```
콘솔 메뉴에서 `1. 초기 데모 데이터 생성`을 먼저 선택하면, 발표용 시나리오(교수 1명, 학생 4명, 과목/분반/초대 코드/게시글)가
자동으로 만들어집니다.
## 4. 콘솔 메뉴
```
1. 초기 데모 데이터 생성
2. 교수: 분반 생성
3. 교수: 초대 코드 생성
4. 학생: 초대 코드로 수강 등록
5. 학생: 게시글 작성
6. 학생: 분반별 게시글 조회
7. 학생: 내 게시글 조회
8. 학생: 게시글 수정
9. 학생: 게시글 삭제
10. 학생: 댓글 작성
11. 학생: 대댓글 작성
12. 학생: 댓글 수정
13. 학생: 게시글 북마크
14. 학생: 북마크 해제
15. 학생: 마이페이지 조회
16. 전체 학생 목록 조회
17. 전체 교수 목록 조회
18. 전체 분반 목록 조회
19. 저장 후 종료
```

## 5. 패키지 구조
```
src/qna/
├─ Main.java                       // 콘솔 진입점, 메뉴 루프
├─ domain/                         // 상태 + 자기 행위만 담당하는 도메인
│  ├─ User.java (abstract)         // OCP의 추상화 지점
│  ├─ Student.java
│  ├─ Professor.java
│  ├─ Course.java
│  ├─ Section.java                 // 분반별 격리된 Q&A 공간
│  ├─ Enrollment.java
│  ├─ Post.java
│  ├─ Comment.java                 // 자기참조 구조 (대댓글)
│  └─ Invitation.java              // 1회성 초대 코드
├─ repository/                     // Repository Pattern. AppData 접근 책임 분리
├─ service/                        // 비즈니스 로직, 권한 검증
├─ factory/                        // 객체 생성 책임 분리 (Factory Pattern)
├─ facade/                         // QnaBoardFacade (Facade Pattern)
├─ storage/                        // AppData + StorageManager (Serializable 저장)
└─ util/IdGenerator.java
```
## 6. 적용한 디자인 패턴

- **Repository Pattern**: DB는 없지만 데이터 접근 책임을 별도 계층으로 분리. Service는 `AppData`를 직접 모르고 Repository만 사용.
- **Factory Pattern**: `UserFactory`/`InvitationFactory`가 ID 생성과 시간 계산을 한곳에 모아 객체 생성 책임을 분리.
- **Facade Pattern**: `QnaBoardFacade`가 여러 Service 호출을 묶어 Main이 메뉴/입력에만 집중하게 함.
- **Serializable File Persistence**: `AppData` 한 덩어리를 `data/qna-data.ser` 파일로 저장/복원.
## 7. 적용한 SOLID 원칙

- **SRP**: 도메인은 상태/자기 행위, Service는 비즈니스 흐름, Repository는 데이터 접근, StorageManager는 저장만 담당.
- **OCP**: `User` 추상 클래스 + `Student/Professor` 상속. `Assistant` 같은 새 역할이 추가돼도 기존 구조를 거의 손대지 않음.
- **DIP**: `PostService`/`CommentService`가 Enrollment 목록을 직접 보지 않고 `EnrollmentService.isEnrolled()`에 위임.
## 8. 주요 정책 요약

- 초대 코드는 `usedAt` 한 번 기록으로 1회성을 보장.
- 분반 등록(`Enrollment`)이 모든 작성 권한의 진입 조건.
- 게시글/댓글 익명 정책: 게시글은 수정 시 변경 가능, 댓글은 작성 시점에 고정.
- 댓글-대댓글은 `parentComment + replies` 자기참조 구조.
- 북마크: Student는 List<Post> 상태만 보유, 추가/삭제는 BookmarkService가 담당.
## 9. 데이터 저장 위치

- `data/qna-data.ser`
- `.gitignore`에 `data/`가 포함되어 있어 GitHub에는 올라가지 않습니다. 각자 로컬에서 새로 생성됩니다.
## 10. 데모 시나리오
1. 메뉴 1번 → 초기 데모 데이터 생성 (교수: 조혜진 / 학생 4명 / 과목 704828 / 분반 501)
2. 메뉴 6번 → 501 분반의 게시글 조회 (서윤선의 글이 보입니다)
3. 메뉴 5번 → 김현진(미등록 학생)으로 글 작성 시도 → 실패 안내 (DIP 검증 흐름)
4. 메뉴 4번 → 김현진을 추가 발급한 초대 코드로 등록
5. 메뉴 5번 → 김현진이 글 작성 성공
6. 메뉴 10/11번 → 댓글, 대댓글 작성
7. 메뉴 12번 → 댓글 수정 (익명 여부는 그대로 유지되는지 확인)
8. 메뉴 13/14번 → 북마크 추가/해제 (중복 시 안내 확인)
9. 메뉴 15번 → 마이페이지 조회
10. 메뉴 19번 → 저장 후 종료. 다시 실행 시 데이터가 그대로 유지됨
