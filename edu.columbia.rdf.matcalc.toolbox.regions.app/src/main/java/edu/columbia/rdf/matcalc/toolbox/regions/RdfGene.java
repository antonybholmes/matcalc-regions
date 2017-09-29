package edu.columbia.rdf.matcalc.toolbox.regions;

import java.util.ArrayList;
import java.util.List;

import org.jebtk.bioinformatics.genomic.GenomicRegion;

public class RdfGene implements Comparable<RdfGene> {
	private String mId;
	private String mRefseq;
	private String mEntrez;
	private String mSymbol;
	private char mStrand;
	private GenomicRegion mRegion;
	
	private List<GenomicRegion> mExons = new ArrayList<GenomicRegion>();
	
	private GenomicRegion mTss;

	public RdfGene(String id, 
			String refseq, 
			String entrez, 
			String symbol,
			char strand,
			GenomicRegion region) {
		mId = id;
		mRefseq = refseq;
		mEntrez = entrez;
		mSymbol = symbol;
		mStrand = strand;
		mRegion = region;
		
		if (mStrand == '+') {
			mTss = new GenomicRegion(region.getChr(), region.getStart(), region.getStart());
		} else {
			mTss = new GenomicRegion(region.getChr(), region.getEnd(), region.getEnd());
		}
	}

	public GenomicRegion getRegion() {
		return mRegion;
	}

	public String getRefSeq() {
		return mRefseq;
	}

	public char getStrand() {
		return mStrand;
	}

	public String getEntrez() {
		return mEntrez;
	}

	public String getSymbol() {
		return mSymbol;
	}

	public List<GenomicRegion> getExons() {
		return mExons;
	}
	
	public GenomicRegion getTss() {
		return mTss;
	}
	
	@Override
	public String toString() {
		return mSymbol + " (" + mRefseq +")";
	}

	/**
	 * Returns the TSS of the gene accounting for the strand.
	 * 
	 * @param gene
	 * @return
	 */
	public static int getTss(RdfGene gene) {
		if (gene.mStrand == '+') {
			return gene.mRegion.getStart();
		} else {
			return gene.mRegion.getEnd();
		}
	}
	
	/**
	 * Get the distance from the mid point of a region to a gene
	 * accounting for the strand.
	 * 
	 * @param gene
	 * @param region
	 * @return
	 */
	public static int getTssMidDist(RdfGene gene, GenomicRegion region) {
		int mid = GenomicRegion.mid(region);
		
		return getTssMidDist(gene, mid);
	}
	
	/**
	 * Returns the distance of the mid to the gene tss. If the mid is
	 * downstream, the value is positive.
	 * 
	 * @param gene
	 * @param mid
	 * @return
	 */
	public static int getTssMidDist(RdfGene gene, int mid) {
		if (gene.mStrand == '+') {
			return mid - gene.mRegion.getStart();
		} else {
			return gene.mRegion.getEnd() - mid;
		}
	}

	@Override
	public int compareTo(RdfGene g) {
		return mRegion.compareTo(g.mRegion);
	}
}
