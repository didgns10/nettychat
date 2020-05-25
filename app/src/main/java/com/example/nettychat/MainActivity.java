package com.example.nettychat;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
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


    private static String IP_ADDRESS="13.125.232.78/netty";  // 퍼블릭 IPv4 주소
    private static String TAG = "스토리";          // 로그에 사용할 태그
    private String mJsonString,mJsonString1;                         // JSON 값을 저장할 String 변수

    private ImageButton bt_send;
    private EditText et_message;
    private String formatDate;

    private MessageAdapter mAdapter;
    private ArrayList<MessageData> mArrayList;
    private RecyclerView recyclerView;

    private  String receive_name;
    private String receive_date;
    private String receive_msg;

    private String room_idx,email;
    private String name,user_idx,profile;

    private SharedPreferences sf;
    private SharedPreferences sf_idx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_send = findViewById(R.id.btn_send);
        et_message = findViewById(R.id.messageBox);
        recyclerView = findViewById(R.id.messageList);


        //로그인 저장 정보
        sf = getSharedPreferences("LOGIN",MODE_PRIVATE);
        email = sf.getString("et_email","");


        sf_idx = getSharedPreferences("USER_INFO",MODE_PRIVATE);
        name = sf_idx.getString("name","");
        user_idx = sf_idx.getString("user_idx","");
        profile = sf_idx.getString("profile","");

        room_idx = getIntent().getStringExtra("room_idx");

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

        // execute() 사용 시 DB의 값을 JSON 형태로 가져오는 코드가 적힌 php 파일의 경로를 적어
        // AsyncTask로 값들을 JSON 형태로 가져올 수 있게 한다

        GetData task = new GetData();

        task.execute( "http://" + IP_ADDRESS + "/message.php?room_idx="+room_idx+"&email="+email, "");

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
    /* HTTPUrlConnection을 써서 POST 방식으로 phpmyadmin DB에서 값들을 가져오는 AsyncTask 클래스 정의 */
    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        /* AsyncTask 작업 시작 전에 UI 처리할 내용을 정의하는 함수 */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mArrayList.clear();

/*            // 프래그먼트에 프로그레스 다이얼로그를 띄우고, 값이 가져와지는 동안 기다리라는 메시지를 띄운다
            // 마찬가지로 프래그먼트를 쓰기 때문에 context 대신 getActivity() 사용
            progressDialog = ProgressDialog.show(StoryVIewActivity.this,
                    "Please Wait",
                    null,
                    true,
                    true);*/
        }

        /* AsyncTask 작업 종료 후 UI 처리할 내용을 정의하는 함수 */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

     /*       // 프로그레스 다이얼로그를 죽이고
            progressDialog.dismiss();*/

            // doInBackground()의 리턴값이 담긴 result를 버튼 밑 텍스트뷰에 setText()해서 JSON 형태로 받아온 값들을 출력
//            mTextViewResult.setText(result);
            Log.e(TAG, "response - " + result);

            // 결과가 없으면 에러 때문에 못 받아온 거니까 에러 문구를 버튼 밑 텍스트뷰에 출력
            if (result == null) {
//                mTextViewResult.setText(errorString);
            } else {
                // 결과가 있다면 버튼 위 텍스트뷰에 JSON 데이터들을 텍스트뷰 형태에 맞게 출력한다
                mJsonString = result;
                showResult();
            }
        }

        /* AsyncTask가 수행할 작업 내용을 정의하는 함수 */
        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];       // IP_ADDRESS에 적은 퍼블릭 IPv4 주소를 저장할 변수
//            Log.e("params[0] : ", params[0].toString());
            String postParameters = params[1];  // HttpUrlConnection 결과로 얻은 Request body에 담긴 내용들을 저장할 변수
//            Log.e("params[1] : ", params[1].toString());


            try {

                URL url = new URL(serverURL);
                Log.e("확인",serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

    /* DB 테이블 컬럼의 값들을 JSON 형태로 받아와서 리사이클러뷰에 연결된 ArrayList에 박는 함수 */
    private void showResult() {

        String TAG_JSON="messages";                         // JSON 배열의 이름

        /* DB 컬럼명을 적는 String 변수 */
        String TAG_MSG_IDX = "message_idx";     // 밑으로 3개는
        String TAG_MSG = "message";
        String TAG_DATE= "datem";
        String TAG_IMG = "image";     // 밑으로 3개는
        String TAG_NAME = "name";
        String TAG_EMAIL = "email";
        String TAG_SERVER = "server";
        String TAG_PROFILE = "profile";


        try {

            // JSON 배열로 받아오기 위해 JSONObject, JSONArray 차례로 선언
            JSONObject jsonObject = new JSONObject(mJsonString);

            // JSONArray에는 root를 넣어서 root란 이름의 JSON 배열을 가져올 수 있도록 한다
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            // for문으로 JSONArray의 길이만큼 반복해서 String 변수에 담는다
            for(int i = 0; i < jsonArray.length(); i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String message_idx = item.getString(TAG_MSG_IDX);
                String message = item.getString(TAG_MSG);
                String date = item.getString(TAG_DATE);
                String image = item.getString(TAG_IMG);
                String name = item.getString(TAG_NAME);
                String email = item.getString(TAG_EMAIL);
                String server = item.getString(TAG_SERVER);
                String profile = item.getString(TAG_PROFILE);


                MessageData messageData = new MessageData();

                messageData.setMessage(message);
                messageData.setDatetime(date);
                messageData.setName(name);
                messageData.setEmail(email);
                messageData.setServer(server);
                messageData.setImage(image);
                messageData.setMessage_idx(message_idx);
                messageData.setOpp_profile_img(profile);


                mArrayList.add(messageData);
                mAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
            }

        } catch (JSONException e) {
            // 에러 뜨면 결과 출력
            Log.e(TAG, "showResult : ", e);
        }

    }   // showResult() end

}


