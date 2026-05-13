package qna.storage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

예외를 잡아 안내 메시지를 출력하고 빈 AppData로 동작을 이어간다.
public class StorageManager {
    // 저장 파일 경로. data 디렉터리가 없으면 자동 생성한다.
    private static final String DATA_DIR = "data";
    private static final String DATA_FILE = DATA_DIR + File.separator + "qna-data.ser";

//     저장 파일이 있으면 AppData를 복원하고, 없으면 새 AppData를 만들어 반환한다.
    public AppData load() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return new AppData();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof AppData) {
                return (AppData) obj;
            }
            System.out.println("저장 파일 형식이 올바르지 않아 새 데이터로 시작합니다.");
            return new AppData();
        } catch (IOException | ClassNotFoundException e) {
            // 요구사항: 데이터 불러오기 중 오류가 발생했습니다.
            System.out.println("데이터 불러오기 중 오류가 발생했습니다: " + e.getMessage());
            return new AppData();
        }
    }

//    data 디레터리가 없으면 자동 생성, 파일 기록
    public void save(AppData data) {
        try {
            File dir = new File(DATA_DIR);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    System.out.println("data 폴더 생성에 실패했지만 저장을 계속 시도합니다.");
                }
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
                oos.writeObject(data);
            }
        } catch (IOException e) {
            // 요구사항: 데이터 저장 중 오류가 발생했습니다.
            System.out.println("데이터 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}