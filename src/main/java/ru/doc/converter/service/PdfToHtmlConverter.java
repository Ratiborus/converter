package ru.doc.converter.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PRIndirectReference;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PRTokeniser;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import com.spire.pdf.FileFormat;
import com.spire.pdf.PdfDocument;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.fit.pdfdom.PDFDomTree;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.office.OfficeUtils;
import org.jodconverter.local.JodConverter;
import org.jodconverter.local.office.LocalOfficeManager;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

@Service
public class PdfToHtmlConverter {

  public static void main(String[] args) throws Exception{
//    convert();
//    generateHTMLFromPDF("/home/ratiboraks/work/doc-to-html/src/main/resources/files/fz.pdf");
//    processPdfToWord("/home/ratiboraks/work/doc-to-html/src/main/resources/files/fz.pdf");
//    processWithJod();
    convertWithIceBlue();
  }
  public static void convert() {
    // load the PDF file using PDFBox
    PDDocument pdf;
    try {
      pdf = PDDocument.load(new File("/home/ratiboraks/work/doc-to-html/src/main/resources/files/fz.pdf"));
      // create the DOM parser
      PDFDomTree parser;
      parser = new PDFDomTree();
      // parse the file and get the DOM Document
//      Document dom = parser.createDOM(pdf);
//      System.out.println(dom.toString());
    } catch (IOException | ParserConfigurationException e) {
      throw new IllegalArgumentException();
    }
  }

  private static void generateHTMLFromPDF(String filename) throws IOException, ParserConfigurationException{
    PDDocument pdf = PDDocument.load(new File(filename));
    Writer output = new PrintWriter("/home/ratiboraks/work/doc-to-html/src/main/resources/files/pdf.html", "utf-8");
    new PDFDomTree().writeText(pdf, output);

    output.close();
  }


//  public static void processPdfToWord(String filename) throws Exception{
//    XWPFDocument doc = new XWPFDocument();
//    String pdf = filename;
//    PdfReader reader = new PdfReader(pdf);
//    PdfReaderContentParser parser = new PdfReaderContentParser(reader);
//
//    for (int i = 1; i <= reader.getNumberOfPages(); i++) {
//      TextExtractionStrategy strategy =
//          parser.processContent(i, new SimpleTextExtractionStrategy());
//      String text = strategy.getResultantText();
//      XWPFParagraph p = doc.createParagraph();
//      XWPFRun run = p.createRun();
//      run.setText(text);
//      run.addBreak(BreakType.PAGE);
//    }
//    FileOutputStream out = new FileOutputStream("/home/ratiboraks/work/doc-to-html/src/main/resources/files/pdf.docx");
//    doc.write(out);
//  }

  public static void processWithJod() throws Exception{
    File inputFile = new File("/home/ratiboraks/work/doc-to-html/src/main/resources/files/sogl.docx");
    File outputFile = new File("/home/ratiboraks/work/doc-to-html/src/main/resources/files/sogl.html");

// Create an office manager using the default configuration.
// The default port is 2002. Note that when an office manager
// is installed, it will be the one used by default when
// a converter is created.
    final LocalOfficeManager officeManager = LocalOfficeManager.install();
    try {

      // Start an office process and connect to the started instance (on port 2002).
      officeManager.start();

      // Convert
      JodConverter
          .convert(inputFile)
          .as(DefaultDocumentFormatRegistry.DOCX)
          .to(outputFile)
          .as(DefaultDocumentFormatRegistry.HTML)
          .execute();
    } finally {
      // Stop the office process
      OfficeUtils.stopQuietly(officeManager);
    }
  }

  public static void convertWithIceBlue() {
    //Load the PDF file
    PdfDocument pdf = new PdfDocument();
    pdf.loadFromFile("/home/ratiboraks/work/doc-to-html/src/main/resources/files/fz.pdf");
    //Save to HTML format
    pdf.saveToFile("/home/ratiboraks/work/doc-to-html/src/main/resources/files/ToHTML.html", FileFormat.HTML);
  }
}
