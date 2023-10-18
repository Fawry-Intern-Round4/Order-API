package com.example.orderapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Table (name = "orders")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Order{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Guest email is mandatory")
    @Email(message = "Guest email must be a valid email")
    private String guestEmail;

    private String couponCode;

    @NotNull(message = "Amount is mandatory")
    @Digits(integer = 10, fraction = 10, message = "Amount must be a number")
    @PositiveOrZero(message = "Amount must be greater than or equal to 0")
    private BigDecimal amount;

    @CreationTimestamp
    private Date createdAt;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="orderId")
    private List<OrderItem> orderItems;
}
