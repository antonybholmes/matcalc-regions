package edu.columbia.rdf.matcalc.toolbox.regions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jebtk.bioinformatics.gapsearch.BinaryGapSearch;
import org.jebtk.bioinformatics.gapsearch.GappedSearchFeatures;
import org.jebtk.bioinformatics.genomic.GenomicRegion;
import org.jebtk.core.collections.CollectionUtils;

public class RdfGeneSearch extends BinaryGapSearch<RdfGene> {

	/**
	 * Get the closest features distance n from location. For example if
	 * n = 0, return the closest, n = 1, return the second closest, n = 3
	 * the third closest etc.
	 * 
	 * @param chr
	 * @param start
	 * @param end
	 * @param n
	 * 
	 * @return
	 */
	public List<RdfGene> getClosestGenes(GenomicRegion region, int n) {
		// Make sure everything is sorted before doing anything
		organize();

		List<GappedSearchFeatures<RdfGene>> features = 
				(List<GappedSearchFeatures<RdfGene>>) mFeatures.get(region.getChr());
		
		List<Integer> bins = mBins.get(region.getChr());


		// The closest indices
		int is = getStartIndex(bins, region.getStart());
		int ie = getEndIndex(bins, region.getEnd());

		// find the index of the features closest to our point
		
		int minD = Integer.MAX_VALUE;
		int closestIndex = Integer.MAX_VALUE;
		
		for (int i = is; i <= ie; ++i) {
			int d = Math.abs(GenomicRegion.mid(region) - features.get(i).getPosition());

			if (d < minD) {
				closestIndex = i;
				minD = d;
			}
		}

		int closestP = features.get(closestIndex).getPosition();
		
		// Sort the closest n points around this index to find 1st, 2nd, 3rd
		// etc closest.
		Map<Integer, Integer> dMap = new TreeMap<Integer, Integer>();
		
		// Skip variants to find interesting genes
		Set<String> usedSymbols = new HashSet<String>();
		
		//for (RdfGene gene : features.get(closestIndex)) {
		//	usedSymbols.add(gene.getEntrez());
		//}

		int i = 0;
		int c = 0;
		while (c <= n) {
			//System.err.println("c " + closestIndex + " " + i + " " + n);
			
			int it = closestIndex - i;
			
			if (it >= 0) {
				boolean add = true;
				
				for (RdfGene gene : features.get(it)) {
					if (usedSymbols.contains(gene.getEntrez())) {
						add = false;
						break;
					}
				}
				
				if (add) {
					dMap.put(Math.abs(features.get(it).getPosition() - closestP), it);
				
					for (RdfGene gene : features.get(it)) {
						//System.err.println("gene1 " + gene.getSymbol() + " " + c);
						
						usedSymbols.add(gene.getEntrez());
					}
					
					++c;
				}
				
				++i;
			} else {
				break;
			}
		}
		
		i = 0;
		c = 0;
		
		while (c <= n) {
			//System.err.println("c " + closestIndex + " " + i + " " + n);
			
			int it = closestIndex + i;
			
			if (it < features.size()) {
				boolean add = true;
				
				for (RdfGene gene : features.get(it)) {
					if (usedSymbols.contains(gene.getEntrez())) {
						add = false;
						break;
					}
				}
				
				if (add) {
					dMap.put(Math.abs(features.get(it).getPosition() - closestP), it);
				
					for (RdfGene gene : features.get(it)) {
						//System.err.println("gene2 " + gene.getSymbol() + " " + c);
						
						usedSymbols.add(gene.getEntrez());
					}
					
					++c;
				}
				
				++i;
			} else {
				break;
			}
		}
		
		// If n = 0, thats the closest, n = 1, is the second closest
		int closestIndexN = dMap.get(CollectionUtils.sort(dMap.keySet()).get(n));

		GappedSearchFeatures<RdfGene> closestFeaturesN = 
				features.get(closestIndexN);
		
		List<RdfGene> ret = new ArrayList<RdfGene>();
		Set<RdfGene> used = new HashSet<RdfGene>();
		
		for (RdfGene item : closestFeaturesN) {
			if (used.contains(item)) {
				continue;
			}

			ret.add(item);
			used.add(item);
		}

		return ret;
	}
}
