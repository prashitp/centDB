package com.example.services.util;

import com.example.util.InputOperation;
import org.junit.Test;

public class InputOperationTest {

    @Test
    public void selectQueryTest() {
        InputOperation.select("SELECT * FROM users WHERE username = 'adesh'");
    }
}
