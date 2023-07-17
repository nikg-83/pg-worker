package util;

import java.util.HashSet;
import java.util.Set;

public class CharacterUtil {


        public static void main(String[] args) {
            String inputString = "0000311870930826";
            int length = inputString.length();

            if (length >= 12) {
                String result = inputString.substring(length - 12);
                System.out.println(result);
            } else {
                System.out.println("Input string is not long enough.");
            }

            //int[] A = [1,2,3];
            int[] A = new int[]{1,2,5};
            int j = function(A);
        }

        static int function(int[] A ){

            //int[] numss = new int[]{ 1,2,3,4,5,6,7,8,9};
            Set<Integer> numbers = new HashSet<>();
            for (int i = 0; i < A.length; i++) {
                numbers.add(A[i]);
            }
            for (int  i= 1; i < 10; i++) {
                if(!numbers.contains(i)){
                    System.out.println(i);
                    return i;
                }

            }
            return Integer.MIN_VALUE;
        }

}
