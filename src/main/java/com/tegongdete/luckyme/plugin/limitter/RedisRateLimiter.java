package com.tegongdete.luckyme.plugin.limitter;

import com.tegongdete.luckyme.service.JedisService;
import com.tegongdete.luckyme.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class RedisRateLimiter implements RequestRateLimiter {
    private static final Logger logger = LoggerFactory.getLogger(RequestRateLimiter.class);

    private static final String KEY = "TokenBucketRateLimiter";
    private static final String MAX_PERMITS = "1000";
    private static final String PERMITS_PER_SECOND = "5";
    private JedisService jedisService;
    private String script;
    private List<String> keys;
    private List<String> args;

    public RedisRateLimiter(@Value("${limitscript}") String scriptName, JedisService jedisService) {
        this.jedisService = jedisService;
        this.script = FileUtil.loadFile(scriptName);
        this.keys = new ArrayList<>();
        this.args = new ArrayList<>();
        this.init();
    }

    private void init() {
        this.keys.add(KEY);
        this.keys.add(MAX_PERMITS);
        this.keys.add(PERMITS_PER_SECOND);
        this.args.add("1");
        this.args.add("0");
    }

    public boolean isAccessible() {
        String key = String.valueOf(System.currentTimeMillis() / 1000);
        if (script.equals("")) {
            logger.error("Empty script!  Limitter is not working!");
            return true;
        }
        Long l =  jedisService.eval(script, keys, args);
        if (l == null) return false;
        //logger.info(String.valueOf(l));
        return l == 0;
    }
}
