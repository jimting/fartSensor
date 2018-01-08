package ntou.jt.fartsensor;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
{
    //這個Activity是主畫面，會顯示使用者資料、連接藍芽與設備、註冊使用者與設備、顯示歷史紀錄
    //一偵測到屁就會顯示分析結果！

    //藍芽模組
    private AlertDialog equipList;
    private AlertDialog history_info;
    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;
    private Handler mHandler;
    // Our main handler that will receive callback notifications
    private ConnectedThread mConnectedThread;
    // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null;
    // bi-directional client-to-client data path

    private static final UUID BTMODULEUUID = UUID.fromString
            ("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1;
    // used to identify adding bluetooth names
    private final static int MESSAGE_READ = 2;
    // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3;
    // used in bluetooth handler to identify message status
    private  String _recieveData = "";
    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                mBTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    Button bluetoothSetBtn;
    Button historyBtn;
    TextView connectStatus;
    TextView response;
    TextView userData;
    ListView historyListView;
    ListView mDevicesListView;
    String userName;
    String userID;

    private History[] historyList;
    ArrayAdapter<String> historyArrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = this.getIntent();
        //取得傳遞過來的資料
        userName = intent.getStringExtra("Name");
        userID = intent.getStringExtra("ID");
        System.out.println(userName + " : " + userID);

        //初始化視窗物件
        bluetoothSetBtn = (Button)findViewById(R.id.bluetooth_btn);
        historyBtn = (Button)findViewById(R.id.history_btn);
        userData = (TextView)findViewById(R.id.main_userdata);
        connectStatus = (TextView)findViewById(R.id.equip_status);
        //historyList = (ListView)findViewById(R.id.history_list);
        mDevicesListView = (ListView)findViewById(R.id.device_list);
        response = (TextView)findViewById(R.id.response);
        historyArrayAdapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1);


        mBTArrayAdapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        // get a handle on the bluetooth radio

        mDevicesListView = (ListView)findViewById(R.id.device_list);
        mDevicesListView.setAdapter(mBTArrayAdapter); // assign model to view
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);

        connectStatus.setText("尚未連接設備");

        //設定使用者資料
        userData.setText(userName + "您好！");
        //拿到歷史紀錄
        new Thread() {
            public void run() {
                try {
                    historyList = functionList.getHistory();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        //設定歷史紀錄Button
        historyBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {

                //清空歷史表單
                historyArrayAdapter.clear();

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                //String shopName = marker.getTitle();
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.history_list, null, false);
                historyListView = (ListView) view.findViewById(R.id.listView_showHistory);
                historyListView.setAdapter(historyArrayAdapter);
                historyListView.setOnItemClickListener(historyClickListener);
                if(historyList.length > 0)
                {
                    for(int i = 0;i < historyList.length;i++)
                    {
                        historyArrayAdapter.add(historyList[i].getDate() + "\n" + historyList[i].getResult());
                    }
                }
                else
                {
                    historyArrayAdapter.add("您還沒有任何紀錄哦！");
                }

                //設定頁面
                builder.setView(view);

                //設定取消按鈕
                builder.setNegativeButton("返回主畫面", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {

                    }
                });
                //show出畫面
                equipList = builder.create();
                equipList.show();
            }});

        //藍芽偵測與Handler宣告
        // 詢問藍芽裝置權限
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        //定義執行緒 當收到不同的指令做對應的內容
        mHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == MESSAGE_READ){ //收到MESSAGE_READ 開始接收資料
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                        readMessage =  readMessage.substring(0,1);
                        //取得傳過來字串的第一個字元，其餘為雜訊
                        _recieveData += readMessage; //拼湊每次收到的字元成字串
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    response.setText(_recieveData); //將收到的字串呈現在畫面上

                }

                if(msg.what == CONNECTING_STATUS){
                    //收到CONNECTING_STATUS 顯示以下訊息
                    if(msg.arg1 == 1)
                        connectStatus.setText("已配對 : "
                                + (String)(msg.obj));
                    else
                        connectStatus.setText("配對失敗QQ");
                }
            }
        };

        //設定藍芽按鈕
        bluetoothSetBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {

                //先開藍芽再說
                if (!mBTAdapter.isEnabled()) {//如果藍芽沒開啟
                    Intent enableBtIntent = new
                            Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);//跳出視窗
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    //開啟設定藍芽畫面
                    connectStatus.setText("藍芽打開了！");
                    Toast.makeText(getApplicationContext(),"藍芽打開啦！太感動了",Toast.LENGTH_SHORT).show();
                }

                //再來先顯示有連接過的藍芽設備，再慢慢往下加搜尋到的設備
                listPairedDevices(v);
                discover(v);
            }});

    }

    private void listPairedDevices(View view)
    {
        //先清空表單
        mBTArrayAdapter.clear();
        mPairedDevices = mBTAdapter.getBondedDevices();
        if(mBTAdapter.isEnabled()) {
            // put it's one to the adapter
            for (BluetoothDevice device : mPairedDevices)
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
        }
    }

    private void discover(View view)
    {
        // Check if the device is already discovering
        if(mBTAdapter.isDiscovering()){ //如果已經找到裝置
            mBTAdapter.cancelDiscovery(); //取消尋找
            Toast.makeText(getApplicationContext(),"搜尋中斷！你按太多下了！",Toast.LENGTH_SHORT).show();
        }
        else{
            if(mBTAdapter.isEnabled()) { //如果沒找到裝置且已按下尋找
                mBTArrayAdapter.clear(); // clear items
                mBTAdapter.startDiscovery(); //開始尋找
                Toast.makeText(getApplicationContext(), "開始搜尋囉～",
                        Toast.LENGTH_SHORT).show();
                registerReceiver(blReceiver, new
                        IntentFilter(BluetoothDevice.ACTION_FOUND));
            }
            else{
                Toast.makeText(getApplicationContext(), "你還沒開藍芽啊！",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new
            AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

                    if(!mBTAdapter.isEnabled()) {
                        Toast.makeText(getBaseContext(), "你還沒開藍芽啊！",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    connectStatus.setText("嘗試配對中...");
                    // Get the device MAC address, which is the last 17 chars in the View
                    String info = ((TextView) v).getText().toString();
                    final String address = info.substring(info.length() - 17);
                    final String name = info.substring(0,info.length() - 17);

                    // Spawn a new thread to avoid blocking the GUI one
                    new Thread()
                    {
                        public void run() {
                            boolean fail = false;
                            //取得裝置MAC找到連接的藍芽裝置
                            BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                            try {
                                mBTSocket = createBluetoothSocket(device);
                                //建立藍芽socket
                            } catch (IOException e) {
                                fail = true;
                                Toast.makeText(getBaseContext(), "Socket 建立失敗",
                                        Toast.LENGTH_SHORT).show();
                            }
                            // Establish the Bluetooth socket connection.
                            try {
                                mBTSocket.connect(); //建立藍芽連線
                            } catch (IOException e) {
                                try {
                                    fail = true;
                                    mBTSocket.close(); //關閉socket
                                    //開啟執行緒 顯示訊息
                                    mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                            .sendToTarget();
                                } catch (IOException e2) {
                                    //insert code to deal with this
                                    Toast.makeText(getBaseContext(), "Socket 建立失敗",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                            if(fail == false) {
                                //開啟執行緒用於傳輸及接收資料
                                mConnectedThread = new ConnectedThread(mBTSocket);
                                mConnectedThread.start();
                                //開啟新執行緒顯示連接裝置名稱
                                mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                                        .sendToTarget();
                            }
                        }
                    }.start();
                }
            };

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws
            IOException {
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connection with BT device using UUID
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if(bytes != 0) {
                        SystemClock.sleep(100);
                        //pause and wait for rest of data
                        bytes = mmInStream.available();
                        // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer, 0, bytes);
                        // record how many bytes we actually read
                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget(); // Send the obtained bytes to the UI activity
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    private AdapterView.OnItemClickListener historyClickListener = new
            AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
                    //arg2是表示第幾個項目被點擊了~
                    AlertDialog.Builder historyBuilder = new AlertDialog.Builder(MainActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View view = inflater.inflate(R.layout.history_info, null, false);
                    TextView history_id = (TextView) view.findViewById(R.id.history_id);
                    TextView history_date = (TextView) view.findViewById(R.id.history_date);
                    TextView history_result = (TextView) view.findViewById(R.id.history_result);
                    ImageView history_img = (ImageView) view.findViewById(R.id.history_img);
                    //設定文字部分的板塊
                    history_id.setText(historyList[arg2].gethistory_ID());
                    history_date.setText(historyList[arg2].getDate());
                    history_result.setText(historyList[arg2].getResult());

                    //設定結果圖片
                    history_img.setImageBitmap(historyList[arg2].getResult_img());

                    //設定頁面
                    historyBuilder.setView(view);

                    //設定取消按鈕
                    historyBuilder.setNegativeButton("返回歷史紀錄清單", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {

                        }
                    });
                    //show出畫面
                    history_info = historyBuilder.create();
                    history_info.show();
                }
            };
}
