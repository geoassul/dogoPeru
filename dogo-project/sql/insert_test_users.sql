-- Insert test users for DoGo Project
-- The password for both users is "password" encrypted with BCrypt

-- Delete existing test users if they exist
DELETE FROM user WHERE username IN ('admin', 'user');

-- Insert admin user (username: admin, password: password)
INSERT INTO user (username, password, role) VALUES 
('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ADMIN');

-- Insert regular user (username: user, password: password)  
INSERT INTO user (username, password, role) VALUES 
('user', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'USER');

-- Show created users
SELECT id, username, role FROM user WHERE username IN ('admin', 'user');
