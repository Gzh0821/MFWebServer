package cn.monkey.commons.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class File implements Serializable {
    /**
     * @see FileType
     */
    private String name;
    private String type;
    private String path;
}
