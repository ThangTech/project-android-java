# Ai Là Triệu Phú - Who Wants to Be a Millionaire

Ứng dụng Android tái hiện trò chơi "Ai Là Triệu Phú" với các tính năng:
- Chơi game với 15 câu hỏi phân theo mức độ khó
- Hệ thống lifeline (50:50, Gọi điện, Hỏi ý kiến khán giả)
- Đăng ký/đăng nhập tài khoản người chơi
- Bảng xếp hạng & Lịch sử chơi
- Admin Panel: Quản lý câu hỏi (thêm/sửa/xóa), Xem thống kê

## Cấu Trúc Thư Mục

```
D--projectandroidjava/
├── app/
│   └── src/main/
│       ├── java/com/example/project_android_java/
│       │   ├── database/       # DatabaseHelper - SQLite operations
│       │   ├── manager/        # AuthManager, QuestionManager, SoundManager
│       │   ├── model/          # Question model
│       │   └── ui/             # Activities (Game, Login, Admin, ...)
│       ├── res/
│       │   ├── drawable/       # Backgrounds, buttons
│       │   ├── layout/          # XML layouts
│       │   └── values/          # Strings, colors, themes
│       └── assets/
│           └── question.json    # File câu hỏi gốc
├── data/                        # Data mẫu (backup câu hỏi JSON)
├── reports/                      # Báo cáo (để trống - thêm sau)
├── slides/                       # Slide thuyết trình (để trống - thêm sau)
├── demo/                         # Script demo/test
├── requirements.txt             # Android SDK dependencies
└── README.md
```

## Tính Năng Chính

### Người Chơi
- **Chơi Game**: 15 câu hỏi (5 dễ level 1-5, 5 trung bình level 6-10, 5 khó level 11-15)
- **Lifelines**: 50:50, Gọi điện thoại, Hỏi ý kiến khán giả
- **Tài Khoản**: Đăng ký, đăng nhập, chơi với tư cách khách
- **Bảng Xếp Hạng**: Top điểm cao toàn thời đại
- **Lịch Sử Chơi**: Xem lại các ván đã chơi

### Admin
- **Quản Lý Câu Hỏi**: Thêm, sửa, xóa câu hỏi trực tiếp trong SQLite
- **Thống Kê**: Xem tổng số câu hỏi, phân bố theo level, số người chơi
- **Nạp Lại JSON**: Khôi phục câu hỏi từ file question.json gốc
- **Đăng nhập Admin**: Username `admin`, Password `admin123`

## Dataset

### Nguồn Câu Hỏi
- File: `app/src/main/assets/question.json`
- Định dạng JSON với ~105 câu hỏi ban đầu
- Phân bố: Level 1-15

### Cấu Trúc Câu Hỏi
```json
{
  "id": 1,
  "question": "Câu hỏi...",
  "options": ["A", "B", "C", "D"],
  "correct": 0,
  "level": 1,
  "category": "General",
  "evidence": "Giải thích đáp án"
}
```

## Hướng Dẫn Cài Đặt

### Yêu Cầu
- Android Studio Hedgehog (2024.1.1) hoặc mới hơn
- JDK 17
- Android SDK 34 (Android 14)
- Gradle 8.4

### Cài Đặt

1. **Clone/Download dự án**

2. **Mở Android Studio**
   ```
   File → Open → Chọn thư mục D--projectandroidjava
   ```

3. **Sync Gradle**
   ```
   Android Studio sẽ tự động sync khi mở project
   Hoặc: File → Sync Project with Gradle Files
   ```

4. **Chạy ứng dụng**
   ```
   Run → Run 'app'
   Hoặc nhấn Shift + F10
   ```

### Build APK
```bash
./gradlew assembleDebug
```
APK sẽ nằm ở: `app/build/outputs/apk/debug/app-debug.apk`

## Tài Khoản Demo

| Loại | Username | Password |
|------|---------|----------|
| Admin | admin | admin123 |
| Người chơi | (tự đăng ký) | (tự đặt, >=4 ký tự) |
| Khách | Không cần | Không cần |

## Kiến Trúc Kỹ Thuật

- **Ngôn ngữ**: Java 11
- **Database**: SQLite (SQLiteOpenHelper)
- **UI**: XML Layouts + Activities
- **Quản lý trạng thái**: Singleton pattern (AuthManager, QuestionManager)
- **Audio**: SoundPool (Android)

### Các Class Chính

| Class | Mô Tả |
|-------|-------|
| `DatabaseHelper` | SQLite operations: CRUD questions, users, scores |
| `AuthManager` | Singleton - quản lý đăng nhập/đăng ký |
| `QuestionManager` | Singleton - load questions, shuffle game questions |
| `SoundManager` | Singleton - quản lý âm thanh game |
| `Question` | Model class cho câu hỏi |

## Thông Tin Tác Giả

| Thông Tin | Chi Tiết                       |
|-----------|--------------------------------|
| Họ Tên    | Nguyễn Văn Thắng               |
| Email     | nguyenvanthangktpm@gmail.com   |
| Mã SV     | 12523081                       |
| Lớp       | 12523W.1                       |
| Dự Án     | Ai Là Triệu Phú - Android Game |
| Version   | 1.0                            |


