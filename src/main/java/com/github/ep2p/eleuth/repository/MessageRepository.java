package com.github.ep2p.eleuth.repository;

import com.github.ep2p.eleuth.model.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
}
