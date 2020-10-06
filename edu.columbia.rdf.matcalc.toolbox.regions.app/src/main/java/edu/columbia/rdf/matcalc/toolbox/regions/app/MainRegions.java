package edu.columbia.rdf.matcalc.toolbox.regions.app;

import java.awt.FontFormatException;
import java.io.IOException;

import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.parsers.ParserConfigurationException;

import org.jebtk.core.AppService;
import org.jebtk.modern.theme.ThemeService;
import org.xml.sax.SAXException;

import edu.columbia.rdf.matcalc.MainMatCalc;
import edu.columbia.rdf.matcalc.ModuleLoader;
import edu.columbia.rdf.matcalc.bio.BioModuleLoader;
import edu.columbia.rdf.matcalc.toolbox.regions.RegionsModule;

public class MainRegions {
  public static final void main(String[] args)
      throws FontFormatException, IOException, SAXException, ParserConfigurationException, ClassNotFoundException,
      InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
    AppService.getInstance().setAppInfo("regions");

    ThemeService.getInstance().setTheme();

    ModuleLoader ml = new BioModuleLoader().addModule(RegionsModule.class);

    MainMatCalc.main(new RegionsInfo(), ml);
  }
}
