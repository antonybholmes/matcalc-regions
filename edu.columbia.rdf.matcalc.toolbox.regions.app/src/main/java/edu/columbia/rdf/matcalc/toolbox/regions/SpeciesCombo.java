package edu.columbia.rdf.matcalc.toolbox.regions;

import org.jebtk.modern.UI;
import org.jebtk.modern.combobox.ModernComboBox;

import edu.columbia.rdf.matcalc.bio.AnnotationService;

public class SpeciesCombo extends ModernComboBox {

	private static final long serialVersionUID = 1L;
	
	
	public SpeciesCombo() {
		for (String name : AnnotationService.getInstance()) {
			addMenuItem(name);
		}
		
		UI.setSize(this, VERY_LARGE_SIZE);
	}

}
