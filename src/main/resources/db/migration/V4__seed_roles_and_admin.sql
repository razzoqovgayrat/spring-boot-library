-- ============================================================
-- V4: Seed — default rollar, permission'lar, birinchi ADMIN
-- ============================================================
-- Diqqat: bu migratsiya faqat DEV/DEMO muhitida shu ko'rinishda
-- qoldiriladi. PRODUCTION'da parolni migratsiya faylida saqlash
-- yaxshi amaliyot emas — real loyihada admin akkaunt alohida,
-- xavfsizroq usulda (masalan bir martalik setup script/CLI orqali)
-- yaratiladi.

INSERT INTO roles (name) VALUES ('ADMIN'), ('LIBRARIAN');

-- ADMIN — hamma narsaga ruxsat
INSERT INTO role_permission (role_id, permission)
SELECT id, permission
FROM roles, (VALUES
                 ('AUTHOR_WRITE'), ('CATEGORY_WRITE'), ('BOOK_WRITE'), ('BOOK_COPY_WRITE'),
                 ('MEMBER_WRITE'), ('MEMBER_STATUS_MANAGE'),
                 ('LOAN_MANAGE'), ('RESERVATION_MANAGE'),
                 ('FINE_PAY'), ('FINE_WAIVE'),
                 ('USER_MANAGE'), ('ROLE_MANAGE')
) AS p(permission)
WHERE roles.name = 'ADMIN';

-- LIBRARIAN — kundalik operatsion ishlar, ADMIN-only narsalarsiz
-- (MEMBER_STATUS_MANAGE, FINE_WAIVE, USER_MANAGE, ROLE_MANAGE yo'q)
INSERT INTO role_permission (role_id, permission)
SELECT id, permission
FROM roles, (VALUES
                 ('AUTHOR_WRITE'), ('CATEGORY_WRITE'), ('BOOK_WRITE'), ('BOOK_COPY_WRITE'),
                 ('MEMBER_WRITE'),
                 ('LOAN_MANAGE'), ('RESERVATION_MANAGE'),
                 ('FINE_PAY')
) AS p(permission)
WHERE roles.name = 'LIBRARIAN';

-- Birinchi ADMIN — tizimga kirish uchun boshqa hech kim yo'q, shuning
-- uchun kamida bitta ADMIN oldindan seed qilinishi shart.
--
-- password_hash pastdagi qiymat PLACEHOLDER. Haqiqiy BCrypt hash bilan
-- almashtiring, masalan quyidagi kichik Java kod bilan generatsiya qiling:
--
--   new BCryptPasswordEncoder().encode("sizning_parolingiz")
--
-- yoki https://bcrypt-generator.com kabi ishonchli vositadan foydalaning
-- (faqat DEV muhitida, production parolni hech qachon shunday saytda
-- generatsiya qilmang).
INSERT INTO users (username, password_hash, full_name, status, role_id, created_at, version)
SELECT
    'admin',
    '$2a$10$REPLACE_THIS_WITH_REAL_BCRYPT_HASH_BEFORE_DEPLOY',
    'Bosh Administrator',
    'ACTIVE',
    id,
    now(),
    0
FROM roles WHERE name = 'ADMIN';