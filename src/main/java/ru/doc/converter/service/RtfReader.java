package ru.doc.converter.service;


import org.apache.poi.hwpf.HWPFDocumentCore;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.converter.WordToHtmlUtils;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.zwobble.mammoth.DocumentConverter;
import org.zwobble.mammoth.Result;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.rtf.RTFEditorKit;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.Set;

@Service
public class RtfReader {

    public static void main(String[] args) throws Exception {
//        String text = getTextFromRtf("/home/ratiboraks/work/convert-service/src/main/resources/files/fz.rtf");
//
//        BufferedWriter writer = new BufferedWriter(new FileWriter("/home/ratiboraks/work/convert-service/src/main/resources/files/new-from-rtf.rtf"));
//        writer.write(text);
//        writer.close();
        docxConvert();
    }

    /**
     * @param filePath file path
     * @return Read the contents of rtf
     */
    public static String getTextFromRtf(String filePath) throws Exception {
        String result = null;
        File file = new File(filePath);
        DefaultStyledDocument styledDoc = new DefaultStyledDocument();
        InputStream is = new FileInputStream(file);
        new RTFEditorKit().read(is, styledDoc, 0);
        result = new String(styledDoc.getText(0, styledDoc.getLength()).
                getBytes("ISO8859_1"));
        //Extract the text, read Chinese needs to use ISO8859_1 encoding, otherwise there will be garbled
        String newStr = new String();
        return newStr;
    }

    public static void docConverter() throws Exception {
        HWPFDocumentCore wordDocument = WordToHtmlUtils.loadDoc(new FileInputStream("/home/ratiboraks/work/convert-service/src/main/resources/files/single_paragraph.docx"));

        WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
                DocumentBuilderFactory.newInstance().newDocumentBuilder()
                        .newDocument());
        wordToHtmlConverter.processDocument(wordDocument);
        Document htmlDocument = wordToHtmlConverter.getDocument();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DOMSource domSource = new DOMSource(htmlDocument);
        StreamResult streamResult = new StreamResult(out);

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer serializer = tf.newTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.METHOD, "html");
        serializer.transform(domSource, streamResult);
        out.close();

        String result = new String(out.toByteArray());
        System.out.println(result);
    }

    public static void docxConvert() throws Exception {
        DocumentConverter converter = new DocumentConverter();
        converter.addStyleMap("p[style-name='Aside Text'] => div");
        Result<String> result = converter.convertToHtml(new File("/home/ratiboraks/work/convert-service/src/main/resources/files/sogl.docx"));
        String html = result.getValue(); // The generated HTML
        Set<String> warnings = result.getWarnings(); // Any warnings during conversion
        BufferedWriter writer = new BufferedWriter(new FileWriter("/home/ratiboraks/work/convert-service/src/main/resources/files/new-from-docx2.html"));

        writer.write(markParagraphs(html));
        writer.close();
    }

    public static String markParagraphs(String text) {
        return text.replaceAll("<p>", System.lineSeparator() + "<start-paragraph/>" + System.lineSeparator() + "<p>");
    }
}