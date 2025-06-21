package com.example.sse.sse.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseService {

    private final List<SseEmitter> clients = new CopyOnWriteArrayList<>();

    public SseEmitter addClient() {
        SseEmitter emitter = new SseEmitter(0L); // no timeout
        clients.add(emitter);

        emitter.onCompletion(() -> clients.remove(emitter));
        emitter.onTimeout(() -> clients.remove(emitter));
        emitter.onError((e) -> clients.remove(emitter));

        return emitter;
    }

    public void broadcast(String message) {
        for (SseEmitter emitter : clients) {
            try {
                emitter.send(SseEmitter.event().name("report-update").data(message));
            } catch (IOException e) {
                emitter.completeWithError(e);
                clients.remove(emitter);
            }
        }
    }
}