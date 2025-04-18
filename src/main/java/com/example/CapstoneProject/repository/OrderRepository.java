package com.example.CapstoneProject.repository;

import com.example.CapstoneProject.model.Order;
import com.example.CapstoneProject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {
    boolean existsByOrderCode(String orderCode);

    @Modifying
    @Transactional
    @Query("UPDATE Order o SET o.feedback = true WHERE o.id = :id")
    void updateFeedbackStatus(String id);

    List<Order> findByUser(User user);
    @Query("SELECT o FROM Order o WHERE o.orderCode = :orderId")
    Optional<Order> findByOrderCode(String orderId);
    @Query("SELECT o FROM Order o WHERE o.createdAt > :oneHourAgo")
    List<Order> findByCreatedAtAfter(LocalDateTime oneHourAgo);
}
