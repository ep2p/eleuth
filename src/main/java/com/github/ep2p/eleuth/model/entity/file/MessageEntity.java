package com.github.ep2p.eleuth.model.entity.file;

import lombok.*;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name = "messages", indexes = {
        @Index(columnList = "receiver,creationDate")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageEntity {
    @Id
    @GeneratedValue
    private Long id;
    private Date creationDate;
    private BigInteger receiver;
    private byte[] data;
    @Builder.Default
    private int version = 1;
}
