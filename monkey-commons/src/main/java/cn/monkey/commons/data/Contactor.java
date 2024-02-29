package cn.monkey.commons.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class Contactor implements Serializable {
    private String uid;
    private String phoneNo;
    private String nickname;
}
