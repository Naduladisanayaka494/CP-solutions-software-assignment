import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Problem2 {

    public static List<Object> combine(List<?> list1, List<?> list2) {
        List<Object> result = new ArrayList<>();
        int maxSize = Math.max(list1.size(), list2.size());

        for (int i = 0; i < maxSize; i++) {
            if (i < list1.size()) {
                result.add(list1.get(i));
            }
            if (i < list2.size()) {
                result.add(list2.get(i));
            }
        }

        return result;
    }

    public static void main(String[] args) {
        List<String> list1 = Arrays.asList("a", "b", "c");
        List<Integer> list2 = Arrays.asList(1, 2, 3);

        System.out.println(combine(list1, list2));
    }
}
