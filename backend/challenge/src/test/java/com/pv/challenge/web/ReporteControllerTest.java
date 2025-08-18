package com.pv.challenge.web;

import com.pv.challenge.dto.ReporteResponseDtos;
import com.pv.challenge.service.ReporteService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ReporteControllerTest {

  @Test
  void retorna_json_ok() throws Exception {
    ReporteService svc = mock(ReporteService.class);
    when(svc.generar(eq(1L), any(LocalDate.class), any(LocalDate.class), eq(false)))
        .thenReturn(new ReporteResponseDtos());

    MockMvc mvc = standaloneSetup(new ReporteController(svc)).build();

    mvc.perform(get("/api/reportes")
            .param("clienteId", "1")
            .param("desde", "2025-08-01")
            .param("hasta", "2025-08-31"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }
}
