package edu.columbia.rdf.matcalc.toolbox.regions.tasks;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import org.jebtk.bioinformatics.gapsearch.BinaryGapSearch;
import org.jebtk.bioinformatics.genomic.Chromosome;
import org.jebtk.bioinformatics.genomic.GenomicRegion;
import org.jebtk.core.io.Io;
import org.jebtk.core.io.PathUtils;
import org.jebtk.core.text.TextUtils;
import org.jebtk.math.matrix.DataFrame;

import edu.columbia.rdf.matcalc.MainMatCalcWindow;
import edu.columbia.rdf.matcalc.bio.Annotation;

/**
 * Overlap segments.
 * 
 * @author Antony Holmes Holmes
 *
 */
public class ClosestTask extends SwingWorker<Void, Void> {

	private BinaryGapSearch<Annotation> mGappedSearch;
	private DataFrame mNewModel;
	private Path mFile2;
	private MainMatCalcWindow mWindow;

	public ClosestTask(MainMatCalcWindow window,
			BinaryGapSearch<Annotation> gappedSearch, 
			Path file2) {
		mWindow = window;
		mGappedSearch = gappedSearch;
		mFile2 = file2;
	}

	@Override
	public Void doInBackground() {
		try {
			mNewModel = closest();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void done() {
		if (mNewModel != null) {
			mWindow.addToHistory("Closest from " + PathUtils.getName(mFile2), mNewModel);
		}
	}

	private DataFrame closest() throws Exception {
		DataFrame m = mWindow.getCurrentMatrix();

		DataFrame matrix = DataFrame.createDataFrame(m.getRowCount(), m.getColumnCount() + 3);

		matrix.setColumnName(m.getColumnCount(), "Closest Feature");
		matrix.setColumnName(m.getColumnCount() + 1, "Closest Feature Location");
		matrix.setColumnName(m.getColumnCount() + 2, "Closest Feature Distance (bp)");

		DataFrame.copyColumnAnnotations(m, matrix);
		matrix.copyRows(m, 0, m.getRowCount() - 1);

		int c = m.getColumnCount();

		for (int i = 0; i < m.getRowCount(); ++i) {
			GenomicRegion region = null;

			if (Io.isEmptyLine(m.getText(i, 0))) {
				region = null;
			} else if (m.getText(i, 0).contains(TextUtils.NA)) {
				region = null;
			} else if (GenomicRegion.isGenomicRegion(m.getText(i, 0))) {
				region = GenomicRegion.parse(m.getText(i, 0));
			} else if (isThreeColumnGenomicLocation(m, i)) {
				// three column format

				region = new GenomicRegion(Chromosome.parse(m.getText(i, 0)),
						TextUtils.parseInt(m.getText(i, 1)),
						TextUtils.parseInt(m.getText(i, 2)));
			} else {
				region = null;
			}

			List<Annotation> results = 
					mGappedSearch.getClosestFeatures(region);

			List<String> names = new ArrayList<String>();
			List<String> regions = new ArrayList<String>();
			List<Integer> distances = new ArrayList<Integer>();

			for (Annotation annotation : results) {
				names.add(annotation.getName());
				regions.add(annotation.getRegion().getLocation());
				distances.add(GenomicRegion.midDist(annotation.getRegion(), region));
			}

			if (names.size() > 0) {
				matrix.set(i, c, TextUtils.scJoin(names));
				matrix.set(i, c + 1, TextUtils.scJoin(regions));
				matrix.set(i, c + 2, TextUtils.scJoin(distances));
			} else {
				matrix.set(i, c, TextUtils.NA);
				matrix.set(i, c + 1, TextUtils.NA);
				matrix.set(i, c + 2, TextUtils.NA);
			}


		}

		return matrix;
	}

	private static boolean isThreeColumnGenomicLocation(DataFrame m, int row) {
		if (!GenomicRegion.isChr(m.getText(row, 0))) {
			return false;
		}

		if (!TextUtils.isNumber(m.getText(row, 1))) {
			return false;
		}

		if (!TextUtils.isNumber(m.getText(row, 2))) {
			return false;
		}

		return true;
	}
}
