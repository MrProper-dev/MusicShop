DELETE FROM pictures;
DELETE FROM products;
DELETE FROM categories;

INSERT INTO categories (id, name) VALUES (1, 'Guitars');
INSERT INTO categories (id, name) VALUES (2, 'Drums');

INSERT INTO products (id, name, description, price, quantity, category_id) 
VALUES (1, 'Fender Stratocaster', 'Electric guitar', 1200.0, 5, 1);

INSERT INTO pictures (id, path, is_main, product_id) 
VALUES (1, 'fender_main.jpg', true, 1);