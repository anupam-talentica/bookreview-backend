-- V4__Insert_sample_reviews.sql
-- Insert sample reviews and ratings for books from various users

-- Reviews from John Doe (user_id: 2) - Science Fiction enthusiast
INSERT INTO reviews (user_id, book_id, rating, review_text, created_at) VALUES
(2, 4, 5, 'Dune is an absolute masterpiece! Herbert created an incredibly complex and detailed universe. The political intrigue, ecology, and mysticism blend perfectly. A must-read for any science fiction fan.', CURRENT_TIMESTAMP - INTERVAL '20 days'),
(2, 7, 4, 'Ender''s Game presents fascinating ethical questions about war and childhood. Card''s exploration of military strategy through a child''s perspective is brilliant, though some aspects feel dated now.', CURRENT_TIMESTAMP - INTERVAL '18 days'),
(2, 6, 5, 'Douglas Adams'' humor is unmatched! This book made me laugh out loud while also presenting clever commentary on life, the universe, and everything. Pure genius.', CURRENT_TIMESTAMP - INTERVAL '15 days'),
(2, 3, 5, '1984 remains terrifyingly relevant today. Orwell''s vision of surveillance and thought control feels prophetic. Essential reading for understanding modern society.', CURRENT_TIMESTAMP - INTERVAL '12 days');

-- Reviews from Jane Smith (user_id: 3) - Fantasy and mystery enthusiast
INSERT INTO reviews (user_id, book_id, rating, review_text, created_at) VALUES
(3, 5, 5, 'Tolkien''s world-building is unparalleled. The Fellowship of the Ring draws you into Middle-earth completely. Every page is filled with rich detail and beautiful prose.', CURRENT_TIMESTAMP - INTERVAL '19 days'),
(3, 8, 4, 'Gone Girl kept me guessing until the very end. Flynn''s psychological insights are disturbing and brilliant. The unreliable narrators make it even more compelling.', CURRENT_TIMESTAMP - INTERVAL '16 days'),
(3, 10, 5, 'Agatha Christie is the queen of mystery! The plot twists in this book are incredible, and I never saw the ending coming. Classic detective fiction at its best.', CURRENT_TIMESTAMP - INTERVAL '13 days'),
(3, 18, 5, 'Harry Potter ignited my love for fantasy. Rowling created a magical world that feels real and characters you genuinely care about. Timeless and enchanting.', CURRENT_TIMESTAMP - INTERVAL '8 days');

-- Reviews from Mike Wilson (user_id: 4) - History and biography reader
INSERT INTO reviews (user_id, book_id, rating, review_text, created_at) VALUES
(4, 1, 5, 'Harper Lee''s portrayal of racial injustice is both heartbreaking and necessary. Atticus Finch remains one of literature''s great moral characters. Powerful and moving.', CURRENT_TIMESTAMP - INTERVAL '21 days'),
(4, 13, 4, 'Harari presents human history in a fascinating new light. Some arguments are controversial, but his writing is engaging and thought-provoking throughout.', CURRENT_TIMESTAMP - INTERVAL '14 days'),
(4, 15, 4, 'Westover''s memoir is both inspiring and disturbing. Her journey from an isolated childhood to academic success is remarkable and beautifully written.', CURRENT_TIMESTAMP - INTERVAL '9 days'),
(4, 20, 5, 'Isaacson captures Jobs'' complexity perfectly. This biography reveals both the genius and the flaws of one of technology''s most influential figures.', CURRENT_TIMESTAMP - INTERVAL '5 days');

-- Reviews from Sarah Brown (user_id: 5) - Romance and contemporary fiction
INSERT INTO reviews (user_id, book_id, rating, review_text, created_at) VALUES
(5, 2, 4, 'Austen''s wit and social commentary remain sharp and relevant. Elizabeth Bennet is a wonderful protagonist, and the romance with Darcy is perfectly developed.', CURRENT_TIMESTAMP - INTERVAL '22 days'),
(5, 16, 5, 'Me Before You destroyed me emotionally in the best way. Moyes handles difficult topics with grace and creates characters you truly care about.', CURRENT_TIMESTAMP - INTERVAL '11 days'),
(5, 17, 4, 'Nicholas Sparks knows how to tug at heartstrings. The Notebook is a beautiful love story that spans generations. Perfect for romance lovers.', CURRENT_TIMESTAMP - INTERVAL '7 days'),
(5, 11, 4, 'Hosseini''s storytelling is incredibly powerful. The friendship between Amir and Hassan is beautifully portrayed against the backdrop of Afghanistan''s history.', CURRENT_TIMESTAMP - INTERVAL '6 days');

-- Reviews from Alex Chen (user_id: 6) - Non-fiction enthusiast
INSERT INTO reviews (user_id, book_id, rating, review_text, created_at) VALUES
(6, 14, 5, 'James Clear breaks down habit formation in an incredibly practical way. This book has genuinely changed how I approach personal development. Highly recommended!', CURRENT_TIMESTAMP - INTERVAL '10 days'),
(6, 21, 4, 'Ries presents a systematic approach to building startups. The lean methodology is valuable for any entrepreneur, though some concepts have evolved since publication.', CURRENT_TIMESTAMP - INTERVAL '4 days'),
(6, 13, 5, 'Sapiens challenges everything you think you know about human civilization. Harari''s insights about cognitive and agricultural revolutions are brilliant.', CURRENT_TIMESTAMP - INTERVAL '3 days');

-- Reviews from Emma Davis (user_id: 7) - YA and classic literature
INSERT INTO reviews (user_id, book_id, rating, review_text, created_at) VALUES
(7, 12, 5, 'Life of Pi is a philosophical masterpiece disguised as an adventure story. Martel''s exploration of faith, survival, and storytelling is profound and beautiful.', CURRENT_TIMESTAMP - INTERVAL '17 days'),
(7, 19, 4, 'The Hunger Games creates a compelling dystopian world while exploring themes of power, sacrifice, and survival. Katniss is a strong, relatable protagonist.', CURRENT_TIMESTAMP - INTERVAL '2 days'),
(7, 18, 5, 'The magic of Harry Potter never fades. Rowling''s ability to create wonder and excitement while dealing with serious themes is extraordinary.', CURRENT_TIMESTAMP - INTERVAL '1 day');

-- Reviews from Tom Anderson (user_id: 8) - Thriller and crime novels
INSERT INTO reviews (user_id, book_id, rating, review_text, created_at) VALUES
(8, 9, 5, 'Stieg Larsson created something special with this series. The mystery is complex, the characters are compelling, and the social commentary is powerful.', CURRENT_TIMESTAMP - INTERVAL '23 days'),
(8, 8, 4, 'Gillian Flynn''s psychological thriller is masterfully crafted. The twists keep coming, and both main characters are fascinatingly unreliable narrators.', CURRENT_TIMESTAMP - INTERVAL '24 days'),
(8, 10, 4, 'Classic Christie at her finest. The isolated setting and methodical elimination of characters creates incredible tension. A masterclass in mystery writing.', CURRENT_TIMESTAMP - INTERVAL '25 days');

-- Additional reviews to boost some books' ratings
-- Admin reviews (user_id: 1)
INSERT INTO reviews (user_id, book_id, rating, review_text, created_at) VALUES
(1, 1, 5, 'A timeless classic that addresses important themes with grace and wisdom. Required reading for understanding American literature and social justice.', CURRENT_TIMESTAMP - INTERVAL '30 days'),
(1, 4, 5, 'Herbert''s Dune is science fiction at its absolute best. Complex politics, fascinating ecology, and unforgettable characters make this a true epic.', CURRENT_TIMESTAMP - INTERVAL '28 days'),
(1, 18, 5, 'J.K. Rowling created magic in the truest sense. Harry Potter has inspired millions and will continue to do so for generations to come.', CURRENT_TIMESTAMP - INTERVAL '26 days');

-- Cross-reviews to create more diverse ratings
INSERT INTO reviews (user_id, book_id, rating, review_text, created_at) VALUES
(3, 14, 4, 'Clear''s approach to habits is practical and actionable. As someone who struggles with consistency, this book provided valuable insights.', CURRENT_TIMESTAMP - INTERVAL '5 days'),
(4, 8, 3, 'Well-written psychological thriller, but the dark themes and unreliable narrators made it a difficult read for me personally.', CURRENT_TIMESTAMP - INTERVAL '4 days'),
(6, 5, 4, 'Tolkien''s world-building is incredible, though the pacing can be slow at times. Still a foundational work of fantasy literature.', CURRENT_TIMESTAMP - INTERVAL '3 days'),
(7, 2, 5, 'Austen''s social commentary and character development are timeless. Elizabeth Bennet remains one of literature''s great heroines.', CURRENT_TIMESTAMP - INTERVAL '2 days'),
(8, 12, 4, 'Martel''s philosophical questions about truth and survival are thought-provoking. The tiger metaphor works on multiple levels.', CURRENT_TIMESTAMP - INTERVAL '1 day');