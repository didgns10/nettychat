package com.example.nettychat;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.nettychat.Adapter.MessageAdapter;
import com.example.nettychat.Model.MessageData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity{

    Handler handler;
    private String data;
    SocketChannel socketChannel;
    private static final String HOST = "192.168.0.3";
    private static final int PORT = 5001;
    private  String msg;

    private ImageButton bt_send;
    private EditText et_message;
    private String formatDate;

    private MessageAdapter mAdapter;
    private ArrayList<MessageData> mArrayList;
    private RecyclerView recyclerView;

    private  String receive_name;
    private String receive_date;
    private String receive_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_send = findViewById(R.id.btn_send);
        et_message = findViewById(R.id.messageBox);
        recyclerView = findViewById(R.id.messageList);


        //현재 시간을 구하는 메소드
        // 현재시간을 msec 으로 구한다.
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        final Date date = new Date(now);
        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        SimpleDateFormat sdfNow = new SimpleDateFormat("MM월dd일 HH:mm");
        // nowDate 변수에 값을 저장한다.
        formatDate = sdfNow.format(date);

        //네비게이션 드로어 만드는 부분
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back); //뒤로가기 버튼 이미지 지정
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>"+"채팅방" + "</font>"));



        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mArrayList = new ArrayList<>();
        mArrayList.clear();

        mAdapter = new MessageAdapter(mArrayList,MainActivity.this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);

        mAdapter.notifyDataSetChanged();

        handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socketChannel = SocketChannel.open();
                    socketChannel.configureBlocking(true);
                    socketChannel.connect(new InetSocketAddress(HOST, PORT));
                } catch (Exception ioe) {
                    Log.d("asd", ioe.getMessage() + "a");
                    ioe.printStackTrace();

                }
                checkUpdate.start();
            }
        }).start();

        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String name = "수호";

                    final String return_msg = et_message.getText().toString();
                    String img_url = "http://13.125.232.78/images/profile_test1@naver.com.jpg";
                    JSONObject jsonObject = new JSONObject();

                    try{
                        jsonObject.put("name",name);
                        jsonObject.put("date",formatDate);
                        jsonObject.put("msg",return_msg);
                        jsonObject.put("img_url",img_url);

                    }catch (JSONException e){
                        e.printStackTrace();
                    }


                    if (!TextUtils.isEmpty(return_msg)) {
                        new SendmsgTask().execute(jsonObject.toString());

                        String server = "send";
                        MessageData messageData = new MessageData();

                        messageData.setMessage(return_msg);
                        messageData.setDatetime(formatDate);
                        messageData.setServer(server);

                        mArrayList.add(messageData);
                        mAdapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class SendmsgTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            try {
                socketChannel
                        .socket()
                        .getOutputStream()
                        .write(strings[0].getBytes("EUC-KR")); // 서버로


            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    et_message.setText("");

                }
            });
        }
    }

    void receive() {
        while (true) {
            try {
                ByteBuffer byteBuffer = ByteBuffer.allocate(256);
                //서버가 비정상적으로 종료했을 경우 IOException 발생
                int readByteCount = socketChannel.read(byteBuffer); //데이터받기
                Log.d("readByteCount", readByteCount + "");
                //서버가 정상적으로 Socket의 close()를 호출했을 경우
                if (readByteCount == -1) {
                    throw new IOException();
                }

                byteBuffer.flip(); // 문자열로 변환
                Charset charset = Charset.forName("EUC-KR");
                data = charset.decode(byteBuffer).toString();



                Log.d("receive", "msg :" + data);
                handler.post(showUpdate);
            } catch (IOException e) {
                Log.d("getMsg", e.getMessage() + "");
                try {
                    socketChannel.close();
                    break;
                } catch (IOException ee) {
                    ee.printStackTrace();
                }
            }
        }
    }

    private Thread checkUpdate = new Thread() {

        public void run() {
            try {
                String line;
                receive();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private Runnable showUpdate = new Runnable() {

        public void run() {
            String receive = data;

            JSONObject jsonObject = null;

            try {
                jsonObject = new JSONObject(data);

                receive_name = (String)jsonObject.get("name");
                receive_date = (String)jsonObject.get("date");
                receive_msg = (String)jsonObject.get("msg");
                String opp_imgurl = (String)jsonObject.get("img_url");

                Log.e("로그",receive_msg);
                Log.e("로그",receive_date);
                Log.e("로그",receive_name);
                Log.e("로그",opp_imgurl);

                String server = "receive";

                MessageData messageData = new MessageData();

                messageData.setMessage(receive_msg);
                messageData.setDatetime(receive_date);
                messageData.setName(receive_name);
                messageData.setServer(server);
                messageData.setOpp_profile_img(opp_imgurl);

                mArrayList.add(messageData);
                mAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            socketChannel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

