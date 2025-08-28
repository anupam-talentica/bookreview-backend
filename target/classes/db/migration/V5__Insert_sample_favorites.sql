-- V5__Insert_sample_favorites.sql
-- Insert sample user favorites to demonstrate the favorites functionality

-- John Doe's favorites (Science Fiction enthusiast)
INSERT INTO user_favorites (user_id, book_id, created_at) VALUES
(2, 4, CURRENT_TIMESTAMP - INTERVAL '15 days'),  -- Dune
(2, 7, CURRENT_TIMESTAMP - INTERVAL '12 days'),  -- Ender's Game
(2, 6, CURRENT_TIMESTAMP - INTERVAL '10 days'),  -- Hitchhiker's Guide
(2, 3, CURRENT_TIMESTAMP - INTERVAL '8 days'),   -- 1984
(2, 14, CURRENT_TIMESTAMP - INTERVAL '5 days');  -- Sapiens

-- Jane Smith's favorites (Fantasy and Mystery)
INSERT INTO user_favorites (user_id, book_id, created_at) VALUES
(3, 5, CURRENT_TIMESTAMP - INTERVAL '14 days'),  -- LOTR Fellowship
(3, 19, CURRENT_TIMESTAMP - INTERVAL '11 days'), -- Harry Potter
(3, 8, CURRENT_TIMESTAMP - INTERVAL '9 days'),   -- Gone Girl
(3, 10, CURRENT_TIMESTAMP - INTERVAL '7 days'),  -- And Then There Were None
(3, 20, CURRENT_TIMESTAMP - INTERVAL '3 days');  -- Hunger Games

-- Mike Wilson's favorites (History and Biography)
INSERT INTO user_favorites (user_id, book_id, created_at) VALUES
(4, 1, CURRENT_TIMESTAMP - INTERVAL '16 days'),  -- To Kill a Mockingbird
(4, 14, CURRENT_TIMESTAMP - INTERVAL '13 days'), -- Sapiens
(4, 16, CURRENT_TIMESTAMP - INTERVAL '6 days'),  -- Educated
(4, 21, CURRENT_TIMESTAMP - INTERVAL '4 days');  -- Steve Jobs

-- Sarah Brown's favorites (Romance and Contemporary)
INSERT INTO user_favorites (user_id, book_id, created_at) VALUES
(5, 2, CURRENT_TIMESTAMP - INTERVAL '17 days'),  -- Pride and Prejudice
(5, 17, CURRENT_TIMESTAMP - INTERVAL '9 days'),  -- Me Before You
(5, 18, CURRENT_TIMESTAMP - INTERVAL '6 days'),  -- The Notebook
(5, 11, CURRENT_TIMESTAMP - INTERVAL '4 days'),  -- The Kite Runner
(5, 12, CURRENT_TIMESTAMP - INTERVAL '2 days');  -- Life of Pi

-- Alex Chen's favorites (Non-fiction and Self-help)
INSERT INTO user_favorites (user_id, book_id, created_at) VALUES
(6, 15, CURRENT_TIMESTAMP - INTERVAL '8 days'),  -- Atomic Habits
(6, 22, CURRENT_TIMESTAMP - INTERVAL '5 days'),  -- Lean Startup
(6, 14, CURRENT_TIMESTAMP - INTERVAL '3 days'),  -- Sapiens
(6, 21, CURRENT_TIMESTAMP - INTERVAL '1 day');   -- Steve Jobs

-- Emma Davis's favorites (YA and Classics)
INSERT INTO user_favorites (user_id, book_id, created_at) VALUES
(7, 19, CURRENT_TIMESTAMP - INTERVAL '10 days'), -- Harry Potter
(7, 20, CURRENT_TIMESTAMP - INTERVAL '7 days'),  -- Hunger Games
(7, 12, CURRENT_TIMESTAMP - INTERVAL '5 days'),  -- Life of Pi
(7, 1, CURRENT_TIMESTAMP - INTERVAL '3 days'),   -- To Kill a Mockingbird
(7, 2, CURRENT_TIMESTAMP - INTERVAL '1 day');    -- Pride and Prejudice

-- Tom Anderson's favorites (Thriller and Crime)
INSERT INTO user_favorites (user_id, book_id, created_at) VALUES
(8, 9, CURRENT_TIMESTAMP - INTERVAL '12 days'),  -- Girl with Dragon Tattoo
(8, 8, CURRENT_TIMESTAMP - INTERVAL '9 days'),   -- Gone Girl
(8, 10, CURRENT_TIMESTAMP - INTERVAL '6 days'),  -- And Then There Were None
(8, 3, CURRENT_TIMESTAMP - INTERVAL '4 days'),   -- 1984
(8, 11, CURRENT_TIMESTAMP - INTERVAL '2 days');  -- The Kite Runner

-- Admin favorites (diverse selection)
INSERT INTO user_favorites (user_id, book_id, created_at) VALUES
(1, 1, CURRENT_TIMESTAMP - INTERVAL '18 days'),  -- To Kill a Mockingbird
(1, 4, CURRENT_TIMESTAMP - INTERVAL '15 days'),  -- Dune
(1, 19, CURRENT_TIMESTAMP - INTERVAL '12 days'), -- Harry Potter
(1, 14, CURRENT_TIMESTAMP - INTERVAL '8 days'),  -- Sapiens
(1, 15, CURRENT_TIMESTAMP - INTERVAL '4 days');  -- Atomic Habits