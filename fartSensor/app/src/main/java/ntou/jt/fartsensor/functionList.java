package ntou.jt.fartsensor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by lp123 on 2018/1/2.
 */

public class functionList
{
    //註冊使用者
    public static String register(String user_id,String user_name) throws IOException
    {
        String registerUrl = "http://114.42.108.178/userRegister.php?user_id="+user_id;
        System.out.println(registerUrl);
        Connection con = Jsoup.connect(registerUrl).timeout(3000);
        Document doc = con.get();
        String result = doc.select("body").html();
        return result;
    }

    //註冊配備
    public static String addEquip(String user_id,String device_mac) throws IOException
    {
        String registerUrl = "http://114.42.108.178/addDevice.php?user_id="+user_id+"&device_mac="+device_mac;
        System.out.println(registerUrl);
        Connection con = Jsoup.connect(registerUrl).timeout(3000);
        Document doc = con.get();
        String result = doc.select("body").html();
        return result;
    }

    //新增屁感測資料，以及拿到結果
    public static String newData(String user_id,String device_id,ArrayList<String> ch,ArrayList<String> LPG) throws IOException
    {
        ArrayList<String> total = new ArrayList<>();
        for(int i = 0;i < ch.size();i++)
        {
            String tmp = String.valueOf(Integer.parseInt(ch.get(i).toString()) + Integer.parseInt(LPG.get(i).toString()));
            total.add(tmp);
        }
        String registerUrl = "http://114.42.108.178/newData.php?user_id="+user_id+"&device_id="+device_id+"&P=";
        for(int i = 0;i < total.size();i++)
        {
            if(i != total.size()-1)
                registerUrl += total.get(i) + "l";
            else
                registerUrl += total.get(i);
        }
        System.out.println(registerUrl);
        Connection con = Jsoup.connect(registerUrl).timeout(3000);
        Document doc = con.get();
        String result = doc.select("body").html();
        return result;
    }

    //拿到所有歷史紀錄
    public static History[] getHistory(String user_id) throws IOException
    {
        //目前是測試的URL，要再做更改
        String historyUrl = "http://114.42.108.178/getHistory.php?user_id="+user_id;
        System.out.println(historyUrl);
        Connection con = Jsoup.connect(historyUrl).timeout(10000);
        Connection.Response resp = con.execute();
        Document doc = null;
        if (resp.statusCode() == 200)
        {
            doc = con.get();
        }
        String result = doc.select("body").html();

        History[] tempHistory = null;
        try {
            JSONArray jsonTotal = new JSONArray(result);
            tempHistory = new History[jsonTotal.length()];
            if (jsonTotal.length() > 0)
            {
                for (int top = 0; top < jsonTotal.length(); top++)
                {
                    JSONObject selectToilet = jsonTotal.getJSONObject(top);
                    String history_id = selectToilet.getString("history_id");
                    String history_result =  selectToilet.getString("result");
                    String resultURL =  selectToilet.getString("resultURL");
                    String history_date =  selectToilet.getString("date");

                    //byte[] data = functionList.getPic(resultURL);
                    //Bitmap result_img = BitmapFactory.decodeByteArray(data, 0, data.length);

                    tempHistory[top] = new History(history_id, history_result, resultURL, history_date);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(result);
        return tempHistory;
    }

    public static History selectHistory(String select_history_id) throws  IOException
    {
        //目前是測試的URL，要再做更改
        String historyUrl = "http://114.42.108.178/selectHistory.php?history_id="+select_history_id;
        System.out.println(historyUrl);
        Connection con = Jsoup.connect(historyUrl).timeout(10000);
        Connection.Response resp = con.execute();
        Document doc = null;
        if (resp.statusCode() == 200)
        {
            doc = con.get();
        }
        String result = doc.select("body").html();

        History tempHistory = null;
        try {
            JSONObject selectHistory = new JSONObject(result);
            String history_id = selectHistory.getString("history_id");
            String history_result = selectHistory.getString("result");
            String resultURL = selectHistory.getString("resultURL");
            String history_date = selectHistory.getString("date");

            //byte[] data = functionList.getPic(resultURL);
            //Bitmap result_img = BitmapFactory.decodeByteArray(data, 0, data.length);

            tempHistory = new History(history_id, history_result, resultURL, history_date);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(result);
        return tempHistory;
    }

    public static byte[] getPic(String urlpath)
    {
        try
        {
            InputStream inputstream;
            URL url = null;
            url = new URL(urlpath);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            inputstream = conn.getInputStream();
            ByteArrayOutputStream outputstream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = -1;
            while((len = inputstream.read(buffer)) != -1){
                outputstream.write(buffer,0,len);
            }
            inputstream.close();
            outputstream.close();
            return outputstream.toByteArray();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

}
