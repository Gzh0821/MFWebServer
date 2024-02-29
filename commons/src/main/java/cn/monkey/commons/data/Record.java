package cn.monkey.commons.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class Record implements Serializable {
    private String id;
    private Long timestamp;
    private KVPair<String, String> operator;
    private String content;
    private Object event;
}
