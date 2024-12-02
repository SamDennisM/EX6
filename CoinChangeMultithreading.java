import java.util.Scanner;

public class CoinChangeMultithreading {
    private static int countWays(int[] coins, int n, int sum) {
        int[] dp = new int[sum + 1];
        dp[0] = 1;
        for (int coin : coins) {
            for (int j = coin; j <= sum; j++) {
                dp[j] += dp[j - coin];
            }
        }
        return dp[sum];
    }

    static class CoinChangeTask extends Thread {
        private final int[] coins;
        private final int sum;
        private int result;

        public CoinChangeTask(int[] coins, int sum) {
            this.coins = coins;
            this.sum = sum;
        }

        @Override
        public void run() {
            result = countWays(coins, coins.length, sum);
        }

        public int getResult() {
            return result;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the number of coin types (N): ");
        int n = sc.nextInt();
        int[] coins = new int[n];
        System.out.println("Enter the coin denominations:");
        for (int i = 0; i < n; i++) {
            coins[i] = sc.nextInt();
        }
        System.out.print("Enter the target sum: ");
        int sum = sc.nextInt();
        CoinChangeTask task1 = new CoinChangeTask(coins, sum);
        CoinChangeTask task2 = new CoinChangeTask(coins, sum);
        task1.start();
        task2.start();
        try {
            task1.join();
            task2.join();
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted: " + e.getMessage());
        }
        System.out.println("Number of ways to make the sum using the given coins: " + task1.getResult());
    }
}
