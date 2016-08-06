
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Iterator;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import redis.clients.jedis.Jedis;


public class WikiCrawler {
	// keeps track of where we started
	private final String source;
	
	// the index where the results go
	private JedisIndex index;
	
	// queue of URLs to be indexed
	private Queue<String> queue = new LinkedList<String>();
	
	// fetcher used to get pages from Wikipedia
	final static WikiFetcher wf = new WikiFetcher();

	/**
	 * Constructor.
	 * 
	 * @param source
	 * @param index
	 */
	public WikiCrawler(String source, JedisIndex index) {
		this.source = source;
		this.index = index;
		queue.offer(source);
	}

	/**
	 * Returns the number of URLs in the queue.
	 * 
	 * @return
	 */
	public int queueSize() {
		return queue.size();	
	}

	/**
	 * Gets a URL from the queue and indexes it.
	 * @param b 
	 * 
	 * @return Number of pages indexed.
	 * @throws IOException
	 */

// When testing is true, the crawl method should:

// Choose and remove a URL from the queue in FIFO order.

// Read the contents of the page using WikiFetcher.readWikipedia, which reads cached copies of pages we have included in this repository for testing purposes (to avoid problems if the Wikipedia version changes).

// It should index pages regardless of whether they are already indexed.

// It should find all the internal links on the page and add them to the queue in the order they appear. "Internal links" are links to other Wikipedia pages.

// And it should return the URL of the page it indexed.




// When testing is false, this method should:

// Choose and remove a URL from the queue in FIFO order.

// If the URL is already indexed, it should not index it again, and should return null.

// Otherwise it should read the contents of the page using WikiFetcher.fetchWikipedia, which reads current content from the Web.

// Then it should index the page, add links to the queue, and return the URL of the page it indexed.

	public String crawl(boolean testing) throws IOException {
        // FILL THIS IN!
        if (queue.isEmpty()) {
            return null;
        }
        String url = queue.poll();
        System.out.println("Crawling " + url);

        if (testing==false && index.isIndexed(url)) {
            System.out.println("Already indexed.");
            return null;
        }

        Elements paragraphs;
        if (testing) {
            paragraphs = wf.readWikipedia(url);
        } else {
            paragraphs = wf.fetchWikipedia(url);
        }
        index.indexPage(url, paragraphs);
        queueInternalLinks(paragraphs);
        return url;
    







   //      if (testing){
   //      	String url = queue.remove();
        	
			// Elements paragraphs = wf.readWikipedia(url);

   //      	index.indexPage(url, paragraphs);
			// queueInternalLinks(paragraphs);

   //      	return url;
   //      } else {
			// String url = queue.remove();
			// if (index.isIndexed(url)){
			// 	return null;
			// } else {
			// 	Elements paragraphs = wf.fetchWikipedia(url);

	  //       	index.indexPage(url, paragraphs);
	  //       	queueInternalLinks(paragraphs);
				
	  //       	return url;
			// }
   //      }
		//return null;
	}
	
	/**
	 * Parses paragraphs and adds internal links to the queue.
	 * 
	 * @param paragraphs
	 */
	// NOTE: absence of access level modifier means package-level
	void queueInternalLinks(Elements paragraphs) {
        // FILL THIS IN!


        for (Element paragraph: paragraphs) {
            queueInternalLinks(paragraph);
        }
    }

    private void queueInternalLinks(Element paragraph) {
        Elements elts = paragraph.select("a[href]");
        for (Element elt: elts) {
            String relURL = elt.attr("href");

            if (relURL.startsWith("/wiki/")) {
                String absURL = elt.attr("abs:href");
                queue.offer(absURL);
            }
        }
		// Elements links = paragraphs.select("a[href*=/]");
		// Iterator<Element> iter = links.iterator();

		// // String [] words = (String.valueOf(paras)).split("\\s+");
		// // String curr;

  //   	while (iter.hasNext()){
	 //    	queue.add(String.valueOf(iter.next()));
  //   	}
	}

	public static void main(String[] args) throws IOException {
		
		// make a WikiCrawler
		Jedis jedis = JedisMaker.make();
		JedisIndex index = new JedisIndex(jedis); 
		String source = "https://en.wikipedia.org/wiki/Java_(programming_language)";
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
		
		Map<String, Integer> map = index.getCounts("the");
		for (Entry<String, Integer> entry: map.entrySet()) {
			System.out.println(entry);
		}
	}
}
