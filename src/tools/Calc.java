package tools;

public class Calc {
        
    public static double preciseMod(double value, double modulus) {
        return ((value % modulus) + modulus) % modulus;
    }
}
