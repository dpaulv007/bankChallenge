package com.pv.challenge.web;

import com.pv.challenge.entity.Movimiento;
import com.pv.challenge.exception.BusinessException;
import com.pv.challenge.repo.MovimientoRepository;
import com.pv.challenge.service.MovimientoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MovimientoController.class)
@AutoConfigureMockMvc
@Import(GlobalExceptionHandler.class) 
class MovimientoControllerTest {

  @Autowired private MockMvc mvc;

  @MockBean private MovimientoRepository movimientoRepository; 
  @MockBean private MovimientoService movimientoService;       

  @Test
  void retiro_saldoNoDisponible_retorna400() throws Exception {
    when(movimientoService.retirar(any(Long.class), any(BigDecimal.class), any(String.class)))
        .thenThrow(new BusinessException("Saldo no disponible."));

    mvc.perform(post("/api/movimientos/retiro")
            .param("cuentaId", "1")
            .param("monto", "100")
            .param("ref", "test")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Saldo no disponible.")); // del ErrorResponse
  }

  @Test
  void deposito_ok_retornaMovimientoConSaldo() throws Exception {
    Movimiento m = new Movimiento();
    m.setId(10L);
    m.setTipo("DEPOSITO");
    m.setValor(new BigDecimal("150.00"));
    m.setSaldo(new BigDecimal("250.00"));
    m.setReferencia("demo");

    when(movimientoService.depositar(any(Long.class), any(BigDecimal.class), any(String.class)))
        .thenReturn(m);

    mvc.perform(post("/api/movimientos/deposito")
            .param("cuentaId", "1")
            .param("monto", "150")
            .param("ref", "demo")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tipo").value("DEPOSITO"))
        .andExpect(jsonPath("$.valor").value(150.00))
        .andExpect(jsonPath("$.saldo").value(250.00));
  }
}
