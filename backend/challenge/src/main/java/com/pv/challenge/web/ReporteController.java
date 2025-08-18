package com.pv.challenge.web;

import com.pv.challenge.dto.ReporteResponseDtos;
import com.pv.challenge.service.ReporteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Base64;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

  private final ReporteService service;
  public ReporteController(ReporteService service) { this.service = service; }

  @GetMapping
  public ReporteResponseDtos json(
      @RequestParam Long clienteId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
    return service.generar(clienteId, desde, hasta, false);
  }

  @GetMapping("/pdf")
  public ResponseEntity<byte[]> pdf(
      @RequestParam Long clienteId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
    ReporteResponseDtos r = service.generar(clienteId, desde, hasta, true);
    byte[] bytes = r.getPdfBase64() == null ? new byte[0] : Base64.getDecoder().decode(r.getPdfBase64());
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte.pdf")
        .contentType(MediaType.APPLICATION_PDF)
        .body(bytes);
  }
}
