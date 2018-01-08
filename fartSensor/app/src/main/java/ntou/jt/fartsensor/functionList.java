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

/**
 * Created by lp123 on 2018/1/2.
 */

public class functionList
{
    //註冊使用者
    public static void register(){}

    //註冊配備
    public static void addEquip(){}

    //新增屁感測資料，以及拿到結果
    public static void newData(){}

    //拿到所有歷史紀錄
    public static History[] getHistory() throws IOException
    {
        //目前是測試的URL，要再做更改
        String toiletUrl = "http://114.42.108.178/getHistory.php";
        Connection con = Jsoup.connect(toiletUrl).timeout(10000);
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

                    byte[] data = functionList.getPic(resultURL);
                    Bitmap result_img = BitmapFactory.decodeByteArray(data, 0, data.length);

                    tempHistory[top] = new History(history_id, history_result, result_img, history_date);
                }
            }
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
