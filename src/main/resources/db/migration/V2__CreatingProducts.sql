CREATE TABLE product (
   id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
   name varchar UNIQUE,
   description varchar,
   image_url varchar,
   price float,
   category varchar
);

ALTER TABLE "order_products"
ADD CONSTRAINT fk_product
FOREIGN KEY ("product_id") REFERENCES "product" (id);