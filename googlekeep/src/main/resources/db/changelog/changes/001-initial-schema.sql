CREATE TABLE notes
(
    ID  BIGINT NOT NULL AUTO_INCREMENT,
    TITLE VARCHAR(256) NOT NULL,
    CONTENT VARCHAR(512),
    PRIMARY KEY(ID)
);
