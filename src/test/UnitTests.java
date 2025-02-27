package test;

import java.io.*;

// Imported from Eli's HW1 testing. Repurposed for CSE360Monday25

public class UnitTests {
    public int numTests = 0;
    public int successes = 0;
    public int failures = 0;
    private String testedClass;
    PrintStream out;

    public UnitTests(String testedClass) {
        this.testedClass = testedClass;
        // This used to be a file writer. This was the easiest way to get console output.
        out = System.out;

        out.printf("==UNIT TESTS: %s==\n\n", testedClass);
    }

    public void addNote(String note) {
        out.println(note + "\n");
    }
    
    public boolean manualPassFail(boolean pass, String input) {
    	numTests++;
    	out.printf("Unit test #%d: %s\n", numTests, input);
    	out.printf("	Test Result Manually Set: %b\n", pass);
    	if (pass) {
    		successes++;
    	}
    	else {
    		failures++;
    	}
    	return pass;
    }

    public boolean stringEqualityTest(String input, String testedString, String comparedString) {
        numTests++;
        out.printf("Unit test #%d: %s\n", numTests, input);
        out.printf("    Expected: %s == %s\n", comparedString, comparedString);
        out.printf("    Result:   %s == %s\n", testedString, comparedString);
        if (testedString.equals(comparedString)) {
            out.println("PASS\n\n");
            successes++;
            return true;
        }
        else {
            out.println("FAIL\n\n");
            failures++;
            return false;
        }

    }

    public boolean integerEqualityTest(String input, int testedInt, int comparedInt) {
        numTests++;
        out.printf("Unit test #%d: %s\n", numTests, input);
        out.printf("    Expected: %d == %d\n", comparedInt, comparedInt);
        out.printf("    Result:   %d == %d\n", testedInt, comparedInt);
        if (testedInt == comparedInt) {
            out.println("PASS\n\n");
            successes++;
            return true;
        }
        else {
            out.println("FAIL\n\n");
            failures++;
            return false;
        }
    }
    
    public boolean boolEqualityTest(String input, boolean testedInt, boolean comparedInt) {
        numTests++;
        out.printf("Unit test #%d: %s\n", numTests, input);
        out.printf("    Expected: %b == %b\n", comparedInt, comparedInt);
        out.printf("    Result:   %b == %b\n", testedInt, comparedInt);
        if (testedInt == comparedInt) {
            out.println("PASS\n\n");
            successes++;
            return true;
        }
        else {
            out.println("FAIL\n\n");
            failures++;
            return false;
        }
    }

    public void close() {
        out.println("==END OF TESTS==");
        out.printf("Successes: %d | Failures: %d | Total: %d\n", successes, failures, numTests);
        out.close();
    }
}
