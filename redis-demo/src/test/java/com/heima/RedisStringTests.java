package com.heima;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heima.redis.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class RedisStringTests {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 使用StringRedisTemplate，来测试写入redis字符串键值对(它的key和value的序列化方式默认就是String方式)。
     */
    @Test
    void testString() {
        // 写入一条String数据
        stringRedisTemplate.opsForValue().set("verify:phone:13600527634", "124143");
        // 获取string数据
        Object name = stringRedisTemplate.opsForValue().get("name");
        System.out.println("name = " + name);
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * 序列化兑现然后获取之后进行反序列化
     * @throws JsonProcessingException
     */
    @Test
    void testSaveUser() throws JsonProcessingException {
        // 创建对象
        User user = new User("虎哥", 21);
        // 手动序列化
        String json = mapper.writeValueAsString(user);
        // 写入数据
        stringRedisTemplate.opsForValue().set("user:200", json);

        // 获取数据
        String jsonUser = stringRedisTemplate.opsForValue().get("user:200");
        // 手动反序列化
        User user1 = mapper.readValue(jsonUser, User.class);
        System.out.println("user1 = " + user1);
    }

    /**
     * 在reids中存储并取出hash类型数据
     */
    @Test
    void testHash() {
        stringRedisTemplate.opsForHash().put("user:400", "name", "虎哥");
        stringRedisTemplate.opsForHash().put("user:400", "age", "21");

        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries("user:400");
        System.out.println("entries = " + entries);
    }


    /**
     * 测试普通的大量写入命令的效率
     */
    @Test
    void testNormal() {
        long begin = System.currentTimeMillis();
        for (int i = 1; i <= 100000; i++) {
            stringRedisTemplate.opsForValue().set("key_" + i, "value_" + i);
        }
        long end = System.currentTimeMillis();
        System.out.println("耗时 = " + (end - begin));
    }


    /**
     * 测试mset批量写入命令
     */
    @Test
    void testMset() {
        long begin = System.currentTimeMillis();
        Map<String, String> map = new HashMap<>(1000);
        for (int i = 1; i <= 100000; i++) {
            map.put("key_" + i, "value_" + i);
            if (map.size() >= 1000) {
                stringRedisTemplate.opsForValue().multiSet(map);
                map.clear();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("mset耗时 = " + (end - begin));
    }


    /**
     * 测试Pipeline批量写入耗时
     */
    @Test
    void testPipeline() {
        long begin = System.currentTimeMillis();
        for (int i = 0; i <= 100; i++) {
            int c = i * 1000;
            stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                for (int j = 1; j <= 1000; j++) {
                    int d = c + j;
                    connection.set(
                            ("key_" + d).getBytes(StandardCharsets.UTF_8),
                            ("value_" + d).getBytes(StandardCharsets.UTF_8)
                    );
                }
                return null;
            });
        }
        long end = System.currentTimeMillis();
        System.out.println("Pipeline耗时 = " + (end - begin));
    }

    /**
     * 将BigKey的数据通过hash的方式写入，测试运行效率
     */
    @Test
    void testBigHash() {
        Map<String, String> map = new HashMap<>(1000);
        for (int i = 1; i <= 1000000; i++) {
            map.put("key_" + i, "value_" + i);
            if (map.size() >= 1000) {
                stringRedisTemplate.opsForHash().putAll("hk", map);
                map.clear();
            }
        }
    }

    /**
     * 将BigKey的数据通过分成小份的hashmap写入，测试效率
     */
    @Test
    void testSmallHash() {
        Map<String, String> map = new HashMap<>(1000);
        for (int i = 0; i < 1000000; i++) {
            map.put("key_" + (i % 500), "value_" + i);
            if (map.size() >= 500) {
                stringRedisTemplate.opsForHash().putAll("k" + (i / 500), map);
                map.clear();
            }
        }
    }

    /**
     *  Spring集群环境下批处理代码（下方使用并行slot方法）
     */
    @Test
    void testMSetInCluster() {
        Map<String, String> map = new HashMap<>(3);
        map.put("name", "Rose");
        map.put("age", "21");
        map.put("sex", "Female");
        stringRedisTemplate.opsForValue().multiSet(map);


        List<String> strings = stringRedisTemplate.opsForValue().multiGet(Arrays.asList("name", "age", "sex"));
        strings.forEach(System.out::println);

    }
}
