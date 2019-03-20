package com.tegongdete.luckyme.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

@Service
public class JedisService {
    private static final Logger logger = LoggerFactory.getLogger(JedisPool.class);
    JedisPool jedisPool;

    @Bean
    public JedisPool initJedisPool() {
        JedisPool jedisPool = new JedisPool("127.0.0.1");
        this.jedisPool = jedisPool;
        return jedisPool;
    }

    public Long eval(String script, List<String> keys,  List<String> args) {
        try (Jedis jedis = jedisPool.getResource()) {
            return (Long) jedis.eval(script, keys, args);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        }
        return null;
    }
}
