package com.neuedu.utils;
import java.util.Random;
public class GenerateCode {
    public static String generateCode(){
        Random random = new Random();
        int sixDigitNumber = 100000 + random.nextInt(900000);
        return String.valueOf(sixDigitNumber);
    }
}
