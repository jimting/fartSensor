package ntou.jt.fartsensor;

import android.graphics.Bitmap;

import java.net.URL;

/**
 * Created by lp123 on 2018/1/8.
 */

public class History
{
    private String history_ID;
    private String result;
    private String resultURL;
    private String date;

    History(String history_ID,String result,String resultURL,String date)
    {
        this.history_ID = history_ID;
        this.result = result;
        this.resultURL = resultURL;
        this.date = date;
    }

    public String gethistory_ID() {
        return history_ID;
    }

    public String getResult() {
        return result;
    }

    public String getResultURL() {
        return resultURL;
    }

    public String getDate() {
        return date;
    }

}
