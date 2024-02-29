package cn.monkey.spring.bean;

import cn.monkey.commons.data.KVPair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DictHandler extends Bean {
    Page<KVPair<String, String>> getDict(Pageable pageable);
}
