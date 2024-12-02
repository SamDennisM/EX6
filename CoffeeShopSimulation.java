import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

class CounterEmptyException extends Exception {
    public CounterEmptyException(String message) {
        super(message);
    }
}

class CoffeeCounter {
    private final Queue<String> counter = new LinkedList<>();
    private final int capacity = 3;

    public synchronized void addCoffee(String coffee) throws InterruptedException {
        while (counter.size() == capacity) {
            wait();
        }
        counter.add(coffee);
        System.out.println(Thread.currentThread().getName() + " prepared " + coffee + ". Counter: " + counter.size());
        notifyAll();
    }

    public synchronized String takeCoffee() throws CounterEmptyException, InterruptedException {
        while (counter.isEmpty()) {
            wait();
        }
        String coffee = counter.poll();
        System.out.println(Thread.currentThread().getName() + " picked up " + coffee + ". Counter: " + counter.size());
        notifyAll();
        return coffee;
    }

    public synchronized String reviewCoffee() throws CounterEmptyException, InterruptedException {
        while (counter.isEmpty()) {
            wait();
        }
        String coffee = counter.peek();
        System.out.println(Thread.currentThread().getName() + " reviewed " + coffee + ". Counter: " + counter.size());
        notifyAll();
        return coffee;
    }
}

class Barista extends Thread {
    private final CoffeeCounter counter;
    private final int coffeeCount;

    public Barista(CoffeeCounter counter, int coffeeCount, String name) {
        super(name);
        this.counter = counter;
        this.coffeeCount = coffeeCount;
    }

    @Override
    public void run() {
        for (int i = 1; i <= coffeeCount; i++) {
            try {
                counter.addCoffee("Coffee " + i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Customer extends Thread {
    private final CoffeeCounter counter;
    private final int coffeeCount;

    public Customer(CoffeeCounter counter, int coffeeCount, String name) {
        super(name);
        this.counter = counter;
        this.coffeeCount = coffeeCount;
    }

    @Override
    public void run() {
        for (int i = 1; i <= coffeeCount; i++) {
            try {
                counter.takeCoffee();
            } catch (CounterEmptyException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class CoffeeReviewer extends Thread {
    private final CoffeeCounter counter;

    public CoffeeReviewer(CoffeeCounter counter, String name) {
        super(name);
        this.counter = counter;
    }

    @Override
    public void run() {
        try {
            counter.reviewCoffee();
        } catch (CounterEmptyException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class CoffeeShopSimulation {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CoffeeCounter counter = new CoffeeCounter();

        System.out.print("Enter the number of coffees Barista 1 will prepare: ");
        int barista1Count = scanner.nextInt();
        System.out.print("Enter the number of coffees Barista 2 will prepare: ");
        int barista2Count = scanner.nextInt();

        System.out.print("Enter the number of coffees Customer 1 will pick up: ");
        int customer1Count = scanner.nextInt();
        System.out.print("Enter the number of coffees Customer 2 will pick up: ");
        int customer2Count = scanner.nextInt();
        System.out.print("Enter the number of coffees Customer 3 will pick up: ");
        int customer3Count = scanner.nextInt();

        Barista barista1 = new Barista(counter, barista1Count, "Barista 1");
        Barista barista2 = new Barista(counter, barista2Count, "Barista 2");

        Customer customer1 = new Customer(counter, customer1Count, "Customer 1");
        Customer customer2 = new Customer(counter, customer2Count, "Customer 2");
        Customer customer3 = new Customer(counter, customer3Count, "Customer 3");

        CoffeeReviewer reviewer = new CoffeeReviewer(counter, "Reviewer");

        barista1.start();
        barista2.start();

        customer1.start();
        customer2.start();
        customer3.start();

        reviewer.start();
    }
}
