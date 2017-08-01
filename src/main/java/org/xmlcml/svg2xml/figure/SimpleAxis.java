package org.xmlcml.svg2xml.figure;

import org.xmlcml.euclid.Angle;
import org.xmlcml.euclid.Real;
import org.xmlcml.euclid.Real2;
import org.xmlcml.euclid.Transform2;
import org.xmlcml.graphics.svg.GraphicsElement;
import org.xmlcml.graphics.svg.SVGG;
import org.xmlcml.graphics.svg.SVGLine;
import org.xmlcml.graphics.svg.SVGText;
import org.xmlcml.graphics.svg.linestuff.ComplexLine.LineOrientation;

/** support routines for building Axis for tests.
 * 
 * <p>Might get integrated into paths.Axis </p>
 * 
 * @author pm286
 *
 */
public class SimpleAxis {

	private static final Double EPS = 0.1;
	
	private LineOrientation lineOrientation = LineOrientation.HORIZONTAL;
	private Integer nsteps = null;
	private Double stepDelta = null;
	private Double axisMin = 0.;
	private Double axisMax = null;
	private Double axisConstant = 50.;
	private Double tickLen = 20.;
	private Integer tickSign = 1;
	private Double tickLabelMin = 0.;
	private Double tickLabelDelta = 10.;
	private String label;
	private boolean rotateVerticalLabel = false;
	private double labelFontSize = 10;

	private double fontSizeFactor = 0.4; // this is a pure guess
	private double labelMargin = 3.0;

	private double verticalLabelOffset = 1.0;
	private boolean rotateVerticalCharacters = true;;
	
	public SimpleAxis(LineOrientation lineOrientation) {
		this.lineOrientation = lineOrientation;
	}
	
	public SVGG createAxis() {
		if (!createStepValues()) {
			return null;
		}
		SVGG g = new SVGG();
		
		SVGLine mainAxis = createMainAxis();
		g.appendChild(mainAxis);
	    Double currentPos = axisMin;
	    Double currentTickLabelPos = tickLabelMin;
	    Real2 startPoint = null;
	    SVGLine tick = null;
		for (int i = 0; i <= nsteps; i++) {
			tick = createTick(currentPos);
			if (startPoint == null) {
				startPoint = new Real2(tick.getXY(0));
			}
			g.appendChild(tick);
			GraphicsElement tickLabel = createTickLabel(tick, currentTickLabelPos);
			g.appendChild(tickLabel);
			currentPos += stepDelta;
			currentTickLabelPos += tickLabelDelta;
		}
		if (label != null) {
			Real2 endPoint = new Real2(tick.getXY(0));
			Real2 midPoint = startPoint.getMidPoint(endPoint);
			Real2 labelStart = null;
			double stringLength = label.length() * labelFontSize; 
			if (LineOrientation.VERTICAL.equals(lineOrientation)) {
				if (rotateVerticalLabel) {
					double ylen = stringLength + fontSizeFactor; // monospace crude
					double x = midPoint.getX();
					double y = midPoint.getY() - ylen /2.0;
					double ydelta = ylen / label.length();
//					int start = (rotateVerticalCharacters) ? label.length() : 0;
//					int end = (rotateVerticalCharacters) ? 0 : label.length();
//					int sign = (rotateVerticalCharacters) ? -1 : 1;  
					for (int i = 0; i < label.length(); i++) {
						Real2 xy = new Real2(x - verticalLabelOffset  * labelFontSize , y + i * ydelta /* * sign*/);
						int charIndex = (rotateVerticalCharacters) ? label.length() - i - 1 : i;
						SVGText ch = new SVGText(xy, String.valueOf(label.charAt(charIndex)));
						if (rotateVerticalCharacters) {
							Transform2 t2 = Transform2.getRotationAboutPoint(new Angle(Math.PI / 2.), xy);
							ch.setTransform(t2);
						}
						ch.setFontSize(labelFontSize);
						g.appendChild(ch);
					}
				} else {
					labelStart = midPoint.subtract(new Real2((stringLength + (labelMargin * labelFontSize)) * fontSizeFactor, 0.));
				}
			} else {
				labelStart = midPoint.subtract(new Real2(fontSizeFactor * stringLength / 2., -3.*tickLen));
			}
			if (!rotateVerticalLabel) {
				GraphicsElement labelText = new SVGText(labelStart, label);
				labelText.setFontSize(labelFontSize);
				g.appendChild(labelText);
			}
		}
		return g;
	}

	public boolean isRotateVerticalCharacters() {
		return rotateVerticalCharacters;
	}

	public void setRotateVerticalCharacters(boolean rotateVerticalCharacters) {
		this.rotateVerticalCharacters = rotateVerticalCharacters;
	}

	private boolean createStepValues() {
		if (nsteps == null) {
			nsteps = createNSteps();
		} else if (axisMax == null) {
			axisMax = createAxisMax();
		} else if (stepDelta == null) {
			stepDelta = createStepDelta();
		}
		return checkStepValues();
	}

	private boolean checkStepValues() {
		boolean badValues = nsteps == null || nsteps == 0 ||
			stepDelta == null || Real.isZero(stepDelta, EPS) ||
			axisMax == null || Real.isEqual(axisMin,  axisMax, EPS);
		return !badValues;
	}

	private Double createStepDelta() {
		if (nsteps != null && nsteps > 0 && axisMax != null) {
			stepDelta = (axisMax - axisMin) / nsteps;
		}
		return stepDelta;
	}

	private Integer createNSteps() {
		if (axisMax != null && stepDelta != null && stepDelta != 0) {
			nsteps = (int) ((axisMax - axisMin) / stepDelta);
		}
		return nsteps;
	}

	private Double createAxisMax() {
		if (nsteps != null && stepDelta != null) {
			axisMax = axisMin + nsteps * stepDelta;
		}
		return axisMax;
	}

	private GraphicsElement createTickLabel(SVGLine tick, Double currentLabel) {
		GraphicsElement text = null;
		Real2 pos0 = tick.getXY(0);
		Real2 pos1 = tick.getXY(1);
		Real2 offset = pos1.subtract(pos0);
		Real2 pos2 = pos1.plus(offset);
		String label = String.valueOf(currentLabel);
		text = new SVGText(pos2, label);
		text.setFontSize(10.);
		return text;
	}

	private SVGLine createTick(Double currentPos) {
		SVGLine tick = null;
		Double tick0 = axisConstant;
		Double tick1 = axisConstant + (tickSign) * tickLen;
		if (LineOrientation.HORIZONTAL.equals(lineOrientation)) {
			tick = new SVGLine(new Real2(currentPos, tick0), new Real2(currentPos, tick1));
		} else {
			tick = new SVGLine(new Real2(tick0, currentPos), new Real2(tick1, currentPos));
		}
		return tick;
	}

	private SVGLine createMainAxis() {
		SVGLine mainAxis = null;
		if (LineOrientation.HORIZONTAL.equals(lineOrientation)) {
			mainAxis = new SVGLine(new Real2(axisMin, axisConstant), new Real2(axisMax, axisConstant));
		} else {
			mainAxis = new SVGLine(new Real2(axisConstant, axisMin), new Real2(axisConstant, axisMax));
		}
		return mainAxis;
	}

	public LineOrientation getLineOrientation() {
		return lineOrientation;
	}

	public void setLineOrientation(LineOrientation lineOrientation) {
		this.lineOrientation = lineOrientation;
	}

	public int getNsteps() {
		return nsteps;
	}

	public void setNsteps(int nsteps) {
		this.nsteps = nsteps;
	}

	public double getDelta() {
		return stepDelta;
	}

	public void setDelta(double delta) {
		this.stepDelta = delta;
	}

	public double getAxisMin() {
		return axisMin;
	}

	public void setAxisMin(double axisMin) {
		this.axisMin = axisMin;
	}

	public double getAxisMax() {
		return axisMax;
	}

	public void setAxisMax(double axisMax) {
		this.axisMax = axisMax;
	}

	public double getAxisConstant() {
		return axisConstant;
	}

	public void setAxisConstant(double axisConstant) {
		this.axisConstant = axisConstant;
	}

	public double getTickLen() {
		return tickLen;
	}

	public void setTickLen(double tickLen) {
		this.tickLen = tickLen;
	}

	public int getTickSign() {
		return tickSign;
	}

	public void setTickSign(int tickSign) {
		this.tickSign = tickSign;
	}

	public double getTickLabelMin() {
		return tickLabelMin;
	}

	public void setTickLabelMin(double tickLabelMin) {
		this.tickLabelMin = tickLabelMin;
	}

	public double getTickLabelDelta() {
		return tickLabelDelta;
	}

	public void setTickLabelDelta(double tickLabelDelta) {
		this.tickLabelDelta = tickLabelDelta;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isRotateLabel() {
		return rotateVerticalLabel;
	}

	public void setRotateLabel(boolean rotateLabel) {
		this.rotateVerticalLabel = rotateLabel;
	}

	public double getLabelFontSize() {
		return labelFontSize;
	}

	public void setLabelFontSize(double labelFontSize) {
		this.labelFontSize = labelFontSize;
	}


}
