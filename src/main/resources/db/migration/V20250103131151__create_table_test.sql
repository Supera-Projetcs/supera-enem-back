CREATE TABLE IF NOT EXISTS test (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    student_id BIGINT,
    CONSTRAINT fk_test_student FOREIGN KEY (student_id) REFERENCES student (id)
);
