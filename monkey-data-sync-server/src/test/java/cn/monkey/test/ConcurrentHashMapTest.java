package cn.monkey.test;

import com.google.common.base.Stopwatch;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

public class ConcurrentHashMapTest {
    @Test
    public void test01(){
        Map<String, List<String>> map = new ConcurrentHashMap<>();
        for(int i =0 ;i< 5000;i++){
            map.put(String.valueOf(i), IntStream.range(0,10).mapToObj(String::valueOf).toList());
        }
        String i = "300";
        Stopwatch stopwatch = Stopwatch.createUnstarted();
        stopwatch.start();
        List<String> vv = map.compute(i, (k, v) -> {
            if (v == null) {
                return new ArrayList<>();
            }
            if (!v.contains("5")) {
                v.add("5");
            }
            return v;
        });
        stopwatch.stop();
        Duration elapsed = stopwatch.elapsed();
        System.out.println("cost time: "+ elapsed.getNano());
    }
}
