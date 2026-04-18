-- SQL Script để import câu hỏi vào database
-- Sử dụng: Copy các INSERT statement và chạy trong SQLite browser
-- Hoặc chạy qua ADB: adb shell < import_questions.sql

-- Xóa tất cả câu hỏi cũ (tùy chọn)
-- DELETE FROM questions;

-- Câu hỏi mẫu Level 1 (5 câu)
INSERT INTO questions (question, option_a, option_b, option_c, option_d, correct, level, category, evidence) VALUES
('1 + 1 = ?', '1', '2', '3', '4', 1, 1, 'Toán học', '1 + 1 = 2'),
('Con mèo kêu gì?', 'Gâu gâu', 'Mèo mèo', 'Meo meo', 'Ó ó', 2, 1, 'Động vật', 'Con mèo kêu Meo meo'),
('Màu trời ban ngày là gì?', 'Đen', 'Xanh', 'Đỏ', 'Vàng', 1, 1, 'Khoa học', 'Bầu trời ban ngày có màu xanh'),
('Từ nào có 5 chữ cái?', 'Sách', 'Bút', 'Viết', 'Quan', 0, 1, 'Ngôn ngữ', 'Sách có 5 chữ cái: S-A-C-H'),
('Nước sôi ở bao nhiêu độ?', '50 độ', '100 độ', '0 độ', '200 độ', 1, 1, 'Khoa học', 'Nước sôi ở 100 độ C');

-- Câu hỏi mẫu Level 2 (5 câu)
INSERT INTO questions (question, option_a, option_b, option_c, option_d, correct, level, category, evidence) VALUES
('Thủ đô của Việt Nam là?', 'TP.HCM', 'Hà Nội', 'Đà Nẵng', 'Hải Phòng', 1, 2, 'Địa lý', 'Hà Nội là thủ đô của Việt Nam'),
('2 x 5 = ?', '7', '12', '10', '25', 2, 2, 'Toán học', '2 x 5 = 10'),
('Con chó kêu gì?', 'Mèo mèo', 'Ó ó', 'Gâu gâu', 'Cất', '2', '2', 'Động vật', 'Con chó kêu Gâu gâu'),
('Trái cây nào có màu vàng?', 'Táo', 'Cam', 'Nho', 'Dưa hấu', 1, 2, 'Khoa học', 'Cam có màu da cam (vàng cam)'),
('Từ "học" có mấy chữ cái?', '2', '3', '4', '5', 1, 2, 'Ngôn ngữ', 'Học có 3 chữ cái: H-O-C');

-- Câu hỏi mẫu Level 3 (5 câu)
INSERT INTO questions (question, option_a, option_b, option_c, option_d, correct, level, category, evidence) VALUES
('Việt Nam có bao nhiêu tỉnh thành?', '63', '64', '65', '58', 0, 3, 'Địa lý', 'Việt Nam có 63 tỉnh thành'),
('10 + 20 + 30 = ?', '50', '60', '70', '55', 1, 3, 'Toán học', '10 + 20 + 30 = 60'),
('Sông Hồng chảy qua tỉnh nào?', 'Đà Nẵng', 'Hà Nội', 'TP.HCM', 'Cần Thơ', 1, 3, 'Địa lý', 'Sông Hồng chảy qua Hà Nội'),
('Ai là tác giả Truyện Kiều?', 'Nguyễn Trãi', 'Nguyễn Du', 'Nam Cao', 'Tô Hoài', 1, 3, 'Văn học', 'Nguyễn Du là tác giả Truyện Kiều'),
('Nguyên tố hóa học nào có ký hiệu O?', 'Vàng', 'Oxy', 'Sắt', 'Đồng', 1, 3, 'Khoa học', 'O là ký hiệu của Oxy');

-- Câu hỏi mẫu Level 4 (5 câu)
INSERT INTO questions (question, option_a, option_b, option_c, option_d, correct, level, category, evidence) VALUES
('Chiến tranh thế giới thứ 2 kết thúc năm nào?', '1945', '1944', '1946', '1950', 0, 4, 'Lịch sử', 'Chiến tranh thế giới thứ 2 kết thúc năm 1945'),
('Diện tích Việt Nam là bao nhiêu?', '300.000 km²', '331.000 km²', '350.000 km²', '400.000 km²', 1, 4, 'Địa lý', 'Diện tích Việt Nam khoảng 331.000 km²'),
('100 - 25 x 3 = ?', '225', '75', '25', '50', 2, 4, 'Toán học', '100 - 75 = 25 (nhân chia trước)'),
('Hội An thuộc tỉnh nào?', 'Đà Nẵng', 'Quảng Nam', 'Thừa Thiên Huế', 'Nam Định', 1, 4, 'Địa lý', 'Hội An thuộc tỉnh Quảng Nam'),
('Ai chết dưới bánh xe năm 1802?', 'Lê Văn Duyệt', 'Nguyễn Huệ', 'Trần Hưng Đạo', 'Quang Trung', 3, 4, 'Lịch sử', 'Quang Trung (Nguyễn Huệ) mất năm 1792, không phải 1802. Đây là câu sai mẫu.');

-- Câu hỏi mẫu Level 5 (5 câu)
INSERT INTO questions (question, option_a, option_b, option_c, option_d, correct, level, category, evidence) VALUES
('Tỉnh nào có diện tích lớn nhất Việt Nam?', 'Gia Lai', 'Nghệ An', 'Thanh Hóa', 'Đắk Lắk', 1, 5, 'Địa lý', 'Nghệ An có diện tích lớn nhất'),
('15! / 14! = ?', '1', '14', '15', '210', 2, 5, 'Toán học', '15! / 14! = 15'),
('Ai là vua Hùng Vương đầu tiên?', 'Hùng Vương thứ nhất', 'Lạc Long Quân', 'Âu Lạc', 'An Dương Vương', 0, 5, 'Lịch sử', 'Theo truyền thuyết, Lạc Long Quân và Âu Cơ sinh ra vua Hùng đầu tiên'),
('Ngọn núi cao nhất Việt Nam là?', 'Fansipan', 'Pu Si Lung', 'Kontum', 'Lai Châu', 0, 5, 'Địa lý', 'Fansipan cao 3.147m, là đỉnh núi cao nhất Việt Nam'),
('Thơ Đường gồm bao nhiêu bài?', '48', '50', '42', '45', 0, 5, 'Văn học', 'Thơ Đường gồm 48 bài tiêu biểu');

-- Câu hỏi mẫu Level 6-10 (trung bình - 5 câu)
INSERT INTO questions (question, option_a, option_b, option_c, option_d, correct, level, category, evidence) VALUES
('Tứ đại nghề nghiệp của truyền thuyết Việt Nam gồm?', 'Nông, Công, Thương, Hải', 'Nông, Công, Thương, Giáo', 'Cày, Cuốc, Cấy, Gặt', 'Chài, Lưới, Cấy, Cày', 0, 6, 'Lịch sử', 'Tứ đại nghề nghiệp: Nông, Công, Thương, Hải'),
('Ai viết "Truyện Từ Thức diệt phong hóa"?', 'Nam Cao', 'Ngô Tất Tố', 'Nguyễn Huy Tựu', 'Vũ Trọng Phụng', 0, 7, 'Văn học', 'Truyện Từ Thức diệt phong hóa của Nam Cao'),
('Hằng số Planck có giá trị bao nhiêu?', '6.626 x 10^-34', '3.14 x 10^8', '9.81 m/s²', '1.6 x 10^-19', 0, 8, 'Vật lý', 'Hằng số Planck h = 6.626 × 10⁻³⁴ J.s'),
('Nguyên tố nào có số hiệu nguyên tử 79?', 'Bạc', 'Vàng', 'Đồng', 'Sắt', 1, 9, 'Hóa học', 'Vàng (Au) có số hiệu nguyên tử 79'),
('Ai là tác giả bản Tuyên ngôn Độc lập 1945?', 'Hồ Chí Minh', 'Hoàng Quốc Việt', 'Trường Chinh', 'Võ Nguyên Giáp', 0, 10, 'Lịch sử', 'Hồ Chí Minh là tác giả bản Tuyên ngôn Độc lập');

-- Câu hỏi mẫu Level 11 (5 câu)
INSERT INTO questions (question, option_a, option_b, option_c, option_d, correct, level, category, evidence) VALUES
('Số nguyên tố lớn nhất có 2 chữ số là?', '97', '89', '99', '91', 0, 11, 'Toán học', '97 là số nguyên tố lớn nhất có 2 chữ số'),
('Định lý Pythagoras được phát biểu cho loại tam giác nào?', 'Tam giác bất kỳ', 'Tam giác vuông', 'Tam giác đều', 'Tam giác cân', 1, 11, 'Toán học', 'a² = b² + c² chỉ đúng cho tam giác vuông'),
('Văn bản "Đại Việt sử ký toàn thư" được viết năm nào?', 'Năm 1272', 'Năm 1479', 'Năm 1820', 'Năm 1900', 1, 11, 'Lịch sử', 'Đại Việt sử ký toàn thư hoàn thành năm 1479 dưới thời Lê Thánh Tông'),
('Hằng số hấp dẫn G có giá trị bao nhiêu?', '6.67 x 10^-11', '9.81 m/s²', '1.6 x 10^-19', '6.02 x 10^23', 0, 11, 'Vật lý', 'Hằng số hấp dẫn G = 6.67 × 10⁻¹¹ N.m²/kg²'),
('Ai là người phát minh ra kháng sinh penicillin?', 'Louis Pasteur', 'Alexander Fleming', 'Marie Curie', 'Thomas Edison', 1, 11, 'Khoa học', 'Alexander Fleming phát hiện penicillin năm 1928');

-- Câu hỏi mẫu Level 12 (5 câu)
INSERT INTO questions (question, option_a, option_b, option_c, option_d, correct, level, category, evidence) VALUES
('Ai được mệnh danh "Đệ nhất kỳ đài"?', 'Thái sư Lê Văn Thúy', 'Lê Hoàn', 'Đinh Tiên Hoàng', 'Lê Đại Hành', 0, 12, 'Lịch sử', 'Thái sư Lê Văn Thúy được gọi là Đệ nhất kỳ đài'),
('Tổng của 100 số tự nhiên đầu tiên là?', '5000', '5050', '4950', '5100', 1, 12, 'Toán học', 'Tổng = n(n+1)/2 = 100×101/2 = 5050'),
('Nguyên tử của nguyên tố nào có số hiệu 26?', 'Đồng', 'Sắt', 'Vàng', 'Bạc', 1, 12, 'Hóa học', 'Fe (Sắt) có số hiệu nguyên tử 26'),
('Thiên hà Milky Way thuộc loại nào?', 'Hình xoắn ốc', 'Hình elip', 'Hình không đều', 'Hình cầu', 0, 12, 'Thiên văn', 'Milky Way là thiên hà hình xoắn ốc'),
('Tác phẩm "Tắt đèn" của ai?', 'Ngô Tất Tố', 'Nam Cao', 'Vũ Trọng Phụng', 'Lê Văn Trương', 0, 12, 'Văn học', 'Ngô Tất Tố là tác giả truyện "Tắt đèn"');

-- Câu hỏi mẫu Level 13 (5 câu)
INSERT INTO questions (question, option_a, option_b, option_c, option_d, correct, level, category, evidence) VALUES
('Định lý Fermat nhỏ được phát biểu cho?', 'Số nguyên dương', 'Số nguyên', 'Số nguyên tố', 'Số hữu tỉ', 2, 13, 'Toán học', 'Định lý Fermat nhỏ: a^p ≡ a (mod p) với p nguyên tố'),
('Ai là hoàng đế đầu tiên của nhà Nguyễn?', 'Minh Mạng', 'Gia Long', 'Thiệu Trị', 'Tự Đức', 1, 13, 'Lịch sử', 'Gia Long (Nguyễn Ánh) là hoàng đế đầu tiên của nhà Nguyễn'),
('Số lượng nhiễm sắc thể của người là?', '23', '44', '46', '48', 2, 13, 'Sinh học', 'Người có 46 nhiễm sắc thể (23 cặp)'),
('Tần số của sóng ánh sáng đỏ là?', '4 x 10^14 Hz', '5 x 10^12 Hz', '6 x 10^10 Hz', '7 x 10^8 Hz', 0, 13, 'Vật lý', 'Ánh sáng đỏ có tần số khoảng 4×10¹⁴ Hz'),
('Nguyên tố có số hiệu 92 là?', 'Urani', 'Thori', 'Plutoni', 'Neptuni', 0, 13, 'Hóa học', 'Urani (U) có số hiệu nguyên tử 92');

-- Câu hỏi mẫu Level 14 (5 câu)
INSERT INTO questions (question, option_a, option_b, option_c, option_d, correct, level, category, evidence) VALUES
('Tế bào có bao nhiêu loại ARN?', '1', '3', '4', '5', 1, 14, 'Sinh học', 'Có 3 loại ARN chính: mARN, tARN, rARN'),
('Định lý cơ bản của số học phát biểu rằng?', 'Mọi số đều là số nguyên', 'Mọi số tự nhiên đều phân tích được thành tích các số nguyên tố', 'Mọi số đều là số hữu tỉ', 'Mọi số đều là số thực', 1, 14, 'Toán học', 'Định lý cơ bản của số học: mọi số tự nhiên > 1 đều có thể phân tích thành tích các số nguyên tố duy nhất'),
('Ai là người đặt tên "Việt Nam"?', 'Lý Công Uẩn', 'Trần Hưng Đạo', 'Nguyễn Trãi', 'Jules Verne', 2, 14, 'Lịch sử', 'Nguyễn Trãi là người đặt tên "Việt Nam" trong bài "Nam Quốc Sơn Hà"'),
('Tốc độ quay của Trái Đất quanh Mặt Trời là?', '30 km/s', '50 km/s', '100 km/s', '200 km/s', 0, 14, 'Thiên văn', 'Trái Đất quay quanh Mặt Trời với tốc độ khoảng 30 km/s'),
('Phương trình Schrödinger mô tả?', 'Chuyển động của vật rắn', 'Hành vi của hạt vi mô', 'Sóng âm thanh', 'Dòng điện', 1, 14, 'Vật lý', 'Phương trình Schrödinger mô tả hành vi của các hạt lượng tử');

-- Câu hỏi mẫu Level 15 (5 câu)
INSERT INTO questions (question, option_a, option_b, option_c, option_d, correct, level, category, evidence) VALUES
('Điểm cực Bắc của Việt Nam nằm ở đâu?', 'Hà Giang', 'Lai Châu', 'Điểm cực Bắc thuộc xã Sín Cái, huyện Mường Tè, tỉnh Lào Cai', 'Hà Nội', 2, 15, 'Địa lý', 'Điểm cực Bắc Việt Nam ở Lào Cai'),
('Số phức được biểu diễn dưới dạng a + bi, với i² = ?', '-1', '1', '0', 'i', 0, 15, 'Toán học', 'i² = -1 là định nghĩa cơ bản của số ảo'),
('Vụ thảm sát Đống Đa xảy ra năm nào?', '1771', '1789', '1802', '1812', 2, 15, 'Lịch sử', 'Vụ thảm sát Đống Đa xảy ra năm 1802 dưới thời nhà Nguyễn'),
('Hằng số Avogadro có giá trị là?', '6.02 x 10^22', '6.02 x 10^23', '6.02 x 10^24', '6.02 x 10^25', 1, 15, 'Hóa học', 'Số Avogadro NA = 6.02 × 10²³ mol⁻¹'),
('Lỗ đen siêu nặng ở trung tâm thiên hà Milky Way có tên là?', 'Sagittarius A', 'Andromeda A', 'Orion A', 'Perseus A', 0, 15, 'Thiên văn', 'Sagittarius A* là lỗ đen siêu nặng ở trung tâm Milky Way, khối lượng khoảng 4 triệu M☉');
