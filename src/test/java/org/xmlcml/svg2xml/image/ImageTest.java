package org.xmlcml.svg2xml.image;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.svg2xml.Fixtures;
import org.xmlcml.svg2xml.page.PageAnalyzer;
import org.xmlcml.svg2xml.pdf.PDFAnalyzer;

public class ImageTest {
	@Test
	public void testImage() {
		PageAnalyzer pageAnalyzer = new PageAnalyzer(new File(Fixtures.IMAGES_DIR, "multiple-image-page4.svg"));
		pageAnalyzer.splitChunksAndCreatePage();
	}

	@Test
	@Ignore // fails with image conversion
	public void testImagePDF() {
		new PDFAnalyzer().analyzePDFFile(new File(Fixtures.IMAGES_DIR, "multiple-image.pdf"));
	}

}
