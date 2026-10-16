CREATE DATABASE IF NOT EXISTS library_management;
USE library_management;

CREATE TABLE IF NOT EXISTS books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    author VARCHAR(120) NOT NULL,
    category VARCHAR(20) NOT NULL DEFAULT 'REGULAR',
    available BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS members (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS borrow_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    member_id INT NOT NULL,
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE NULL,
    CONSTRAINT fk_record_book FOREIGN KEY (book_id) REFERENCES books(id),
    CONSTRAINT fk_record_member FOREIGN KEY (member_id) REFERENCES members(id)
);

-- Optional sample records:
-- INSERT INTO books (title, author, category) VALUES
-- ('Introduction to Java', 'Herbert Schildt', 'REGULAR'),
-- ('Computer Networks Reference', 'Andrew Tanenbaum', 'REFERENCE');
-- INSERT INTO members (full_name, email) VALUES ('Sample Student', 'student@example.com');
