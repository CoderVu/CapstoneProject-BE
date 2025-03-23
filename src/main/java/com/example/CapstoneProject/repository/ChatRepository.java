package com.example.CapstoneProject.repository;

import com.example.CapstoneProject.model.Chat;
import com.example.CapstoneProject.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, String> {

    @Query("SELECT DISTINCT m.sender FROM Chat m WHERE m.receiver = :receiver AND m.isRead = false")
    List<User> findUsersWithUnreadMessages(@Param("receiver") User receiver);

    @Modifying
    @Transactional
    @Query("UPDATE Chat m SET m.isRead = true WHERE m.receiver = :receiver AND m.sender = :sender AND m.isRead = false")
    void markMessagesAsRead(@Param("receiver") User receiver, @Param("sender") User sender);

    @Modifying
    @Transactional
    @Query("UPDATE Chat m SET m.isRead = true WHERE m.isRead = false AND m.receiver.id = :sender AND m.sender.id= :user")
    int updateMessagesToOnline(@Param("sender") Long sender,@Param("user") Long user);

    @Query("SELECT m FROM Chat m WHERE m.sender.id = :sender AND m.receiver.id = :receiver OR m.sender.id = :receiver AND m.receiver.id = :sender")
    List<Chat> findBySenderAndReceiver(@Param("sender") String sender, @Param("receiver") String receiver);
    @Query("SELECT DISTINCT m.receiver FROM Chat m WHERE m.sender.id = :senderId")
    List<User> findDistinctUsersBySenderId(@Param("senderId") String senderId);
}