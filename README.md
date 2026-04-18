# AI LÀ TRIỆU PHÚ - Who Wants to Be a Millionaire

## Mục lục
1. [Giới thiệu đề tài](#1-giới-thiệu-đề-tài)
2. [Dataset](#2-dataset)
3. [Game Flow (Pipeline)](#3-game-flow-pipeline)
4. [Game Mechanics](#4-game-mechanics)
5. [Kết quả & Thống kê](#5-kết-quả--thống-kê)
6. [Hướng dẫn cài đặt](#6-hướng-dẫn-cài-đặt)
7. [Cấu trúc thư mục](#7-cấu-trúc-thư-mục)
8. [Tác giả](#8-tác-giả)

---

## 1. Giới thiệu đề tài

### Bài toán
Tái hiện trò chơi "Ai Là Triệu Phú" (Who Wants to Be a Millionaire) trên nền tảng Android với:
- Hệ thống câu hỏi phân theo 15 mức độ khó
- 3 lifeline hỗ trợ người chơi: 50:50, Gọi điện, Hỏi ý kiến khán giả
- Hệ thống tài khoản và bảng xếp hạng
- Quản lý câu hỏi cho admin

### Mục tiêu
- Xây dựng ứng dụng Android hoàn chỉnh có thể cài đặt và chơi được
- Hệ thống quản lý câu hỏi (CRUD) cho admin
- Lưu trữ điểm số và lịch sử chơi bằng SQLite
- Giao diện đẹp, dễ sử dụng

### Tính năng chính
| Tính năng | Mô tả |
|-----------|-------|
| Chơi Game | 15 câu hỏi/phần thưởng theo mức độ |
| Lifelines | 50:50, Gọi điện thoại, Hỏi ý kiến khán giả |
| Tài khoản | Đăng ký/đăng nhập/chơi khách |
| Bảng xếp hạng | Top điểm cao |
| Lịch sử chơi | Xem lại các ván đã chơi |
| Admin Panel | Thêm/sửa/xóa câu hỏi, quản lý tài khoản, xem thống kê |

---

## 2. Dataset

### Nguồn data
- File JSON: `app/src/main/assets/question.json`
- Tổng: ~105 câu hỏi ban đầu
- Phân bố theo level:

| Level | Số câu trong JSON gốc |
|-------|-----------------------|
| 1     | 5                     |
| 2     | 8                     |
| 3     | 12                    |
| 4     | 13                    |
| 5     | 19                    |
| 6     | 21                    |
| 7     | 13                    |
| 8     | 6                     |
| 9     | 3                     |
| 10    | 5                     |
| 11    | 5                     |
| 12    | 5                     |
| 13    | 5                     |
| 14    | 5                     |
| 15    | 5                     |

### Link tải data (nếu cần)
- Không có link tải - data được đính kèm trong thư mục `app/src/main/assets/`
- Backup data mẫu: `data/import_questions.sql` (~30 câu bổ sung)

### Mô tả cột

#### Bảng `questions`
| Column | Kiểu | Mô tả |
|--------|------|-------|
| `id` | INTEGER PRIMARY KEY | ID câu hỏi |
| `question` | TEXT | Nội dung câu hỏi |
| `option_a` | TEXT | Đáp án A |
| `option_b` | TEXT | Đáp án B |
| `option_c` | TEXT | Đáp án C |
| `option_d` | TEXT | Đáp án D |
| `correct` | INTEGER | Đáp án đúng (0=A, 1=B, 2=C, 3=D) |
| `level` | INTEGER | Mức độ khó (1-15) |
| `category` | TEXT | Chủ đề (General, Toán, Khoa học...) |
| `evidence` | TEXT | Giải thích đáp án đúng |

#### Bảng `users`
| Column | Kiểu | Mô tả |
|--------|------|-------|
| `id` | INTEGER PRIMARY KEY | ID người dùng |
| `username` | TEXT UNIQUE | Tên đăng nhập |
| `password_hash` | TEXT | Mật khẩu đã hash |
| `password_salt` | TEXT | Salt cho password |
| `created_at` | TEXT | Thời gian tạo |

#### Bảng `leaderboard`
| Column | Kiểu | Mô tả |
|--------|------|-------|
| `id` | INTEGER PRIMARY KEY | ID bản ghi |
| `user_id` | INTEGER | FK → users.id |
| `player_name` | TEXT | Tên người chơi |
| `score` | INTEGER | Điểm đạt được |
| `questions_correct` | INTEGER | Số câu đúng |
| `date_played` | TEXT | Thời gian chơi |

---

## 3. Game Flow (Pipeline)

```
┌─────────────────────────────────────────────────────────────┐
│                        LUỒNG GAME                           │
└─────────────────────────────────────────────────────────────┘

[1] KHỞI ĐỘNG
       │
       ▼
┌─────────────────┐
│  LoginRegister   │ ←── Đăng nhập / Đăng ký / Chơi khách
│     Activity     │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   MainActivity   │ ←── Menu chính: Chơi, Bảng xếp hạng, Lịch sử
│                  │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   GameActivity   │ ←── Game chính
│                  │
│  ┌────────────┐  │
│  │ 15 câu hỏi │  │
│  │ 3 lifelines │  │
│  │ Timer 30s   │  │
│  └────────────┘  │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ ResultActivity   │ ←── Hiển thị điểm, số câu đúng
│                  │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│    Database     │ ←── Lưu điểm vào SQLite (leaderboard)
│   SQLiteHelper   │
└─────────────────┘
```

### Chi tiết từng bước

| Bước | Mô tả | Xử lý |
|------|-------|-------|
| 1. Khởi động | Mở app → LoginRegisterActivity | AuthManager.checkLoggedIn() |
| 2. Đăng nhập | Nhập username/password | AuthManager.login() → SQLite |
| 3. Chọn menu | MainActivity | 4 options: Chơi, Xếp hạng, Lịch sử, Đăng xuất |
| 4. Load câu hỏi | QuestionManager.loadQuestions() | SQLite → shuffle → 15 câu |
| 5. Trả lời | Chọn A/B/C/D hoặc dùng lifeline | Kiểm tra correct → cập nhật điểm |
| 6. Timer | Đếm ngược 30 giây | Hết giờ → chuyển câu tiếp theo |
| 7. Kết thúc | Trả lời sai hoặc hết 15 câu | ResultActivity → Lưu vào DB |

---

## 4. Game Mechanics

### 4.1 Hệ thống câu hỏi
- **Nguồn**: SQLite database (import từ question.json)
- **Chọn câu hỏi**:
  - 5 câu level 1-5 (dễ)
  - 5 câu level 6-10 (trung bình)
  - 5 câu level 11-15 (khó)
- **Shuffle**: Xáo trộn ngẫu nhiên trong mỗi nhóm

### 4.2 Hệ thống lifeline

| Lifeline | Mô tả | Cách hoạt động |
|----------|-------|----------------|
| 50:50 | Xóa 2 đáp án sai | Random xóa 2 trong 3 đáp án sai |
| Gọi điện | Gọi cho người biết | Random 70% đúng hoặc sai |
| Hỏi khán giả | Biểu quyết % | Random theo xác suất đúng |

### 4.3 Hệ thống điểm

| Câu hỏi | Điểm thưởng |
|---------|------------|
| 1       | 100,000    |
| 2       | 200,000    |
| 3       | 300,000    |
| 4       | 500,000    |
| 5       | 1,000,000  |
| ...     | ...        |
| 15      | 100,000,000|

### 4.4 Mô hình đã sử dụng
Vì đây là **game show** không phải bài toán ML, nên không có mô hình LR/DT/RF.
Thay vào đó dùng:

| Thành phần | Công nghệ | Lý do chọn |
|-----------|-----------|------------|
| Database | SQLite | Nhẹ, tích hợp sẵn Android |
| Auth | Password hashing (SHA-256 + Salt) | Bảo mật mật khẩu |
| Random | Collections.shuffle() | Chọn câu hỏi ngẫu nhiên |
| Timer | Handler + Runnable | Đếm ngược 30 giây |
| Audio | SoundPool | Âm thanh game hiệu quả |

---

## 5. Kết quả & Thống kê

### 5.1 Cách tính điểm
- **Điểm tối đa**: 100,000,000 (15 câu đúng liên tiếp)
- **Điểm mỗi câu**: 100K → 200K → 300K → 500K → 1M → 2M → 3M → 5M → 7M → 10M → 15M → 20M → 30M → 40M → 100M
- **Câu hỏi sai**: Mất hết điểm, game kết thúc

### 5.2 Thống kê game (trong app)

| Metric | Mô tả |
|--------|-------|
| Tổng câu hỏi | Số câu trong database |
| Điểm cao nhất | MAX(score) từ leaderboard |
| Số người chơi | COUNT(DISTINCT user_id) |
| Phân bố level | COUNT questions theo level |

### 5.3 Màn hình thống kê (Admin)
```
┌──────────────────────────────────┐
│         THỐNG KÊ                 │
├──────────────────────────────────┤
│  📚 105 Câu hỏi                  │
│  👥 3 Người chơi                 │
│  🏆 50,000,000 Điểm cao nhất     │
├──────────────────────────────────┤
│  Chi tiết theo level:            │
│  Level 1-5 (Dễ): 57 câu          │
│  Level 6-10 (TB): 44 câu         │
│  Level 11-15 (Khó): 30 câu       │
└──────────────────────────────────┘
```

---

## 6. Hướng dẫn cài đặt

### 6.1 Yêu cầu hệ thống

| Phần mềm | Phiên bản tối thiểu |
|----------|---------------------|
| Android Studio | Hedgehog (2024.1.1) |
| JDK | 17 |
| Android SDK | 34 (Android 14) |
| Gradle | 8.4 |
| Thiết bị | Android 5.0+ (API 21) |

### 6.2 Cài môi trường

1. **Cài Android Studio**
   ```
   Download: https://developer.android.com/studio
   ```

2. **Cài JDK 17**
   ```
   Download: https://www.oracle.com/java/technologies/downloads/#java17
   ```

3. **Clone dự án**
   ```bash
   git clone <repo-url>
   cd D--projectandroidjava
   ```

4. **Sync Gradle**
   ```
   Android Studio: File → Sync Project with Gradle Files
   Hoặc: ./gradlew sync
   ```

### 6.3 Build & Run

**Build Debug APK:**
```bash
./gradlew assembleDebug
```
→ APK: `app/build/outputs/apk/debug/app-debug.apk`

**Run trên thiết bị/giả lập:**
```
Android Studio: Run → Run 'app' (Shift + F10)
```

**Clean build:**
```bash
./gradlew clean
./gradlew assembleDebug
```

### 6.4 Chạy Demo/Inference

Không có script inference riêng vì đây là Android app.

**Cách test nhanh:**

1. **Đăng nhập Admin:**
   - Username: `admin`
   - Password: `admin123`

2. **Thêm câu hỏi mới:**
   - Admin Panel → Quản lý câu hỏi → Thêm mới

3. **Test game:**
   - Chơi 1 ván từ đầu đến cuối
   - Dùng hết 3 lifeline
   - Kiểm tra bảng xếp hạng

4. **SQL test (nếu cần):**
   ```bash
   # Qua ADB shell
   adb shell
   sqlite3 /data/data/com.example.project_android_java/databases/millionaire.db
   ```

---

## 7. Cấu trúc thư mục

```
D--projectandroidjava/
├── app/
│   └── src/main/
│       ├── java/com/example/project_android_java/
│       │   ├── database/
│       │   │   └── DatabaseHelper.java     # SQLite CRUD
│       │   ├── manager/
│       │   │   ├── AuthManager.java         # Login/Register singleton
│       │   │   ├── QuestionManager.java     # Load & shuffle questions
│       │   │   └── SoundManager.java        # Audio effects
│       │   ├── model/
│       │   │   └── Question.java            # Question model
│       │   └── ui/
│       │       ├── MainActivity.java        # Menu chính
│       │       ├── LoginRegisterActivity.java
│       │       ├── GameActivity.java        # Game play
│       │       ├── ResultActivity.java      # Kết quả
│       │       ├── RankActivity.java        # Bảng xếp hạng
│       │       ├── HistoryActivity.java     # Lịch sử chơi
│       │       ├── AdminActivity.java       # Admin dashboard
│       │       ├── QuestionManagementActivity.java
│       │       ├── AccountManagementActivity.java
│       │       ├── StatisticsActivity.java
│       │       └── HelpAudienceActivity.java
│       ├── res/
│       │   ├── drawable/                    # Background, buttons
│       │   ├── layout/                      # XML layouts
│       │   ├── raw/                         # Audio files
│       │   └── values/                      # Strings, colors, themes
│       └── assets/
│           └── question.json                 # Source questions
├── data/
│   ├── README.md
│   └── import_questions.sql                 # SQL import script
├── demo/
│   └── README.md                            # Test checklist
├── reports/                                  # (Thêm sau)
│   └── README.md
├── slides/                                   # (Thêm sau)
│   └── README.md
├── requirements.txt                          # Dependencies
├── .gitignore
└── README.md
```

---

## 8. Tác giả

| Thông tin | Chi tiết |
|-----------|----------|
| Họ Tên | Nguyễn Văn Thắng |
| Mã Sinh Viên | 12523081 |
| Lớp | 12523W.1 |
| Email | nguyenvanthangktpm@gmail.com |
| Dự Án | Ai Là Triệu Phú - Android Game |
| Phiên bản | 1.0 |


