package com.example.schedulingtasks;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.atLeast;
import static org.awaitility.Awaitility.await;
import org.awaitility.Durations;

// Explicitly specify your main application class
@SpringBootTest(classes = Tutorial112Application.class)
class Tutorial112ApplicationTests {
	@SpyBean
	ScheduledTasks tasks;

	@Test
	void contextLoads() {
		await().atMost(Durations.TEN_SECONDS).untilAsserted(() -> {
			verify(tasks, atLeast(2)).reportCurrentTime();
		});
	}
}