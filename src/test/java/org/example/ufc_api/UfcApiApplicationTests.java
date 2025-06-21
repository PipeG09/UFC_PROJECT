package org.example.ufc_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UfcApiApplicationTests {

    @Test
    void applicationStartsSuccessfully() {
        UfcApiApplication.main(new String[]{});
    }

    @Test
    void applicationFailsWithInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> {
            UfcApiApplication.main(new String[]{"invalidArg"});
        });
    }
}