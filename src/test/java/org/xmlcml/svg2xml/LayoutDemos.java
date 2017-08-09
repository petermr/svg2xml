package org.xmlcml.svg2xml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.RealArray;
import org.xmlcml.euclid.Univariate;
import org.xmlcml.euclid.util.MultisetUtil;
import org.xmlcml.graphics.svg.GraphicsElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGSVG;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.svg2xml.page.PageLayoutAnalyzer;
import org.xmlcml.svg2xml.pdf.PDFAnalyzerTest;

public class LayoutDemos {

	
	public static final String IMAGE_G = "image.g";
	public static final Logger LOG = Logger.getLogger(LayoutDemos.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private final static File ELS_DIR = new File(SVG2XMLFixtures.TABLE_PDF_DIR, "els");
	private final static File DEMOS_DIR = new File("demos");
	private final static File CLINICAL_SVG_DIR = new File(DEMOS_DIR, "clinical/svg");
	private final static File TARGET_SVG = new File("target/svg");

	public static void main(String[] args) {
		elsevier();
	}

	/** results are in target/svg/0415/*.svg or *.png and target/output/0415/*.png (probably less valuable)
	 * 
	 */
	private static void elsevier() {
		PDFAnalyzerTest.analyzePDF(new File(ELS_DIR, "0415.pdf").toString());
		PDFAnalyzerTest.analyzePDF(new File(ELS_DIR, "1092.pdf").toString());
		PDFAnalyzerTest.analyzePDF(new File(ELS_DIR, "1323.pdf").toString());
		PDFAnalyzerTest.analyzePDF(new File(ELS_DIR, "1967.pdf").toString());
	}

	private List<File> filterChunkFiles(File rootDir, String root) {
		File inputDir = new File(rootDir, root+"/");
		LOG.debug("analysing directory: "+inputDir+"/"+inputDir.isDirectory());
		List<File> svgChunkFiles = new ArrayList<File>(FileUtils.listFiles(inputDir, new String[] {"svg"},  true));
		for (int i = svgChunkFiles.size() - 1; i >= 0; i--) {
			if (!svgChunkFiles.get(i).getName().startsWith(IMAGE_G)) {
				svgChunkFiles.remove(i);
			}
		}
		return svgChunkFiles;
	}

	@Test
	@Ignore // files are lost?
	public void analyse0415Table() {
		String[] fileNames = {
			"els/0415/image.g.4.4.svg", 
			"els/0415/image.g.6.2.svg",
			"els/0415/image.g.10.2.svg",
			"els/0415/image.g.11.2.svg",
			}; 
		PageLayoutAnalyzer pageLayoutAnalyzer = new PageLayoutAnalyzer();
		pageLayoutAnalyzer.setIncludeRulers(true);
		pageLayoutAnalyzer.setIncludePhrases(true);
		for (String fileName : fileNames) {
			LOG.debug("==========>"+fileName);
			File inputFile = new File(CLINICAL_SVG_DIR, fileName);
			pageLayoutAnalyzer.analyzeXRangeExtents(inputFile);
		}
		System.out.println(pageLayoutAnalyzer.getXRangeSet());
		System.out.println(pageLayoutAnalyzer.getXRangeStartSet());
		System.out.println(pageLayoutAnalyzer.getXRangeEndSet());
	}

	@Test
	@Ignore // files are lost
	public void analyseAllImageChunks() {
		String[] roots = {
				"0415", 
				"1092", 
				"1323", 
				"1967",
				};
		File rootDir = /*TARGET_SVG*/ new File("demos/clinical/svg/els");
		PageLayoutAnalyzer pageLayoutAnalyzer = new PageLayoutAnalyzer();
		pageLayoutAnalyzer.setIncludeRulers(true);
		pageLayoutAnalyzer.setIncludePhrases(true);
		pageLayoutAnalyzer.setXRangeRangeMin(100);
		
		for (String root : roots) {
			LOG.debug("===="+root);
			List<File> svgChunkFiles = filterChunkFiles(rootDir, root);
			for (File inputFile : svgChunkFiles) {
				LOG.debug("==========>"+inputFile);
				pageLayoutAnalyzer.analyzeXRangeExtents(inputFile);
			}
			
		}
		System.out.println(pageLayoutAnalyzer.getXRangeSet());
//		Assert.assertEquals("xrangeStart", "["
//				+ "37 x 205, 282 x 136, 49 x 124, 41 x 109, 60 x 106, 297 x 92, 43 x 84, 54 x 64, 47 x 55, 200 x 54, 301 x 52, "
//				+ "56 x 48, 286 x 43, 53 x 34, 83 x 34, 59 x 32, 42 x 23, 196 x 23, 310 x 20, 337 x 19, 146 x 18, 48 x 16, 294 x 16, "
//				+ "103 x 13, 105 x 13, 259 x 13, 82 x 11, 101 x 11, 121 x 11, 81 x 10, 93 x 10, 129 x 10, 141 x 10, 288 x 10, "
//				+ "408 x 10, 45 x 9, 75 x 9, 109 x 9, 113 x 9, 290 x 9, 112 x 8, 174 x 8, 134 x 7, 207 x 7, 69 x 6, 393 x 6, "
//				+ "405 x 6, 65 x 5, 74 x 5, 114 x 5, 133 x 5, 138 x 5, 160 x 5, 205 x 5, 267 x 5, 298 x 5, 332 x 5, 341 x 5, 368 x 5, "
//				+ "378 x 5, 66 x 4, 143 x 4, 154 x 4, 169 x 4, 171 x 4, 180 x 4, 238 x 4, 239 x 4, 262 x 4, 287 x 4, 326 x 4, 333 x 4, "
//				+ "381 x 4, 46 x 3, 63 x 3, 78 x 3, 106 x 3, 111 x 3, 125 x 3, 147 x 3, 152 x 3, 175 x 3, 176 x 3, 181 x 3, 187 x 3, "
//				+ "194 x 3, 199 x 3, 211 x 3, 271 x 3, 289 x 3, 295 x 3, 303 x 3, 331 x 3, 342 x 3, 351 x 3, 355 x 3, 359 x 3, 384 x 3, "
//				+ "391 x 3, 402 x 3, 404 x 3, 68 x 2, 70 x 2, 72 x 2, 73 x 2, 85 x 2, 94 x 2, 97 x 2, 102 x 2, 127 x 2, 131 x 2, 132 x 2, "
//				+ "140 x 2, 145 x 2, 148 x 2, 153 x 2, 156 x 2, 158 x 2, 159 x 2, 167 x 2, 185 x 2, 197 x 2, 201 x 2, 202 x 2, 208 x 2, "
//				+ "212 x 2, 215 x 2, 216 x 2, 220 x 2, 223 x 2, 228 x 2, 241 x 2, 243 x 2, 248 x 2, 252 x 2, 265 x 2, 270 x 2, 292 x 2, "
//				+ "302 x 2, 308 x 2, 312 x 2, 317 x 2, 319 x 2, 320 x 2, 325 x 2, 327 x 2, 328 x 2, 334 x 2, 335 x 2, 347 x 2, 348 x 2, "
//				+ "349 x 2, 401 x 2, 414 x 2, 418 x 2, 419 x 2, 420 x 2, 421 x 2, 424 x 2, 38, 50, 55, 71, 76, 79, 80, 88, 90, 96, 107, "
//				+ "108, 110, 115, 116, 117, 120, 122, 123, 126, 128, 130, 135, 137, 139, 142, 150, 151, 155, 157, 162, 163, 164, 165, "
//				+ "168, 173, 182, 186, 188, 191, 192, 203, 206, 218, 219, 226, 227, 233, 234, 235, 236, 237, 245, 247, 249, 251, 257, "
//				+ "261, 264, 268, 269, 273, 274, 275, 278, 280, 281, 283, 285, 291, 299, 300, 306, 311, 313, 314, 321, 322, 336, 339, "
//				+ "344, 346, 350, 357, 362, 364, 365, 372, 373, 375, 376, 382, 386, 387, 389, 397, 398, 410, 411, 417, 422, 426, 427, "
//				+ "430, 434, 448, 489, 492"
//				+ "]", MultisetUtil.getIntegerEntriesSortedByCount(pageLayoutAnalyzer.getXRangeStartSet()).toString());

		Assert.assertEquals("xrangeStart", 
				"[37 x 167, 282 x 133, 49 x 124, 60 x 106, 297 x 93, 41 x 84, 43 x 83, 54 x 64, 47 x 55, 200 x 55, 301 x 53, 56 x 48, 35 x 44, 286 x 44, 83 x 35, 40 x 34, 53 x 34, 59 x 32, 42 x 23, 196 x 21, 310 x 20, 337 x 20, 146 x 19, 48 x 16, 294 x 16, 259 x 13, 103 x 12, 104 x 12, 127 x 12, 81 x 11, 101 x 11, 112 x 11, 121 x 11, 288 x 11, 82 x 10, 109 x 10, 141 x 10, 290 x 10, 45 x 9, 93 x 9, 113 x 9, 133 x 9, 406 x 9, 69 x 7, 72 x 7, 74 x 7, 169 x 7, 65 x 6, 134 x 6, 205 x 6, 212 x 6, 267 x 6, 280 x 6, 332 x 6, 393 x 6, 153 x 5, 171 x 5, 172 x 5, 174 x 5, 180 x 5, 326 x 5, 378 x 5, 405 x 5, 73 x 4, 94 x 4, 105 x 4, 144 x 4, 145 x 4, 156 x 4, 158 x 4, 176 x 4, 187 x 4, 194 x 4, 198 x 4, 202 x 4, 238 x 4, 250 x 4, 295 x 4, 320 x 4, 333 x 4, 335 x 4, 341 x 4, 347 x 4, 349 x 4, 368 x 4, 382 x 4, 404 x 4, 46 x 3, 63 x 3, 76 x 3, 110 x 3, 114 x 3, 123 x 3, 137 x 3, 138 x 3, 142 x 3, 148 x 3, 163 x 3, 166 x 3, 181 x 3, 185 x 3, 190 x 3, 195 x 3, 208 x 3, 215 x 3, 221 x 3, 227 x 3, 237 x 3, 248 x 3, 271 x 3, 285 x 3, 287 x 3, 289 x 3, 298 x 3, 306 x 3, 312 x 3, 317 x 3, 319 x 3, 325 x 3, 334 x 3, 342 x 3, 346 x 3, 355 x 3, 359 x 3, 364 x 3, 375 x 3, 381 x 3, 391 x 3, 402 x 3, 64 x 2, 68 x 2, 77 x 2, 88 x 2, 97 x 2, 100 x 2, 102 x 2, 107 x 2, 122 x 2, 124 x 2, 125 x 2, 129 x 2, 131 x 2, 136 x 2, 139 x 2, 147 x 2, 150 x 2, 151 x 2, 152 x 2, 159 x 2, 165 x 2, 167 x 2, 173 x 2, 175 x 2, 178 x 2, 182 x 2, 201 x 2, 206 x 2, 207 x 2, 211 x 2, 213 x 2, 218 x 2, 220 x 2, 224 x 2, 228 x 2, 229 x 2, 235 x 2, 239 x 2, 241 x 2, 243 x 2, 246 x 2, 247 x 2, 258 x 2, 261 x 2, 262 x 2, 265 x 2, 268 x 2, 269 x 2, 278 x 2, 281 x 2, 292 x 2, 299 x 2, 300 x 2, 302 x 2, 303 x 2, 307 x 2, 311 x 2, 324 x 2, 327 x 2, 329 x 2, 330 x 2, 331 x 2, 340 x 2, 348 x 2, 352 x 2, 366 x 2, 372 x 2, 384 x 2, 390 x 2, 400 x 2, 414 x 2, 418 x 2, 419 x 2, 420 x 2, 421 x 2, 422 x 2, 424 x 2, 427 x 2, 430 x 2, 432 x 2, 38, 50, 55, 66, 70, 71, 79, 80, 85, 92, 95, 98, 111, 115, 116, 120, 126, 128, 130, 132, 140, 143, 149, 154, 157, 160, 162, 164, 168, 179, 184, 186, 188, 191, 192, 197, 199, 203, 204, 209, 210, 214, 216, 217, 219, 222, 226, 232, 233, 234, 242, 244, 252, 256, 260, 264, 270, 273, 275, 276, 279, 283, 293, 296, 305, 309, 313, 314, 316, 322, 323, 328, 336, 343, 345, 356, 358, 360, 361, 362, 365, 367, 370, 373, 377, 379, 380, 385, 386, 387, 388, 396, 397, 398, 401, 403, 410, 411, 412, 416, 417, 426, 437, 447, 455, 470, 489, 492]",
//				"["
//				+ "37 x 205, 282 x 136, 49 x 124, 41 x 109, 60 x 106, 297 x 92, 43 x 84, 54 x 64, 47 x 55, 200 x 54, 301 x 52, 56 x 48, 286 x 43, 53 x 34, 83 x 34, 59 x 32, 42 x 23, 196 x 23, 310 x 20, 337 x 19, 146 x 18, 48 x 16, 294 x 16, 103 x 13, 105 x 13, 259 x 13, 82 x 11, 101 x 11, 121 x 11, 81 x 10, 93 x 10, 129 x 10, 141 x 10, 288 x 10, 45 x 9, 75 x 9, 109 x 9, 113 x 9, 290 x 9, 112 x 8, 174 x 8, 408 x 8, 134 x 7, 69 x 6, 393 x 6, 405 x 6, 65 x 5, 74 x 5, 114 x 5, 133 x 5, 138 x 5, 205 x 5, 267 x 5, 298 x 5, 332 x 5, 341 x 5, 368 x 5, 378 x 5, 66 x 4, 143 x 4, 154 x 4, 160 x 4, 169 x 4, 171 x 4, 180 x 4, 238 x 4, 239 x 4, 262 x 4, 287 x 4, 326 x 4, 333 x 4, 381 x 4, 46 x 3, 63 x 3, 78 x 3, 106 x 3, 111 x 3, 125 x 3, 147 x 3, 152 x 3, 175 x 3, 176 x 3, 181 x 3, 187 x 3, 194 x 3, 199 x 3, 207 x 3, 211 x 3, 271 x 3, 289 x 3, 295 x 3, 303 x 3, 331 x 3, 342 x 3, 351 x 3, 355 x 3, 359 x 3, 384 x 3, 391 x 3, 402 x 3, 404 x 3, 68 x 2, 70 x 2, 72 x 2, 73 x 2, 85 x 2, 94 x 2, 97 x 2, 102 x 2, 127 x 2, 131 x 2, 132 x 2, 140 x 2, 145 x 2, 148 x 2, 153 x 2, 156 x 2, 158 x 2, 159 x 2, 167 x 2, 185 x 2, 197 x 2, 201 x 2, 202 x 2, 208 x 2, 212 x 2, 215 x 2, 216 x 2, 220 x 2, 223 x 2, 228 x 2, 241 x 2, 243 x 2, 248 x 2, 252 x 2, 265 x 2, 270 x 2, 292 x 2, 302 x 2, 308 x 2, 312 x 2, 317 x 2, 319 x 2, 320 x 2, 325 x 2, 327 x 2, 328 x 2, 334 x 2, 335 x 2, 347 x 2, 348 x 2, 349 x 2, 401 x 2, 414 x 2, 418 x 2, 419 x 2, 420 x 2, 421 x 2, 424 x 2, 38, 50, 55, 71, 76, 79, 80, 88, 90, 96, 107, 108, 110, 115, 116, 117, 120, 122, 123, 126, 128, 130, 135, 137, 139, 142, 150, 151, 155, 157, 162, 163, 164, 165, 168, 173, 182, 186, 188, 191, 192, 203, 206, 218, 219, 226, 227, 233, 234, 235, 236, 237, 245, 247, 249, 251, 257, 261, 264, 268, 269, 273, 274, 275, 278, 280, 281, 283, 285, 291, 299, 300, 306, 311, 313, 314, 321, 322, 336, 339, 344, 346, 350, 357, 362, 364, 365, 372, 373, 375, 376, 382, 386, 387, 389, 397, 398, 410, 411, 417, 422, 426, 427, 430, 434, 448, 489, 492"
//				+ "]", 
				MultisetUtil.getIntegerEntriesSortedByCount(pageLayoutAnalyzer.getXRangeStartSet()).toString());
		System.out.println(MultisetUtil.getIntegerEntriesSortedByCount(pageLayoutAnalyzer.getXRangeEndSet()));

		Assert.assertEquals("xrangeEnd", 
				"[503 x 225, 492 x 113, 507 x 111, 258 x 78, 256 x 59, 487 x 56, 262 x 50, 508 x 46, 505 x 33, 491 x 21, 509 x 18, 45 x 15, 87 x 15, 86 x 14, 102 x 14, 126 x 14, 252 x 14, 260 x 14, 105 x 13, 114 x 13, 106 x 12, 429 x 12, 497 x 12, 413 x 11, 488 x 11, 88 x 10, 219 x 10, 163 x 9, 264 x 9, 483 x 9, 85 x 8, 124 x 8, 312 x 8, 378 x 8, 494 x 8, 107 x 7, 123 x 7, 139 x 7, 140 x 7, 176 x 7, 207 x 7, 237 x 7, 365 x 7, 366 x 7, 367 x 7, 377 x 7, 384 x 7, 391 x 7, 462 x 7, 44 x 6, 58 x 6, 94 x 6, 103 x 6, 108 x 6, 118 x 6, 146 x 6, 168 x 6, 177 x 6, 183 x 6, 235 x 6, 288 x 6, 289 x 6, 319 x 6, 369 x 6, 379 x 6, 424 x 6, 472 x 6, 62 x 5, 71 x 5, 83 x 5, 104 x 5, 125 x 5, 133 x 5, 135 x 5, 153 x 5, 156 x 5, 160 x 5, 171 x 5, 172 x 5, 178 x 5, 240 x 5, 244 x 5, 246 x 5, 285 x 5, 323 x 5, 341 x 5, 364 x 5, 371 x 5, 390 x 5, 404 x 5, 406 x 5, 442 x 5, 455 x 5, 464 x 5, 476 x 5, 481 x 5, 486 x 5, 493 x 5, 59 x 4, 79 x 4, 113 x 4, 128 x 4, 136 x 4, 138 x 4, 142 x 4, 143 x 4, 144 x 4, 147 x 4, 148 x 4, 159 x 4, 166 x 4, 167 x 4, 185 x 4, 191 x 4, 196 x 4, 199 x 4, 201 x 4, 211 x 4, 213 x 4, 218 x 4, 221 x 4, 225 x 4, 226 x 4, 227 x 4, 229 x 4, 231 x 4, 234 x 4, 241 x 4, 247 x 4, 248 x 4, 253 x 4, 254 x 4, 290 x 4, 295 x 4, 305 x 4, 320 x 4, 322 x 4, 327 x 4, 334 x 4, 335 x 4, 368 x 4, 382 x 4, 387 x 4, 394 x 4, 395 x 4, 399 x 4, 402 x 4, 407 x 4, 421 x 4, 426 x 4, 427 x 4, 431 x 4, 437 x 4, 449 x 4, 454 x 4, 460 x 4, 465 x 4, 471 x 4, 473 x 4, 482 x 4, 484 x 4, 485 x 4, 490 x 4, 495 x 4, 496 x 4, 499 x 4, 67 x 3, 74 x 3, 75 x 3, 80 x 3, 82 x 3, 90 x 3, 95 x 3, 96 x 3, 97 x 3, 111 x 3, 112 x 3, 115 x 3, 131 x 3, 149 x 3, 150 x 3, 154 x 3, 155 x 3, 157 x 3, 161 x 3, 164 x 3, 165 x 3, 170 x 3, 173 x 3, 174 x 3, 175 x 3, 181 x 3, 188 x 3, 192 x 3, 202 x 3, 205 x 3, 206 x 3, 209 x 3, 210 x 3, 214 x 3, 216 x 3, 217 x 3, 224 x 3, 242 x 3, 243 x 3, 245 x 3, 250 x 3, 251 x 3, 263 x 3, 298 x 3, 303 x 3, 314 x 3, 315 x 3, 316 x 3, 326 x 3, 338 x 3, 349 x 3, 352 x 3, 354 x 3, 363 x 3, 370 x 3, 372 x 3, 373 x 3, 381 x 3, 383 x 3, 392 x 3, 393 x 3, 401 x 3, 409 x 3, 412 x 3, 414 x 3, 425 x 3, 432 x 3, 433 x 3, 438 x 3, 440 x 3, 441 x 3, 445 x 3, 452 x 3, 453 x 3, 456 x 3, 457 x 3, 459 x 3, 463 x 3, 501 x 3, 46 x 2, 49 x 2, 52 x 2, 63 x 2, 70 x 2, 72 x 2, 73 x 2, 77 x 2, 81 x 2, 92 x 2, 99 x 2, 100 x 2, 101 x 2, 110 x 2, 117 x 2, 122 x 2, 134 x 2, 137 x 2, 145 x 2, 158 x 2, 162 x 2, 169 x 2, 179 x 2, 184 x 2, 187 x 2, 190 x 2, 193 x 2, 195 x 2, 203 x 2, 204 x 2, 208 x 2, 212 x 2, 215 x 2, 223 x 2, 230 x 2, 232 x 2, 233 x 2, 236 x 2, 238 x 2, 249 x 2, 255 x 2, 270 x 2, 271 x 2, 277 x 2, 282 x 2, 292 x 2, 300 x 2, 301 x 2, 302 x 2, 304 x 2, 307 x 2, 308 x 2, 310 x 2, 311 x 2, 318 x 2, 328 x 2, 331 x 2, 337 x 2, 339 x 2, 340 x 2, 342 x 2, 343 x 2, 344 x 2, 345 x 2, 347 x 2, 351 x 2, 357 x 2, 358 x 2, 360 x 2, 361 x 2, 375 x 2, 380 x 2, 386 x 2, 389 x 2, 396 x 2, 408 x 2, 410 x 2, 415 x 2, 435 x 2, 436 x 2, 446 x 2, 461 x 2, 466 x 2, 468 x 2, 470 x 2, 475 x 2, 480 x 2, 498 x 2, 500 x 2, 502 x 2, 48, 51, 53, 57, 64, 65, 66, 69, 76, 78, 89, 91, 98, 109, 116, 119, 120, 121, 129, 130, 132, 151, 152, 180, 182, 186, 194, 197, 198, 222, 228, 239, 259, 273, 274, 276, 278, 279, 280, 281, 284, 286, 287, 291, 293, 294, 296, 297, 299, 306, 313, 321, 325, 329, 330, 332, 346, 348, 350, 353, 356, 362, 374, 376, 388, 397, 398, 400, 411, 416, 417, 418, 420, 422, 423, 428, 439, 447, 448, 451, 458, 467, 469, 474, 478, 489, 504]",
//				"["
//				+ "503 x 255, 507 x 121, 492 x 112, 258 x 92, 256 x 58, 262 x 58, 487 x 56, 508 x 51, 491 x 21, 252 x 17, 45 x 15, 86 x 15, 87 x 15, 112 x 14, 497 x 14, 102 x 13, 105 x 13, 126 x 13, 482 x 13, 427 x 12, 488 x 12, 106 x 11, 88 x 9, 124 x 9, 219 x 9, 411 x 9, 312 x 8, 367 x 8, 494 x 8, 85 x 7, 107 x 7, 123 x 7, 159 x 7, 163 x 7, 237 x 7, 244 x 7, 366 x 7, 376 x 7, 391 x 7, 44 x 6, 58 x 6, 83 x 6, 103 x 6, 118 x 6, 140 x 6, 146 x 6, 171 x 6, 225 x 6, 235 x 6, 379 x 6, 384 x 6, 424 x 6, 62 x 5, 71 x 5, 104 x 5, 108 x 5, 142 x 5, 143 x 5, 148 x 5, 156 x 5, 165 x 5, 183 x 5, 207 x 5, 209 x 5, 246 x 5, 247 x 5, 289 x 5, 364 x 5, 365 x 5, 387 x 5, 404 x 5, 449 x 5, 462 x 5, 472 x 5, 476 x 5, 486 x 5, 490 x 5, 493 x 5, 59 x 4, 77 x 4, 94 x 4, 97 x 4, 111 x 4, 125 x 4, 131 x 4, 136 x 4, 138 x 4, 144 x 4, 153 x 4, 166 x 4, 174 x 4, 178 x 4, 181 x 4, 185 x 4, 196 x 4, 201 x 4, 213 x 4, 218 x 4, 227 x 4, 240 x 4, 241 x 4, 251 x 4, 253 x 4, 288 x 4, 290 x 4, 318 x 4, 320 x 4, 323 x 4, 327 x 4, 339 x 4, 340 x 4, 352 x 4, 363 x 4, 371 x 4, 372 x 4, 377 x 4, 378 x 4, 399 x 4, 406 x 4, 421 x 4, 426 x 4, 435 x 4, 442 x 4, 453 x 4, 464 x 4, 465 x 4, 471 x 4, 473 x 4, 481 x 4, 485 x 4, 495 x 4, 496 x 4, 67 x 3, 73 x 3, 75 x 3, 80 x 3, 89 x 3, 95 x 3, 113 x 3, 133 x 3, 135 x 3, 137 x 3, 154 x 3, 162 x 3, 167 x 3, 168 x 3, 170 x 3, 173 x 3, 176 x 3, 177 x 3, 193 x 3, 202 x 3, 204 x 3, 230 x 3, 243 x 3, 250 x 3, 285 x 3, 293 x 3, 297 x 3, 298 x 3, 305 x 3, 314 x 3, 317 x 3, 322 x 3, 335 x 3, 347 x 3, 348 x 3, 368 x 3, 369 x 3, 370 x 3, 373 x 3, 381 x 3, 390 x 3, 392 x 3, 394 x 3, 395 x 3, 401 x 3, 402 x 3, 412 x 3, 414 x 3, 425 x 3, 432 x 3, 436 x 3, 440 x 3, 441 x 3, 452 x 3, 454 x 3, 455 x 3, 456 x 3, 457 x 3, 460 x 3, 463 x 3, 501 x 3, 46 x 2, 49 x 2, 52 x 2, 63 x 2, 70 x 2, 72 x 2, 74 x 2, 81 x 2, 82 x 2, 92 x 2, 96 x 2, 100 x 2, 101 x 2, 110 x 2, 114 x 2, 115 x 2, 116 x 2, 122 x 2, 128 x 2, 129 x 2, 132 x 2, 134 x 2, 139 x 2, 147 x 2, 151 x 2, 152 x 2, 155 x 2, 157 x 2, 160 x 2, 161 x 2, 172 x 2, 175 x 2, 184 x 2, 187 x 2, 188 x 2, 192 x 2, 195 x 2, 198 x 2, 199 x 2, 200 x 2, 206 x 2, 210 x 2, 212 x 2, 214 x 2, 216 x 2, 221 x 2, 224 x 2, 229 x 2, 233 x 2, 236 x 2, 242 x 2, 245 x 2, 264 x 2, 276 x 2, 284 x 2, 304 x 2, 308 x 2, 315 x 2, 316 x 2, 319 x 2, 321 x 2, 329 x 2, 332 x 2, 333 x 2, 334 x 2, 338 x 2, 342 x 2, 343 x 2, 358 x 2, 361 x 2, 375 x 2, 382 x 2, 386 x 2, 389 x 2, 393 x 2, 396 x 2, 398 x 2, 407 x 2, 408 x 2, 409 x 2, 415 x 2, 429 x 2, 431 x 2, 433 x 2, 445 x 2, 446 x 2, 458 x 2, 459 x 2, 468 x 2, 475 x 2, 480 x 2, 483 x 2, 484 x 2, 498 x 2, 500 x 2, 502 x 2, 48, 51, 53, 57, 64, 65, 66, 69, 76, 78, 79, 90, 91, 98, 99, 109, 117, 119, 120, 121, 130, 141, 145, 149, 150, 158, 164, 169, 179, 182, 186, 189, 190, 191, 194, 197, 203, 205, 208, 211, 215, 217, 222, 223, 226, 231, 234, 239, 248, 249, 254, 255, 259, 261, 263, 267, 269, 270, 271, 272, 273, 274, 277, 280, 281, 286, 287, 291, 292, 294, 296, 299, 300, 301, 302, 303, 307, 309, 311, 313, 325, 344, 345, 346, 349, 351, 353, 354, 355, 356, 357, 360, 380, 383, 397, 403, 410, 413, 417, 418, 420, 422, 423, 428, 430, 438, 439, 443, 447, 448, 451, 461, 466, 467, 469, 470, 474, 478, 499, 504"
//				+ "]",
				MultisetUtil.getIntegerEntriesSortedByCount(pageLayoutAnalyzer.getXRangeEndSet()).toString());
		
		SVGG g = new SVGG();
		int YMAX = 500;
		for (Integer xEnd : pageLayoutAnalyzer.getXRangeStartSet()) {
			plotLine(g, YMAX, "red", 0.1, xEnd);
		}
		for (Integer xEnd : pageLayoutAnalyzer.getXRangeEndSet()) {
			plotLine(g, YMAX, "cyan", 0.1, xEnd);
		}
		for (int i = 0; i <= 500; i+=50) {
			GraphicsElement text = new SVGText(new Real2(i, 100.), ""+i);
			text.setFontSize(8.);
			g.appendChild(text);
			SVGLine line = new SVGLine(new Real2(i, 30), new Real2(i, 70));
			g.appendChild(line);
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/"+"table"+"/"+"startEnd.svg"));
		RealArray realArray = pageLayoutAnalyzer.getXRangeStartArray();
//		LOG.debug(realArray.size()+"/"+realArray);
		Univariate xStartUnivariate = pageLayoutAnalyzer.getXStartUnivariate();
		xStartUnivariate.setBinCount(500);
		List<Real2> startBins = xStartUnivariate.getBinsSortedByFrequency();
		LOG.debug(startBins.size()+"/"+startBins);
		System.out.println(pageLayoutAnalyzer.getXRangeStartSet());
		System.out.println(pageLayoutAnalyzer.getXRangeEndSet());
	}

	private void plotLine(SVGG g, int YMAX, String stroke, double strokeWidth, Integer x) {
		double delta = 0.5 - Math.random();
		double x1 = x + delta;
		SVGLine line = new SVGLine(new Real2(x1, 0), new Real2(x1, YMAX));
		line.setStroke(stroke);
		line.setStrokeWidth(strokeWidth);
		g.appendChild(line);
	}

}
