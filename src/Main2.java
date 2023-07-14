public class Main2 {
    public static void main(String[] args) {
        Object mutex1 = new Object();
        Object mutex2 = new Object();
        Thread thread1 = new Thread(() ->
        {
            synchronized (mutex1) {
                try {
                    System.out.println(Thread.currentThread().getName() + " захватил монитор: " + mutex1);
                    Thread.sleep(1000);
                    System.out.println(Thread.currentThread().getName() + " ожидает монитор: " + mutex2);
                    synchronized (mutex2) {
                        System.out.println(Thread.currentThread().getName() + " захватил монитор: " + mutex2);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread thread2 = new Thread(() -> {

            synchronized (mutex2) {
                try {
                    System.out.println(Thread.currentThread().getName() + " захватил монитор: " + mutex2);
                    Thread.sleep(1000);
                    System.out.println(Thread.currentThread().getName() + " ожидает монитор: " + mutex1);
                    synchronized (mutex1) {
                        System.out.println(Thread.currentThread().getName() + " захватил монитор: " + mutex1);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        thread1.start();
        thread2.start();
    }


}
