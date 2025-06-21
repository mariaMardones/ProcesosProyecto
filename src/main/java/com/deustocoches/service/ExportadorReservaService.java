package com.deustocoches.service;

import com.deustocoches.model.Reserva;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import com.opencsv.CSVWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

@Service
public class ExportadorReservaService {

    public ByteArrayInputStream exportarReservasAPdf(List<Reserva> reservas) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfPTable table = new PdfPTable(4);
            table.addCell("Fecha");
            table.addCell("Coche");
            table.addCell("Precio");
            table.addCell("Estado");

            for (Reserva r : reservas) {
                table.addCell(r.getFecha());
                table.addCell(r.getCoche().getMarca() + " " + r.getCoche().getModelo());
                table.addCell(String.valueOf(r.getPrecioTotal()));
                table.addCell(r.getEstado().toString());
            }

            PdfWriter.getInstance(document, out);
            document.open();
            document.add(new Paragraph("Historial de compras"));
            document.add(table);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
    
    public ByteArrayInputStream exportarReservasACsv(List<Reserva> reservas) {
        StringWriter stringWriter = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(stringWriter);

        // Cabeceras
        String[] header = {"Fecha", "Coche", "Precio", "Estado"};
        csvWriter.writeNext(header);

        // Contenido
        for (Reserva r : reservas) {
            String[] data = {
                r.getFecha(),
                r.getCoche().getMarca() + " " + r.getCoche().getModelo(),
                String.valueOf(r.getPrecioTotal()),
                r.getEstado().toString()
            };
            csvWriter.writeNext(data);
        }

        try {
            csvWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        byte[] csvBytes = stringWriter.toString().getBytes(StandardCharsets.UTF_8);
        return new ByteArrayInputStream(csvBytes);
    }
}
