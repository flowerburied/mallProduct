package com.example.mall.search.thread;


public class ThreadTest {

    public static void main(String[] args) {
        /**
         * 继承thread
         * 实现Runnable接口
         * 实现callback接口+futureTask (可以拿到返回结果，处理异常)
         * 线程池
         *
         */
        Thread01 thread01 = new Thread01();
        thread01.start();//启动线程
    }

    public static class Thread01 extends Thread {
        @Override
        public void run() {
            System.out.println("current thread" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果" + i);
//            super.run();
        }
    }
}
