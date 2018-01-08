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
    private Bitmap result_img;
    private String date;

    History(String history_ID,String result,Bitmap result_img,String date)
    {
        this.history_ID = history_ID;
        this.result = result;
        this.result_img = result_img;
        this.date = date;
    }

    public String gethistory_ID() {
        return history_ID;
    }

    public String getResult() {
        return result;
    }

    public Bitmap getResult_img() {
        return result_img;
    }

    public String getDate() {
        return date;
    }

}
