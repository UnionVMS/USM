package eu.europa.ec.mare.usm.administration.service.user;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class PasswordGeneratorTest {

    private static final int minLen = 8;
    private static final int maxLen = 32;
    private static final int noOfCAPSAlpha = 1;
    private static final int noOfAlpha = 1;
    private static final int noOfDigits = 1;
    private static final int noOfSplChars = 1;

    @Test
    public void testGenerate() {
        generate();
    }

    @Test
    public void testGenerateCheckForDuplicates() {
        Set<String> set = new HashSet<>();

        for (int i = 0; i < 50; i++) {
            String pwd = generate();
            if (set.contains(pwd)) {
                fail("Found duplicate: " + pwd);
            } else {
                set.add(pwd);
            }
        }
    }

    private String generate() {
        String result = PasswordGenerator.generatePswd(minLen, maxLen, noOfCAPSAlpha, noOfAlpha, noOfDigits, noOfSplChars);
        assertNotNull("Unexpected null result", result);
        assertFalse("Unexpected empty result", result.isEmpty());

        System.out.println("Password : " + result);
        return result;
    }

}
