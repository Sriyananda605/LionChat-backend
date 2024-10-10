package model;

public class Validations {
    
    public static boolean isPasswordValid(String password) {
    
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$");
    
    }
    
    public static boolean isMobileNumberValid(String mobile) {
    
        return mobile.matches("^07[01245678]{1}[0-9]{7}$");
    
    }
    
    public static boolean isInteger(String text) {
        return text.matches("^\\d+$");
    }
    
}
