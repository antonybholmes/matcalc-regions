package edu.columbia.rdf.matcalc.toolbox.regions;

import java.text.ParseException;

import javax.swing.Box;

import org.jebtk.modern.UI;
import org.jebtk.modern.combobox.ModernComboBox;
import org.jebtk.modern.dialog.ModernDialogTaskWindow;
import org.jebtk.modern.panel.HExpandBox;
import org.jebtk.modern.panel.HSpacedBox;
import org.jebtk.modern.panel.VBox;
import org.jebtk.modern.spinner.ModernCompactSpinner;
import org.jebtk.modern.text.ModernAutoSizeLabel;
import org.jebtk.modern.tree.ModernCheckTreeMode;
import org.jebtk.modern.widget.ModernWidget;
import org.jebtk.modern.window.ModernWindow;
import org.jebtk.modern.window.WindowWidgetFocusEvents;

import edu.columbia.rdf.matcalc.bio.GenomesPanel;

/**
 * Control which conservation scores are shown.
 * 
 * @author Antony Holmes Holmes
 *
 */
public class TssPlotDialog extends ModernDialogTaskWindow {
  private static final long serialVersionUID = 1L;

  private ModernCompactSpinner mFieldStart = new ModernCompactSpinner(-100, -1,
      -4, 1, false);

  private ModernCompactSpinner mFieldEnd = new ModernCompactSpinner(1, 100, 4,
      1, false);

  private ModernCompactSpinner mFieldBin = new ModernCompactSpinner(1, 1000,
      100, 1, false);

  private ModernComboBox mUnitsCombo = new UnitsComboBox();

  private ModernComboBox mBinUnitsCombo = new UnitsComboBox();

  private GenomesPanel mGenomesPanel = new GenomesPanel(
      ModernCheckTreeMode.RADIO);

  public TssPlotDialog(ModernWindow parent) {
    super(parent);

    setTitle("Plot Options");

    createUi();

    setup();
  }

  private void setup() {
    addWindowListener(new WindowWidgetFocusEvents(mOkButton));

    setSize(640, 480);

    UI.centerWindowToScreen(this);
  }

  private final void createUi() {
    Box box = VBox.create();

    sectionHeader("Genome", box);
    // box.add(new HExpandBox("Species", mSpeciesCombo));

    UI.setSize(mGenomesPanel, 600, 200);
    box.add(mGenomesPanel);

    midSectionHeader("Distance", box);

    box.add(new HExpandBox("Range", new HSpacedBox(mFieldStart,
        new ModernAutoSizeLabel("to"), mFieldEnd, mUnitsCombo)));

    box.add(UI.createVGap(10));

    box.add(new HExpandBox("Bins", new HSpacedBox(mFieldBin, mBinUnitsCombo)));

    setDialogCardContent(box);

    mUnitsCombo.setSelectedIndex(1);

    UI.setSize(mUnitsCombo, ModernWidget.SMALL_SIZE);
    UI.setSize(mBinUnitsCombo, ModernWidget.SMALL_SIZE);
  }

  public double getStart() throws ParseException {
    return mFieldStart.getValue();
  }

  public double getEnd() throws ParseException {
    return mFieldEnd.getValue();
  }

  public int getUnits() {
    switch (mUnitsCombo.getSelectedIndex()) {
    case 2:
      return 1000000;
    case 1:
      return 1000;
    default:
      return 1;
    }
  }

  public double getBinSize() throws ParseException {
    return mFieldBin.getValue();
  }

  public int getBinUnits() {
    switch (mBinUnitsCombo.getSelectedIndex()) {
    case 2:
      return 1000000;
    case 1:
      return 1000;
    default:
      return 1;
    }
  }

  public String getGenome() {
    return mGenomesPanel.getGenome();
  }
}
