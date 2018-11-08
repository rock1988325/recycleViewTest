package com.example.tysw02.myapplication;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by tysw02 on 2018/10/9.
 */
public class Scheduler {
    private PausableThreadPoolExecutor executor;
    private LinkedBlockingQueue<Runnable> queue;

    public Scheduler() {
//        int processors = Runtime.getRuntime().availableProcessors();
        queue = new LinkedBlockingQueue<Runnable>();
        executor = new PausableThreadPoolExecutor(1, 10, 10, TimeUnit.SECONDS, queue);
    }

    public void schedule(Runnable runnable) {
        executor.execute(runnable);
    }

    public void pause() {
        executor.pause();
    }

    public void resume() {
        executor.resume();
    }

    public void clear() {
        queue.clear();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int getQueue(){
        return executor.getQueue().size();
    }

    public List<Runnable> shutdownNow (){
        return executor.shutdownNow();
    }
}
