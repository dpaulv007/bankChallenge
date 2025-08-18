package com.pv.challenge;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Evitamos cargar el contexto completo; probamos endpoints con @WebMvcTest")
class ChallengeApplicationTests {
  @Test void contextLoads() {}
}
