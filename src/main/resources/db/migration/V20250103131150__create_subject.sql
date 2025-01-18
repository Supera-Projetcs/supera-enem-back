CREATE TABLE IF NOT EXISTS subject
(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS content
(
    id BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255),
    content_weight  DOUBLE PRECISION NOT NULL,
    question_weight DOUBLE PRECISION NOT NULL,
    subject_id      BIGINT,
    CONSTRAINT fk_content_subject FOREIGN KEY (subject_id) REFERENCES subject (id)
);

CREATE TABLE IF NOT EXISTS question
(
    id BIGSERIAL PRIMARY KEY,
    text         VARCHAR(255),
    answer       CHAR NOT NULL,
    answers_json TEXT
);

CREATE TABLE IF NOT EXISTS image
(
    id BIGSERIAL PRIMARY KEY,
    url         VARCHAR(255),
    description VARCHAR(255),
    question_id BIGINT NOT NULL,
    CONSTRAINT fk_image_question FOREIGN KEY (question_id) REFERENCES question (id)
);


ALTER TABLE content
    ADD CONSTRAINT FK_CONTENT_ON_SUBJECT FOREIGN KEY (subject_id) REFERENCES subject (id);

ALTER TABLE image
    ADD CONSTRAINT FK_IMAGE_ON_QUESTION FOREIGN KEY (question_id) REFERENCES question (id);
