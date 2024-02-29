package cn.monkey.commons.data;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum NodeDirection implements Serializable {
    PREVIOUS("previous"), NEXT("next");

    private final String name;

    NodeDirection(String name) {
        this.name = name;
    }

    public static NodeDirection valueof(String name) {
        for (NodeDirection direction : values()) {
            if (direction.name.equals(name)) {
                return direction;
            }
        }
        throw new IllegalArgumentException("invalid name: " + name);
    }
}
