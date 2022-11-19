package com.newcoder.community;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueTests {
    public static void main(String[] args) {
        BlockingQueue queue=new ArrayBlockingQueue(10); //队列容量 10个数
        new Thread(new Producer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
    }
}

class Producer implements Runnable{

    private BlockingQueue<Integer> queue;

    public Producer(BlockingQueue<Integer> queue){
        this.queue=queue;
    }

    @Override
    public void run(){
        try{
            for(int i=0;i<20;i++){
                Thread.sleep(20);
                queue.put(i);
                System.out.println(Thread.currentThread().getName()+"生产："+queue.size());

            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

class Consumer implements Runnable{

    private BlockingQueue<Integer> queue;

    public Consumer(BlockingQueue<Integer> queue){
        this.queue=queue;
    }

    @Override
    public  void run(){
        try{
            while(true){
                Thread.sleep(new Random().nextInt(1000)); //模拟实际情况下的时间间隔 随机0-1000ms不等
                queue.take();
                System.out.println(Thread.currentThread().getName()+"消费："+queue.size());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
