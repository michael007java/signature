package com.cheche365.cheche.signature;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;

public class IntegrationTest extends TestCase {

    public IntegrationTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(IntegrationTest.class);
    }

    @org.junit.Test
    public void testIntegration() throws IOException {
       int port =  8987;
       try {
           ServerApp.start(port);
       } catch (Exception e) {
           fail(e.getMessage());
           e.printStackTrace();
       }
       ServerApp.stop();
    }
}
