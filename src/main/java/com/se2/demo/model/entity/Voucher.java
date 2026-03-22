package com.se2.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vouchers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(unique = true, nullable = false)
    String code; // Ví dụ: BROSPORT20

    BigDecimal discountValue;
    BigDecimal minOrderValue;
    Integer usageLimit;
    Integer usedCount;

    LocalDateTime startDate;
    LocalDateTime endDate;
    boolean isActive;
}