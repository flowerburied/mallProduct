package com.example.mall.search.thread;


import org.elasticsearch.client.watcher.ActionStatus;

import java.util.concurrent.*;

public class ThreadTest {

    public static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main start");
//        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
//            System.out.println("current thread" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结果" + i);
//        }, executorService);
//        CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
//            System.out.println("current thread" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结果" + i);
//            return i;
//        }, executorService).whenComplete((res, exception) -> {
////            虽然能得到异常现象，但不能修改返回数据
//            System.out.println("异步任务成功完成结果：" + res + "异常：" + exception);
//        }).exceptionally(throwable -> {
//            //出现异常默认返回
//            return 10;
//        });

//        CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
//            System.out.println("current thread" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结果" + i);
//            return i;
//        }).handle((res, thr) -> {
//            if (res != null) {
//                return res * 2;
//            }
//            if (thr != null) {
//                return 0;
//            }
//            return 0;
//        });
//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
////            System.out.println("current thread" + Thread.currentThread().getId());
////            int i = 10 / 2;
////            System.out.println("运行结果" + i);
////            return i;
////        }, executorService).thenApplyAsync(res -> {
////            System.out.println("运行2启动");
////            return res + 1;
////        });
        CompletableFuture<String> future01 = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品信息");
            return "hello.jpg";
        }, executorService);
        CompletableFuture<String> future02 = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品属性");
            return "黑色256G";

        }, executorService);
        CompletableFuture<String> future03 = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品介绍");
            return "华为";
        }, executorService);
//        CompletableFuture<String> future03 = future01.thenCombineAsync(future02, (f1, f2) -> {
//            System.out.println("运行3");
//            return f1 + f2;
//        }, executorService);
//        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.anyOf(future01, future02, future03);
//        voidCompletableFuture.get();  //等待所有结果完成·
//        //阻塞获取
//        Integer integer = integerCompletableFuture.get();
        System.out.println("main end：" + future03.get());
    }

    public static void thread(String[] args) throws ExecutionException, InterruptedException {
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
