package com.tegongdete.luckyme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class LuckymeApplication {
    public static void main(String[] args) {
        SpringApplication.run(LuckymeApplication.class, args);
    }

}
