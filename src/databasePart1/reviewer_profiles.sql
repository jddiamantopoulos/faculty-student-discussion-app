CREATE TABLE IF NOT EXISTS reviewer_profiles (
    username VARCHAR(255) PRIMARY KEY,
    bio TEXT,
    expertise_areas TEXT,
    student_feedback TEXT,
    FOREIGN KEY (username) REFERENCES cse360users(userName)
); 