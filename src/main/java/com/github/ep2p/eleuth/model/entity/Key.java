package com.github.ep2p.eleuth.model.entity;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Key {
    private String id;
    private Type type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Key key = (Key) o;
        return Objects.equals(getId(), key.getId()) &&
                getType() == key.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getType());
    }

    public enum Type {
        NODE_INFO, MESSAGE
    }
}
