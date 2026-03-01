-- RevPlay Database Schema (User Provided)

-- Table USER_ACCOUNT
CREATE TABLE IF NOT EXISTS USER_ACCOUNT (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  full_name VARCHAR(100),
  email VARCHAR(100) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  phone VARCHAR(20),
  security_question VARCHAR(255),
  security_answer_hash VARCHAR(255),
  password_hint VARCHAR(255),
  status VARCHAR(20),
  bio VARCHAR(1000),
  profile_image_url VARCHAR(500),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Table ARTIST_ACCOUNT
CREATE TABLE IF NOT EXISTS ARTIST_ACCOUNT (
  artist_id INT AUTO_INCREMENT PRIMARY KEY,
  stage_name VARCHAR(100) NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  bio VARCHAR(1000),
  genre VARCHAR(50),
  instagram_link VARCHAR(255),
  youtube_link VARCHAR(255),
  spotify_link VARCHAR(255),
  security_question VARCHAR(255),
  security_answer_hash VARCHAR(255),
  password_hint VARCHAR(255),
  status VARCHAR(20),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Table GENRE
CREATE TABLE IF NOT EXISTS GENRE (
  genre_id INT AUTO_INCREMENT PRIMARY KEY,
  genre_name VARCHAR(50) UNIQUE NOT NULL
);

-- Default Genres
INSERT INTO GENRE (genre_name) SELECT 'Pop' WHERE NOT EXISTS (SELECT * FROM GENRE WHERE genre_name='Pop');
INSERT INTO GENRE (genre_name) SELECT 'Rock' WHERE NOT EXISTS (SELECT * FROM GENRE WHERE genre_name='Rock');
INSERT INTO GENRE (genre_name) SELECT 'Hip-Hop' WHERE NOT EXISTS (SELECT * FROM GENRE WHERE genre_name='Hip-Hop');
INSERT INTO GENRE (genre_name) SELECT 'Jazz' WHERE NOT EXISTS (SELECT * FROM GENRE WHERE genre_name='Jazz');
INSERT INTO GENRE (genre_name) SELECT 'Classical' WHERE NOT EXISTS (SELECT * FROM GENRE WHERE genre_name='Classical');
INSERT INTO GENRE (genre_name) SELECT 'Electronic' WHERE NOT EXISTS (SELECT * FROM GENRE WHERE genre_name='Electronic');

-- Table ALBUM
CREATE TABLE IF NOT EXISTS ALBUM (
  album_id INT AUTO_INCREMENT PRIMARY KEY,
  artist_id INT NOT NULL,
  title VARCHAR(100) NOT NULL,
  release_date DATE,
  description VARCHAR(255),
  cover_image_url VARCHAR(255),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (artist_id) REFERENCES ARTIST_ACCOUNT(artist_id)
);

-- Table SONG
CREATE TABLE IF NOT EXISTS SONG (
  song_id INT AUTO_INCREMENT PRIMARY KEY,
  artist_id INT NOT NULL,
  album_id INT,
  genre_id INT NOT NULL,
  title VARCHAR(100) NOT NULL,
  duration_seconds INT NOT NULL,
  release_date DATE,
  file_url VARCHAR(255),
  play_count INT DEFAULT 0,
  is_active VARCHAR(20),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (artist_id) REFERENCES ARTIST_ACCOUNT(artist_id),
  FOREIGN KEY (album_id) REFERENCES ALBUM(album_id),
  FOREIGN KEY (genre_id) REFERENCES GENRE(genre_id)
);

-- Table PLAYLIST
CREATE TABLE IF NOT EXISTS PLAYLIST (
  playlist_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(255),
  privacy_status VARCHAR(20) NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES USER_ACCOUNT(user_id)
);

-- Table PLAYLIST_SONG
CREATE TABLE IF NOT EXISTS PLAYLIST_SONG (
  playlist_id INT,
  song_id INT,
  added_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (playlist_id, song_id),
  FOREIGN KEY (playlist_id) REFERENCES PLAYLIST(playlist_id),
  FOREIGN KEY (song_id) REFERENCES SONG(song_id)
);

-- Table FAVORITE_SONG
CREATE TABLE IF NOT EXISTS FAVORITE_SONG (
  user_id INT,
  song_id INT,
  favorited_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id, song_id),
  FOREIGN KEY (user_id) REFERENCES USER_ACCOUNT(user_id),
  FOREIGN KEY (song_id) REFERENCES SONG(song_id)
);

-- Table LISTENING_HISTORY
CREATE TABLE IF NOT EXISTS LISTENING_HISTORY (
  history_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  song_id INT NOT NULL,
  played_at DATETIME NOT NULL,
  action_type VARCHAR(50) NOT NULL,
  FOREIGN KEY (user_id) REFERENCES USER_ACCOUNT(user_id),
  FOREIGN KEY (song_id) REFERENCES SONG(song_id)
);

-- Table PODCAST (Stub)
CREATE TABLE IF NOT EXISTS PODCAST (
  podcast_id INT AUTO_INCREMENT PRIMARY KEY,
  artist_id INT NOT NULL,
  title VARCHAR(100) NOT NULL,
  host_name VARCHAR(100),
  category VARCHAR(50),
  description VARCHAR(255),
  cover_image_url VARCHAR(255),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (artist_id) REFERENCES ARTIST_ACCOUNT(artist_id)
);

-- Table PODCAST_EPISODE (Stub)
CREATE TABLE IF NOT EXISTS PODCAST_EPISODE (
  episode_id INT AUTO_INCREMENT PRIMARY KEY,
  podcast_id INT NOT NULL,
  title VARCHAR(100) NOT NULL,
  duration_seconds INT NOT NULL,
  release_date DATE,
  file_url VARCHAR(255),
  play_count INT DEFAULT 0,
  cover_image_url VARCHAR(255),
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (podcast_id) REFERENCES PODCAST(podcast_id)
);
