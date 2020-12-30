package com.github.ep2p.eleuth.repository.file;

import com.github.ep2p.eleuth.model.entity.file.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
}
