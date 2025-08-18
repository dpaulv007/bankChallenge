package com.pv.challenge.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReporteResponseDtos {
  private Long clienteId;
  private String clienteNombre;
  private LocalDate desde;
  private LocalDate hasta;

  private List<ReporteLineaDtos> items = new ArrayList<>();
  private BigDecimal totalDebitos = BigDecimal.ZERO;
  private BigDecimal totalCreditos = BigDecimal.ZERO;

  // PDF en base64 opcional
  private String pdfBase64;

  public Long getClienteId() { return clienteId; }
  public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

  public String getClienteNombre() { return clienteNombre; }
  public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

  public LocalDate getDesde() { return desde; }
  public void setDesde(LocalDate desde) { this.desde = desde; }

  public LocalDate getHasta() { return hasta; }
  public void setHasta(LocalDate hasta) { this.hasta = hasta; }

  public List<ReporteLineaDtos> getItems() { return items; }
  public void setItems(List<ReporteLineaDtos> items) { this.items = items; }

  public BigDecimal getTotalDebitos() { return totalDebitos; }
  public void setTotalDebitos(BigDecimal totalDebitos) { this.totalDebitos = totalDebitos; }

  public BigDecimal getTotalCreditos() { return totalCreditos; }
  public void setTotalCreditos(BigDecimal totalCreditos) { this.totalCreditos = totalCreditos; }

  public String getPdfBase64() { return pdfBase64; }
  public void setPdfBase64(String pdfBase64) { this.pdfBase64 = pdfBase64; }
}
