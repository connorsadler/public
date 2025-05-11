package cfs.kalah.integration

import cfs.kalah.controller.GamesController
import org.springframework.test.context.ActiveProfiles

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ActiveProfiles("test")
public class SmokeTest {

    @Autowired
    private GamesController controller;

    @Test
    public void contextLoads() throws Exception {
        assertThat(controller).isNotNull();
    }
}
