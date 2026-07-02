package com.handymanhub;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// Uses H2 in-memory DB configured in application-test.yml
// No Docker needed — schema created by Hibernate from JPA entities
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {
}