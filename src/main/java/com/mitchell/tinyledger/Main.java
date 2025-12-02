package com.mitchell.tinyledger;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    @SuppressWarnings("resource")
    public static void main(String[] args) throws Exception {
        // Load Spring XML context (DI only)
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        // Keep JVM alive (HttpServer runs in its own executor)
        Runtime.getRuntime().addShutdownHook(new Thread(ctx::close));
        Thread.currentThread().join();
    }
}
