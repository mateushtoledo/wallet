CREATE TABLE users 
( 
   id       SERIAL, 
   NAME     VARCHAR(50), 
   password VARCHAR, 
   email    VARCHAR(100), 
   PRIMARY KEY (id) 
); 

CREATE TABLE wallet 
( 
   id    SERIAL, 
   NAME  VARCHAR(60), 
   value NUMERIC(10, 2), 
   PRIMARY KEY (id) 
); 

CREATE TABLE users_wallet 
( 
   id     SERIAL, 
   wallet INTEGER, 
   users  INTEGER, 
   PRIMARY KEY(id), 
   FOREIGN KEY(users) REFERENCES users(id), 
   FOREIGN KEY(wallet) REFERENCES wallet(id) 
); 

CREATE TABLE wallet_items 
( 
   id          SERIAL, 
   wallet      INTEGER, 
   date        DATE, 
   type        VARCHAR(2), 
   description VARCHAR(500), 
   value       NUMERIC(10, 2), 
   PRIMARY KEY(id), 
   FOREIGN KEY(wallet) REFERENCES wallet(id) 
); 