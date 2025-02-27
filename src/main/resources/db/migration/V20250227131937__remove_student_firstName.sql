-- Adiciona a nova coluna "name"
ALTER TABLE student ADD COLUMN name VARCHAR(255) NOT NULL DEFAULT '';

-- Remove as colunas "first_name" e "last_name"
ALTER TABLE student DROP COLUMN first_name;
ALTER TABLE student DROP COLUMN last_name;

-- Torna "email" um campo único
ALTER TABLE student ADD CONSTRAINT unique_email UNIQUE (email);
