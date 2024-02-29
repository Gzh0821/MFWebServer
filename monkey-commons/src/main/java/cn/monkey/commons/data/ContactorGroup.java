package cn.monkey.commons.data;

import lombok.Data;

import java.io.Serializable;
import java.util.Collection;

@Data
public class ContactorGroup implements Serializable {
    private String id;
    private String type;
    // 机构/公司的名称
    private String name;
    private Collection<Contactor> contactors;
}