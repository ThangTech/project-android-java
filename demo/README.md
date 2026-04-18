# Demo Scripts

Thư mục này chứa các script demo để kiểm thử nhanh các tính năng.

## Android App

Ứng dụng Android chạy trực tiếp trên thiết bị/giả lập, không cần script demo.

## Database Scripts

Các câu lệnh SQLite để kiểm tra database:

```sql
-- Xem tất cả câu hỏi
SELECT * FROM questions;

-- Đếm câu hỏi theo level
SELECT level, COUNT(*) as total FROM questions GROUP BY level;

-- Xem bảng xếp hạng
SELECT * FROM leaderboard ORDER BY score DESC LIMIT 10;

-- Xem người dùng
SELECT * FROM users;
```

## Test Checklist

- [ ] Đăng nhập Admin: admin / admin123
- [ ] Thêm câu hỏi mới
- [ ] Sửa câu hỏi
- [ ] Xóa câu hỏi
- [ ] Xem thống kê
- [ ] Chơi game với câu hỏi mới thêm
- [ ] Đăng ký tài khoản mới
- [ ] Xem bảng xếp hạng
