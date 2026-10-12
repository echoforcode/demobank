SELECT u.username, a.code, a.balance AS account_balance,
       c.balance AS card_balance, c.card_id
FROM bank_user u
         JOIN account a ON a.owner_id = u.uid AND a.code = 'USD'
         LEFT JOIN card c ON c.owner_id = u.uid
WHERE u.username = 'your-demo-username';
SELECT t.tx_id, t.type, t.amount, t.tx_fee,
       a.code, t.account_id, t.card_id,
       CASE WHEN a.account_id IS NOT NULL THEN t.owner_id = a.owner_id
            WHEN c.card_id IS NOT NULL THEN t.owner_id = c.owner_id
            ELSE false END AS owner_matches
FROM "transaction" t
         JOIN bank_user u ON u.uid = t.owner_id
         LEFT JOIN account a ON a.account_id = t.account_id
         LEFT JOIN card c ON c.card_id = t.card_id
WHERE u.username = 'your-demo-username'
ORDER BY t.created_at, t.tx_id;