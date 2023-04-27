BEGIN TRANSACTION;

DROP TABLE IF EXISTS tenmo_user, account, transfer;

DROP SEQUENCE IF EXISTS seq_user_id, seq_account_id, seq_transfer_id;

-- Sequence to start user_id values at 1001 instead of 1
CREATE SEQUENCE seq_user_id
  INCREMENT BY 1
  START WITH 1001
  NO MAXVALUE;

CREATE TABLE tenmo_user (
	user_id int NOT NULL DEFAULT nextval('seq_user_id'),
	username varchar(50) NOT NULL,
	password_hash varchar(200) NOT NULL,
	CONSTRAINT PK_tenmo_user PRIMARY KEY (user_id),
	CONSTRAINT UQ_username UNIQUE (username)
);

-- Sequence to start account_id values at 2001 instead of 1
-- Note: Use similar sequences with unique starting values for additional tables
CREATE SEQUENCE seq_account_id
  INCREMENT BY 1
  START WITH 2001
  NO MAXVALUE;

CREATE TABLE account (
	account_id int NOT NULL DEFAULT nextval('seq_account_id'),
	user_id int NOT NULL,
	balance numeric(13, 2) NOT NULL,
	CONSTRAINT PK_account PRIMARY KEY (account_id),
	CONSTRAINT FK_account_tenmo_user FOREIGN KEY (user_id) REFERENCES tenmo_user (user_id)
);

--Starting transfer table creation
CREATE SEQUENCE seq_transfer_id
  INCREMENT BY 1
  START WITH 3001
  NO MAXVALUE;

CREATE TABLE transfer (
    transfer_id int NOT NULL DEFAULT nextval('seq_transfer_id'),
    account_id_send int NOT NULL,
    account_id_receive int NOT NULL,
    amount numeric(13,2) NOT NULL,
    pending boolean DEFAULT true,
    CONSTRAINT PK_transfer PRIMARY KEY (transfer_id),
    CONSTRAINT FK_account_send FOREIGN KEY (account_id_send) REFERENCES account (account_id),
    CONSTRAINT FK_account_receive FOREIGN KEY (account_id_receive) REFERENCES account (account_id)
);

INSERT INTO tenmo_user (username, password_hash)
VALUES
('user1', '2421sd'), -- 1001
('user2', '3243a'); -- 1002

INSERT INTO account (user_id, balance)
VALUES
(1001, 1000), -- 2001
(1001, 2000), -- 2002
(1001, 3000), -- 2003
(1002, 1000); -- 2004

INSERT INTO transfer (account_id_send, account_id_receive, amount, pending)
VALUES
(2004, 2003, 500, true), -- 3001
(2001, 2004, 600, false), -- 3002
(2001, 2004, 200, false),-- 3003
(2004, 2001, 800, true), -- 3004
(2004, 2002, 200, false); -- 3005
(2005, 2001, 500, true); -- 3006
(2002, 2005, 250, false); -- 3007

COMMIT;
