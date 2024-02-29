package cn.monkey.data.json;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Tolerate;

import java.io.Serializable;
import java.util.List;

public class Command {

    @Builder
    @Getter
    public static class Pkg implements Serializable {

        @Tolerate
        private Pkg(){}
        private String id;
        private Long cmdType;
        private String groupId;

        private String to;

        private byte[] content;

        private Long timestamp;
    }

    @Builder
    @Getter
    public static class PkgGroup implements Serializable {
        private List<Pkg> pkg;

        @Tolerate
        private PkgGroup(){}
    }
}
