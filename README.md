## redis-demo





### LettuceTest



```
用于测试客户端缓存并在Redis缓存中使用Lettuce库连接到Redis服务器。测试运行在一个循环中，不断查询缓存以获取"user"键的值并打印其值。

代码片段的逐个部分如下：

1. 使用Lettuce库连接到Redis服务器。
2. 检查连接是否为LettuceConnection实例。
3. 如果它是，调用连接对象的`doGetAsyncDedicatedConnection`方法以获取用于缓存的 dedicated connection。
4. 启用客户端缓存，使用`ClientSideCaching.enable`方法。此方法接受三个参数：缓存访问器（一个映射），dedicated connection和tracking arguments builder。缓存访问器用于跟踪缓存miss和hit。
5. 创建一个无限循环，直到测试停止。
6. 在循环中，使用缓存前端的方法`get`来获取"user"键的值。
7. 打印键的值。
8. 让线程休眠3秒（3000毫秒）。

注意，这个代码片段不包含实际测试逻辑，而是模拟了客户端缓存的用法场景。测试逻辑在于断言缓存的行为是否符合预期。
```





### RedisDemoApplicationTests



```
redis简单的写入和获取String类型的String类型的数据
```



```
redis简单的写入和获取String类型的对象类型数据
```





### RedisStringTests



```
使用StringRedisTemplate，来测试写入redis字符串键值对(它的key和value的序列化方式默认就是String方式)。
```



```
序列化兑现然后获取之后进行反序列化
```



```
在reids中存储并取出hash类型数据
```



```
测试普通的大量写入命令的效率
```



```
测试mset批量写入命令
```



```
测试Pipeline批量写入耗时
```



```
将BigKey的数据通过hash的方式写入，测试运行效率
```



```
将BigKey的数据通过分成小份的hashmap写入，测试效率
```



```
Spring集群环境下批处理代码（下方使用并行slot方法）
```































## jedis-demo



### JedisClusterTest



```
初始化JedisCluster实例
```



```
使用MSet来批量设置String类型的key-value
```



```
/**
 * 将一个HashMap中的键值对分组并使用JedisCluster进行批量设置（mset）
 * 创建了一个包含键值对的HashMap，并将其条目按特定规则分组（通过ClusterSlotHashUtil.calculateSlot计算槽位）。
 * (由于切换redis中集群的节点时会有性能损耗，所以先把所有的key存储的插槽计算出来之后，在一组插槽的同时都写入同一个节点可以节省性能)
 * ，将分组后的键值对转换为JedisCluster的mset操作所需的字符串数组格式，并执行mset命令将数据批量写入Redis集群。
 */
```



```
/**
 * 确保关闭JedisCluster实例以释放资源
 */
```



### JedisTest



```
/**
 * 连接redis数据库
 */
```



```
/**
 * redis写入字符串
 */
```



```
/**
 * 插入hash数据并获取
 */
```



```
final static int STR_MAX_LEN = 10 * 1024; // 字符串最大长度
final static int HASH_MAX_LEN = 500; // 哈希集合最大长度
```



```
/**
 * 扫描redis库，找出所有符合条件的BigKey
 */
```



```
/**
 * HashSet插入BigKey
 */
```



```
/**
 * HashSet插入BigKey
 */
```



```
/**
 * String插入BigKey
 */
```



```
/**
 * 将BigKey拆分成多组HashSet，然后逐个插入
 */
```



```
/**
 * 将BigKey拆分成多个String，然后逐个插入
 */
```



```
/**
 *  测试MSet批量插入
 */
```



```
/**
 * 测试Pipeline批量插入
 */
```



```
/**
 * 释放资源
 */
```





### com.heima.jedis.util



#### Assert

```java
package com.heima.jedis.util;


public class Assert {
    /**
     * 断言检查给定的对象不为空
     * @param obj
     * @param msg
     */
    public static void notNull(Object obj, String msg){
        if (obj == null) {
            throw new RuntimeException(msg);
        }
    }

    /**
     * 断言检查给定的字符串不为空
     * @param str
     * @param msg
     */
    public static void hasText(String str, String msg){
        if (str == null) {
            throw new RuntimeException(msg);
        }
        if (str.trim().isEmpty()) {
            throw new RuntimeException(msg);
        }
    }
}
```





#### ByteUtils

```java
package com.heima.jedis.util;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Some handy methods for dealing with {@code byte} arrays.
 *
 * @author Christoph Strobl
 * @author Mark Paluch
 * @since 1.7
 */
public final class ByteUtils {

    private ByteUtils() {}

    /**
     * 将给定的 {@code byte} 数组连接成一个数组，并包含两次重叠的数组元素
     * Concatenate the given {@code byte} arrays into one, with overlapping array elements included twice.
     * <p />
     * 保留原始数组中元素的顺序
     * The order of elements in the original arrays is preserved.
     *
     * @param array1 the first array.
     * @param array2 the second array.
     * @return the new array.
     */
    public static byte[] concat(byte[] array1, byte[] array2) {

       byte[] result = Arrays.copyOf(array1, array1.length + array2.length);
       System.arraycopy(array2, 0, result, array1.length, array2.length);

       return result;
    }

    /**
     * 将给定的 {@code byte} 数组连接成一个数组，并包含两次重叠的数组元素。如果 {@code arrays} 为空，
     * 则返回一个新的空数组，如果 {@code arrays} 仅包含单个数组，则返回第一个数组。
     * Concatenate the given {@code byte} arrays into one, with overlapping array elements included twice. Returns a new,
     * empty array if {@code arrays} was empty and returns the first array if {@code arrays} contains only a single array.
     * <p />
     * The order of elements in the original arrays is preserved.
     *
     * @param arrays the arrays.
     * @return the new array.
     */
    public static byte[] concatAll(byte[]... arrays) {

       if (arrays.length == 0) {
          return new byte[] {};
       }
       if (arrays.length == 1) {
          return arrays[0];
       }

       byte[] cur = concat(arrays[0], arrays[1]);
       for (int i = 2; i < arrays.length; i++) {
          cur = concat(cur, arrays[i]);
       }
       return cur;
    }

    /**
     * 使用分隔符 {@code@code c} 将 { source} 拆分为分区数组。
     * Split {@code source} into partitioned arrays using delimiter {@code c}.
     *
     * @param source the source array.
     * @param c delimiter.
     * @return the partitioned arrays.
     */
    public static byte[][] split(byte[] source, int c) {

       if (ObjectUtils.isEmpty(source)) {
          return new byte[][] {};
       }

       List<byte[]> bytes = new ArrayList<>();
       int offset = 0;
       for (int i = 0; i <= source.length; i++) {

          if (i == source.length) {

             bytes.add(Arrays.copyOfRange(source, offset, i));
             break;
          }

          if (source[i] == c) {
             bytes.add(Arrays.copyOfRange(source, offset, i));
             offset = i + 1;
          }
       }
       return bytes.toArray(new byte[bytes.size()][]);
    }

    /**
     * 将多个 {@code byte} 数组合并为一个数组
     * Merge multiple {@code byte} arrays into one array
     *
     * @param firstArray must not be {@literal null}
     * @param additionalArrays must not be {@literal null}
     * @return
     */
    public static byte[][] mergeArrays(byte[] firstArray, byte[]... additionalArrays) {

       Assert.notNull(firstArray, "first array must not be null");
       Assert.notNull(additionalArrays, "additional arrays must not be null");

       byte[][] result = new byte[additionalArrays.length + 1][];
       result[0] = firstArray;
       System.arraycopy(additionalArrays, 0, result, 1, additionalArrays.length);

       return result;
    }

    /**
     * 从 {@link ByteBuffer} 中提取字节数组，而不使用它。
     * Extract a byte array from {@link ByteBuffer} without consuming it.
     *
     * @param byteBuffer must not be {@literal null}.
     * @return
     * @since 2.0
     */
    public static byte[] getBytes(ByteBuffer byteBuffer) {

       Assert.notNull(byteBuffer, "ByteBuffer must not be null!");

       ByteBuffer duplicate = byteBuffer.duplicate();
       byte[] bytes = new byte[duplicate.remaining()];
       duplicate.get(bytes);
       return bytes;
    }

    /**
     * 测试 {@code haystack} 是否以给定的 {@code 前缀}开头。
     * Tests if the {@code haystack} starts with the given {@code prefix}.
     *
     * @param haystack the source to scan.
     * @param prefix the prefix to find.
     * @return {@literal true} if {@code haystack} at position {@code offset} starts with {@code prefix}.
     * @since 1.8.10
     * @see #startsWith(byte[], byte[], int)
     */
    public static boolean startsWith(byte[] haystack, byte[] prefix) {
       return startsWith(haystack, prefix, 0);
    }

    /**
     * 测试从指定的 {@code offset} 开始的 {@code haystack} 是否以给定的 {@code 前缀}开头。
     * Tests if the {@code haystack} beginning at the specified {@code offset} starts with the given {@code prefix}.
     *
     * @param haystack the source to scan.
     * @param prefix the prefix to find.
     * @param offset the offset to start at.
     * @return {@literal true} if {@code haystack} at position {@code offset} starts with {@code prefix}.
     * @since 1.8.10
     */
    public static boolean startsWith(byte[] haystack, byte[] prefix, int offset) {

       int to = offset;
       int prefixOffset = 0;
       int prefixLength = prefix.length;

       if ((offset < 0) || (offset > haystack.length - prefixLength)) {
          return false;
       }

       while (--prefixLength >= 0) {
          if (haystack[to++] != prefix[prefixOffset++]) {
             return false;
          }
       }

       return true;
    }

    /**
     * 在指定的字节数组中搜索指定的值。返回
     * Searches the specified array of bytes for the specified value. Returns the index of the first matching value in the
     * {@code haystack}s natural order or {@code -1} of {@code needle} could not be found.
     *
     * @param haystack the source to scan.
     * @param needle the value to scan for.
     * @return index of first appearance, or -1 if not found.
     * @since 1.8.10
     */
    public static int indexOf(byte[] haystack, byte needle) {

       for (int i = 0; i < haystack.length; i++) {
          if (haystack[i] == needle) {
             return i;
          }
       }

       return -1;
    }

    /**
     * 使用 {@link java.nio.charset.StandardCharsets#UTF_8} 将 {@link String} 转换为 {@link ByteBuffer}。
     * Convert a {@link String} into a {@link ByteBuffer} using {@link java.nio.charset.StandardCharsets#UTF_8}.
     *
     * @param theString must not be {@literal null}.
     * @return
     * @since 2.1
     */
    public static ByteBuffer getByteBuffer(String theString) {
       return getByteBuffer(theString, StandardCharsets.UTF_8);
    }

    /**
     * 使用给定的 {@link Charset} 将 {@link String} 转换为 {@link ByteBuffer}。
     * Convert a {@link String} into a {@link ByteBuffer} using the given {@link Charset}.
     *
     * @param theString must not be {@literal null}.
     * @param charset must not be {@literal null}.
     * @return
     * @since 2.1
     */
    public static ByteBuffer getByteBuffer(String theString, Charset charset) {

       Assert.notNull(theString, "The String must not be null!");
       Assert.notNull(charset, "The String must not be null!");

       return charset.encode(theString);
    }

    /**
     * 将给定的 {@link ByteBuffer} 转换为 {@link String}。
     * Extract/Transfer bytes from the given {@link ByteBuffer} into an array by duplicating the buffer and fetching its
     * content.
     *
     * @param buffer must not be {@literal null}.
     * @return the extracted bytes.
     * @since 2.1
     */
    public static byte[] extractBytes(ByteBuffer buffer) {

       ByteBuffer duplicate = buffer.duplicate();
       byte[] bytes = new byte[duplicate.remaining()];
       duplicate.get(bytes);

       return bytes;
    }
}
```



#### ClusterSlotHashUtil

```java
package com.heima.jedis.util;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author Christoph Strobl
 * @since 1.7
 */
public final class ClusterSlotHashUtil {

    // See http://redis.io/topics/cluster-spec
    // 总插槽数
    private static final int SLOT_COUNT = 16384;

    // See http://redis.io/topics/cluster-spec#keys-hash-tags
    private static final byte SUBKEY_START = '{';
    private static final byte SUBKEY_END = '}';

    //
    private static final int[] LOOKUP_TABLE = { 0x0000, 0x1021, 0x2042, 0x3063, 0x4084, 0x50A5, 0x60C6, 0x70E7, 0x8108,
          0x9129, 0xA14A, 0xB16B, 0xC18C, 0xD1AD, 0xE1CE, 0xF1EF, 0x1231, 0x0210, 0x3273, 0x2252, 0x52B5, 0x4294, 0x72F7,
          0x62D6, 0x9339, 0x8318, 0xB37B, 0xA35A, 0xD3BD, 0xC39C, 0xF3FF, 0xE3DE, 0x2462, 0x3443, 0x0420, 0x1401, 0x64E6,
          0x74C7, 0x44A4, 0x5485, 0xA56A, 0xB54B, 0x8528, 0x9509, 0xE5EE, 0xF5CF, 0xC5AC, 0xD58D, 0x3653, 0x2672, 0x1611,
          0x0630, 0x76D7, 0x66F6, 0x5695, 0x46B4, 0xB75B, 0xA77A, 0x9719, 0x8738, 0xF7DF, 0xE7FE, 0xD79D, 0xC7BC, 0x48C4,
          0x58E5, 0x6886, 0x78A7, 0x0840, 0x1861, 0x2802, 0x3823, 0xC9CC, 0xD9ED, 0xE98E, 0xF9AF, 0x8948, 0x9969, 0xA90A,
          0xB92B, 0x5AF5, 0x4AD4, 0x7AB7, 0x6A96, 0x1A71, 0x0A50, 0x3A33, 0x2A12, 0xDBFD, 0xCBDC, 0xFBBF, 0xEB9E, 0x9B79,
          0x8B58, 0xBB3B, 0xAB1A, 0x6CA6, 0x7C87, 0x4CE4, 0x5CC5, 0x2C22, 0x3C03, 0x0C60, 0x1C41, 0xEDAE, 0xFD8F, 0xCDEC,
          0xDDCD, 0xAD2A, 0xBD0B, 0x8D68, 0x9D49, 0x7E97, 0x6EB6, 0x5ED5, 0x4EF4, 0x3E13, 0x2E32, 0x1E51, 0x0E70, 0xFF9F,
          0xEFBE, 0xDFDD, 0xCFFC, 0xBF1B, 0xAF3A, 0x9F59, 0x8F78, 0x9188, 0x81A9, 0xB1CA, 0xA1EB, 0xD10C, 0xC12D, 0xF14E,
          0xE16F, 0x1080, 0x00A1, 0x30C2, 0x20E3, 0x5004, 0x4025, 0x7046, 0x6067, 0x83B9, 0x9398, 0xA3FB, 0xB3DA, 0xC33D,
          0xD31C, 0xE37F, 0xF35E, 0x02B1, 0x1290, 0x22F3, 0x32D2, 0x4235, 0x5214, 0x6277, 0x7256, 0xB5EA, 0xA5CB, 0x95A8,
          0x8589, 0xF56E, 0xE54F, 0xD52C, 0xC50D, 0x34E2, 0x24C3, 0x14A0, 0x0481, 0x7466, 0x6447, 0x5424, 0x4405, 0xA7DB,
          0xB7FA, 0x8799, 0x97B8, 0xE75F, 0xF77E, 0xC71D, 0xD73C, 0x26D3, 0x36F2, 0x0691, 0x16B0, 0x6657, 0x7676, 0x4615,
          0x5634, 0xD94C, 0xC96D, 0xF90E, 0xE92F, 0x99C8, 0x89E9, 0xB98A, 0xA9AB, 0x5844, 0x4865, 0x7806, 0x6827, 0x18C0,
          0x08E1, 0x3882, 0x28A3, 0xCB7D, 0xDB5C, 0xEB3F, 0xFB1E, 0x8BF9, 0x9BD8, 0xABBB, 0xBB9A, 0x4A75, 0x5A54, 0x6A37,
          0x7A16, 0x0AF1, 0x1AD0, 0x2AB3, 0x3A92, 0xFD2E, 0xED0F, 0xDD6C, 0xCD4D, 0xBDAA, 0xAD8B, 0x9DE8, 0x8DC9, 0x7C26,
          0x6C07, 0x5C64, 0x4C45, 0x3CA2, 0x2C83, 0x1CE0, 0x0CC1, 0xEF1F, 0xFF3E, 0xCF5D, 0xDF7C, 0xAF9B, 0xBFBA, 0x8FD9,
          0x9FF8, 0x6E17, 0x7E36, 0x4E55, 0x5E74, 0x2E93, 0x3EB2, 0x0ED1, 0x1EF0 };

    private ClusterSlotHashUtil() {

    }

    /**
     * @param keys must not be {@literal null}.
     * @return
     * @since 2.0
     */
    public static boolean isSameSlotForAllKeys(Collection<ByteBuffer> keys) {

       Assert.notNull(keys, "Keys must not be null!");

       if (keys.size() <= 1) {
          return true;
       }

       return isSameSlotForAllKeys((byte[][]) keys.stream() //
             .map(ByteBuffer::duplicate) //
             .map(ByteUtils::getBytes) //
             .toArray(byte[][]::new));
    }

    /**
     * @param keys must not be {@literal null}.
     * @return
     * @since 2.0
     */
    public static boolean isSameSlotForAllKeys(ByteBuffer... keys) {

       Assert.notNull(keys, "Keys must not be null!");
       return isSameSlotForAllKeys(Arrays.asList(keys));
    }

    /**
     * @param keys must not be {@literal null}.
     * @return
     */
    public static boolean isSameSlotForAllKeys(byte[]... keys) {

       Assert.notNull(keys, "Keys must not be null!");

       if (keys.length <= 1) {
          return true;
       }

       int slot = calculateSlot(keys[0]);
       for (int i = 1; i < keys.length; i++) {
          if (slot != calculateSlot(keys[i])) {
             return false;
          }
       }
       return true;
    }

    /**
     * Calculate the slot from the given key.
     *
     * @param key must not be {@literal null} or empty.
     * @return
     */
    public static int calculateSlot(String key) {

       Assert.hasText(key, "Key must not be null or empty!");
       return calculateSlot(key.getBytes());
    }

    /**
     * Calculate the slot from the given key.
     *
     * @param key must not be {@literal null}.
     * @return
     */
    public static int calculateSlot(byte[] key) {

       Assert.notNull(key, "Key must not be null!");

       byte[] finalKey = key;
       int start = indexOf(key, SUBKEY_START);
       if (start != -1) {
          int end = indexOf(key, start + 1, SUBKEY_END);
          if (end != -1 && end != start + 1) {

             finalKey = new byte[end - (start + 1)];
             System.arraycopy(key, start + 1, finalKey, 0, finalKey.length);
          }
       }
       return crc16(finalKey) % SLOT_COUNT;
    }

    private static int indexOf(byte[] haystack, byte needle) {
       return indexOf(haystack, 0, needle);
    }

    private static int indexOf(byte[] haystack, int start, byte needle) {

       for (int i = start; i < haystack.length; i++) {

          if (haystack[i] == needle) {
             return i;
          }
       }

       return -1;
    }

    private static int crc16(byte[] bytes) {

       int crc = 0x0000;

       for (byte b : bytes) {
          crc = ((crc << 8) ^ LOOKUP_TABLE[((crc >>> 8) ^ (b & 0xFF)) & 0xFF]);
       }
       return crc & 0xFFFF;
    }
}
```



#### JedisConnectionFactory

```java
package com.heima.jedis.util;

import redis.clients.jedis.*;

public class JedisConnectionFactory {

    private static JedisPool jedisPool;
    private static final JedisCluster jedisCluster;

    static {
        // 配置连接池
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(8);
        poolConfig.setMaxIdle(8);
        poolConfig.setMinIdle(0);
        poolConfig.setMaxWaitMillis(1000);
        // 创建连接池对象
        // jedisPool = new JedisPool(poolConfig,
        //         "192.168.150.101", 6379, 1000, "123321");
        jedisCluster = new JedisCluster(
                new HostAndPort("192.168.150.101", 7001), poolConfig);
    }

    public static Jedis getJedis(){
        return jedisPool.getResource();
    }

    public static JedisCluster getJedisCluster(){
        return jedisCluster;
    }
}
```



#### ObjectUtils

```java
package com.heima.jedis.util;

import com.sun.istack.internal.Nullable;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class ObjectUtils {
    public static boolean isEmpty(@Nullable Object obj) {
        if (obj == null) {
            return true;
        }

        if (obj instanceof Optional) {
            return !((Optional<?>) obj).isPresent();
        }
        if (obj instanceof CharSequence) {
            return ((CharSequence) obj).length() == 0;
        }
        if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        }
        if (obj instanceof Collection) {
            return ((Collection<?>) obj).isEmpty();
        }
        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).isEmpty();
        }

        // else
        return false;
    }
}
```



































































