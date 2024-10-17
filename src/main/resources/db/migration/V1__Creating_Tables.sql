CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE "order" (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  order_number SERIAL UNIQUE,
  customer_id uuid ,
  created_at timestamp,
  amount float,
  status varchar,
  paid_at timestamp
);

CREATE TABLE "order_history" (
  order_id uuid,
  previous_status varchar,
  new_status varchar,
  moment timestamp,
  FOREIGN KEY (order_id) REFERENCES "order" (id),
  PRIMARY KEY (order_id, "previous_status", "new_status")
);

CREATE TABLE "order_products" (
  order_id uuid,
  product_id uuid,
  quantity int,
  FOREIGN KEY ("order_id") REFERENCES "order" (id),
  PRIMARY KEY ("order_id", "product_id")
);