CREATE TABLE IF NOT EXISTS student_subject (
                                 id BIGSERIAL PRIMARY KEY,
                                 student_id BIGINT NOT NULL,
                                 subject_id BIGINT NOT NULL,
                                 subject_weight DOUBLE PRECISION NOT NULL,
                                 CONSTRAINT fk_student FOREIGN KEY (student_id) REFERENCES student (id) ON DELETE CASCADE,
                                 CONSTRAINT fk_subject FOREIGN KEY (subject_id) REFERENCES subject (id) ON DELETE CASCADE
);