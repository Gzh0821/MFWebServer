package cn.monkey.commons.data;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum FileType implements Serializable {

    IMG("img"), VIDEO("video"), AUDIO("audio"), FILE("file");

    private final String type;

    FileType(String type) {
        this.type = type;
    }
}
