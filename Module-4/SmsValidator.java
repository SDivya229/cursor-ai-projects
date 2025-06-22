public class SmsValidator {
    public static boolean isValidNumber(String number) {
        return number != null && number.matches("\\+?[1-9][0-9]{7,14}");
    }
    public static boolean isValidMessage(String msg) {
        return msg != null && !msg.trim().isEmpty() && msg.length() <= 1600;
    }
} 