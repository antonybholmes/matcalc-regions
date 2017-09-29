package edu.columbia.rdf.matcalc.toolbox.regions.enhancers;

import org.jebtk.core.NameProperty;

public class Enhancer implements NameProperty {

	private String mName;

	public Enhancer(String name) {
		mName = name;
	}
	
	@Override
	public String getName() {
		return mName;
	}
	
}