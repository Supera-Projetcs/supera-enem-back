CREATE TABLE IF NOT EXISTS weekly_report (
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL,
    student_id BIGINT,
    CONSTRAINT fk_weekly_report_student FOREIGN KEY (student_id) REFERENCES student (id)
);

CREATE TABLE IF NOT EXISTS content (
    id BIGSERIAL PRIMARY KEY ,
    name VARCHAR(255),
    content_weight DOUBLE PRECISION NOT NULL,
    question_weight DOUBLE PRECISION NOT NULL,
    subject_id BIGINT,
    CONSTRAINT fk_content_subject FOREIGN KEY (subject_id) REFERENCES subject (id)
);

CREATE TABLE IF NOT EXISTS weekly_report_content (
     weekly_report_id BIGINT NOT NULL,
     content_id BIGINT NOT NULL,
     PRIMARY KEY (weekly_report_id, content_id),
     CONSTRAINT fk_weekly_report FOREIGN KEY (weekly_report_id) REFERENCES weekly_report (id),
     CONSTRAINT fk_content FOREIGN KEY (content_id) REFERENCES content (id)
);
