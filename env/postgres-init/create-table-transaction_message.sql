CREATE TYPE PAYMENT_VENDOR AS ENUM (
    'ali-pay',
    'wechat-pay',
    'credit-card'
);

CREATE TYPE PAYMENT_CURRENCY AS ENUM (
    'CHY',
    'USD',
    'JPY'
);

CREATE TYPE PAYMENT_TYPE AS ENUM (
    'purchase',
    'refund'
);

CREATE TYPE PAYMENT_STATUS AS ENUM (
    'success',
    'failed'
);

CREATE TABLE transaction_message (
    id UUID,
    mid NUMERIC(8),
    uid NUMERIC(16),
    datetime TIMESTAMP (3) WITH TIME ZONE,    
    amount NUMERIC(24, 4),
    currency PAYMENT_CURRENCY,
    type PAYMENT_TYPE,
    vendor PAYMENT_VENDOR,
    status PAYMENT_STATUS,
    is_valid Bool,
    origin_purchase_transaction_id UUID
);


/* CREATE index for columns 
 */

INSERT INTO transaction_message VALUES (
    '8AE281B8-9EBE-4026-AFD8-FC980D0E8325',
    '01890432',
    '0088723951648203',
    '2020-01-01T13:26:54.281+08:00',  
    12.35,
    'JPY',
    'purchase',
    'wechat-pay',
    'success'
);
