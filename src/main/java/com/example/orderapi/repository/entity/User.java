package com.example.orderapi.repository.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "users")
@Entity(name = "user")
@Data
public class User {

}