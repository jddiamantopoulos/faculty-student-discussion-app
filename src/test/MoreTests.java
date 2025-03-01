package test;

public class MoreTests {
    public static void main(String[] args) {
        UnitTests tests = new UnitTests("Extended Unit Tests");

        tests.addNote("Starting extended unit tests.");

        // String equality tests
        tests.stringEqualityTest("Test 1: Check if \"hello\" equals \"hello\"",
                                   "hello", "hello");
        tests.stringEqualityTest("Test 2: Check if \"Hello\" equals \"hello\"",
                                   "Hello", "hello");
        tests.stringEqualityTest("Test 3: Check if empty strings are equal",
                                   "", "");

        // Integer equality tests
        tests.integerEqualityTest("Test 4: Compare integers 10 and 10", 10, 10);
        tests.integerEqualityTest("Test 5: Compare integers 15 and 20", 15, 20);
        tests.integerEqualityTest("Test 6: Compare negative integers -5 and -5", -5, -5);

        // Boolean equality tests
        tests.boolEqualityTest("Test 7: Check boolean true equals true", true, true);
        tests.boolEqualityTest("Test 8: Check boolean false equals true", false, true);
        tests.boolEqualityTest("Test 9: Check boolean false equals false", false, false);

        // Manual pass/fail tests
        tests.manualPassFail(true, "Test 10: Manual test set to pass");
        tests.manualPassFail(false, "Test 11: Manual test set to fail");

        tests.addNote("All additional tests executed.");

        tests.close();
    }
}
