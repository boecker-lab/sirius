/*
 *  This file is part of the SIRIUS Software for analyzing MS and MS/MS data
 *
 *  Copyright (C) 2013-2020 Kai Dührkop, Markus Fleischauer, Marcus Ludwig, Martin A. Hoffman, Fleming Kretschmer, Marvin Meusel and Sebastian Böcker,
 *  Chair of Bioinformatics, Friedrich-Schiller University.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Affero General Public License
 *  as published by the Free Software Foundation; either
 *  version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License along with SIRIUS.  If not, see <https://www.gnu.org/licenses/agpl-3.0.txt>
 */

package de.unijena.bioinf.ms.gui.ms_viewer;

import de.unijena.bioinf.ms.gui.configs.Colors;
import de.unijena.bioinf.ms.gui.configs.Fonts;
import de.unijena.bioinf.ms.gui.ms_viewer.data.MSViewerDataModel;
import de.unijena.bioinf.ms.gui.ms_viewer.data.MSViewerDataModelListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MSViewerPanel extends JPanel implements MouseMotionListener, MouseListener, ComponentListener, KeyListener, MSViewerDataModelListener {

	private static Logger logger = LoggerFactory.getLogger(MSViewerPanel.class);

	private MSViewerDataModel dataModel;

	private boolean overviewMode;

	private int firstVisibleIndex, lastVisibleIndex;

	private double minScaleInt, maxScaleInt;
	private double minScaleMass, maxScaleMass;

	private Font monoFont, titleFont, labelFont, detailFont;

	private int[]    peakPositions;
	private double[] peakInts;

	private int upperYBorder, lowerYBorder;

	private HashSet<Double> selectionMap;

	///// Data for data information

	private int peakIndex;

	private int dragStartPos, dragEndPos;
	private boolean dragged;
	private int pressXPos;

	private final Queue<MSViewerPanelListener> listeners;

	private final Lock paintLock = new ReentrantLock();

	///////////////////////////////////// Variablen fuer die Darstellung //////////////////////////////////////////

	private int sizeX, sizeY;

	private int yAxisHorizontalPos, yAxisVerticalEndPos,yAxisVerticalStartPos;

	private int xAxisHorizontalStartPos, xAxisHorizontalEndPos, xAxisVerticalPos;

	private int yPixelNumber;
	private double yAxisPixelPerIntVal;

	private int xPixelNumber;
	private double xAxisPixelPerMassVal;

	private boolean notInitialized;
	private boolean peakArrayNotInitialized;

	private void initBasicPositionParameter(int sizeX, int sizeY){
		this.sizeX = sizeX - 1;
		this.sizeY = sizeY - 1;

		this.yAxisHorizontalPos = 50;
		this.yAxisVerticalEndPos = sizeY-40;
		this.yAxisVerticalStartPos = 30;

		this.xAxisHorizontalStartPos = yAxisHorizontalPos;
		this.xAxisHorizontalEndPos = sizeX-10;
		this.xAxisVerticalPos = yAxisVerticalEndPos;

		this.yPixelNumber = yAxisVerticalEndPos - yAxisVerticalStartPos;
		this.xPixelNumber = xAxisHorizontalEndPos - xAxisHorizontalStartPos;

		this.notInitialized = false;
		this.peakArrayNotInitialized = true;

		lowerYBorder = yAxisVerticalEndPos;
		upperYBorder = yAxisVerticalStartPos;
	}

	private void initPeakPositionParameter(){
		double intDiff = this.maxScaleInt - this.minScaleInt;
		this.yAxisPixelPerIntVal = ((double) yPixelNumber) / intDiff;

		intDiff = this.maxScaleMass - this.minScaleMass;
		this.xAxisPixelPerMassVal = ((double) xPixelNumber) / intDiff;


	}

	private int getXPeakPosition(double mass){
		double visibleMass = mass - this.minScaleMass;
		int val = (int)(xAxisHorizontalStartPos + (xAxisPixelPerMassVal * visibleMass));
		return val;
	}

	private int getYPeakPosition(double intensity){
		double visibleIntensity = intensity - this.minScaleInt;

		return (int) (yAxisVerticalEndPos - (yAxisPixelPerIntVal * visibleIntensity));
	}

	private double getMassOfXPosition(int xPos){

		return (((double)(xPos - xAxisHorizontalStartPos)) / xAxisPixelPerMassVal)+this.minScaleMass;
	}

	///////////////////////////////////// Ende ////////////////////////////////////////////////////////////////////


	///////////////////////////////////// Variablen fuer Markieren ////////////////////////////////////////////////

	private boolean strgPressed;

	private List<Integer> markedList;

	private void initForMarking(){
		this.strgPressed = false;
		this.markedList  = new ArrayList<Integer>(50);
	}

	///////////////////////////////////// Ende ////////////////////////////////////////////////////////////////////

	boolean showPeakInfoOnlyForImportantPeaks;

	public MSViewerPanel(){

		this.dataModel = null;
		this.overviewMode = false;

		this.notInitialized = true;

		this.listeners = new ConcurrentLinkedQueue<>();
		this.selectionMap = new HashSet<Double>();

		this.showPeakInfoOnlyForImportantPeaks = false;
		this.peakIndex = -1;

		minScaleInt = 0.0;
		minScaleMass = 0.0;
		maxScaleMass = 1000.0;
		maxScaleInt = 0.0;

		dragStartPos = -1;
		dragEndPos = -1;
		dragged = false;
		pressXPos = -1;

		this.setPreferredSize(new Dimension(500, 500));

		monoFont = Fonts.FONT_MONO.deriveFont(12f);

		detailFont = Fonts.FONT_BOLD.deriveFont(10f);
		labelFont = Fonts.FONT_BOLD.deriveFont(12f);
		titleFont = Fonts.FONT_BOLD.deriveFont(24f);

		this.initColors();
		this.initForMarking();
//		this.initBasicPositionParameter(this.getSize().width, this.getSize().height);


		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addComponentListener(this);
		this.addKeyListener(this);


	}

	public void showPeakInfoOnlyForImportantPeaks(boolean val){
		this.showPeakInfoOnlyForImportantPeaks = val;
	}

	private Color innerBlue, outerBlue, plusZeroPeak, isotopePeak, defaultPeak, importantPeak, unimportantPeak, markedPeak, backgroundColor, defaultColor;

	private void initColors(){
		innerBlue = Colors.LIST_UNEVEN_BACKGROUND;    //infoBox innere Farbe
		outerBlue = Colors.FOREGROUND;     //infoBox aeussere Farbe

		plusZeroPeak = new Color(9,27,195);   //Farbe monoiso.Peak
		isotopePeak = Colors.ICON_BLUE;//new Color(86,174,108);    //Farbe Isotopenpeak

//		otherPeak = new Color(175,184,226);    //Farbe fuer unwichtige Peaks
		importantPeak = Colors.ICON_GREEN; //new Color(240,59,32	);           //Peak der hervorgehoben werden soll
//		importantPeak = new Color(168,0,255);
		markedPeak = new Color(254,178,76);       //Peak wurde mit der Maus markiert

		float[] c = new float[4];
		Colors.FOREGROUND.getComponents(c);
		unimportantPeak = new Color(c[0], c[1], c[2], 0.1f * c[3]); //Peak soll mehr oder weniger ausgeblendet werden
		defaultPeak = Colors.FOREGROUND;                //jeder Peak ohne spezielle Eigenschaft


//		noisePeak = new Color(175,184,226);
//		signalPeak = Color.BLACK;

		backgroundColor = Colors.BACKGROUND;         //Farbe fuer das Panel
		defaultColor = Colors.FOREGROUND;            //Grundfarbe fuer Achsen usw.
	}

	public void addMSViewerPanelListener(MSViewerPanelListener listener){
		listeners.add(listener);
	}

	public void removeMSViewerPanelListener(MSViewerPanelListener listener){
		listeners.remove(listener);
	}

	public void setSelectedPeaks(double[] masses){
		selectionMap.clear();
		for(double d : masses) selectionMap.add(d);
	}

	public void setData(MSViewerDataModel dataModel){
		try {
			paintLock.lock();
			this.dataModel = dataModel;
			this.firstVisibleIndex = 0;
			this.lastVisibleIndex = dataModel.getSize();
			this.setOverviewMode(false);
			this.setVisibleIndices(0, dataModel.getSize()-1);
			this.computeScale();
			this.notInitialized = true;
			this.peakArrayNotInitialized = true;
		} finally {
			paintLock.unlock();
		}
	}

	protected boolean isOverviewModeActive(){
		return this.overviewMode;
	}

	protected void setOverviewMode(boolean active){
		this.overviewMode = active;
	}

	protected void setVisibleIndices(int firstIndex, int lastIndex){
		this.firstVisibleIndex = Math.max(0, Math.min(this.dataModel.getSize()-1, firstIndex));
//		this.lastVisibleIndex  = Math.max(this.firstVisibleIndex ,Math.min(this.dataModel.getSize()-1,lastIndex));
		this.lastVisibleIndex = lastIndex;

//		System.out.println("visible Index: "+this.firstVisibleIndex+" "+this.lastVisibleIndex);
	}

	public void showZoomedView(int firstIndex, int lastIndex){
		this.setOverviewMode(false);
		this.setVisibleIndices(firstIndex, lastIndex);
		this.computeScale();
		this.initPeakPositionParameter();
		this.repaint();
		this.notInitialized = true;
		this.peakArrayNotInitialized = true;
	}

	public void showOverview(){
//		this.setOverviewMode(true);
		this.setOverviewMode(false);
		this.setVisibleIndices(0, dataModel.getSize()-1);
		computeScale();
		this.initPeakPositionParameter();
		this.repaint();
	}

	private void computeScale(){

		double minInt = 0d; double maxInt = 1d;
		final double minMass, maxMass;
		if (firstVisibleIndex > 0 || lastVisibleIndex < dataModel.getSize()-1) {
			minMass = Math.max(dataModel.getMass(firstVisibleIndex), dataModel.minMz());
			maxMass = Math.min(dataModel.maxMz(), dataModel.getMass(lastVisibleIndex));
		} else {
			minMass = dataModel.minMz();
			maxMass = dataModel.maxMz();
		}




		//calculate max intensity
		double temp = maxInt * 10.0;
		temp = Math.ceil(temp);
		maxScaleInt = temp / 10.0;

		//calculate min intensity
		minInt = Math.max(0,minInt-0.1); //damit der kleinste Peak nicht nur als Punkt gerendert wird...
		temp = minInt * 10.0;
		temp = Math.floor(temp);
		minScaleInt = temp / 10.0;

		//calculate max mass
		temp = Math.ceil(maxMass);

		maxScaleMass = (int) temp;

		double diff = maxMass - minMass;

		//calculate min mass
		if(overviewMode) minScaleMass = 0.0;
		else if(diff>50){

			temp = Math.floor(minMass);
			int tempInt = (int) temp;
			if(tempInt % 5 != 0){
				minScaleMass = (tempInt / 5) * 5.0; //Reihenfolge wegen integer und double wichtig !!!!
			}else{
				minScaleMass = Math.max(temp-5,0); //-5 weil sonst der erste Peak auf der y-Achse klebt
			}
		}else{
			minScaleMass = Math.floor(minMass)-1;
		}
		/*
		for (firstVisibleIndex = 0; firstVisibleIndex < dataModel.getSize(); ++firstVisibleIndex) {
		    if (dataModel.getMass(firstVisibleIndex) >= minMass) break;
        }
        for (lastVisibleIndex = dataModel.getSize()-1; lastVisibleIndex >= 0; --lastVisibleIndex) {
            if (dataModel.getMass(lastVisibleIndex) < maxMass) break;
        }
        */
        this.initPeakPositionParameter();


	}

	/*public MSViewerDataModel getDataModel(){
		return this.dataModel;
	}*/

	@Override
	public void paint(Graphics g){
		try {
			if (paintLock.tryLock()){
				if(notInitialized){
					this.initBasicPositionParameter(this.getSize().width, this.getSize().height);
					computeScale();
				}
				if(peakArrayNotInitialized){
					this.initPeakPositionArray();
				}

				Graphics2D g2 = (Graphics2D)g;

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

				this.paintBasics(g2);

				if(dragged) this.paintSelectionRect(g2);

				this.paintPeaks(g2);

				this.paintAxes(g2);
				if(this.peakIndex>=0 && !dragged) this.paintExtendedPeakInformation(g2);
			}
		} finally {
			paintLock.unlock();
		}

	}

	/**
	 *
	 * Hintergrundfarbe + Titel
	 *
	 * @param g2
	 */
	private void paintBasics(Graphics2D g2){
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(backgroundColor);
		g2.fillRect(0, 0, sizeX+1, sizeY+1);

		g2.setColor(defaultColor);

		g2.setFont(titleFont);

		FontMetrics fm = g2.getFontMetrics();

		int titleSizeX = fm.stringWidth(this.dataModel.getLabel());

		int titleStartPos = (sizeX - titleSizeX) / 2;

		g2.drawString(this.dataModel.getLabel(), titleStartPos, 25);
	}

	/**
	 *
	 * Die Achsen...
	 *
	 * @param g2
	 */
	private void paintAxes(Graphics2D g2){
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setFont(monoFont);
		g2.setColor(this.defaultColor);

		// y-Achse zeichnen

		g2.drawLine(yAxisHorizontalPos, yAxisVerticalStartPos, yAxisHorizontalPos, yAxisVerticalEndPos);

//		int startY = yAxisVerticalEndPos;

		for(double i = this.minScaleInt+0.1; i<=this.maxScaleInt; i+=0.1){

			//nervige Nachkommastellen entfernen
			i *= 10;
			i = Math.round(i);
			i /= 10;

//			int yPos = (int) (startY - i*yAxisPixelPerIntVal);

			int yPos = this.getYPeakPosition(i);

			g2.drawLine(yAxisHorizontalPos-5, yPos, yAxisHorizontalPos, yPos);

			String label = String.valueOf(i);

//			int stringX = fm.stringWidth(label);

			g2.drawString(label, 20 , yPos+5);

		}


		// x-Achse zeichnen

		g2.drawLine(xAxisHorizontalStartPos,xAxisVerticalPos,xAxisHorizontalEndPos,xAxisVerticalPos);

		//finde erste Position auf x-Achse + lege Schrittweite fest

		int stepSize;
		int firstLabelMass;

		double massDiff = maxScaleMass - minScaleMass;

		int minScaleMassInt = (int) this.minScaleMass;

		if(massDiff<=10){
			stepSize = 1;
			firstLabelMass = (int) (minScaleMassInt == this.minScaleMass ? this.minScaleMass+1 : Math.ceil(minScaleMass));
		}else if(massDiff>10 && massDiff<=50){
			stepSize = 5;

			int temp = ((int)Math.ceil(this.minScaleMass))/5 * 5;
			if(temp <= minScaleMassInt) firstLabelMass = temp + 5;
			else firstLabelMass = temp;

		}else if(massDiff>50 && massDiff<=100){
			stepSize = 10;

			int temp = ((int)Math.ceil(this.minScaleMass))/10 * 10;
			if(temp <= minScaleMassInt) firstLabelMass = temp + 10;
			else firstLabelMass = temp;

		}else if(massDiff>100 && massDiff<=250){
			stepSize = 25;

			int temp = ((int)Math.ceil(this.minScaleMass))/25 * 25;
			if(temp <= minScaleMassInt) firstLabelMass = temp + 25;
			else firstLabelMass = temp;

		}else if(massDiff>200 && massDiff<=500){
			stepSize = 50;

			int temp = ((int)Math.ceil(this.minScaleMass))/50 * 50;
			if(temp <= minScaleMassInt) firstLabelMass = temp + 50;
			else firstLabelMass = temp;

		}else{
			stepSize = 100;

			if(this.minScaleMass<100){
				firstLabelMass = 100;
			}else{
				int temp = (int)this.minScaleMass / 10 + 1;
				firstLabelMass = temp * 10;
			}
		}

		int startX = xAxisHorizontalStartPos;

//		int startMass = (int) this.minScaleMass;

		g2.setFont(this.monoFont);

		FontMetrics fm = g2.getFontMetrics();

		for(int i = firstLabelMass; i<=this.maxScaleMass; i+=stepSize){

//			int startOffset = i - startMass;

//			int xPos = (int) (startX + startOffset*xAxisPixelPerIntVal);

			int xPos = this.getXPeakPosition(i);

			g2.drawLine(xPos, xAxisVerticalPos, xPos, xAxisVerticalPos+5);

			String xLabel = String.valueOf(i);

			int xLabelSize = fm.stringWidth(xLabel);

			if(xLabelSize/2 + xPos >= sizeX){
				g2.drawString(xLabel, sizeX-xLabelSize-5, xAxisVerticalPos+20);
			}else{
				g2.drawString(xLabel, xPos-(xLabelSize/2), xAxisVerticalPos+20);
			}

		}

		// x-Achsen Label zeichnen

		g2.setFont(this.labelFont);
		String xAxisLabel = "mass/charge in Da";
		fm = g2.getFontMetrics();
		int labelSize = fm.stringWidth(xAxisLabel);
		g2.drawString(xAxisLabel, (int) (startX + (xPixelNumber/2.0) - (labelSize/2.0)), xAxisVerticalPos+35);

		// y-Achsen Label zeichnen

		g2.setFont(this.labelFont);
		fm = g2.getFontMetrics();
		String yLabel = "relative intensity";
		int yLabelLength = fm.stringWidth(yLabel);
		int startYPos = yAxisVerticalStartPos + (yPixelNumber/2) + (yLabelLength/2);

		g2.rotate(Math.toRadians(-90)); //Hinweis: Koordinatenachse um (0,0) gedreht. X,Y vertauscht, sichtbarer bereich bei -,+

		g2.setFont(this.labelFont);

		g2.drawString(yLabel,-startYPos,15);

		g2.rotate(Math.toRadians(90));

	}

	private void paintSelectionRect(Graphics2D g2){
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

		if(dragStartPos<=yAxisHorizontalPos) dragStartPos = yAxisHorizontalPos+1;
		if(dragEndPos>xAxisHorizontalEndPos) dragEndPos = xAxisHorizontalEndPos;

		g2.setColor(innerBlue);
		g2.fillRect(dragStartPos,yAxisVerticalStartPos,dragEndPos-dragStartPos,yAxisVerticalEndPos - yAxisVerticalStartPos - 1);
		g2.setColor(outerBlue);
		g2.drawRect(dragStartPos,yAxisVerticalStartPos,dragEndPos-dragStartPos,yAxisVerticalEndPos - yAxisVerticalStartPos - 1);
		g2.setColor(defaultColor);

	}

	private void initPeakPositionArray(){
		this.peakPositions = new int[sizeX];
		Arrays.fill(this.peakPositions, -1);

		peakInts = new double[sizeX];
		Arrays.fill(peakInts,Double.NEGATIVE_INFINITY);
		for(int i=this.firstVisibleIndex;i<=this.lastVisibleIndex;i++){
			double mass = this.dataModel.getMass(i);
			if (mass < dataModel.minMz() || mass > dataModel.maxMz())
			    continue;
			double relInt = this.dataModel.getRelativeIntensity(i);
			int peakXPos = this.getXPeakPosition(mass);
			if(relInt>peakInts[peakXPos]) {
				this.peakPositions[peakXPos] = i;
				peakInts[peakXPos] = relInt;
			}
		}
	}

	private void paintPeaks(Graphics2D g2){
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		final Stroke before = g2.getStroke();
		try {
			final Stroke largePeak = new BasicStroke(1.5f);
			final Stroke tinyPeak = new BasicStroke(1);
			final Stroke importantPeakStroke = new BasicStroke(2f);
			for(int i=this.firstVisibleIndex;i<=this.lastVisibleIndex;i++){

				double mass = this.dataModel.getMass(i);
                if (mass < dataModel.minMz() || mass > dataModel.maxMz())
                    continue;
				double intensity = this.dataModel.getRelativeIntensity(i);
				if (intensity >= 0.05) {
					g2.setStroke(largePeak);
				} else g2.setStroke(tinyPeak);

				if(this.dataModel.isMarked(i)){
					g2.setColor(markedPeak);
				}else {
					if(this.dataModel.isImportantPeak(i)){
						if (this.dataModel.isIsotope(i)) {
							g2.setColor(isotopePeak);
						} else {
							g2.setColor(importantPeak);
						}
						g2.setStroke(importantPeakStroke);
					}else if(this.dataModel.isPlusZeroPeak(i)){
						g2.setColor(plusZeroPeak);
					}else if(this.dataModel.isIsotope(i)){
						g2.setColor(isotopePeak);
					}else if(this.dataModel.isUnimportantPeak(i)){
						g2.setColor(unimportantPeak);
					}else{
						g2.setColor(defaultPeak);
					}}

				int peakXPos = this.getXPeakPosition(mass);
				int peakYPos = this.getYPeakPosition(intensity);

				g2.drawLine(peakXPos,yAxisVerticalEndPos,peakXPos,peakYPos);
			}
		} finally {
			g2.setStroke(before);
		}
	}

	private void paintExtendedPeakInformation(Graphics2D g2){
		try {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setFont(detailFont);
			FontMetrics fm = g2.getFontMetrics();

			String mfLabel = "molecular formula:";
			String relIntLabel = "rel. intensity:";
			String massLabel   = "mass/charge:";
//		String snLabel     = "signal/noise:";
			String absIntLabel = "abs. intensity:";
			String ionLabel   = "ionization:";

			double mass = this.dataModel.getMass(this.peakIndex);
			double absInt = this.dataModel.getAbsoluteIntensity(this.peakIndex);
			double relInt = this.dataModel.getRelativeIntensity(this.peakIndex);
			String mfVal = this.dataModel.getMolecularFormula(this.peakIndex);
//		double sn = this.dataModel.getSignalNoise(this.peakIndex);
			String ionVal = this.dataModel.getIonization(this.peakIndex);

			String relIntVal  = String.valueOf(relInt);
			String absIntVal  = absInt > 0 ? String.valueOf(absInt) : "unknown";
			String massVal = String.valueOf(mass);
//		String snVal   = sn > 0 ? String.valueOf(sn) : "unknown";

			int relIntLabelLength = fm.stringWidth(relIntLabel);
			int absIntLabelLength = fm.stringWidth(absIntLabel);
			int massLabelLength   = fm.stringWidth(massLabel);
//		int snLabelLength     = fm.stringWidth(snLabel);
			int mfLabelLength     = mfVal==null ? 0 : fm.stringWidth(mfLabel);
			int ionLabelLength	  = ionVal==null ? 0 : fm.stringWidth(ionLabel);

			int relIntValLength = fm.stringWidth(relIntVal);
			int absIntValLength = fm.stringWidth(absIntVal);
			int massValLength   = fm.stringWidth(massVal);
//		int snValLength     = fm.stringWidth(snVal);
			int mfValLength     = mfVal==null ? 0 : fm.stringWidth(mfVal);
			int ionValLength    = ionVal==null ? 0 : fm.stringWidth(ionVal);

			int maxFirstLength  = Math.max(ionLabelLength, Math.max(absIntLabelLength,Math.max(Math.max(relIntLabelLength,massLabelLength),mfLabelLength)));
			int maxSecondLength = Math.max(ionValLength, Math.max(absIntValLength,Math.max(Math.max(relIntValLength,massValLength),mfValLength)));
//		int maxFirstLength  = Math.max(absIntLabelLength,Math.max(Math.max(relIntLabelLength,massLabelLength),snLabelLength));
//		int maxSecondLength = Math.max(absIntValLength,Math.max(Math.max(relIntValLength,massValLength),snValLength));


			boolean noExtendedInfo = mfVal == null || ionVal == null;

			int totalHorizontalSize = maxFirstLength + maxSecondLength + 20;
			int totalVerticalSize = noExtendedInfo ? 50 : 80;

			int leftX = 0;
			int leftY = 0;

			int selectedPeakX = this.getXPeakPosition(mass);
			int selectedPeakY = this.getYPeakPosition(relInt);

			if(selectedPeakX + (totalHorizontalSize/2) + 5 >= sizeX){
				leftX = sizeX - totalHorizontalSize - 5;
			}else if(selectedPeakX - (totalHorizontalSize/2) - 5 <= 0){
				leftX = 5;
			}else{
				leftX = selectedPeakX - (totalHorizontalSize/2);
			}

			if(selectedPeakY - totalVerticalSize - 5 < yAxisVerticalStartPos){
				leftY = yAxisVerticalStartPos;
			}else{
				leftY = selectedPeakY - totalVerticalSize - 5;
			}

			g2.setColor(innerBlue);
			g2.fillRect(leftX, leftY, totalHorizontalSize, totalVerticalSize);
			g2.setColor(outerBlue);
			g2.drawRect(leftX, leftY, totalHorizontalSize, totalVerticalSize);
			g2.setColor(defaultColor);

			int firstColumnX = leftX + 5;

			int secondColumnX = leftX + 15 + maxFirstLength;

			if (noExtendedInfo) {
				g2.drawString(massLabel, firstColumnX, leftY + 15);
				g2.drawString(relIntLabel, firstColumnX, leftY + 30);
				g2.drawString(absIntLabel, firstColumnX, leftY + 45);

				g2.drawString(massVal, secondColumnX, leftY + 15);
				g2.drawString(relIntVal, secondColumnX, leftY + 30);
				g2.drawString(absIntVal, secondColumnX, leftY + 45);
			} else {
				g2.drawString(mfLabel, firstColumnX, leftY + 15);
				g2.drawString(massLabel, firstColumnX, leftY + 30);
				g2.drawString(relIntLabel, firstColumnX, leftY+45);
				g2.drawString(absIntLabel, firstColumnX, leftY+60);
				g2.drawString(ionLabel, firstColumnX, leftY+75);

				g2.drawString(mfVal,secondColumnX,leftY+15);
				g2.drawString(massVal,secondColumnX,leftY+30);
				g2.drawString(relIntVal,secondColumnX,leftY+45);
				g2.drawString(absIntVal,secondColumnX,leftY+60);
				g2.drawString(ionVal,secondColumnX,leftY+75);
			}


//		g2.drawString(relIntLabel, firstColumnX, leftY+15);
//		g2.drawString(absIntLabel, firstColumnX, leftY+30);
//		g2.drawString(massLabel, firstColumnX, leftY+45);
//		g2.drawString(snLabel, firstColumnX, leftY+60);

//		int secondColumnX = leftX+15+maxFirstLength;

//		g2.drawString(relIntVal,secondColumnX,leftY+15);
//		g2.drawString(absIntVal,secondColumnX,leftY+30);
//		g2.drawString(massVal,secondColumnX,leftY+45);
//		g2.drawString(snVal,secondColumnX,leftY+60);
		} catch (Exception e) {//todo this is a hotfix to not crash the GUI when Index out of bound occurs
			LoggerFactory.getLogger(getClass()).error("Error when painting spectrum", e);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		dragged = true;

		if(e.getX()<pressXPos){
			dragStartPos = e.getX();
		}else{
			dragEndPos = e.getX();
		}

//		if(e.getX()<dragEndPos){
//			dragStartPos = e.getX();
//		}else{
//			dragEndPos = e.getX();
//		}
		this.repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {

		int xPos = e.getX();
		int yPos = e.getY();

		if(yPos>lowerYBorder || yPos<upperYBorder){ // Verwirrung ... (0,0) ist links oben, lower ist daher ueber upper
			this.peakIndex = -1;
			this.repaint();
			return;
		}

		int indexWithMaxInt = -1;
		double maxRelativeInt = -1;

		int maxHorPos = Math.min(xPos+5,this.peakPositions.length-1);

		boolean relativeIntensitiesPresent = false;
		for(int i = Math.max(xPos-5,0);i<=maxHorPos;i++){
			if(this.peakPositions[i]>=0){
				if(this.dataModel.getRelativeIntensity(peakPositions[i])>0){
					relativeIntensitiesPresent = true;
					break;
				}
			}
		}

		//falls vorhanden nutze rel. Intensitaeten
		if(relativeIntensitiesPresent){
//			System.out.println("-------------");
			for(int i = Math.max(xPos-5,0);i<=maxHorPos;i++){

				if(this.peakPositions[i]>=0){
//					System.out.println(this.dataModel.getMass(peakPositions[i])+" "+this.dataModel.getRelativeIntensity(peakPositions[i])+" "+this.dataModel.getIntensity(peakPositions[i]));
					if(this.dataModel.getRelativeIntensity(peakPositions[i])>maxRelativeInt){
						indexWithMaxInt = peakPositions[i];
						maxRelativeInt = this.dataModel.getRelativeIntensity(peakPositions[i]);
					}
				}

			}

//			if(indexWithMaxInt>=0){
//				System.out.println("max: "+this.dataModel.getMass(indexWithMaxInt)+" "+this.dataModel.getRelativeIntensity(indexWithMaxInt));
//			}else{
//				System.out.println("nichts gefunden");
//			}
		}else{
			//ansonsten halte dich an die absoluten (eins von beiden muss gesetzt sein...)
			for(int i = Math.max(xPos-5,0);i<=maxHorPos;i++){

				if(this.peakPositions[i]>=0){
					if(this.dataModel.getAbsoluteIntensity(peakPositions[i])>indexWithMaxInt){
						indexWithMaxInt = peakPositions[i];
						maxRelativeInt = this.dataModel.getAbsoluteIntensity(peakPositions[i]);
					}
				}

			}
		}


		if(maxRelativeInt<=0){
			this.peakIndex = -1;
			this.repaint();
		}else{
			boolean isImportant = this.dataModel.isImportantPeak(indexWithMaxInt) || this.dataModel.isIsotope(indexWithMaxInt) || maxRelativeInt>(maxScaleInt/20);
			if(isImportant){
				this.peakIndex = indexWithMaxInt;
				this.repaint();
			}
//			System.out.println("repaint "+this.peakIndex);
		}


	}

	private int getPeakIndex(int xPos){

		int minIndex = Math.max(xPos-5, 0);
		int maxIndex = Math.min(xPos+5,this.peakPositions.length-1);

		int maxPos    = peakPositions[minIndex];
		double maxInt = peakInts[minIndex];

		for(int i=minIndex+1;i<=maxIndex;i++){
			if(peakInts[i]>maxInt){
				maxInt = peakInts[i];
				maxPos = peakPositions[i];
			}
		}

		return maxPos;

//		int pIndex;
//
//		if(this.peakPositions[xPos] > -1){
//			pIndex = this.peakPositions[xPos];
//		}else{
//			pIndex = -1;
//			for(int i=1;i<=5;i++){
//
//				int leftPeakPos = Math.max(xPos-i,0);
//				int rightPeakPos = Math.min(xPos+i,this.peakPositions.length-1);
//
//				if(this.peakPositions[leftPeakPos]>-1){
//					pIndex = this.peakPositions[leftPeakPos];
//					break;
//				}else if(this.peakPositions[rightPeakPos]>-1){
//					pIndex = this.peakPositions[rightPeakPos];
//					break;
//				}
//			}
//
//		}
//
//		return pIndex;
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		int posX = e.getX();

		int pIndex = getPeakIndex(posX);

		if(pIndex>-1){

			if(this.markedList.size()==1 && this.markedList.get(0)==pIndex){
				this.markedList.clear();
				for(MSViewerPanelListener listener : listeners){
					listener.markingsRemoved();
				}
			}else{
				if(pIndex>-1){
					if(markedList.contains(pIndex)) markedList.remove(markedList.indexOf(pIndex));
					else markedList.add(pIndex);
					for(MSViewerPanelListener listener : listeners){
						listener.peaksMarked(markedList);
					}
				}
			}

		}else{
			if(!this.strgPressed){
				this.markedList.clear();
				for(MSViewerPanelListener listener : listeners){
					listener.markingsRemoved();
				}
			}
		}

	}


	@Override
	public void mouseEntered(MouseEvent e) {

	}


	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}


	@Override
	public void mousePressed(MouseEvent e) {

		if(e.isPopupTrigger()) return;

		dragStartPos = e.getX();
		dragEndPos = e.getX();
		pressXPos = e.getX();
		if(!this.strgPressed){

			if(this.markedList.size()==1){
				int index = this.getPeakIndex(e.getX());
				if(index == markedList.get(0)) return;
			}

			this.markedList.clear();
			for(MSViewerPanelListener listener : listeners){
				listener.markingsRemoved();
			}
		}
	}


	@Override
	public void mouseReleased(MouseEvent e) {

		if(e.isPopupTrigger()) return;

		if(dragged){
			pressXPos = -1;

			double startMass = this.getMassOfXPosition(dragStartPos);
			double endMass = this.getMassOfXPosition(dragEndPos);

			dragStartPos = -1;
			dragEndPos = -1;
			dragged = false;

			if(!this.strgPressed) this.markedList = new ArrayList<Integer>(50);

			for(int i=0;i<this.dataModel.getSize();i++){
				double mass = this.dataModel.getMass(i);
				if(mass>=startMass && mass <=endMass) markedList.add(i);
			}

			List<Integer> unmodList = Collections.unmodifiableList(markedList);
			if(markedList.size()>0){
				for(MSViewerPanelListener listener : listeners){
					if(this.strgPressed) listener.peaksMarked(unmodList);
					else listener.peaksMarkedPerDrag(unmodList);
				}
			}


			this.repaint();
//			for(MSViewerPanelListener listener : listeners) listener.
		}
	}

	@Override
	public void componentHidden(ComponentEvent e) {

	}

	@Override
	public void componentMoved(ComponentEvent e) {
//		System.out.println("componentMoved "+e.paramString());
		this.initBasicPositionParameter(this.getSize().width, this.getSize().height);
		computeScale();
		this.peakArrayNotInitialized = true;
	}

	@Override
	public void componentResized(ComponentEvent e) {
//		System.out.println("new Size "+this.getSize().getWidth()+" "+this.getHeight());
		this.initBasicPositionParameter(this.getSize().width, this.getSize().height);
        computeScale();
		this.peakArrayNotInitialized = true;
	}

	@Override
	public void componentShown(ComponentEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()==17){
			this.strgPressed = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode()==17){
			this.strgPressed = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void signalNoiseInformationChanged() {
		this.repaint();
	}

	@Override
	public void markedInformationChanged() {
		this.repaint();
	}

	@Override
	public void spectrumChanged() {
		this.setData(dataModel);
		this.repaint();
	}


}
