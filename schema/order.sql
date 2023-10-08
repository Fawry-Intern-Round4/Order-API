CREATE TABLE `OrdersDatabase`.`orders` (
  id INT NOT NULL AUTO_INCREMENT,
  guest_email VARCHAR(255) NOT NULL,
  coupon_id INT NULL,
  amount DECIMAL(10, 2) NOT NULL,
  created_at DATETIME NOT NULL,
  PRIMARY KEY (id)
);