package util;

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
        }

}
