ALTER TABLE wallet ADD COLUMN fk_user_id BIGINT;
UPDATE wallet SET fk_user_id = (SELECT uw.users FROM users_wallet uw WHERE uw.wallet = id);
ALTER TABLE wallet ADD CONSTRAINT fk_user_id_constraint FOREIGN KEY (fk_user_id) REFERENCES users(id);
DROP TABLE users_wallet;
