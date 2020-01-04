package edu.columbia.rdf.matcalc.toolbox.regions.enhancers;

import org.jebtk.core.NameGetter;

public class Enhancer implements NameGetter {

  private String mName;

  public Enhancer(String name) {
    mName = name;
  }

  @Override
  public String getName() {
    return mName;
  }

}