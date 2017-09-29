package edu.columbia.rdf.matcalc.toolbox.regions.app;

import org.jebtk.core.AppVersion;
import org.jebtk.modern.UIService;
import org.jebtk.modern.help.GuiAppInfo;


public class RegionsInfo extends GuiAppInfo {

	public RegionsInfo() {
		super("Regions", 
				new AppVersion(28),
				"Copyright (C) 2014-${year} Antony Holmes",
				UIService.getInstance().loadIcon(RegionsIcon.class, 32),
				UIService.getInstance().loadIcon(RegionsIcon.class, 128),
				"Annotate genomic regions.");
	}

}
