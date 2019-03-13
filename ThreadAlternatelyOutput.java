import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.IntStream;

/**
 * 多线程交替输出
 */
public class ThreadAlternatelyOutput {

    public static void main(String[] args) {
        int num = 5; // 线程数量
        List<Thread> threads = new ArrayList<>(num);
        AtomicInteger ai = new AtomicInteger(); // 计数原子类
        Runnable runnable = () -> {
            Thread current = Thread.currentThread();
            int index = threads.indexOf(current);
            Thread next = threads.get((index + 1) % threads.size());  // 下一个线程
            boolean f = true; // 循环结束标志
            while (f) {
                LockSupport.park();
                int c = ai.incrementAndGet();
                if (c > 100) {
                    f = false;
                } else {
                    System.out.println(Thread.currentThread().getName() + "：" + c);
                }
                // 唤醒下一个
                LockSupport.unpark(next);
            }
        };
        // 创建线程，加入List
        IntStream.range(0, num).forEach(i -> threads.add(new Thread(runnable, "线程" + (i + 1))));
        // 启动线程
        threads.forEach(Thread::start);
        // 手动解锁第一个线程
        LockSupport.unpark(threads.get(0));
    }
}
