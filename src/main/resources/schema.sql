DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS accounts;

CREATE TABLE accounts
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20)    NOT NULL UNIQUE,
    holder_name    VARCHAR(100)   NOT NULL,
    account_type   VARCHAR(20)    NOT NULL,
    balance        DECIMAL(19, 4) NOT NULL DEFAULT 0.0000,
    status         VARCHAR(20)    NOT NULL DEFAULT 'ACTIVE',
    created_at     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transactions
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id       BIGINT         NOT NULL,
    reference_number VARCHAR(30)    NOT NULL UNIQUE,
    type             VARCHAR(20)    NOT NULL,
    amount           DECIMAL(19, 4) NOT NULL,
    description      VARCHAR(255),
    transaction_date TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES accounts (id)
);
