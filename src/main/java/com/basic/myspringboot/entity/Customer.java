package com.basic.myspringboot.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "customers")
@Getter @Setter
@DynamicUpdate
public class Customer {
    //Primary Key, PK값을 persistence provider가 결정해라
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //유니크한 값을 가져야한다.
    @Column(unique = true, nullable = false)      //중복된 값을 허용하지 않는다, null허용하지 않는다.
    private String customerId;

    @Column(nullable = false)       //null 허용X
    private String customerName;
}
