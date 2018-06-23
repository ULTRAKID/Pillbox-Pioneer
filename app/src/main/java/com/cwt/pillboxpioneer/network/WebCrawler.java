package com.cwt.pillboxpioneer.network;

import android.util.Log;

import com.cwt.pillboxpioneer.search.MedicineBrief;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 爬取网站信息http://ypk.39.net/
 * 若有侵权，请及时告知
 * ultrakid@vip.jiangnan.edu.cn
 * Created by 曹吵吵 on 2018/6/15 0015.
 */

public class WebCrawler {
    public static String ypkWeb="http://ypk.39.net";
    public static String searchWebsite="http://ypk.39.net/search/all?k=";

    public static Document getSearchDoc(String name) throws IOException {
        String searchResult=searchWebsite+name;
        Document document= Jsoup.connect(searchResult).get();
        return document;
    }

    public static List<MedicineBrief> getResultBrief(Document searchDoc){
        List<MedicineBrief> briefList=new ArrayList<>();
        Elements results=searchDoc.select("ul.search_ul.search_ul_yb");
        Elements medicinElements=results.select("li");
        for (Element element:medicinElements){
            MedicineBrief nBrief=new MedicineBrief();

            //Element firstMedicine=medicinElements.first();
            String href=element.select("a").attr("href");
            String detailsLink=ypkWeb+href+"manual";
            nBrief.detailsLink=detailsLink; //详细链接
            Element msgs=element.selectFirst("div.msgs");
            Element nameElement=msgs.selectFirst("a[href]");
            String medName=nameElement.text();
            nBrief.name=medName;    //药品名称
            Element brief=element.selectFirst("p.p1");
            String medBrief=brief.text();
            nBrief.brief=medBrief;  //药品简介
            Element picMedicine=element.selectFirst("a");
            String picLink=picMedicine.select("img").attr("src");
            nBrief.picLink=picLink;
            briefList.add(nBrief);
        }
        Log.e("Medicine", "返回结果："+briefList.size());
        return briefList;
    }

    public static String getManualHtml(String manualUrl) throws IOException {
        Document document=Jsoup.connect(manualUrl).get();
        Element manualElement=document.selectFirst("div.tab_box");
        return manualElement.toString();
    }
}
