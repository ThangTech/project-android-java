# Thư Mục Data

Thư mục này chứa data mẫu và script import cho dự án "Ai Là Triệu Phú".

## Các File

| File | Mô Tả |
|------|-------|
| `import_questions.sql` | Script SQL import ~30 câu hỏi mẫu |
| `question.json` | File JSON câu hỏi gốc (trong `app/src/main/assets/`) |

## Hướng Dẫn Sử Dụng

### Cách 1: Import qua SQLite Browser

1. Mở SQLite Browser (https://sqlitebrowser.org/)
2. Mở file database: `millionaire.db` trong data app
3. Vào tab "Execute SQL"
4. Copy nội dung file `import_questions.sql` và dán vào
5. Nhấn "Execute"

### Cách 2: Import qua ADB Shell

```bash
# Push file vào device
adb push import_questions.sql /sdcard/

# Mở SQLite
adb shell
sqlite3 /data/data/com.example.project_android_java/databases/millionaire.db

# Chạy script
.read /sdcard/import_questions.sql

# Thoát
.exit
```

### Cách 3: Qua Android App (Admin Panel)

1. Đăng nhập Admin: `admin` / `admin123`
2. Vào "Quản lý câu hỏi"
3. Dùng nút "Thêm câu hỏi" để thêm thủ công

## Cấu Trúc Bảng questions

```sql
CREATE TABLE questions (
    id INTEGER PRIMARY KEY,
    question TEXT NOT NULL,
    option_a TEXT NOT NULL,
    option_b TEXT NOT NULL,
    option_c TEXT NOT NULL,
    option_d TEXT NOT NULL,
    correct INTEGER NOT NULL,  -- 0=A, 1=B, 2=C, 3=D
    level INTEGER NOT NULL,    -- 1-15
    category TEXT,
    evidence TEXT
);
```

## Data Mẫu

File `import_questions.sql` chứa ~30 câu hỏi mẫu:
- Level 1-5: 25 câu (5 câu mỗi level)
- Level 6-10: 5 câu
- Level 11-15: 5 câu

Câu hỏi đủ các chủ đề: Toán học, Địa lý, Lịch sử, Văn học, Khoa học, Động vật.

## Lưu Ý

- Data chính nằm trong `app/src/main/assets/question.json`
- Nếu cần reset data, vào Admin Panel → "Nạp lại câu hỏi từ JSON"
- Các câu hỏi thêm qua SQL sẽ bị xóa khi nhấn "Nạp lại JSON"
