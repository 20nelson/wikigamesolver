
public class Page {
	public String url;
	public String title;
	public Page parent;
	public Page(String url, String title, Page parent) {
		this.url = url;
		this.title = title;
		this.parent = parent;
	}
}
