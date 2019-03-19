package com.tegongdete.luckyme.plugin.limitter;

import org.springframework.stereotype.Component;

@Component
public class RedisNumLimitter implements RequestNumLimitter {

    public boolean isAccessible() {
        return false;
    }
}
