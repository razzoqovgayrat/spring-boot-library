-- ============================================================
-- V4: Seed — ADMIN/LIBRARIAN rollari (yangi Permission enum'ga mos),
-- birinchi ADMIN. Faqat DEV/DEMO uchun.
-- ============================================================

INSERT INTO roles (name)
VALUES ('ADMIN'),
       ('LIBRARIAN');

-- ADMIN — barcha 40 ta permission
INSERT INTO role_permission (role_id, permission)
SELECT id, permission
FROM roles,
     (VALUES ('USER_CREATE'),
             ('USER_READ'),
             ('USER_UPDATE'),
             ('USER_DELETE'),
             ('AUTHOR_CREATE'),
             ('AUTHOR_READ'),
             ('AUTHOR_UPDATE'),
             ('AUTHOR_DELETE'),
             ('CATEGORY_CREATE'),
             ('CATEGORY_READ'),
             ('CATEGORY_UPDATE'),
             ('CATEGORY_DELETE'),
             ('BOOK_CREATE'),
             ('BOOK_READ'),
             ('BOOK_UPDATE'),
             ('BOOK_DELETE'),
             ('ROLE_CREATE'),
             ('ROLE_READ'),
             ('ROLE_UPDATE'),
             ('ROLE_DELETE'),
             ('BOOK_COPY_CREATE'),
             ('BOOK_COPY_READ'),
             ('BOOK_COPY_UPDATE'),
             ('BOOK_COPY_DELETE'),
             ('MEMBER_CREATE'),
             ('MEMBER_READ'),
             ('MEMBER_UPDATE'),
             ('MEMBER_DELETE'),
             ('LOAN_CREATE'),
             ('LOAN_READ'),
             ('LOAN_UPDATE'),
             ('LOAN_RETURN'),
             ('FINE_PAY'),
             ('FINE_READ'),
             ('FINE_UPDATE'),
             ('RESERVATION_CREATE'),
             ('RESERVATION_READ'),
             ('RESERVATION_UPDATE'),
             ('RESERVATION_CANCEL')) AS p(permission)
WHERE roles.name = 'ADMIN';

-- Birinchi ADMIN. password_hash — PLACEHOLDER, deploy'dan oldin
-- BCryptPasswordEncoder().encode("...") bilan almashtiring.
INSERT INTO users (created_at, created_by, version, full_name, password_hash, phone_number, status, username, role_id)
SELECT now(), 'ROLE_ADMIN', 1, 'Ali Aliyev', '$2a$12$TYNxvbwlkimBiavThIMUCOOvZK3kpbeZGxy40EGIgZOO7TycgBruG', '+998934445566', 'ACTIVE', 'ali@gmail.com', id
FROM roles
WHERE name = 'ADMIN';