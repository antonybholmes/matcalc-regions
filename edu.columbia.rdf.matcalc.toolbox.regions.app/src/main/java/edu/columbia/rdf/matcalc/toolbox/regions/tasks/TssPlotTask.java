package edu.columbia.rdf.matcalc.toolbox.regions.tasks;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import org.jebtk.bioinformatics.gapsearch.BinarySearch;
import org.jebtk.bioinformatics.genomic.Chromosome;
import org.jebtk.bioinformatics.genomic.GenomicRegion;
import org.jebtk.core.Mathematics;
import org.jebtk.core.io.Io;
import org.jebtk.core.text.TextUtils;
import org.jebtk.graphplot.figure.Figure;
import org.jebtk.graphplot.figure.PlotStyle;
import org.jebtk.graphplot.plotbox.PlotBoxGridLayout;
import org.jebtk.graphplot.plotbox.PlotBoxGridStorage;
import org.jebtk.math.matrix.DataFrame;
import org.jebtk.math.statistics.HistBin;
import org.jebtk.math.statistics.Statistics;

import edu.columbia.rdf.matcalc.MainMatCalcWindow;
import edu.columbia.rdf.matcalc.bio.AnnotationGene;
import edu.columbia.rdf.matcalc.figure.graph2d.Graph2dWindow;
import edu.columbia.rdf.matcalc.toolbox.regions.plot.tss.Log10TssSubFigure;
import edu.columbia.rdf.matcalc.toolbox.regions.plot.tss.TssSubFigure;

/**
 * Overlap segments.
 * 
 * @author Antony Holmes Holmes
 *
 */
public class TssPlotTask extends SwingWorker<Void, Void> {

	private BinarySearch<AnnotationGene> mTssSearch;
	private double mStart;
	private double mEnd;
	private int mUnits;
	private double mBinSize;
	private int mBinUnits;
	private MainMatCalcWindow mWindow;

	public TssPlotTask(MainMatCalcWindow window, 
			BinarySearch<AnnotationGene> tssSearch,
			double start,
			double end,
			int units,
			double binSize,
			int binUnits) {
		mWindow = window;
		mTssSearch = tssSearch;
		mStart = start;
		mEnd = end;
		mUnits = units;
		mBinSize = Math.abs(binSize);
		mBinUnits = binUnits;
	}

	@Override
	public Void doInBackground() {
		try {
			plot();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public void plot() throws ParseException {
		DataFrame matrix = mWindow.getCurrentMatrix();
		
		List<Double> tssPoints = new ArrayList<Double>();

		for (int i = 0; i < matrix.getRowCount(); ++i) {
			GenomicRegion region = null;

			if (Io.isEmptyLine(matrix.getText(i, 0))) {
				continue;
			} else if (matrix.getText(i, 0).contains(TextUtils.NA)) {
				continue;
			} else if (GenomicRegion.isGenomicRegion(matrix.getText(i, 0))) {
				region = GenomicRegion.parse(matrix.getText(i, 0));
			} else {
				// three column format

				region = new GenomicRegion(Chromosome.parse(matrix.getText(i, 0)),
						TextUtils.parseInt(matrix.getText(i, 1)),
						TextUtils.parseInt(matrix.getText(i, 2)));
			}

			//System.err.println("region: " + region);

			GenomicRegion midPoint = GenomicRegion.midRegion(region);

			// Find Gene TSS near the midpoint
			List<AnnotationGene> results = 
					mTssSearch.getClosestFeatures(midPoint);

			if (results != null) {
				double tss = Double.MAX_VALUE;

				for (AnnotationGene gene : results) {
					tss = Math.min(tss, AnnotationGene.getTssMidDist(gene, midPoint.getStart()));
				}

				tssPoints.add(tss);
			}
		}

		plot(mWindow,
				tssPoints,
				mStart,
				mEnd,
				mUnits,
				mBinSize,
				mBinUnits);
	}
	
	public static void plot(MainMatCalcWindow window,
			List<Double> tssPoints,
			double start,
			double end,
			int units,
			double binSize,
			int binUnits) {

		// Nearest plot

		List<Double> plotTssPoints = new ArrayList<Double>();

		double s = start * units;
		double e = end * units;

		System.err.println("s " + s + " " + e + " " + (binSize * binUnits / units));

		for (double x : tssPoints) {
			if (x < s || x > e) {
				continue;
			}

			plotTssPoints.add(x / units);
		}

		// Convert to histograms
		List<HistBin> tssHist = Statistics.histogram(plotTssPoints, 
				start, 
				end, 
				binSize * binUnits / units);


		TssSubFigure tssCanvas = new TssSubFigure("TSS", 
				"TSS", 
				tssHist,
				start,
				end,
				Math.pow(10, Math.floor(Math.log10(Math.abs(start)))));


		List<Double> log10TssPoints = new ArrayList<Double>();

		for (double x : tssPoints) {
			double v = Mathematics.log10(Math.max(1, Math.abs(x)));

			if (v <= 8) {
				log10TssPoints.add(v);
			}
		}

		List<HistBin> log10TssHist = 
				Statistics.histogram(log10TssPoints, 0, 8, 0.1);

		Log10TssSubFigure tssLogCanvas = 
				new Log10TssSubFigure("TSS", "TSS", log10TssHist);

		Figure figure = new Figure("TSS Figure", new PlotBoxGridStorage(1, 2), new PlotBoxGridLayout(1, 2));
		figure.addChild(tssCanvas, 0, 0);
		figure.addChild(tssLogCanvas, 0, 1);

		Graph2dWindow plotWindow = new Graph2dWindow(window, figure);

		plotWindow.getStyle().set(PlotStyle.JOINED_BARS);


		plotWindow.setVisible(true);
	}
}