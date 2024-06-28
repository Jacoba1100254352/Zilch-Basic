CREATE DATABASE Zilch;

USE Zilch;

-- Users Table
CREATE TABLE Users
(
    Username  VARCHAR(50)  NOT NULL PRIMARY KEY,
    Password  VARCHAR(255) NOT NULL,              -- Storing a hashed password
    Email     VARCHAR(100) NOT NULL UNIQUE,
    CreatedAt TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    isAdmin   BOOLEAN      NOT NULL DEFAULT FALSE -- New field to indicate if the user is an admin
);

-- Auth Tokens Table
CREATE TABLE AuthTokens
(
    TokenID   INT AUTO_INCREMENT PRIMARY KEY,
    Token     VARCHAR(255) NOT NULL UNIQUE,
    Username  VARCHAR(50)  NOT NULL,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (Username) REFERENCES Users (Username)
);

-- Games Table
CREATE TABLE Games
(
    GameID        INT AUTO_INCREMENT PRIMARY KEY,
    WhiteUsername VARCHAR(50),           -- Username of the user playing white
    BlackUsername VARCHAR(50),           -- Username of the user playing black
    GameName      VARCHAR(255) NOT NULL,
    ChessGame     TEXT         NOT NULL, -- Store the serialized game state
    CreatedAt     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    LastMoveAt    TIMESTAMP,
    FOREIGN KEY (WhiteUsername) REFERENCES Users (Username),
    FOREIGN KEY (BlackUsername) REFERENCES Users (Username)
);
