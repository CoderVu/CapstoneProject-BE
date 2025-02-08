package com.example.CapstoneProject.repository;

import com.example.CapstoneProject.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface OrderRepository extends JpaRepository<Order, String> {
    boolean existsByOrderCode(String orderCode);

    @Modifying
    @Transactional
    @Query("UPDATE Order o SET o.feedback = true WHERE o.id = :id")
    void updateFeedbackStatus(String id);
}
