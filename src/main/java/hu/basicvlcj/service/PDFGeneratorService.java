package hu.basicvlcj.service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import hu.basicvlcj.model.Word;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.stream.Stream;

/**
 * Class that is responsible for generating PDF documents from {@link hu.basicvlcj.model.Word} objects that are saved to the database.
 */
public class PDFGeneratorService {

    private void addTableHeader(PdfPTable table) {
        Stream.of("Word", "Meaning", "Example")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(0);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    private void addRow(PdfPTable table, String word, String meaning, String example) {
        PdfPCell wordCell = new PdfPCell();
        PdfPCell meaningCell = new PdfPCell();
        PdfPCell exampleCell = new PdfPCell();

        wordCell.setBorderWidth(0);
        meaningCell.setBorderWidth(0);
        exampleCell.setBorderWidth(0);

        wordCell.setPhrase(new Phrase(word));
        meaningCell.setPhrase(new Phrase(meaning));
        exampleCell.setPhrase(new Phrase(example));

        table.addCell(wordCell);
        table.addCell(meaningCell);
        table.addCell(exampleCell);
    }

    /**
     * A method that creates the document.
     *
     * @param filename the name of the output PDF document
     * @throws FileNotFoundException
     * @throws DocumentException
     */
    public void createDictionary(String filename) throws FileNotFoundException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filename + ".pdf"));

        document.open();

        PdfPTable table = new PdfPTable(3);
        addTableHeader(table);

        for (Word word : new WordService().getAllByFilename(filename)) {
            addRow(table, word.getForeignWord(), word.getMeaning(), word.getExample());
        }

        document.add(table);
        document.close();
    }
}
