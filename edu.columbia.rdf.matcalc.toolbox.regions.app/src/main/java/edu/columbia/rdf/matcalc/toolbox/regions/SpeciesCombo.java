package edu.columbia.rdf.matcalc.toolbox.regions;

import java.io.IOException;

import org.jebtk.modern.UI;
import org.jebtk.modern.combobox.ModernComboBox;

import edu.columbia.rdf.matcalc.bio.AnnotationService;

public class SpeciesCombo extends ModernComboBox {

  private static final long serialVersionUID = 1L;

  public SpeciesCombo() throws IOException {
    for (String name : AnnotationService.getInstance().names()) {
      addMenuItem(name);
    }

    UI.setSize(this, VERY_LARGE_SIZE);
  }

}
