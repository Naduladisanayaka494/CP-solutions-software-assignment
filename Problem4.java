import java.util.Arrays;
import java.util.List;

public class Problem4 {

    public static String largestNumber(List<Integer> list) {

        list.sort((a, b) -> ("" + b + a).compareTo("" + a + b));

        String result = "";
        for (int n : list) {
            result += n;
        }
        return result;
    }

    public static void main(String[] args) {
        List<Integer> nums = Arrays.asList(50, 2, 1, 9);
        System.out.println(largestNumber(nums));
    }
}
