package org.xmlcml.svg2xml.demos;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.PDFText2HTML;
import org.apache.pdfbox.util.PDFTextStripper;

public class MiscDemos {

//    public void testEscapeTitle() throws IOException {
//        PDFTextStripper stripper = new PDFText2HTML();
//        PDDocument doc = createDocument("<script>\u3042", PDType1Font.HELVETICA, "<foo>");
//        String text = stripper.getText(doc);
//       
//        Matcher m = Pattern.compile("<title>(.*?)</title>").matcher(text);
//        assertTrue(m.find());
//        assertEquals("&lt;script&gt;&#12354;", m.group(1));
//
//        assertTrue(text.indexOf("&lt;foo&gt;") >= 0);
//    }


}
