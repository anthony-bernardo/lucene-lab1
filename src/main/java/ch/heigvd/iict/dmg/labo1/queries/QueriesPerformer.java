package ch.heigvd.iict.dmg.labo1.queries;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class QueriesPerformer {
	
	private Analyzer		analyzer		= null;
	private IndexReader 	indexReader 	= null;
	private IndexSearcher 	indexSearcher 	= null;

	public QueriesPerformer(Analyzer analyzer, Similarity similarity) {
		this.analyzer = analyzer;
		Path path = FileSystems.getDefault().getPath("index");
		Directory dir;
		try {
			dir = FSDirectory.open(path);
			this.indexReader = DirectoryReader.open(dir);
			this.indexSearcher = new IndexSearcher(indexReader);
			if(similarity != null)
				this.indexSearcher.setSimilarity(similarity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void printTopRankingTerms(String field, int numTerms) {
		// TODO student
		// This methods print the top ranking term for a field.
		// See "Reading Index".

		try {
			//		TotalTermFreqComparator
			//		DocFreqComparator
			TermStats[] top10Terms = HighFreqTerms.getHighFreqTerms(this.indexReader, 10, field, new HighFreqTerms.TotalTermFreqComparator()); // TODO: pas sur TotalTermFreqComparator
			System.out.println("\nTop ranking terms for field ["  + field +"] are: " );

			for (TermStats term : top10Terms) {
				System.out.println("(" + term.totalTermFreq + ") " + term.termtext.utf8ToString());
//				System.out.println(term.docFreq);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void query(String q) throws ParseException, IOException {
		// TODO student
		// See "Searching" section

		System.out.println("\nSearching for [" + q +"]");

		QueryParser parser = new QueryParser("summary", this.analyzer);
		Query query = parser.parse(q);

		ScoreDoc[] hits = this.indexSearcher.search(query, 1000).scoreDocs;
		System.out.println("Results found : " + hits.length);

		for (ScoreDoc hit : hits){
			Document doc = indexSearcher.doc(hit.doc);
			System.out.println(doc.get("id") + ": " + doc.get("title") + " (" + hit.score + ")");
		}

		this.indexReader.close();


	}
	 
	public void close() {
		if(this.indexReader != null)
			try { this.indexReader.close(); } catch(IOException e) { /* BEST EFFORT */ }
	}
	
}
