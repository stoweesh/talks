import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.common.util.concurrent.Uninterruptibles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Eugene Shelestovich
 */
public class Test {

    List<User> users = new ArrayList<>(1024);
    Lock[] locks = new ReentrantLock[8];
    
    public static void main(String[] args) {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat("corporate-finagle-future-pool-%d")
            .setDaemon(true)
            .build();
        ExecutorService pool = Executors.newFixedThreadPool(4, threadFactory);

        while (true) {
            pool.execute(() -> System.out.println(Thread.currentThread().getName()));
            Uninterruptibles.sleepUninterruptibly(200L, TimeUnit.MILLISECONDS);
        }
    }
    
    public User getUser(int idx) {
        Lock lock = lockForUser(idx);
        lock.lock();
        try {
            return users.get(idx);  
        } finally {
            lock.unlock();    
        }
    } 
    
    private Lock lockForUser(int idx) {
        // x % y = x & (y − 1), when y is a power of 2
        return locks[idx & 7]; 
    }
    
    private static class User {
        int id;
        String name;
    }
}
