INSERT INTO accounts (account_number, holder_name, account_type, balance, status)
VALUES ('RHB-000001', 'Ahmad bin Abdullah', 'SAVINGS', 5000.0000, 'ACTIVE'),
       ('RHB-000002', 'Siti Nur Aisyah', 'CURRENT', 12500.0000, 'ACTIVE'),
       ('RHB-000003', 'Rajesh Kumar', 'SAVINGS', 3200.0000, 'ACTIVE'),
       ('RHB-000004', 'Lee Mei Ling', 'CURRENT', 8750.0000, 'SUSPENDED');

INSERT INTO transactions (account_id, reference_number, type, amount, description)
VALUES (1, 'TXN-20240101-0001', 'CREDIT', 1000.0000, 'Salary deposit'),
       (1, 'TXN-20240102-0002', 'DEBIT', 200.0000, 'ATM withdrawal'),
       (1, 'TXN-20240103-0003', 'DEBIT', 50.0000, 'Online purchase'),
       (2, 'TXN-20240104-0004', 'CREDIT', 5000.0000, 'Business income'),
       (2, 'TXN-20240105-0005', 'DEBIT', 1500.0000, 'Supplier payment'),
       (2, 'TXN-20240106-0006', 'CREDIT', 200.0000, 'Refund received'),
       (3, 'TXN-20240107-0007', 'CREDIT', 800.0000, 'Freelance payment'),
       (3, 'TXN-20240108-0008', 'DEBIT', 100.0000, 'Utility bill'),
       (3, 'TXN-20240109-0009', 'DEBIT', 75.0000, 'Grocery shopping'),
       (4, 'TXN-20240110-0010', 'CREDIT', 3000.0000, 'Investment return');
