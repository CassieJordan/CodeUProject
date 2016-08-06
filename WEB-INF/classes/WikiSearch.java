import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Iterator;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import redis.clients.jedis.Jedis;




/**
 * Represents the results of a search query.
 *
 */
public class WikiSearch {
	
	// map from URLs that contain the term(s) to relevance score
	private Map<String, Integer> map;
	final static WikiFetcher wf = new WikiFetcher();
	/**
	 * Constructor.
	 * 
	 * @param map
	 */
	public WikiSearch(Map<String, Integer> map) {
		this.map = map;
	}
	
	/**
	 * Looks up the relevance of a given URL.
	 * 
	 * @param url
	 * @return
	 */
	public Integer getRelevance(String url) {
		Integer relevance = map.get(url);
		return relevance==null ? 0: relevance;
	}
	
	/**
	 * Prints the contents in order of term frequency.
	 * 
	 * @param map
	 */
	private  void print() {
		List<Entry<String, Integer>> entries = sort();
		for (Entry<String, Integer> entry: entries) {
			System.out.println(entry);
		}
	}
	
	/**
	 * Computes the union of two search results.
	 * 
	 * @param that
	 * @return New WikiSearch object.
	 */
	public WikiSearch or(WikiSearch that) {
		List<Entry<String, Integer>> thisList = this.sort();
		List<Entry<String, Integer>> thatList = that.sort();

        Map<String, Integer> newMap = new HashMap<String, Integer>();

		for (int i = 0; i < thisList.size(); i++){
			if (thisList.get(i).getValue() > 0){
				newMap.put(thisList.get(i).getKey(), thisList.get(i).getValue());
			}
		}

		for (int i = 0; i < thatList.size(); i++){
			if (thatList.get(i).getValue() > 0){
				if (newMap.containsKey(thatList.get(i).getKey())){
					newMap.put(thatList.get(i).getKey(), totalRelevance(thatList.get(i).getValue(), newMap.get(thatList.get(i).getKey())));
				} else {
					newMap.put(thatList.get(i).getKey(), thatList.get(i).getValue());
				}
			}
		}

		return new WikiSearch(newMap);

	}
	
	/**
	 * Computes the intersection of two search results.
	 * 
	 * @param that
	 * @return New WikiSearch object.
	 */
	public WikiSearch and(WikiSearch that) {
        // FILL THIS IN!
        Iterator<Map.Entry<String, Integer>> iter = map.entrySet().iterator();
        Map.Entry<String, Integer> curr;
        Map<String, Integer> newMap = new HashMap<String, Integer>();

        while (iter.hasNext()){
        	curr = iter.next();

        	if (this.getRelevance(curr.getKey()) > 0 && that.getRelevance(curr.getKey()) > 0){
        		newMap.put(curr.getKey(), totalRelevance(this.getRelevance(curr.getKey()), that.getRelevance(curr.getKey())));
        	}

        }



		return new WikiSearch(newMap);
	}
	
	/**
	 * Computes the intersection of two search results.
	 * 
	 * @param that
	 * @return New WikiSearch object.
	 */
	public WikiSearch minus(WikiSearch that) {
        // FILL THIS IN!
        Iterator<Map.Entry<String, Integer>> iter = map.entrySet().iterator();
        Map.Entry<String, Integer> curr;
        Map<String, Integer> newMap = new HashMap<String, Integer>();

        while (iter.hasNext()){
        	curr = iter.next();
        	if (this.getRelevance(curr.getKey()) > 0 && that.getRelevance(curr.getKey()) == 0){
        		newMap.put(curr.getKey(), this.getRelevance(curr.getKey()));
        	}
        }
		return new WikiSearch(newMap);
	}
	
	/**
	 * Computes the relevance of a search with multiple terms.
	 * 
	 * @param rel1: relevance score for the first search
	 * @param rel2: relevance score for the second search
	 * @return
	 */
	protected int totalRelevance(Integer rel1, Integer rel2) {
		// simple starting place: relevance is the sum of the term frequencies.
		return rel1 + rel2;
	}

	/**
	 * Sort the results by relevance.
	 * 
	 * @return List of entries with URL and relevance.
	 */
	public List<Entry<String, Integer>> sort() {
        // FILL THIS IN!
        List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>();

        Iterator<Map.Entry<String, Integer>> iter = map.entrySet().iterator();
        Map.Entry<String, Integer> curr;
        while (iter.hasNext()){
        	curr = iter.next();
        	if (list.size() == 0){
        		list.add(curr);
        	} else {
        		boolean added = false;
        		Iterator<Entry<String, Integer>> iter1 = list.iterator();
        		Entry<String, Integer> pos;
        		int i = 0;
        		while (iter1.hasNext() && !added){
        			pos = iter1.next();
        			if (curr.getValue() < pos.getValue()){
        				list.add(i, curr);
        				added = true;
        			}
        			i++;
        		}
        		if (!added){
        			list.add(curr);
        		}
        	 }
        }
		return list;
	}

	/**
	 * Performs a search and makes a WikiSearch object.
	 * 
	 * @param term
	 * @param index
	 * @return
	 */
	public static WikiSearch search(String term, JedisIndex index) {
		Map<String, Integer> map = index.getCounts(term);
		return new WikiSearch(map);
	}

	public static List<String> doSearch(String term1, String bool, String term2) throws IOException{
				// make a JedisIndex

		Jedis jedis = JedisMaker.make();
		JedisIndex index = new JedisIndex(jedis);

		List<Entry<String, Integer>> entries = new ArrayList<Entry<String, Integer>>();
		List<String> list = new ArrayList<String>();

	
		if (bool == null || bool.equals("null")){

			
			WikiSearch search1 = search(term1, index);


			entries = search1.sort();
			list = new ArrayList<String>();
			for (Entry<String, Integer> entry: entries) {
				list.add(entry.getKey());
			}



		} else if (bool.toLowerCase().equals("and")){
			
			WikiSearch term1Wiki = search(term1, index);
			WikiSearch intersection = term1Wiki.and(search(term2, index));


			entries = intersection.sort();
			list = new ArrayList<String>();
			for (Entry<String, Integer> entry: entries) {
				list.add(entry.getKey());
			}



		} else if (bool.toLowerCase().equals("or")){
			
			WikiSearch term1Wiki = search(term1, index);
			WikiSearch intersection = term1Wiki.or(search(term2, index));


			entries = intersection.sort();
			list = new ArrayList<String>();
			for (Entry<String, Integer> entry: entries) {
				list.add(entry.getKey());
			}



		} else if (bool.toLowerCase().equals("minus")){
			
			WikiSearch term1Wiki = search(term1, index);
			WikiSearch intersection = term1Wiki.or(search(term2, index));

			entries = intersection.sort();
			list = new ArrayList<String>();
			for (Entry<String, Integer> entry: entries) {
				list.add(entry.getKey());
			}



		} else {
			//out.print("Please enter valid search.");
			list = new ArrayList<String>();
			list.add("Please enter valid search.");

		}

		if (list.size() > 0){
			String source = list.get(list.size()-1);
			WikiCrawler wc = new WikiCrawler(source, index);
			
			// for testing purposes, load up the queue
			Elements paragraphs = wf.fetchWikipedia(source);
			wc.queueInternalLinks(paragraphs);

			// loop until we index a new page
			String res;
			do {
				res = wc.crawl(false);

	            // REMOVE THIS BREAK STATEMENT WHEN crawl() IS WORKING
	        
			} while (res == null);
		} else {
			String source = "https://en.wikipedia.org/w/index.php?search=" + term1 ;
			if (bool == null || bool.equals("and")){
				source = "https://en.wikipedia.org/w/index.php?search=" + term1 ;
			} else if (bool.toLowerCase().equals("and")){
				source = "https://en.wikipedia.org/w/index.php?search=" + term1 + "+AND+" + term2;
			} else if (bool.toLowerCase().equals("or")){
				source = "https://en.wikipedia.org/w/index.php?search=" + term1 + "+OR+" + term2;
			} else if(bool.toLowerCase().equals("minus")){
				source = "https://en.wikipedia.org/w/index.php?search=" + term1 + "+-" + term2;
			}


			//String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";

			WikiCrawler wc = new WikiCrawler(source, index);
			
			// for testing purposes, load up the queue
			Elements paragraphs = wf.fetchWikipedia(source);
			wc.queueInternalLinks(paragraphs);

			// loop until we index a new page
			String res;
			do {
				res = wc.crawl(false);

	            // REMOVE THIS BREAK STATEMENT WHEN crawl() IS WORKING
	        
			} while (res == null);
		}




		return list;
		
	}


}
