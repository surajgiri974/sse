package com.example.sse.sse.listener;

import com.example.sse.sse.service.SseService;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.concurrent.Executors;

@Component
public class PostgresListener implements ApplicationRunner {

    private final SseService sseService;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    public PostgresListener(SseService sseService) {
        this.sseService = sseService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Connection conn = DriverManager.getConnection(dbUrl, username, password);
        PGConnection pgConn = conn.unwrap(PGConnection.class);

        Statement stmt = conn.createStatement();
        stmt.execute("LISTEN report_channel");

        Executors.newSingleThreadExecutor().submit(() -> {
            while (true) {
                PGNotification[] notifications = pgConn.getNotifications();
                if (notifications != null) {
                    for (PGNotification notification : notifications) {
                        sseService.broadcast(notification.getParameter());
                    }
                }
                Thread.sleep(1000);
            }
        });
    }
}
