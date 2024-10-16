CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE customer (
    id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    cpf varchar(11) UNIQUE,
    name varchar(256),
    email varchar(256) UNIQUE
);

CREATE TABLE product (
   id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
   name varchar UNIQUE,
   description varchar,
   image_url varchar,
   price float,
   category varchar
);

CREATE TABLE "order" (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  order_number SERIAL UNIQUE,
  customer_id uuid ,
  created_at timestamp,
  amount float,
  status varchar,
  FOREIGN KEY (customer_id) REFERENCES customer (id)
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
  FOREIGN KEY ("product_id") REFERENCES "product" (id),
  PRIMARY KEY ("order_id", "product_id")
);