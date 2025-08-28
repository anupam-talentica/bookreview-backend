-- V3__Insert_sample_books.sql
-- Insert diverse sample books across different genres for development and testing

-- Classic Literature
INSERT INTO books (title, author, description, cover_image_url, genres, published_year, created_at) VALUES
(
    'To Kill a Mockingbird',
    'Harper Lee',
    'A gripping, heart-wrenching, and wholly remarkable tale of coming-of-age in a South poisoned by virulent prejudice.',
    'https://ik.imagekit.io/2uqec4d2s/assignment2-exports/mocking?updatedAt=1756316792937',
    'Classic, Fiction, Historical',
    1960,
    CURRENT_TIMESTAMP - INTERVAL '45 days'
),
(
    'Pride and Prejudice',
    'Jane Austen',
    'A romantic novel of manners that follows the character development of Elizabeth Bennet.',
    'https://ik.imagekit.io/2uqec4d2s/assignment2-exports/pride.jpg?updatedAt=1756360595477',
    'Classic, Romance, Fiction',
    1813,
    CURRENT_TIMESTAMP - INTERVAL '42 days'
),
(
    '1984',
    'George Orwell',
    'A dystopian social science fiction novel that follows Winston Smith, a low-ranking party member.',
    'https://ik.imagekit.io/2uqec4d2s/assignment2-exports/signet-1984-21.jpg?updatedAt=1756360742017',
    'Classic, Dystopian, Science Fiction',
    1949,
    CURRENT_TIMESTAMP - INTERVAL '40 days'
);

-- Science Fiction & Fantasy
INSERT INTO books (title, author, description, cover_image_url, genres, published_year, created_at) VALUES
(
    'Dune',
    'Frank Herbert',
    'Set in the distant future amidst a feudal interstellar society in which various noble houses control planetary fiefs.',
    'https://ik.imagekit.io/2uqec4d2s/assignment2-exports/dune.jpg?updatedAt=1756316793269',
    'Science Fiction, Fantasy, Adventure',
    1965,
    CURRENT_TIMESTAMP - INTERVAL '38 days'
),
(
    'The Lord of the Rings: The Fellowship of the Ring',
    'J.R.R. Tolkien',
    'The first volume of the epic high fantasy novel following the quest to destroy the One Ring.',
    'https://ik.imagekit.io/2uqec4d2s/assignment2-exports/lotr.jpg?updatedAt=1756359149729',
    'Fantasy, Adventure, Epic',
    1954,
    CURRENT_TIMESTAMP - INTERVAL '35 days'
),
(
    'The Hitchhiker''s Guide to the Galaxy',
    'Douglas Adams',
    'A comedy science fiction series that follows the misadventures of Arthur Dent.',
    'https://picsum.photos/seed/642/200/300',
    'Science Fiction, Comedy, Adventure',
    1979,
    CURRENT_TIMESTAMP - INTERVAL '32 days'
),
(
    'Ender''s Game',
    'Orson Scott Card',
    'A young boy is recruited to battle an alien threat in this military science fiction novel.',
    'https://picsum.photos/seed/371/200/300',
    'Science Fiction, Military, Young Adult',
    1985,
    CURRENT_TIMESTAMP - INTERVAL '30 days'
);

-- Mystery & Thriller
INSERT INTO books (title, author, description, cover_image_url, genres, published_year, created_at) VALUES
(
    'Gone Girl',
    'Gillian Flynn',
    'A psychological thriller about a marriage gone terribly wrong on the occasion of a fifth wedding anniversary.',
    'https://ik.imagekit.io/2uqec4d2s/assignment2-exports/GoneGirl.png?updatedAt=1756316793925',
    'Mystery, Thriller, Psychological',
    2012,
    CURRENT_TIMESTAMP - INTERVAL '28 days'
),
(
    'The Girl with the Dragon Tattoo',
    'Stieg Larsson',
    'A journalist and a computer hacker investigate a wealthy industrialist''s family secrets.',
    'https://ik.imagekit.io/2uqec4d2s/assignment2-exports/tatto.jpeg?updatedAt=1756359027680',
    'Mystery, Thriller, Crime',
    2005,
    CURRENT_TIMESTAMP - INTERVAL '25 days'
),
(
    'And Then There Were None',
    'Agatha Christie',
    'Ten strangers are invited to an isolated island where they begin to die one by one.',
    'https://picsum.photos/seed/923/200/300?grayscale',
    'Mystery, Crime, Classic',
    1939,
    CURRENT_TIMESTAMP - INTERVAL '22 days'
);

-- Contemporary Fiction
INSERT INTO books (title, author, description, cover_image_url, genres, published_year, created_at) VALUES
(
    'The Kite Runner',
    'Khaled Hosseini',
    'A story of friendship, guilt, and redemption set against the backdrop of Afghanistan.',
    'https://ik.imagekit.io/2uqec4d2s/assignment2-exports/KiteRunner.jpg?updatedAt=1756316793458',
    'Fiction, Historical, Contemporary',
    2003,
    CURRENT_TIMESTAMP - INTERVAL '20 days'
),
(
    'Life of Pi',
    'Yann Martel',
    'A young man survives a shipwreck and is stranded on a lifeboat with a Bengal tiger.',
    'https://ik.imagekit.io/2uqec4d2s/assignment2-exports/pie.jpeg?updatedAt=1756359027605',
    'Fiction, Adventure, Philosophical',
    2001,
    CURRENT_TIMESTAMP - INTERVAL '18 days'
),
(
    'The Book Thief',
    'Markus Zusak',
    'A young girl living in Nazi Germany finds solace by stealing books and sharing them.',
    'https://ik.imagekit.io/2uqec4d2s/assignment2-exports/thief.jpeg?updatedAt=1756359027626',
    'Historical Fiction, Young Adult, War',
    2005,
    CURRENT_TIMESTAMP - INTERVAL '15 days'
);

-- Non-Fiction & Self-Help
INSERT INTO books (title, author, description, cover_image_url, genres, published_year, created_at) VALUES
(
    'Sapiens: A Brief History of Humankind',
    'Yuval Noah Harari',
    'An exploration of how Homo sapiens conquered the world through cognitive, agricultural, and scientific revolutions.',
    'https://ik.imagekit.io/2uqec4d2s/assignment2-exports/sapiens.jpeg?updatedAt=1756359027628',
    'Non-fiction, History, Anthropology',
    2014,
    CURRENT_TIMESTAMP - INTERVAL '12 days'
),
(
    'Atomic Habits',
    'James Clear',
    'A comprehensive guide to breaking bad routines and creating good ones.',
    'https://ik.imagekit.io/2uqec4d2s/assignment2-exports/atomic.jpg?updatedAt=1756360969572',
    'Self-help, Productivity, Psychology',
    2018,
    CURRENT_TIMESTAMP - INTERVAL '10 days'
),
(
    'Educated',
    'Tara Westover',
    'A memoir about a young woman who grows up in a survivalist family and eventually earns a PhD from Cambridge.',
    'https://ik.imagekit.io/2uqec4d2s/assignment2-exports/educated.jpeg?updatedAt=1756359027947',
    'Memoir, Biography, Education',
    2018,
    CURRENT_TIMESTAMP - INTERVAL '8 days'
);

-- Romance
INSERT INTO books (title, author, description, cover_image_url, genres, published_year, created_at) VALUES
(
    'Me Before You',
    'Jojo Moyes',
    'A young woman becomes a caregiver for a paralyzed man, changing both their lives forever.',
    'https://ik.imagekit.io/2uqec4d2s/assignment2-exports/Meb4U.jpg?updatedAt=1756316793421',
    'Romance, Contemporary, Drama',
    2012,
    CURRENT_TIMESTAMP - INTERVAL '6 days'
),
(
    'The Notebook',
    'Nicholas Sparks',
    'An elderly man reads to a woman with dementia from a notebook containing the story of their love.',
    'https://ik.imagekit.io/2uqec4d2s/assignment2-exports/notebook.jpg?updatedAt=1756316793260',
    'Romance, Drama, Contemporary',
    1996,
    CURRENT_TIMESTAMP - INTERVAL '4 days'
);

-- Young Adult
INSERT INTO books (title, author, description, cover_image_url, genres, published_year, created_at) VALUES
(
    'Harry Potter and the Sorcerer''s Stone',
    'J.K. Rowling',
    'A young boy discovers he is a wizard and attends Hogwarts School of Witchcraft and Wizardry.',
    'https://ik.imagekit.io/2uqec4d2s/HP_Bb0QBFTQY?updatedAt=1756235685240',
    'Fantasy, Young Adult, Adventure',
    1997,
    CURRENT_TIMESTAMP - INTERVAL '2 days'
),
(
    'The Hunger Games',
    'Suzanne Collins',
    'In a dystopian future, teens fight to the death in a televised competition.',
    'https://ik.imagekit.io/2uqec4d2s/assignment2-exports/hunger.webp?updatedAt=1756361282196',
    'Dystopian, Young Adult, Action',
    2008,
    CURRENT_TIMESTAMP - INTERVAL '1 day'
);

-- Business & Technology
INSERT INTO books (title, author, description, cover_image_url, genres, published_year, created_at) VALUES
(
    'Steve Jobs',
    'Walter Isaacson',
    'The exclusive biography of Steve Jobs based on more than forty interviews.',
    'https://ik.imagekit.io/2uqec4d2s/assignment2-exports/jobs.jpeg?updatedAt=1756359028068',
    'Biography, Business, Technology',
    2011,
    CURRENT_TIMESTAMP
),
(
    'The Lean Startup',
    'Eric Ries',
    'A methodology for developing businesses and products through validated learning and iterative design.',
    'https://ik.imagekit.io/2uqec4d2s/assignment2-exports/Lean.jpeg?updatedAt=1756359027860',
    'Business, Entrepreneurship, Technology',
    2011,
    CURRENT_TIMESTAMP
);
