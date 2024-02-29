package cn.monkey.commons.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Currency extends KVPair<String, String> {
}
