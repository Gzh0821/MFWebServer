package cn.monkey.test.WebsocketTest;

import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

public class DebugTest {
    @Test
    public void test01() {
        IntStream.range(0, 1000)
                .parallel()
                .boxed()
                .forEach(i -> {
                    System.out.println(i);
                });
    }
}
