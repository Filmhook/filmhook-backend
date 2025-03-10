package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.annular.filmhook.FilmHookApplication;


@SpringBootTest(classes = FilmHookApplication.class)
class DemoProjectApplicationTests {

	@Test
	void contextLoads() {
		System.out.println("I am from DemoProjectApplicationTests contextLoads()...");
	}

}
