package com.cavsusa.ccastconsole;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

/**
*
* @author Ryan.Oglesby
*/
public class JsonThroughHTTP {

private static String sURL;

/**
 * @param args the command line arguments
 */
public void acceptUrl(String[] args) throws MalformedURLException, IOException {
    sURL = "http://cinet.com";
    System.out.println(sURL);
    URL url = new URL(sURL);
    HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
    //set http request headers
            httpCon.addRequestProperty("Host", "www.cinet.com");
            httpCon.addRequestProperty("Connection", "keep-alive");
            httpCon.addRequestProperty("Cache-Control", "max-age=0");
            httpCon.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            httpCon.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36");
            httpCon.addRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
            httpCon.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
            //httpCon.addRequestProperty("Cookie", "JSESSIONID=EC0F373FCC023CD3B8B9C1E2E2F7606C; lang=tr; __utma=169322547.1217782332.1386173665.1386173665.1386173665.1; __utmb=169322547.1.10.1386173665; __utmc=169322547; __utmz=169322547.1386173665.1.1.utmcsr=stackoverflow.com|utmccn=(referral)|utmcmd=referral|utmcct=/questions/8616781/how-to-get-a-web-pages-source-code-from-java; __gads=ID=3ab4e50d8713e391:T=1386173664:S=ALNI_Mb8N_wW0xS_wRa68vhR0gTRl8MwFA; scrElm=body");
            HttpURLConnection.setFollowRedirects(false);
            httpCon.setInstanceFollowRedirects(false);
            httpCon.setDoOutput(true);
            httpCon.setUseCaches(true);

            httpCon.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(httpCon.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuilder a = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                a.append(inputLine);
            in.close();

            System.out.println(a.toString());
            //Log.d("Response: ", "> " + a.toString());

            httpCon.disconnect();
}
}
