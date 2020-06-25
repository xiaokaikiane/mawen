package task;


import java.io.File;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FileScanner {
    //1.核心线程数,始终运行的线程数量
    //2.最大线程数:有新任务并且当前运行线程数小于最大线程数,回创建新的线程处理任务
    //3-4.超过3这个数量,4这个时间单位,2-1(最大线程数-核心线程数) 这些线程(临时工)就会关闭
    //5.工作的阻塞队列
    //如果超出工作队列的长度,任务要处理的方式
   // private ThreadPoolExecutor POOl=new ThreadPoolExecutor(3,3,0, TimeUnit.MICROSECONDS,
   //         new LinkedBlockingDeque<>(),new ThreadPoolExecutor.CallerRunsPolicy());
    private ExecutorService POOl=Executors.newFixedThreadPool(4);
    /**
     * 计数器
     */
    private volatile AtomicInteger count=new AtomicInteger();
    //线程等待的一个锁对象
    private Object lock=new Object();//第一种:synchronized(lock)进行wait等待
    private CountDownLatch latch=new CountDownLatch(1);//第二种实现:await()阻塞等待
    private Semaphore semaphore=new Semaphore(0);//第三种实现:aquire()阻塞等待一定数量的许可
    private ScanCallback callback;
    public FileScanner(ScanCallback callback) {
        this.callback=callback;
    }

    /**
     * 扫描文件目录
     * @param path
     */
    public void Scan(String path) {
        count.incrementAndGet();//启动根目录扫描任务计数器  ++i
        doScan(new File(path));
    }

    /**
     *
     * @param dir 待处理的文件夹
     */
    private void doScan(File dir){
        POOl.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.callback(dir);//文件保存操作
                    File[] child = dir.listFiles();//下一级文件和文件夹
                    if (child != null) {
                        for (File ch : child) {
                            if (ch.isDirectory()) {//如果是文件,递归处理
    //                            System.out.println("文件夹:" + ch.getPath());
                                count.incrementAndGet();//启动子目录扫描任务计数器  ++i
                                System.out.println("当前任务数:"+count.get());
                                        doScan(ch);
                            }
                            //else {
                             //   System.out.println("文件:" + ch.getPath());
                            //}
                        }
                    }
                }finally {
                    int r = count.decrementAndGet();//计数器减操作
                    System.out.println("当前任务数:"+count.get());
                    if (r == 0) {
                        //第一种
                     //   synchronized (lock) {
                       //     lock.notify();
                        //}
                        //第二种
                       latch.countDown();
                        //第三种
                        //semaphore.release();
                    }
                }
            }
        });
    }
    /**
     * 等待任务结束(Scan方法)
     * 多线程任务等待
     */
    public void waitFinish()throws InterruptedException{
          //  synchronized (lock){
          //      lock.wait();
          //  }
        //第二种
        try {
            latch.await();
        } finally {

            //第三种
            //semaphore.acquire();
            System.out.println("关闭线程池");
            // POOl.shutdown(); //内部为interrupt()
            //POOl.shutdownNow();//内部为interrupt()
            POOl.shutdownNow();
        }
    }
    public static void main(String[] args) {
        /**
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                  System.out.println(Thread.currentThread().getName());
            }
        });
        t.start();
        try {
            synchronized (t){
                t.wait();
                System.out.println(Thread.currentThread().getName());
                t.notify();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
         */
    }
}
