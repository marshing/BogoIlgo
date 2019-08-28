package com.malangyee.bogoilgo;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ProgressBar;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static android.content.ContentValues.TAG;

public class NaverBookCoverSearch extends AsyncTask<String, String, String> implements AbsListView.OnScrollListener {
    private static String CLIENT_ID = "LVs4m6lpd4lvOFGJHPyd";
    private static String CLIENT_SECRET = "YQGscXjg1a";

    private static String BOOKSEARCH_URL = "https://openapi.naver.com/v1/search/book.xml";

    private Context context;
    private String title;
    private GridView gridView;
    private MyAdapter adapter;

    private ArrayList<Book> allBookList;
    private ArrayList<Book> list;
    private int page=0;
    private static int OFFSET = 20;

    private boolean lastItemVisibleFlag = false;    // 리스트 스크롤이 마지막 셀(맨 바닥)로 이동했는지 체크할 변수
    private boolean mLockListView = false;          // 데이터 불러올때 중복안되게 하기위한 변수


    public NaverBookCoverSearch(Context context, String title, GridView gridView) {
        this.title = title;
        this.context = context;
        this.gridView = gridView;
    }

    @Override
    protected String doInBackground(String... strings) {
        try{
            String sourceText = URLEncoder.encode(title, "UTF-8");
            URL url = new URL(BOOKSEARCH_URL+"?query="+sourceText+"&display=100");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Naver-Client-Id", CLIENT_ID);
            con.setRequestProperty("X-Naver-Client-Secret", CLIENT_SECRET);

            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode==200){
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else{
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while((inputLine = br.readLine()) != null){
                response.append(inputLine);
            }
            br.close();
            return response.toString();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d(TAG, "onPostExecute: " + s);

        allBookList = new ArrayList<>();


        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(s)));

            NodeList nodeList = document.getElementsByTagName("item");
            Log.d(TAG, "onPostExecute: "+nodeList.getLength());

            for(int i=0; i<nodeList.getLength(); i++){
                NodeList n = nodeList.item(i).getChildNodes();
                    if(n.getLength() >0){
                        Log.d(TAG, "onPostExecute: " + i);
                        String title = "";
                        String image = "";
                        for(int j=0; j<n.getLength(); j++){
                            Node attr = n.item(j);
                            if(attr.getNodeName().equals("title")){
                                title = attr.getChildNodes().item(0).getNodeValue();
                            }
                            if(attr.getNodeName().equals("image") ){
                                Log.d(TAG, "onPostExecute: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + attr.getChildNodes().getLength());
                                if(attr.getChildNodes().getLength() !=0)
                                    image = attr.getChildNodes().item(0).getNodeValue();
                                else{
                                    image = "";
                                }
                            }
                        }
                        allBookList.add(new Book(image.replace("type=m1&", ""), title));
                    }
                }
        }catch (Exception e){
            e.printStackTrace();
        }
        list = new ArrayList<>();
        adapter = new MyAdapter(context, R.layout.row, list);
        gridView.setAdapter(adapter);

        gridView.setOnScrollListener(this);
        getItem();


    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        if (i == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag && mLockListView == false) {
            // 화면이 바닦에 닿을때 처리
            // 로딩중을 알리는 프로그레스바를 보인다.
//            progressBar.setVisibility(View.VISIBLE);

            // 다음 데이터를 불러온다.
            if(allBookList.size() > (page)*OFFSET){
                Log.d(TAG, "onScrollStateChanged: size - " + allBookList.size() + " p*O - " + page*OFFSET);
                getItem();
            }
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        lastItemVisibleFlag = (i2 > 0) && (i + i1 >= i2);

    }

    private void getItem(){

        mLockListView = true;
        int size = 20;

        if(allBookList.size() < (page+1)*OFFSET){
            size = allBookList.size() - (page)*OFFSET;
            Log.d(TAG, "getItem: @@@@@@@@@@@@@@@@@@@@@@@@@@@");
        }
            

        for(int i=0; i<size; i++){
            Book tmp = allBookList.get((page*OFFSET) + i);
            list.add(tmp);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                page++;
                adapter.notifyDataSetChanged();
//                progressBar.setVisibility(View.GONE);
                mLockListView = false;
            }
        },1000);

    }

    public ArrayList<Book> getAllBookList() {
        return allBookList;
    }
}
