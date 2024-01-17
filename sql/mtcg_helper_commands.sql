-- See which user owns cards in list
SELECT c_id, c_name, c_u_owner, u_username
FROM c_card
         LEFT JOIN u_user on c_u_owner = u_id
WHERE c_id IN ('845f0dc7-37d0-426e-994e-43fc3ac83c08'::uuid,
               '99f8f8dc-e25e-4a95-aa2c-782823f36e2a'::uuid,
               'e85e3976-7c86-4d06-9a80-641c2019a79f'::uuid,
               '171f6076-4eb5-4a7d-b3f2-2d650cc3d237'::uuid);
