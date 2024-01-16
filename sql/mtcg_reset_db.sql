TRUNCATE
    c_card,
    ce_card_element,
    ct_card_type,
    d_deck,
    t_trade,
    u_user;

-- Restore default card & element types
INSERT INTO ct_card_type(ct_name)
VALUES ('MONSTER'),
       ('SPELL');

INSERT INTO ce_card_element(ce_name)
VALUES ('WATER'),
       ('FIRE'),
       ('NORMAL');