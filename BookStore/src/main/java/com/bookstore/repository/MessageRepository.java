package com.bookstore.repository;

import com.bookstore.entity.Message;
import com.bookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderIdAndRecipientIdOrSenderIdAndRecipientIdOrderByTimestamp(
            Long senderId, Long recipientId, Long recipientId2, Long senderId2);
}