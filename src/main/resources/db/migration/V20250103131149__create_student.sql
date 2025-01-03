CREATE TABLE Address (
                         id BIGSERIAL PRIMARY KEY,
                         street VARCHAR(255) NOT NULL,
                         city VARCHAR(255) NOT NULL,
                         state VARCHAR(100) NOT NULL,
                         zip_code VARCHAR(20) NOT NULL,
                         house_number VARCHAR(50) NOT NULL
);

CREATE TABLE Student (
                         id BIGSERIAL PRIMARY KEY,
                         keycloak_id VARCHAR(255) NOT NULL UNIQUE,
                         first_name VARCHAR(255) NOT NULL,
                         last_name VARCHAR(255) NOT NULL,
                         dream_course VARCHAR(255) NOT NULL,
                         phone VARCHAR(50) NOT NULL,
                         email VARCHAR(255) NOT NULL,
                         birth_date DATE NOT NULL,
                         address_id BIGINT,
                         is_registered BOOLEAN DEFAULT FALSE,
                         CONSTRAINT fk_address FOREIGN KEY (address_id) REFERENCES Address (id) ON DELETE CASCADE
);

CREATE TABLE Student_PreferredStudyDays (
                                            student_id BIGINT NOT NULL,
                                            preferred_study_day VARCHAR(50) NOT NULL,
                                            PRIMARY KEY (student_id, preferred_study_day),
                                            CONSTRAINT fk_student FOREIGN KEY (student_id) REFERENCES Student (id) ON DELETE CASCADE
);
