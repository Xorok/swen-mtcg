CREATE TABLE u_user
(
    u_id        UUID PRIMARY KEY,
    u_username  VARCHAR(255) NOT NULL UNIQUE,
    u_pass_hash VARCHAR(255) NOT NULL,
    u_pass_salt BYTEA        NOT NULL,
    u_coins     INTEGER      NOT NULL CHECK (u_coins >= 0),
    u_elo       INTEGER      NOT NULL,
    u_name      VARCHAR(255) NULL,
    u_bio       VARCHAR(255) NULL,
    u_image     VARCHAR(255) NULL
);

CREATE TABLE ce_card_element
(
    ce_name VARCHAR(255) PRIMARY KEY
);

CREATE TABLE ct_card_type
(
    ct_name VARCHAR(255) PRIMARY KEY
);

CREATE TABLE c_card
(
    c_id         UUID PRIMARY KEY,
    c_no         SERIAL,
    c_name       VARCHAR(255) NOT NULL,
    c_damage     FLOAT        NOT NULL,
    c_ct_type    VARCHAR(255) NOT NULL REFERENCES ct_card_type (ct_name),
    c_ce_element VARCHAR(255) NOT NULL REFERENCES ce_card_element (ce_name),
    c_u_owner    UUID         NULL REFERENCES u_user (u_id)
);

CREATE TABLE d_deck
(
    d_u_owner UUID REFERENCES u_user (u_id),
    d_c_card  UUID REFERENCES c_card (c_id),
    PRIMARY KEY (d_u_owner, d_c_card)
);

CREATE TABLE t_trade
(
    t_c_offered_card       UUID PRIMARY KEY REFERENCES c_card (c_id),
    t_ct_requested_type    VARCHAR(255) NOT NULL REFERENCES ct_card_type (ct_name),
    t_ct_requested_element VARCHAR(255) NULL REFERENCES ce_card_element (ce_name),
    t_requested_min_damage FLOAT        NULL CHECK ((t_ct_requested_element IS NOT NULL) OR
                                                    (t_requested_min_damage IS NOT NULL AND t_requested_min_damage > 0))
);

-- Add default card & element types
INSERT INTO ct_card_type(ct_name)
VALUES ('MONSTER'),
       ('SPELL');

INSERT INTO ce_card_element(ce_name)
VALUES ('WATER'),
       ('FIRE'),
       ('NORMAL');