package com.lx862.dclink.data;

import java.util.ArrayList;
import java.util.List;

public class RunnableQueue {
    private final List<Runnable> queues;

    public RunnableQueue() {
        queues = new ArrayList<>();
    }

    public void add(Runnable runnable) {
        queues.add(runnable);
    }

    public void drain() {
        for(Runnable runnable : queues) {
            runnable.run();
        }
        queues.clear();
    }
}
