# Order API

The `Order API` service manages order-related operations such as creating orders, retrieving orders by customer, and searching for orders within a date range. It is part of the organization's microservices architecture.

## Overview

The `Order API` provides functionality for:
- Creating new orders.
- Retrieving orders made by a specific customer.
- Retrieving orders within a specific date range.

## Endpoints

### Create Order

- **URL**: `/order`
- **Method**: `POST`
- **Description**: Creates a new order.
- **Request Body**: 
  ```json
  {
    "couponCode": "string",
    "guestEmail": "string",
    "transactionRequestModel": {
      "transactionDetails": "string"
    },
    "orderRequestItems": [
      {
        "productId": "long",
        "quantity": "int"
      }
    ]
  }
  ```
- **Response**: 
  ```json
  {
    "id": "long",
    "guestEmail": "string",
    "couponCode": "string",
    "amount": "decimal",
    "createdAt": "datetime",
    "orderItems": [
      {
        "productId": "long",
        "quantity": "int"
      }
    ]
  }
  ```
- **Response Code**: `201 Created`

---

### Get Orders by Customer Email

- **URL**: `/order/{guestEmail}`
- **Method**: `GET`
- **Description**: Retrieves all orders made by a specific customer identified by their email.
- **Path Variable**: `guestEmail` (String)
- **Response**: 
  ```json
  [
    {
      "id": "long",
      "guestEmail": "string",
      "couponCode": "string",
      "amount": "decimal",
      "createdAt": "datetime",
      "orderItems": [
        {
          "productId": "long",
          "quantity": "int"
        }
      ]
    }
  ]
  ```
- **Response Code**: `200 OK`

---

### Get Orders by Date Range

- **URL**: `/order`
- **Method**: `GET`
- **Description**: Retrieves orders created within a specific date range. If the range is not specified, it defaults to the last 50 years.
- **Request Params**: 
  - `from` (Date, optional): Start date (yyyy-MM-dd).
  - `to` (Date, optional): End date (yyyy-MM-dd).
- **Response**: 
  ```json
  [
    {
      "id": "long",
      "guestEmail": "string",
      "couponCode": "string",
      "amount": "decimal",
      "createdAt": "datetime",
      "orderItems": [
        {
          "productId": "long",
          "quantity": "int"
        }
      ]
    }
  ]
  ```
- **Response Code**: `200 OK`

---

## Configuration

- Configure the database connection in the `application.properties` file:
  ```properties
  spring.datasource.url=jdbc:mysql://localhost:3306/your_database_name
  spring.datasource.username=your_db_username
  spring.datasource.password=your_db_password
  ```
