package hu.basicvlcj.pdf;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.util.stream.Stream;

public class PDFGenerator {

    private void addTableHeader(PdfPTable table) {
        Stream.of("Word", "Meaning")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(0);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    private void addRow(PdfPTable table, String word, String meaning) {
        PdfPCell wordCell = new PdfPCell();
        PdfPCell meaningCell = new PdfPCell();

        wordCell.setBorderWidth(0);
        meaningCell.setBorderWidth(0);

        wordCell.setPhrase(new Phrase(word));
        meaningCell.setPhrase(new Phrase(meaning));

        table.addCell(wordCell);
        table.addCell(meaningCell);
    }

    public void createDictionary() throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream("dictionary.pdf"));

        document.open();
        //Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        //Chunk chunk = new Chunk("Hello World", font);

        PdfPTable table = new PdfPTable(2);
        addTableHeader(table);
        addRow(table, "apple", "alma");

        document.add(table);
        document.close();
    }
}
