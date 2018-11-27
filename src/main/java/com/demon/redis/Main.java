package com.demon.redis;

import com.demon.redis.lock.AcquireRedisLockTimeoutException;
import com.demon.redis.lock.RedisDistributedLock;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Demon
 */
public class Main {

    private static volatile int num = 0;
    private static int threadCount = 10;

    private static RedisDistributedLock lock = RedisDistributedLock.newInstance("calc", 11000, 11000);

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        Main main = new Main();
        for(int i=0; i<threadCount; i++) {
            executorService.execute(main.new Calc());
        }
        executorService.shutdown();

    }

    class Calc implements Runnable {


        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName());
            //for (int i = 0; i < 10; i++) {
                try {

                    if (lock.tryLock(11000, TimeUnit.MILLISECONDS)) {
                        num++;
                        System.out.println(LocalDateTime.now() + ": " + Thread.currentThread().getName() + " added num to " + num);
                        Thread.sleep(10000);
                    } else {
                        System.out.println(LocalDateTime.now() + ": " + Thread.currentThread().getName() + " get lock fail!");
                    }
                } catch (AcquireRedisLockTimeoutException e) {
                    System.out.println(LocalDateTime.now() + ": " + Thread.currentThread().getName() + " get redis lock fail!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        lock.unlock();
                    } catch (Exception e) {

                    }
                }
            //}
        }
    }

}
