import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main1 {
    public static void main(String[] args) {
        final int THREAD_COUNT = 10;
        AtomicInteger count = new AtomicInteger(0);

        System.out.println("Race Start");


        CountDownLatch startCDL = new CountDownLatch(THREAD_COUNT);
        CountDownLatch finishCDL = new CountDownLatch(THREAD_COUNT);
        Semaphore semaphore = new Semaphore(3);
        CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT);
        ThreadLocal<String> localString = new ThreadLocal<>();
        ThreadLocal<Integer> localCount = new ThreadLocal<>();

        ExecutorService service = Executors.newFixedThreadPool(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++) {

            service.execute(() -> {
                localString.set(Thread.currentThread().getName().split("1-")[1]);


                try {
                    System.out.println(localString.get() + " Готовится");
                    Thread.sleep((long) (Math.random() * 50));
                    startCDL.countDown();
                    startCDL.await();

                    System.out.println(localString.get() + " - готов");
                    Thread.sleep(3);
                    if (barrier.getNumberWaiting() == THREAD_COUNT - 1) {
                        System.out.println("--==Race Begin==--");
                    }
                    barrier.await();
                    long time = System.currentTimeMillis();
                    Thread.sleep((long) (Math.random() * 1000));

                    System.out.println(localString.get() + " у семафора");

                    semaphore.acquire();
                    System.out.println(localString.get() + " захватил семафор");
                    Thread.sleep((long) (Math.random() * 1000));
                    semaphore.release();
                    System.out.println(localString.get() + " освободил семафор");

                    Thread.sleep((long) (100 + Math.random() * 1000));
                    finishCDL.countDown();
                    localCount.set(count.incrementAndGet());
                    if (localCount.get() == 1) {
                        System.out.println("!!!Победитель!!!: " + localString.get() + ": " + (System.currentTimeMillis() - time) + "ms");
                    } else if (localCount.get() == 2) {
                        System.out.println("!!Второе место!!: " + localString.get() + ": " + (System.currentTimeMillis() - time) + "ms");
                    } else if (localCount.get() == 3) {
                        System.out.println("!Третье место!: " + localString.get() + ": " + (System.currentTimeMillis() - time) + "ms");
                    } else
                        System.out.println(localString.get() + " завершил гонку" + ": " + (System.currentTimeMillis() - time) + "ms");
                    finishCDL.await();

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        service.shutdown();

        while (!service.isTerminated()) {
        }
        System.out.println("Race finish");

    }
}