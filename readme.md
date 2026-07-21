<div align="center">

# 📚 Library Management System

Production-darajadagi backend patternlarni (tranzaksiyalar, concurrency, scheduled job'lar, state machine) mashq qilish uchun qurilgan kutubxona boshqaruv API'si.

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-6DB33F?style=flat-square&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14+-4169E1?style=flat-square&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Flyway](https://img.shields.io/badge/Flyway-Migrations-CC0200?style=flat-square&logo=flyway&logoColor=white)](https://flywaydb.org/)
[![JWT](https://img.shields.io/badge/Auth-JWT-000000?style=flat-square&logo=jsonwebtokens&logoColor=white)](https://jwt.io/)
[![Swagger](https://img.shields.io/badge/API%20Docs-Swagger-85EA2D?style=flat-square&logo=swagger&logoColor=black)](https://swagger.io/)
[![License](https://img.shields.io/badge/License-Educational-lightgrey?style=flat-square)](#)

</div>

---

## Mundarija

- [Domen g'oyasi](#domen-goyasi)
- [Texnologiyalar](#texnologiyalar)
- [Arxitektura](#arxitektura)
- [Autentifikatsiya](#autentifikatsiya)
- [Domen modeli](#domen-modeli)
- [Biznes qoidalar](#biznes-qoidalar)
- [Scheduled job'lar](#scheduled-joblar)
- [Ishga tushirish](#ishga-tushirish)
- [API](#api)

---

## Domen g'oyasi

Kutubxonada kitoblar (metadata) va ularning fizik nusxalari (copy) bir-biridan ajratilgan. A'zo nusxa ijaraga oladi, qaytaradi; nusxa bo'sh bo'lmasa navbatga yoziladi; muddatida qaytarilmasa jarima hisoblanadi — soddalashtirilgan **ledger tizimi** (append-only, hard-delete yo'q).

## Texnologiyalar

| | Texnologiya                                                                   | Vazifasi |
|---|-------------------------------------------------------------------------------|---|
| <img height="50" src="https://raw.githubusercontent.com/marwin1991/profile-technology-icons/refs/heads/main/icons/java.png"> | [**Java 21**](https://openjdk.org/projects/jdk/21/)                           | Asosiy til |
| ![Spring](https://cdn.simpleicons.org/spring/6DB33F) | [**Spring Boot**](https://spring.io/projects/spring-boot)                     | Ilova karkasi |
| ![Spring Security](https://cdn.simpleicons.org/springsecurity/6DB33F) | [**Spring Security**](https://spring.io/projects/spring-security)             | Autentifikatsiya/avtorizatsiya |
| ![PostgreSQL](https://cdn.simpleicons.org/postgresql/4169E1) | [**PostgreSQL**](https://www.postgresql.org/)                                 | Ma'lumotlar bazasi |
| ![Hibernate](https://cdn.simpleicons.org/hibernate/59666C) | [**Spring Data JPA / Hibernate**](https://spring.io/projects/spring-data-jpa) | ORM |
| ![Flyway](https://cdn.simpleicons.org/flyway/CC0200) | [**Flyway**](https://flywaydb.org/)                                           | Schema migratsiyasi |
| 🔑 | [**JWT**](https://jwt.io/) + Opaque refresh token                             | Token-based auth |
| ⏱ | [**ShedLock**](https://github.com/lukas-krecan/ShedLock)                      | Distributed scheduler lock |
| ![Swagger](https://cdn.simpleicons.org/swagger/85EA2D) | [**springdoc-openapi**](https://springdoc.org/)                               | API hujjatlashtirish |
| <img height="50" src="https://raw.githubusercontent.com/marwin1991/profile-technology-icons/refs/heads/main/icons/lombok.png">  | [**Lombok**](https://projectlombok.org/)                                      | Boilerplate kamaytirish |
| ![Maven](https://cdn.simpleicons.org/apachemaven/C71A36) | [**Maven**](https://maven.apache.org/)                                        | Build tool |

## Arxitektura

Qatlamli (layered) struktura — Controller faqat routing, biznes-mantiq Service'da, entity API'da hech qachon qaytmaydi:

```
com.library
├── controller/     HTTP kirish/chiqish
├── service/        biznes-mantiq
├── repository/     data access
├── entity/         JPA entity'lar
├── dto/{request,response}
├── enums/
├── exception/
├── config/         Security, Scheduler, JPA Auditing
└── scheduler/       @Scheduled + @SchedulerLock
```

## Autentifikatsiya

Tizimga faqat xodimlar (`ADMIN`, `LIBRARIAN`) kiradi. A'zolar (`Member`) o'zi login qilmaydi — barcha amallarni LIBRARIAN ular nomidan bajaradi.

```
POST /auth/login        → JWT access + opaque refresh token
POST /auth/refresh
POST /auth/logout
```

Ruxsatlar **permission-based** (`Role → Permission[]`), `@PreAuthorize` orqali nazorat qilinadi.

## Domen modeli

```
Author ─┐
        ├─< Book >─┬─ Category
BookCopy ┘         │
   │               │
   └─< Loan >── Member ──< Reservation
                 │
                 └────< Fine
```

**Holat mashinalari:**

```
BookCopy      AVAILABLE ⇄ BORROWED ⇄ RESERVED  ·  → LOST / MAINTENANCE
Loan          ACTIVE → OVERDUE → RETURNED  ·  ACTIVE → LOST
Reservation   WAITING → READY → FULFILLED  ·  → EXPIRED / CANCELLED
Fine          UNPAID → PAID  ·  UNPAID → WAIVED
```

## Biznes qoidalar

| Qoida | Xato kodi |
|---|---|
| Faqat `AVAILABLE` nusxa ijaraga olinadi | `COPY_NOT_AVAILABLE` |
| Bir a'zoda maksimal 5 ta aktiv ijara | `MEMBER_LOAN_LIMIT_EXCEEDED` |
| `SUSPENDED` a'zo ijara ololmaydi | `MEMBER_SUSPENDED` |
| Jarima limitidan oshgan a'zo ijara ololmaydi | `MEMBER_FINE_LIMIT_EXCEEDED` |
| Qaytarilgan ijarani qayta qaytarib bo'lmaydi | `LOAN_ALREADY_RETURNED` |

Sozlanadigan qiymatlar — `application.yml` → `library.*` (ijara muddati, limit, jarima narxi va h.k.)

## Scheduled job'lar

[ShedLock](https://github.com/lukas-krecan/ShedLock) — bir nechta instance ishlaganda ham job faqat bittasida ishga tushishini kafolatlaydi.

| Job | Jadval | Vazifa |
|---|---|---|
| `OverdueLoanScanner` | har kuni 06:00 | Muddati o'tgan ijaralarni `OVERDUE` qiladi, jarima yozadi |
| `ReservationExpiryJob` | har kuni 07:00 | Muddati o'tgan navbatlarni `EXPIRED` qiladi, keyingisiga o'tkazadi |

## Ishga tushirish

```bash
# Baza
createdb library

# Ishga tushirish (Flyway migratsiyalari avtomatik ishlaydi)
./mvnw spring-boot:run
```

`application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/library
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true
```

> Default admin — `V4__seed_roles_and_admin.sql`. Deploy'dan oldin `password_hash`ni haqiqiy BCrypt hash bilan almashtiring.

## API

Swagger UI: **`http://localhost:8080/swagger-ui.html`**

| Modul | Yo'l | Ruxsat |
|---|---|---|
| Auth | `/auth/**` | public / authenticated |
| User, Role | `/users`, `/roles` | `ADMIN` |
| Book, Author, Category | `/books`, `/authors`, `/categories` | o'qish — hamma, yozish — `LIBRARIAN` |
| Member | `/members` | `ADMIN`, `LIBRARIAN` |
| Loan, Reservation | `/loans`, `/reservations` | `LIBRARIAN` |
| Fine | `/fines` | to'lash `LIBRARIAN`, kechirish `ADMIN` |

Javob formati:

```json
{
  "success": true,
  "message": "Kitob qo'shildi",
  "data": { },
  "timestamp": "2026-07-21T10:00:00Z"
}
```

---

<div align="center">

Ichki o'quv loyihasi sifatida ishlab chiqilgan.

</div>