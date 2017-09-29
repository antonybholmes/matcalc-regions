package edu.columbia.rdf.matcalc.toolbox.regions;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jebtk.bioinformatics.gapsearch.BinaryGapSearch;
import org.jebtk.bioinformatics.gapsearch.FixedGapSearch;
import org.jebtk.bioinformatics.genomic.Chromosome;
import org.jebtk.bioinformatics.genomic.GenomicRegion;
import org.jebtk.core.io.Io;
import org.jebtk.core.text.TextUtils;

public class Rdf {

	public static RdfGeneSearch parseTssForGappedSearch(BufferedReader reader) throws IOException {
		RdfGeneSearch gappedSearch = new RdfGeneSearch();

		String line;
		List<String> tokens;

		reader.readLine();
		
		try {
			while ((line = reader.readLine()) != null) {
				if (Io.isEmptyLine(line)) {
					continue;
				}

				tokens = TextUtils.tabSplit(line);
				
				String id = tokens.get(0);
				String refseq = tokens.get(1);
				String entrez = tokens.get(2);
				String symbol = tokens.get(5);
				
				if (refseq.equals(TextUtils.NA) || 
						entrez.equals(TextUtils.NA) || 
						symbol.equals(TextUtils.NA)) {
					continue;
				}
				
				
				Chromosome chr = Chromosome.parse(tokens.get(8));
				char strand = tokens.get(9).charAt(0);
				
				// UCSC convention
				int start = Integer.parseInt(tokens.get(10)) + 1;
				int end = Integer.parseInt(tokens.get(11));
				int exonCount = Integer.parseInt(tokens.get(12));
				
				List<Integer> exonStarts =
						TextUtils.toInt(TextUtils.fastSplit(tokens.get(13), TextUtils.COMMA_DELIMITER));
				
				List<Integer> exonEnds =
						TextUtils.toInt(TextUtils.fastSplit(tokens.get(14), TextUtils.COMMA_DELIMITER));
				
				
				
				GenomicRegion region = new GenomicRegion(chr, start, end);
				
				RdfGene gene = new RdfGene(id, refseq, entrez, symbol, strand, region);
				
				for (int i = 0; i < exonCount; ++i) {
					// UCSC convention
					GenomicRegion exon = new GenomicRegion(chr, exonStarts.get(i) + 1, exonEnds.get(i));
					
					gene.getExons().add(exon);
				}

				if (strand == '+') {
					gappedSearch.addFeature(region.getChr(), start, start, gene);
				} else {
					gappedSearch.addFeature(region.getChr(), end, end, gene);
				}
			}

		} finally {
			reader.close();
		}
		
		return gappedSearch;
	}
	
	public static FixedGapSearch<RdfGene> parseTssForFixedGappedSearch(BufferedReader reader, 
			int ext5p, 
			int ext3p) throws IOException {
		FixedGapSearch<RdfGene> gappedSearch =
				new FixedGapSearch<RdfGene>(10000);

		String line;
		List<String> tokens;

		reader.readLine();
		
		try {
			while ((line = reader.readLine()) != null) {
				if (Io.isEmptyLine(line)) {
					continue;
				}

				tokens = TextUtils.tabSplit(line);
				
				String id = tokens.get(0);
				String refseq = tokens.get(1);
				String entrez = tokens.get(2);
				String symbol = tokens.get(5);
				
				// Don't load genes without valid refseq ids or symbols
				if (refseq.equals(TextUtils.NA) || 
						entrez.equals(TextUtils.NA) || 
						symbol.equals(TextUtils.NA)) {
					continue;
				}
				
				Chromosome chr = Chromosome.parse(tokens.get(8));
				char strand = tokens.get(9).charAt(0);
				
				// UCSC convention
				int start = Integer.parseInt(tokens.get(10)) + 1;
				int end = Integer.parseInt(tokens.get(11));
				int exonCount = Integer.parseInt(tokens.get(12));
				
				List<Integer> exonStarts =
						TextUtils.toInt(TextUtils.fastSplit(tokens.get(13), TextUtils.COMMA_DELIMITER));
				
				List<Integer> exonEnds =
						TextUtils.toInt(TextUtils.fastSplit(tokens.get(14), TextUtils.COMMA_DELIMITER));		TextUtils.toInt(TextUtils.fastSplit(tokens.get(14), TextUtils.COMMA_DELIMITER));
				
				GenomicRegion region = new GenomicRegion(chr, start, end);
				
				RdfGene gene = new RdfGene(id, refseq, entrez, symbol, strand, region);
				
				for (int i = 0; i < exonCount; ++i) {
					// UCSC convention
					GenomicRegion exon = new GenomicRegion(chr, exonStarts.get(i) + 1, exonEnds.get(i));
					
					gene.getExons().add(exon);
				}
				
				// extend so we can find elements in the promoter
				if (strand == '+') {
					start -= ext5p;
				} else {
					end += ext5p;
				}

				gappedSearch.addFeature(region.getChr(), start, end, gene);
			}

		} finally {
			reader.close();
		}
		
		return gappedSearch;
	}

	public static BinaryGapSearch<RdfGene> parsePromoters(BufferedReader reader,
			int upstream,
			int downstream) throws IOException {
		BinaryGapSearch<RdfGene> gappedSearch =
				new BinaryGapSearch<RdfGene>();

		String line;
		List<String> tokens;

		try {
			while ((line = reader.readLine()) != null) {
				if (Io.isEmptyLine(line)) {
					continue;
				}

				tokens = TextUtils.tabSplit(line);
				
				String id = tokens.get(0);
				String refseq = tokens.get(1);
				String entrez = tokens.get(2);
				String symbol = tokens.get(5);
				Chromosome chr = Chromosome.parse(tokens.get(8));
				char strand = tokens.get(9).charAt(0);
				int start = Integer.parseInt(tokens.get(10));
				int end = Integer.parseInt(tokens.get(11));
				
				
				GenomicRegion region = new GenomicRegion(chr, start, end);
						

				
				
				RdfGene gene = new RdfGene(id, refseq, entrez, symbol, strand, region);

				// We search only around the tss regions
				gappedSearch.addFeature(region.getChr(), 
						start - upstream, 
						start + downstream, 
						gene);
			}

		} finally {
			reader.close();
		}

		return gappedSearch;
	}
	
	public static Map<String, RdfGene> parseGeneMap(BufferedReader reader) throws IOException {
		Map<String, RdfGene> ret =
				new HashMap<String, RdfGene>();

		String line;
		List<String> tokens;
		
		reader.readLine();

		try {
			while ((line = reader.readLine()) != null) {
				if (Io.isEmptyLine(line)) {
					continue;
				}

				tokens = TextUtils.tabSplit(line);
				
				String id = tokens.get(0);
				String refseq = tokens.get(1);
				String entrez = tokens.get(2);
				String symbol = tokens.get(5);
				Chromosome chr = Chromosome.parse(tokens.get(8));
				char strand = tokens.get(9).charAt(0);
				int start = Integer.parseInt(tokens.get(10));
				int end = Integer.parseInt(tokens.get(11));
				
				
				GenomicRegion region = new GenomicRegion(chr, start, end);
						

				
				
				RdfGene gene = new RdfGene(id, refseq, entrez, symbol, strand, region);

				ret.put(gene.getSymbol().toLowerCase(), gene);
				ret.put(gene.getEntrez(), gene);
			}

		} finally {
			reader.close();
		}

		return ret;
	}
}
