package com.github.ep2p.eleuth.repository;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Key {
    private String value;
    private Type type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Key key = (Key) o;
        return Objects.equals(getValue(), key.getValue()) &&
                getType() == key.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue(), getType());
    }

    public enum Type {
        NODE_INFO, MESSAGE
    }
}
