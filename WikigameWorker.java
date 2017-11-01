import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WikigameWorker extends SwingWorker<String[], String> {

	private String startURL;
	private String startTitle;
	private String target;
	private ArrayList<Page> pageQueue;
	private ArrayList<String> visited;
	
	private JProgressBar progressBar;
	
	public WikigameWorker(String sURL, String tgt, JProgressBar progress) {
		startURL = sURL;
		target = tgt;
		progressBar = progress;
		pageQueue = new ArrayList<Page>();
		pageQueue.add(new Page(startURL, titleFromURL(startURL), null));
		visited = new ArrayList<String>();
		visited.add(startURL.replaceAll("http.*://.*en.wikipedia.org", ""));
	}
	
	public static String titleFromURL(String url){
		try {
			return URLDecoder.decode(url.replaceAll("http.*://.*en.wikipedia.org", "").substring(6).replaceAll("_", "%20"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private Page processQueue() {
		Random random = new Random();
		ArrayList<Page> newQueue = new ArrayList<Page>();
		int queueLength = pageQueue.size();
		int pagesScanned = 0;
		
		while (!pageQueue.isEmpty()) {
			if(isCancelled()){
				return new Page("CANCELLED", "CANCELLED", null);
			}
			int index = random.nextInt(pageQueue.size());
			Page page = pageQueue.get(index);
			pageQueue.remove(index);
			pagesScanned++;
			publish("PROG;"+pagesScanned+";"+queueLength+";"+page.title);
			try {
				Document document = Jsoup.connect(page.url).get();
				Elements links = document.select("a");
				for(int i = 0; i < links.size(); i++){
					Element link = links.get(i);
					if(link.hasAttr("href")){
						String href = link.attr("href").replaceAll("#.*", "");
						if(href.startsWith("/wiki/") && !href.contains(":") && !visited.contains(href)){
							visited.add(href);
							String title = titleFromURL(href);
							//publish(title);
							if(title.toLowerCase().contains(target.toLowerCase())){
								return new Page("http://en.wikipedia.org" + href, title, page);
							}
							newQueue.add(new Page("http://en.wikipedia.org" + href, title, page));
						}
					}
				}
			} catch (Exception e) {
				publish(e.getMessage());
			}
		}
		pageQueue = newQueue;
		return null;
	}
	
	@Override
	protected String[] doInBackground() {
		Page out;
		while(true){
			out = processQueue();
			if(out != null){
				break;
			}
		}
		if(out.title != "CANCELLED"){
			return pathFrom(out);
		}
		return null;
	}

	@Override
	protected void process(List<String> chunks) {
		for(int i = 0; i < chunks.size(); i++){
			if(chunks.get(i).startsWith("PROG;")){
				String[] progressParts = chunks.get(i).split(";");
				progressBar.setValue((int) Math.floor((Double.parseDouble(progressParts[1])/Double.parseDouble(progressParts[2]))*100));
				progressBar.setString(progressParts[1]+"/"+progressParts[2]+": "+progressParts[3]);
			}
			System.out.println(chunks.get(i));
		}
	}
	
	private String[] pathFrom(Page finalpage) {
		ArrayList<String> pages = new ArrayList<String>();
		Page curPage = finalpage;
		while(curPage.parent != null){
			pages.add(0, curPage.title);
			curPage = curPage.parent;
			publish(""+pages.size());
		}
		pages.add(0, curPage.title);
		return pages.toArray(new String[0]);
	}
	@Override
	protected void done() {
		
	}
	
}
