CREATE TABLE `OrdersDatabase`.`orders` (
  id INT NOT NULL AUTO_INCREMENT,
  guest_email VARCHAR(255) NOT NULL,
  coupon_id INT NULL,
  amount DECIMAL(10, 2) NOT NULL,
  created_at DATETIME NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE `OrdersDatabase`.`order_items` (
  id INT NOT NULL AUTO_INCREMENT,
  order_id INT(255) NOT NULL,
  product_id INT NOT NULL,
  price DECIMAL(10, 2) NOT NULL,
  quantity INT NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (order_id) REFERENCES orders(id)
);