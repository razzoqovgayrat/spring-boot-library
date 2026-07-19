-- ShedLock kutubxonasining o'zi talab qiladigan standart jadval.
-- Bu yerda "kim, qaysi jobni, qachongacha egallab turgani" saqlanadi.
CREATE TABLE shedlock (
                          name       VARCHAR(64)  NOT NULL,
                          lock_until TIMESTAMP    NOT NULL,
                          locked_at  TIMESTAMP    NOT NULL,
                          locked_by  VARCHAR(255) NOT NULL,
                          PRIMARY KEY (name)
);