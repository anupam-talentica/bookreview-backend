-- V2__Insert_sample_users.sql
-- Insert sample users including admin and regular users for development and testing

-- Insert Admin User
-- Password: Admin123! (hashed with BCrypt)
INSERT INTO users (email, password_hash, name, bio, email_verified, active, created_at) VALUES 
(
    'admin@bookreview.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- password: password
    'System Administrator',
    'BookReview platform administrator with full system access.',
    true,
    true,
    CURRENT_TIMESTAMP - INTERVAL '30 days'
);

-- Insert Regular Users for testing
INSERT INTO users (email, password_hash, name, bio, email_verified, active, created_at) VALUES 
(
    'john.doe@example.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- password: password
    'John Doe',
    'Avid reader and software developer. Love science fiction and technical books.',
    true,
    true,
    CURRENT_TIMESTAMP - INTERVAL '25 days'
),
(
    'jane.smith@example.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- password: password
    'Jane Smith',
    'Fantasy and mystery book enthusiast. Always looking for the next great adventure.',
    true,
    true,
    CURRENT_TIMESTAMP - INTERVAL '20 days'
),
(
    'mike.wilson@example.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- password: password
    'Mike Wilson',
    'History buff and biography reader. Enjoys learning about different cultures and time periods.',
    true,
    true,
    CURRENT_TIMESTAMP - INTERVAL '18 days'
),
(
    'sarah.brown@example.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- password: password
    'Sarah Brown',
    'Romance and contemporary fiction lover. Book club organizer and review writer.',
    true,
    true,
    CURRENT_TIMESTAMP - INTERVAL '15 days'
),
(
    'alex.chen@example.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- password: password
    'Alex Chen',
    'Non-fiction enthusiast. Passionate about personal development, business, and psychology.',
    true,
    true,
    CURRENT_TIMESTAMP - INTERVAL '12 days'
),
(
    'emma.davis@example.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- password: password
    'Emma Davis',
    'Young adult and classic literature reader. English literature graduate student.',
    true,
    true,
    CURRENT_TIMESTAMP - INTERVAL '10 days'
),
(
    'tom.anderson@example.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- password: password
    'Tom Anderson',
    'Thriller and crime novel fan. Enjoys psychological suspense and detective stories.',
    true,
    true,
    CURRENT_TIMESTAMP - INTERVAL '8 days'
);

-- Note: All users have the password "password" for development/testing purposes
-- In production, users would register with their own secure passwords
