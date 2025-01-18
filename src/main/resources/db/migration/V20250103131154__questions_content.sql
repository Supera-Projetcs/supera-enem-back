-- Adicionar a tabela intermediária question_contents
CREATE TABLE question_contents (
                                   question_id BIGINT NOT NULL,
                                   content_id BIGINT NOT NULL,
                                   PRIMARY KEY (question_id, content_id),
                                   FOREIGN KEY (question_id) REFERENCES question(id) ON DELETE CASCADE,
                                   FOREIGN KEY (content_id) REFERENCES content(id) ON DELETE CASCADE
);

-- Caso deseje, também pode criar um índice para melhorar o desempenho nas consultas de relacionamento:
CREATE INDEX idx_question_contents_question_id ON question_contents (question_id);
CREATE INDEX idx_question_contents_content_id ON question_contents (content_id);