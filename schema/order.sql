CREATE TABLE `OrdersDatabase`.`coupons` (
  id INT NOT NULL AUTO_INCREMENT,
  value INT NOT NULL,
  type VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE `OrdersDatabase`.`orders` (
  id INT NOT NULL AUTO_INCREMENT,
  guest_email VARCHAR(255) NOT NULL,
  coupon_id INT NULL,
  amount DECIMAL(10, 2) NOT NULL,
  created_at DATETIME NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (coupon_id) REFERENCES coupons (id)
);

CREATE TABLE `OrdersDatabase`.`products` (
  id INT NOT NULL AUTO_INCREMENT,
  price DECIMAL(10, 2) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE `OrdersDatabase`.`orderProducts` (
    order_id INT,
    product_id INT,
    PRIMARY KEY (order_id, product_id),
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

INSERT INTO `OrdersDatabase`.`coupons` VALUES (1);
INSERT INTO `OrdersDatabase`.`coupons` VALUES (2);
INSERT INTO `OrdersDatabase`.`coupons` VALUES (3);

INSERT INTO `OrdersDatabase`.`orders` (guest_email, coupon_id, amount, created_at) VALUES ('guest1@example.com', 1, 100.00, NOW());
INSERT INTO `OrdersDatabase`.`orders` (guest_email, coupon_id, amount, created_at) VALUES ('guest2@example.com', 2, 200.00, NOW());
INSERT INTO `OrdersDatabase`.`orders` (guest_email, coupon_id, amount, created_at) VALUES ('guest3@example.com', NULL, 300.00, NOW());

INSERT INTO `OrdersDatabase`.`products` (price) VALUES (10.99);
INSERT INTO `OrdersDatabase`.`products` (price) VALUES (19.99);
INSERT INTO `OrdersDatabase`.`products` (price) VALUES (29.99);


INSERT INTO `OrdersDatabase`.`orderProducts` (order_id, product_id)
VALUES (1, 1), (1, 2), (1, 3), (2, 1), (2, 2), (2, 3), (3, 1), (3, 2), (3, 3);