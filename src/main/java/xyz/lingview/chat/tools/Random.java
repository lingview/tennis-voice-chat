package xyz.lingview.chat.tools;

public class Random {
    public static String generateRandomNumber(int length, String chars) {
        if (length <= 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }

        return sb.toString();
    }


}
