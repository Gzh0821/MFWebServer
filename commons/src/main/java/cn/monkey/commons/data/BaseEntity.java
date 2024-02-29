package cn.monkey.commons.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseEntity implements Identifiable<String>, Serializable {
    private String id;
    private KVPair<String, String> creator;
    private Long createDateTime;
    private KVPair<String, String> updater;
    private Long updateDateTime;
    private Integer dataStatus = DataStatus.NEW.getCode();
    private String disabled;
}