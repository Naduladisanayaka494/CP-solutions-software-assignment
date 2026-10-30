import java.util.Arrays;
import java.util.List;

public class Problem1 {

    public static int sumWithForLoop(List<Integer> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            return 0;
        }
        int sum = 0;
        for (int i = 0; i < numbers.size(); i++) {
            sum += numbers.get(i);
        }
        return sum;
    }

    public static int sumWithForEachLoop(List<Integer> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            return 0;
        }
        int sum = 0;
        for (int num : numbers) {
            sum += num;
        }
        return sum;
    }

    public static int sumWithWhileLoop(List<Integer> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            return 0;
        }
        int sum = 0;
        int i = 0;
        while (i < numbers.size()) {
            sum += numbers.get(i);
            i++;
        }
        return sum;
    }

    public static int sumWithRecursion(List<Integer> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            return 0;
        }
        return sumRecursiveHelper(numbers, 0);
    }

    private static int sumRecursiveHelper(List<Integer> numbers, int index) {
        if (index >= numbers.size()) {
            return 0;
        }
        return numbers.get(index) + sumRecursiveHelper(numbers, index + 1);
    }

    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 10);
        System.out.println("Input list: " + numbers);
        System.out.println("1. Sum using for-loop:         " + sumWithForLoop(numbers));
        System.out.println("   Sum using for-each loop:    " + sumWithForEachLoop(numbers));
        System.out.println("2. Sum using while-loop:       " + sumWithWhileLoop(numbers));
        System.out.println("3. Sum using recursion:        " + sumWithRecursion(numbers));
    }
}
