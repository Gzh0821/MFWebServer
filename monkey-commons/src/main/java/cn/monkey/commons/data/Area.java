package cn.monkey.commons.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class Area implements Serializable {
    private String value;
    private String unit;
}
