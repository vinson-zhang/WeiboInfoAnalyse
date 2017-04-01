package com.xupt.zxh.graduation.project.spider;

import com.xupt.zxh.graduation.project.bean.weibo.WeiboInfo;
import com.xupt.zxh.graduation.project.spider.parse.ParseWeiboInfo;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.HttpResponse;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;

/**
 * 用于爬去某个用户的全部微博信息
 * 
 * @author 张涛
 * 
 */
public class WeiboCralwer extends BreadthCrawler {

	private String cookie = "";
	
	//微博总页数，默认为1
	private static int page = 1;
	
	public static final String username = "15240789374";

	public static final String password = "a123456";

	public WeiboCralwer(String crawlPath, boolean autoParse) throws Exception {
		super(crawlPath, autoParse);
		this.cookie = GetCookie.getCookie(username, password);
	}
	/**
	 * 暂定关闭自动解析
	 * 
	 * @param url
	 * @throws Exception
	 */
	public WeiboCralwer(String url) throws Exception {
		super("WeiboCralwer", false);
		addSeed(url);
		this.cookie = GetCookie.getCookie(username, password);
		HttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:50.0) Gecko/20100101 Firefox/50.0");
		httpGet.setHeader("Cookie", cookie);
		org.apache.http.HttpResponse httpResponse = httpClient.execute(httpGet);
		HttpEntity entity = httpResponse.getEntity();
		Document doc = Jsoup.parse(EntityUtils.toString(entity));
		Element tempElement = doc.getElementById("pagelist");
		String pageText = tempElement.text();
		String pageStr = pageText.substring(pageText.lastIndexOf('/')+1,pageText.lastIndexOf('页'));
		this.page = Integer.parseInt(pageStr);
	}

	@Override
	public HttpResponse getResponse(CrawlDatum crawlDatum) throws Exception {
		HttpRequest httpRequest = new HttpRequest(crawlDatum);
		httpRequest
				.setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:50.0) Gecko/20100101 Firefox/50.0");
		httpRequest.setCookie(cookie);
		return httpRequest.getResponse();

	}

	@Override
	public void visit(Page page, CrawlDatums next) {
		Document doc = page.doc();
		Elements elements = doc.getElementsByClass("c");
		for(Element e : elements){
			if(e.id() != null && !"".equals(e.id())){
				WeiboInfo weiboInfo = ParseWeiboInfo.parseWeiboInfo(e);
				String url = page.getUrl();
				int sufIndex = url.indexOf("?page");
				if(sufIndex == -1){
					weiboInfo.setWeiboAuthor(page.getUrl().substring(16));
				}else{
					weiboInfo.setWeiboAuthor(page.getUrl().substring(16,sufIndex));
				}
				
				System.out.println(weiboInfo);
			}
		}
		//判断是否有下一页的链接，若有添加到next中，若没有则什么也不做
		Element preNextPage = doc.getElementById("pagelist");
		Elements nextPage = preNextPage.getElementsByAttributeValueContaining("href", "?page=");
		if("下页".equals(nextPage.text().trim())){
			String nextPageUrl = "http://weibo.cn"+nextPage.attr("href");
			next.add(nextPageUrl);
			addSeed(nextPageUrl);
		}
		
	}

	public static void main(String[] args) throws Exception {
		String url = "http://weibo.cn/u/1984666617";
		WeiboCralwer weiboCralwer = new WeiboCralwer(url);
//		for(int i = 1;i<=page;i++){
//			weiboCralwer.addSeed(url+"?page="+i);
//		}
		weiboCralwer.start(page);
	}
	
	

}
