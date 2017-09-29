package edu.columbia.rdf.matcalc.toolbox.regions;

public class Promoter {
	private RdfGene mGene;
	private int mStart;
	private int mEnd;

	public Promoter(RdfGene gene, int start, int end) {
		mGene = gene;
		mStart = start;
		mEnd = end;
	}

	public RdfGene getGene() {
		return mGene;
	}

	public int getStart() {
		return mStart;
	}
	
	public int getEnd() {
		return mEnd;
	}
}
