import java.util.ArrayList;
import java.util.List;

public class Problem3 {
    public static List<Long> getFirst100Fibonacci() {
        List<Long> fib = new ArrayList<>();
        fib.add(0L);
        fib.add(1L);
        for (int i = 2; i < 100; i++) {
            fib.add(fib.get(i - 1) + fib.get(i - 2));
        }

        return fib;
    }
    public static void main(String[] args) {
        List<Long> result = getFirst100Fibonacci();
        System.out.println(result);
    }
}
