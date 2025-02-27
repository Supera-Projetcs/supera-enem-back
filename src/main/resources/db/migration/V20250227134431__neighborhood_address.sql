ALTER TABLE address
    ADD COLUMN neighborhood VARCHAR(255) DEFAULT 'Desconhecido',
ALTER COLUMN street SET NOT NULL,
    ALTER COLUMN city SET NOT NULL,
    ALTER COLUMN state SET NOT NULL;

-- Atualiza registros existentes para evitar valores padrão indesejados
UPDATE address SET neighborhood = 'Bairro Padrão' WHERE neighborhood = 'Desconhecido';

-- Agora torna a coluna obrigatória
ALTER TABLE address ALTER COLUMN neighborhood SET NOT NULL;
