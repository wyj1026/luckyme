package com.tegongdete.luckyme.plugin.limitter;

import com.tegongdete.luckyme.service.JedisService;
import com.tegongdete.luckyme.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class RedisNumLimitter implements RequestNumLimitter {
    private static final Logger logger = LoggerFactory.getLogger(RequestNumLimitter.class);

    private volatile int limit;
    private JedisService jedisService;
    private String script;

    public RedisNumLimitter(@Value("${requestLimit}") int limit, @Value("${limitscript}") String scriptName, JedisService jedisService) {
        this.limit = limit;
        this.jedisService = jedisService;
        this.script = FileUtil.loadFile(scriptName);
    }

    public boolean isAccessible() {
        String key = String.valueOf(System.currentTimeMillis() / 1000);
        if (script.equals("")) {
            logger.error("Empty script!  Limitter is not working!");
            return true;
        }
        Long l =  jedisService.eval(script, Collections.singletonList(key), Collections.singletonList(String.valueOf(limit)));
        if (l == null) return false;
        return l != 0;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
