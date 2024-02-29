package cn.monkey.data.server.msg;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserServerBindingRepository implements UserServerBindingRepository {

    private final Map<String, List<String>> userServerRelMap;

    public InMemoryUserServerBindingRepository() {
        this.userServerRelMap = new HashMap<>();
    }

    @Override
    public List<String> select(String uid) {
        Set<Map.Entry<String, List<String>>> entries = userServerRelMap.entrySet();
        return entries.stream().filter(e -> e.getValue().contains(uid))
                .map(Map.Entry::getKey).toList();
    }

    @Override
    public void add(String serverId, String uid) {
        List<String> serverIds = this.select(uid);
        if (serverIds.contains(serverId)) {
            return;
        }
        this.userServerRelMap.compute(serverId, (k, v) -> {
            if (v == null) {
                v = new ArrayList<>();
                v.add(uid);
                return v;
            }
            if (!v.contains(uid)) {
                v.add(uid);
            }
            return v;
        });
    }

    @Override
    public void remove(String serverId, String uid) {
        this.userServerRelMap.computeIfPresent(serverId, (k,v) -> {
            v.remove(uid);
            return v;
        });
    }
}
