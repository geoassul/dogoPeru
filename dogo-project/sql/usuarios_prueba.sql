-- Script para crear usuarios de prueba en DoGo Store
-- Contraseñas encriptadas con BCrypt para "admin" y "user"

-- Usuario ADMIN (username: admin, password: admin)
INSERT INTO user (username, password, role) VALUES 
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye72VLIzxKyK7kVKtF5JrJ0B8NW5n9LzK', 'ADMIN');

-- Usuario USER (username: user, password: user)  
INSERT INTO user (username, password, role) VALUES 
('user', '$2a$10$XRb2AxKf0UgLZVLO7C8PSuz8OzOV.cD6K8LBvCz0yzKHjHtbB8t8G', 'USER');

-- Verificar que los usuarios se crearon correctamente
SELECT id, username, role FROM user;
