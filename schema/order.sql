CREATE TABLE orders (
  id INT NOT NULL AUTO_INCREMENT,
  customer_email VARCHAR(255) NOT NULL,
  coupon_id INT NULL,
  amount DECIMAL(10, 2) NOT NULL,
  transaction_id INT NOT NULL,
  created_at DATETIME NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (user_id) REFERENCES users (id),
  FOREIGN KEY (coupon_id) REFERENCES coupons (id)
);