package com.example.nettychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.example.nettychat.Fragment.ChatFragment;
import com.example.nettychat.Fragment.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NavigationActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    Fragment selectedFragment = null;
    private FragmentTransaction fragmentTransaction;
    private String mJsonString,mJsonString1;                         // JSON 값을 저장할 String 변수

    private String user_idx;
    private String email;

    private SharedPreferences sf;
    private SharedPreferences sf_idx;

    private static String IP_ADDRESS="13.125.232.78/netty";  // 퍼블릭 IPv4 주소
    private static String TAG = "스토리";          // 로그에 사용할 태그


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);


        //로그인 저장 정보
        sf = getSharedPreferences("LOGIN",MODE_PRIVATE);
        email = sf.getString("et_email","");

        bottomNavigationView = findViewById(R.id.bottom_navigaion);


        //내프로필 불러오는 함수들//////////////////////////////
        // 그 다음 AsyncTask 객체를 만들어 execute()한다
        GetData1 task1 = new GetData1();

        // execute() 사용 시 DB의 값을 JSON 형태로 가져오는 코드가 적힌 php 파일의 경로를 적어
        // AsyncTask로 값들을 JSON 형태로 가져올 수 있게 한다
        task1.execute( "http://" + IP_ADDRESS + "/my_info.php?email="+email, "");
        ////////////////////////////////////////////////////

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        fragmentTransaction = getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_chat:
                            selectedFragment = new ChatFragment();
                            break;
                    }
                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedFragment).commit();
                    }

                    return true;
                }
            };

    /* HTTPUrlConnection을 써서 POST 방식으로 phpmyadmin DB에서 값들을 가져오는 AsyncTask 클래스 정의 */
    private class GetData1 extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        /* AsyncTask 작업 시작 전에 UI 처리할 내용을 정의하는 함수 */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

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
                mJsonString1 = result;
                showResult1();
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
    private void showResult1() {

        String TAG_JSON="user_info";                         // JSON 배열의 이름

        /* DB 컬럼명을 적는 String 변수 */
        String TAG_USER_IDX = "user_idx";                      // 정적 이미지(나중에 DB에서 이미지 뽑아올 것)
        String TAG_NAME = "name";     // 밑으로 3개는
        String TAG_PROFILE = "profile";
        String TAG_EMAIL = "email";

        try {

            // JSON 배열로 받아오기 위해 JSONObject, JSONArray 차례로 선언
            JSONObject jsonObject = new JSONObject(mJsonString1);

            // JSONArray에는 root를 넣어서 root란 이름의 JSON 배열을 가져올 수 있도록 한다
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            // for문으로 JSONArray의 길이만큼 반복해서 String 변수에 담는다
            for(int i = 0; i < jsonArray.length(); i++){

                JSONObject item = jsonArray.getJSONObject(i);

                // 컬럼의 값들을 getString()으로 받아와서 String 변수에 저장
                // 정적 이미지는 나중에 DB에서 받아와야 한다. 일단 기본 이미지 채워넣음
                String name = item.getString(TAG_NAME);
                user_idx = item.getString(TAG_USER_IDX);   // 운동 이름
                String profile = item.getString(TAG_PROFILE);
                //   String email = item.getString(TAG_EMAIL);

                //성공했을경우 회원가입 성공이라는 쉐어드에 담아 메인으로 넘어가게끔 한다.
                SharedPreferences grant = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                SharedPreferences.Editor editor = grant.edit();
                editor.putString("name", name);
                editor.putString("user_idx", user_idx);
                editor.putString("profile", profile);
                editor.apply();


            }

        } catch (JSONException e) {
            // 에러 뜨면 결과 출력
            Log.e(TAG, "showResult : ", e);
        }

    }   // showResult() end
}
