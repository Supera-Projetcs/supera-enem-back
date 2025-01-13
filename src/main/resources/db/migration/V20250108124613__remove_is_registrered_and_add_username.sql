
ALTER TABLE student
    ADD COLUMN username VARCHAR(255);

UPDATE student
SET username = 'user_' || id
WHERE username IS NULL;


ALTER TABLE student
    ALTER COLUMN username SET NOT NULL;


ALTER TABLE student
    ADD CONSTRAINT unique_username UNIQUE (username);

ALTER TABLE student
DROP COLUMN is_registered;
