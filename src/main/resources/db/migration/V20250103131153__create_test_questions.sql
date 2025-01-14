-- Criação da tabela Answer
CREATE TABLE answer (
    id BIGSERIAL PRIMARY KEY,
    text CHAR(1) NOT NULL,
    correct BOOLEAN NOT NULL,
    question_id BIGINT,
    test_id BIGINT,
    CONSTRAINT fk_answer_question FOREIGN KEY (question_id) REFERENCES question (id),
    CONSTRAINT fk_answer_test FOREIGN KEY (test_id) REFERENCES test (id)
);

-- Criação da tabela associativa test_questions (relacionamento ManyToMany entre Test e Question)
CREATE TABLE test_questions (
    test_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    CONSTRAINT pk_test_questions PRIMARY KEY (test_id, question_id),
    CONSTRAINT fk_test_questions_test FOREIGN KEY (test_id) REFERENCES test (id),
    CONSTRAINT fk_test_questions_question FOREIGN KEY (question_id) REFERENCES question (id)
);
