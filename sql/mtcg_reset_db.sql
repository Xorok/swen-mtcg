TRUNCATE
    c_card,
    ce_card_element,
    ct_card_type,
    d_deck,
    dc_deck_card,
    t_trade,

    u_user;

-- Restore default card & element types
INSERT INTO ct_card_type(ct_name)
VALUES ('Monster'),
       ('Spell');

INSERT INTO ce_card_element(ce_name)
VALUES ('Water'),
       ('Fire'),
       ('Normal');