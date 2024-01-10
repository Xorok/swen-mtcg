CREATE DATABASE mctg_db;

\connect mctg_db;

CREATE TABLE u_user (
    u_username VARCHAR(255) PRIMARY KEY,
    u_pass_hash VARCHAR(128) NOT NULL, /* salted sha-512 in hex */
    u_coins INTEGER NOT NULL CHECK (u_coins >= 0) DEFAULT 20,
    u_elo INTEGER NOT NULL DEFAULT 100
);

CREATE TABLE ce_card_element (
    ce_name VARCHAR(255) PRIMARY KEY
);

CREATE TABLE ct_card_type (
    ct_name VARCHAR(255) PRIMARY KEY
);

CREATE TABLE c_card (
    c_id UUID PRIMARY KEY,
    c_name VARCHAR(255) NOT NULL,
    c_damage FLOAT NOT NULL,
    c_ct_type VARCHAR(255) NOT NULL REFERENCES ct_card_type(ct_name),
    c_ce_element VARCHAR(255) NOT NULL REFERENCES ce_card_element(ce_name),
    c_u_owner VARCHAR(255) NULL REFERENCES u_user(u_username)
);

CREATE TABLE d_deck (
    d_u_owner VARCHAR(255) PRIMARY KEY REFERENCES u_user(u_username)
);

CREATE TABLE dc_deck_card (
    dc_d_deck VARCHAR(255) PRIMARY KEY REFERENCES d_deck(d_u_owner),
    dc_c_card UUID REFERENCES c_card(c_id)
);

CREATE TABLE t_trade (
    t_c_offered_card UUID PRIMARY KEY REFERENCES c_card(c_id),
    t_ct_requested_type VARCHAR(255) NOT NULL REFERENCES ct_card_type (ct_name),
    t_ct_requested_element VARCHAR(255) NULL REFERENCES ce_card_element (ce_name),
    t_requested_min_damage FLOAT NULL CHECK ((t_ct_requested_element IS NOT NULL) OR (t_requested_min_damage IS NOT NULL AND t_requested_min_damage > 0))
);

/*
 TODO
CREATE OR REPLACE FUNCTION check_if_card_in_deck()
  RETURNS TRIGGER
  LANGUAGE PLPGSQL
  AS
$$
BEGIN
	IF NEW.t_c_offered_card <> OLD.last_name THEN
		 INSERT INTO employee_audits(employee_id,last_name,changed_on)
		 VALUES(OLD.id,OLD.last_name,now());
END IF;

RETURN NEW;
END;
$$
 */