CREATE TABLE payment (
  id uuid PRIMARY KEY,
  external_id varchar,
  order_id uuid,
  status varchar,
  gateway varchar,
  amount float,
  transaction_data varchar,
  payed_at timestamp,
  FOREIGN KEY (order_id) REFERENCES "order" (id)
);