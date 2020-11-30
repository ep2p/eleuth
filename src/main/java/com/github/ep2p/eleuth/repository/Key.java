package com.github.ep2p.eleuth.repository;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Builder
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

    enum Type {
        NODE_INFO, MESSAGE
    }
}
