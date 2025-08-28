-- V1__Create_initial_schema.sql
-- Initial database schema creation for BookReview platform
-- Creates users, books, reviews, and user_favorites tables

-- Enable required PostgreSQL extensions
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Create users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    bio TEXT,
    avatar_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    email_verified BOOLEAN DEFAULT FALSE,
    active BOOLEAN DEFAULT TRUE,
    
    CONSTRAINT users_email_valid CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT users_name_not_empty CHECK (TRIM(name) != '')
);

-- Create books table
CREATE TABLE books (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    description TEXT,
    cover_image_url VARCHAR(500),
    genres VARCHAR(255),
    published_year INTEGER,
    average_rating DECIMAL(3,2) DEFAULT 0.00,
    review_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT books_title_not_empty CHECK (TRIM(title) != ''),
    CONSTRAINT books_author_not_empty CHECK (TRIM(author) != ''),
    CONSTRAINT books_published_year_valid CHECK (published_year IS NULL OR (published_year >= 1000 AND published_year <= EXTRACT(YEAR FROM CURRENT_DATE) + 5)),
    CONSTRAINT books_average_rating_valid CHECK (average_rating >= 0.00 AND average_rating <= 5.00),
    CONSTRAINT books_review_count_valid CHECK (review_count >= 0)
);

-- Create reviews table
CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    rating INTEGER NOT NULL,
    review_text TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    UNIQUE(user_id, book_id),
    
    CONSTRAINT reviews_rating_valid CHECK (rating >= 1 AND rating <= 5),
    CONSTRAINT reviews_text_length CHECK (review_text IS NULL OR LENGTH(TRIM(review_text)) <= 2000)
);

-- Create user_favorites table
CREATE TABLE user_favorites (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    UNIQUE(user_id, book_id)
);

-- Create indexes for better performance
-- Users table indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_created_at ON users(created_at DESC);
CREATE INDEX idx_users_active ON users(active) WHERE active = TRUE;

-- Books table indexes
CREATE INDEX idx_books_title ON books USING gin(to_tsvector('english', title));
CREATE INDEX idx_books_author ON books USING gin(to_tsvector('english', author));
CREATE INDEX idx_books_title_trigram ON books USING gin(title gin_trgm_ops);
CREATE INDEX idx_books_author_trigram ON books USING gin(author gin_trgm_ops);
CREATE INDEX idx_books_average_rating ON books(average_rating DESC);
CREATE INDEX idx_books_review_count ON books(review_count DESC);
CREATE INDEX idx_books_published_year ON books(published_year DESC);
CREATE INDEX idx_books_created_at ON books(created_at DESC);

-- Reviews table indexes
CREATE INDEX idx_reviews_user_id ON reviews(user_id);
CREATE INDEX idx_reviews_book_id ON reviews(book_id);
CREATE INDEX idx_reviews_rating ON reviews(rating);
CREATE INDEX idx_reviews_created_at ON reviews(created_at DESC);
CREATE INDEX idx_reviews_book_rating ON reviews(book_id, rating);

-- User favorites table indexes
CREATE INDEX idx_user_favorites_user_id ON user_favorites(user_id);
CREATE INDEX idx_user_favorites_book_id ON user_favorites(book_id);
CREATE INDEX idx_user_favorites_created_at ON user_favorites(created_at DESC);

-- Create utility functions
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION random_between(low INT, high INT) 
RETURNS INT AS $$
BEGIN
   RETURN floor(random() * (high - low + 1) + low);
END;
$$ LANGUAGE plpgsql;

-- Create triggers for automatic updated_at timestamp updates
CREATE TRIGGER update_users_updated_at 
    BEFORE UPDATE ON users 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_books_updated_at 
    BEFORE UPDATE ON books 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_reviews_updated_at 
    BEFORE UPDATE ON reviews 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Create function to recalculate book ratings
CREATE OR REPLACE FUNCTION recalculate_book_rating(book_id_param bigint)
RETURNS void AS $$
DECLARE
    avg_rating numeric(3,2);
    review_count_var integer;
BEGIN
    -- Calculate average rating and count
    SELECT 
        COALESCE(ROUND(AVG(rating), 2), 0.00),
        COUNT(*)
    INTO avg_rating, review_count_var
    FROM reviews 
    WHERE book_id = book_id_param;
    
    -- Update book record
    UPDATE books 
    SET 
        average_rating = avg_rating,
        review_count = review_count_var,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = book_id_param;
END;
$$ LANGUAGE plpgsql;

-- Create triggers for automatic book rating updates when reviews change
CREATE OR REPLACE FUNCTION update_book_rating_on_review_change()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' OR TG_OP = 'UPDATE' THEN
        PERFORM recalculate_book_rating(NEW.book_id);
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        PERFORM recalculate_book_rating(OLD.book_id);
        RETURN OLD;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER review_rating_update_trigger
    AFTER INSERT OR UPDATE OR DELETE ON reviews
    FOR EACH ROW
    EXECUTE FUNCTION update_book_rating_on_review_change();
