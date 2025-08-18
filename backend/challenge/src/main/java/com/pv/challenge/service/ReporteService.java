package com.pv.challenge.service;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.pv.challenge.dto.ReporteLineaDtos;
import com.pv.challenge.dto.ReporteResponseDtos;
import com.pv.challenge.entity.Cliente;
import com.pv.challenge.entity.Cuenta;
import com.pv.challenge.entity.Movimiento;
import com.pv.challenge.exception.NotFoundException;
import com.pv.challenge.repo.ClienteRepository;
import com.pv.challenge.repo.CuentaRepository;
import com.pv.challenge.repo.MovimientoRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReporteService {

  private final ClienteRepository clienteRepo;
  private final CuentaRepository cuentaRepo;
  private final MovimientoRepository movRepo;

  public ReporteService(ClienteRepository clienteRepo,
                        CuentaRepository cuentaRepo,
                        MovimientoRepository movRepo) {
    this.clienteRepo = clienteRepo;
    this.cuentaRepo = cuentaRepo;
    this.movRepo = movRepo;
  }

  public ReporteResponseDtos generar(Long clienteId, LocalDate desde, LocalDate hasta, boolean incluirPdf) {
    Cliente cliente = clienteRepo.findById(clienteId)
        .orElseThrow(() -> new NotFoundException("Cliente " + clienteId + " no existe"));
        
    OffsetDateTime desdeOdt = desde.atStartOfDay().atOffset(ZoneOffset.UTC);
    OffsetDateTime hastaOdt = hasta.atTime(23, 59, 59).atOffset(ZoneOffset.UTC);
    
    List<Cuenta> cuentas = cuentaRepo.findByCliente_Id(clienteId);

    List<ReporteLineaDtos> lineas = new ArrayList<>();
    BigDecimal totalDebitos = BigDecimal.ZERO;
    BigDecimal totalCreditos = BigDecimal.ZERO;

    for (Cuenta cta : cuentas) {
      
      BigDecimal running = calcularSaldoAlInicio(cta, desdeOdt);

      List<Movimiento> enRango =
          movRepo.findByCuenta_IdAndFechaBetweenOrderByFechaAsc(cta.getId(), desdeOdt, hastaOdt);

      for (Movimiento m : enRango) {
        BigDecimal valor = m.getValor();               
        BigDecimal signed = "RETIRO".equalsIgnoreCase(m.getTipo()) ? valor.negate() : valor;

        if (signed.signum() < 0) totalDebitos = totalDebitos.add(signed.abs());
        else totalCreditos = totalCreditos.add(signed);

        ReporteLineaDtos r = new ReporteLineaDtos();
        r.setFecha(m.getFecha().toLocalDateTime());
        r.setCliente(cliente.getPersona().getNombre()); 
        r.setNumeroCuenta(cta.getNumero());
        r.setTipoCuenta(cta.getTipo());
        r.setSaldoInicial(running);
        r.setEstado(Boolean.TRUE.equals(cta.getEstado()));
        r.setMovimiento(signed);
        r.setSaldoDisponible(running.add(signed));

        running = r.getSaldoDisponible();
        lineas.add(r);
      }
    }

    lineas = lineas.stream()
        .sorted(Comparator.comparing(ReporteLineaDtos::getFecha))
        .collect(Collectors.toList());

    ReporteResponseDtos resp = new ReporteResponseDtos();
    resp.setClienteId(clienteId);
    resp.setClienteNombre(cliente.getPersona().getNombre());
    resp.setDesde(desde);
    resp.setHasta(hasta);
    resp.setItems(lineas);
    resp.setTotalDebitos(totalDebitos);
    resp.setTotalCreditos(totalCreditos);

    if (incluirPdf) {
      resp.setPdfBase64(generarPdfBase64(resp));
    }
    return resp;
  }
  
  private BigDecimal calcularSaldoAlInicio(Cuenta cta, OffsetDateTime desdeExclusive) {
    List<Movimiento> anteriores =
        movRepo.findByCuenta_IdAndFechaBeforeOrderByFechaAsc(cta.getId(), desdeExclusive);

    BigDecimal neto = BigDecimal.ZERO;
    for (Movimiento m : anteriores) {
      BigDecimal valor = m.getValor();
      if ("RETIRO".equalsIgnoreCase(m.getTipo())) neto = neto.subtract(valor);
      else neto = neto.add(valor);
    }
    return neto;
  }

  private String generarPdfBase64(ReporteResponseDtos r) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      Document doc = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
      PdfWriter.getInstance(doc, baos);
      doc.open();

      Font h1 = new Font(Font.HELVETICA, 16, Font.BOLD);
      Font normal = new Font(Font.HELVETICA, 10);

      Paragraph titulo = new Paragraph("Estado de Cuenta", h1);
      titulo.setAlignment(Paragraph.ALIGN_CENTER);
      doc.add(titulo);

      doc.add(new Paragraph(
          String.format("Cliente: %s (ID: %d)  |  Rango: %s a %s",
              r.getClienteNombre(), r.getClienteId(),
              r.getDesde(), r.getHasta()), normal));
      doc.add(new Paragraph(" ", normal));

      PdfPTable table = new PdfPTable(8);
      table.setWidthPercentage(100);
      table.setWidths(new float[]{16, 18, 12, 12, 12, 10, 12, 12});

      addHeader(table, "Fecha");
      addHeader(table, "Cliente");
      addHeader(table, "Nro. Cuenta");
      addHeader(table, "Tipo");
      addHeader(table, "Saldo Inicial");
      addHeader(table, "Estado");
      addHeader(table, "Movimiento");
      addHeader(table, "Saldo Disp.");

      for (ReporteLineaDtos it : r.getItems()) {
        table.addCell(new PdfPCell(new Phrase(it.getFecha().toString(), normal)));
        table.addCell(new PdfPCell(new Phrase(it.getCliente(), normal)));
        table.addCell(new PdfPCell(new Phrase(it.getNumeroCuenta(), normal)));
        table.addCell(new PdfPCell(new Phrase(it.getTipoCuenta(), normal)));
        table.addCell(new PdfPCell(new Phrase(it.getSaldoInicial().toPlainString(), normal)));
        table.addCell(new PdfPCell(new Phrase(Boolean.toString(it.isEstado()), normal)));
        table.addCell(new PdfPCell(new Phrase(it.getMovimiento().toPlainString(), normal)));
        table.addCell(new PdfPCell(new Phrase(it.getSaldoDisponible().toPlainString(), normal)));
      }
      doc.add(table);

      doc.add(new Paragraph(" ", normal));
      doc.add(new Paragraph("Totales:", h1));
      doc.add(new Paragraph("Créditos: " + r.getTotalCreditos().toPlainString(), normal));
      doc.add(new Paragraph("Débitos: " + r.getTotalDebitos().toPlainString(), normal));

      doc.close();
      return Base64.getEncoder().encodeToString(baos.toByteArray());
    } catch (Exception e) {
      return null;
    }
  }

  private void addHeader(PdfPTable table, String txt) {
    Font bold = new Font(Font.HELVETICA, 10, Font.BOLD);
    PdfPCell cell = new PdfPCell(new Phrase(txt, bold));
    table.addCell(cell);
  }
}
