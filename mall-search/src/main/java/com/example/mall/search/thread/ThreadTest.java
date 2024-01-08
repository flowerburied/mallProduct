package com.example.mall.search.thread;


import org.elasticsearch.client.watcher.ActionStatus;

import java.util.concurrent.*;

public class ThreadTest {

    public static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main start");
        /**
         * 继承thread
         * 实现Runnable接口
         * 实现callback接口+futureTask (可以拿到返回结果，处理异常)
         * 线程池
         *
         */
//        //继承thread
//        Thread01 thread01 = new Thread01();
//        thread01.start();//启动线程
//        //实现Runnable接口
//        Runnable01 runnable01 = new Runnable01();
//        new Thread(runnable01).start();
//        //实现callback接口+futureTask (可以拿到返回结果，处理异常)
//        FutureTask<Integer> futureTask = new FutureTask<>(new Callable01());
//        new Thread(futureTask).start();
//        //阻塞等待，等待线程执行完成执行,获得返回结果
//        Integer integer = futureTask.get();


        executorService.execute(new Runnable01());

        System.out.println("main end：");
    }

    //继承thread
    public static class Thread01 extends Thread {
        @Override
        public void run() {
            System.out.println("current thread" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果" + i);
//            super.run();
        }
    }

    public static class Runnable01 implements Runnable {
        @Override
        public void run() {
            System.out.println("current Runnable01 thread" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("Runnable01运行结果" + i);
        }
    }

    public static class Callable01 implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            System.out.println("current Callable01 thread" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("Callable01 运行结果" + i);
            return i;
        }
    }


}
