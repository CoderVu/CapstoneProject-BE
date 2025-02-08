package com.example.CapstoneProject.repository;

import com.example.CapstoneProject.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {
}
