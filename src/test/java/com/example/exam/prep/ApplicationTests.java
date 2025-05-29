package com.example.exam.prep;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SampleTest {

    @Test
    void testSampleTest(String expected) {
        // Test case 1: Sample test case
        String actual = "Hello, World!";
        assert actual.equals(expected) : "Expected 'Hello, World!' but got '" + actual + "'";
        System.out.println("Running sample test case");
    }
}