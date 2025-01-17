-- V1__create_performance_table.sql

CREATE TABLE performance (
     id BIGSERIAL PRIMARY KEY,
     student_id BIGINT NOT NULL,
     content_id BIGINT NOT NULL,
     performance_rate DOUBLE PRECISION NOT NULL,
     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
     CONSTRAINT fk_performance_student FOREIGN KEY (student_id) REFERENCES student (id) ON DELETE CASCADE,
     CONSTRAINT fk_performance_content FOREIGN KEY (content_id) REFERENCES content (id) ON DELETE CASCADE
);
