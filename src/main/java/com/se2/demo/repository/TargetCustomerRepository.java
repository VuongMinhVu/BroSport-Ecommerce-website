package com.se2.demo.repository;

import com.se2.demo.model.entity.TargetCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TargetCustomerRepository extends JpaRepository<TargetCustomer, Integer> {
}
