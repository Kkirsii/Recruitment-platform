package com.kkirsii.recruitmentplatform.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 设置 key 的序列化方式为字符串
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // 设置 value 的序列化方式为字符串（如果是复杂对象，也可以使用 Jackson）
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }

    //  单独提供一个适用于图片的 RedisTemplate<byte[]>
    @Bean
    public RedisTemplate<String, byte[]> redisByteTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new org.springframework.data.redis.serializer.RedisSerializer<byte[]>() {
            @Override
            public byte[] serialize(byte[] bytes) {
                return bytes;
            }

            @Override
            public byte[] deserialize(byte[] bytes) {
                return bytes;
            }
        });
        return template;
    }
}
