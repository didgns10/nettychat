package com.example.nettychat.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.nettychat.Adapter.FriendAdapter;
import com.example.nettychat.AddFriendActivity;
import com.example.nettychat.ChatAddActivity;
import com.example.nettychat.Model.FriendData;
import com.example.nettychat.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;


public class HomeFragment extends Fragment {


    private RecyclerView rv;
    private static String IP_ADDRESS="13.125.232.78/netty";  // 퍼블릭 IPv4 주소
    private static String TAG = "스토리";          // 로그에 사용할 태그

    private ArrayList<FriendData> mArrayList;             // 모델 클래스의 데이터를 받아 리사이클러뷰에 뿌리는 데 쓸 ArrayList
    private FriendAdapter mAdapter;                       // 리사이클러뷰 어댑터
    private String mJsonString,mJsonString1;                         // JSON 값을 저장할 String 변수

    private ImageButton button_menu;

    private CircleImageView cimgv_profile;
    private TextView tv_name;
    private String user_idx;

    private String email;
    private String name,profile;

    private SharedPreferences sf;
    private SharedPreferences sf_idx;

    public HomeFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        cimgv_profile = rootView.findViewById(R.id.cimgv_profile);
        tv_name = rootView.findViewById(R.id.tv_name);
        button_menu =rootView.findViewById(R.id.imageButton_menu);

        //로그인 저장 정보
        sf = getActivity().getSharedPreferences("LOGIN",MODE_PRIVATE);
        email = sf.getString("et_email","");

        sf_idx = getActivity().getSharedPreferences("USER_INFO",MODE_PRIVATE);
        name = sf_idx.getString("name","");
        user_idx = sf_idx.getString("user_idx","");
        profile = sf_idx.getString("profile","");

        tv_name.setText(name);
        Glide.with(this).load(profile).into(cimgv_profile);



        // 리사이클러뷰 선언
        rv = (RecyclerView) rootView.findViewById(R.id.recyclerview);

        // 프래그먼트기 때문에 context가 아니라 getActivity()를 쓴다!!!
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        // 프래그먼트기 때문에 구분선을 줄 때 context 부분에 getActivity()를 넣어야 한다
        // 그냥 getActivity()만 넣으면 노란 박스 쳐져서 안 보이게 하려고 getActivity()를 다르게 표현함

        // 리사이클러뷰에 연결되서 데이터를 뿌릴 ArrayList 선언
        mArrayList = new ArrayList<>();

        // 프래그먼트에서 리사이클러뷰의 어댑터를 붙일 땐 context 쓰는 부분에 getActivity()를 쓴다
        mAdapter = new FriendAdapter(mArrayList,getContext());
        rv.setAdapter(mAdapter);

        // 버튼 위에 표시되는 텍스트뷰에 mArrayList의 내용들을 뿌리는데 이것들을 전부 지우고
        mArrayList.clear();

        // 어댑터에 데이터가 변경됐다는 걸 알린다
        mAdapter.notifyDataSetChanged();

        button_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(getContext(), view);//v는 클릭된 뷰를 의미

                getActivity().getMenuInflater().inflate(R.menu.menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.add_friend:
                                Intent intent4 = new Intent(getContext(), AddFriendActivity.class);
                                startActivity(intent4);
                                break;
                            case R.id.add_chattion_room:
                                Intent intent = new Intent(getContext(), ChatAddActivity.class);
                                startActivity(intent);
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
                popup.show();//Popup Menu 보이기
            }

        });

        GetData task = new GetData();

        // execute() 사용 시 DB의 값을 JSON 형태로 가져오는 코드가 적힌 php 파일의 경로를 적어
        // AsyncTask로 값들을 JSON 형태로 가져올 수 있게 한다
        task.execute( "http://" + IP_ADDRESS + "/friend_view.php?user_idx="+user_idx, "");

        return rootView;
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

        String TAG_JSON="user";                         // JSON 배열의 이름

        /* DB 컬럼명을 적는 String 변수 */
        String TAG_USER_IDX = "user_idx";                      // 정적 이미지(나중에 DB에서 이미지 뽑아올 것)
        String TAG_NAME = "name";     // 밑으로 3개는
        String TAG_PROFILE = "profile";

        try {

            // JSON 배열로 받아오기 위해 JSONObject, JSONArray 차례로 선언
            JSONObject jsonObject = new JSONObject(mJsonString);

            // JSONArray에는 root를 넣어서 root란 이름의 JSON 배열을 가져올 수 있도록 한다
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            // for문으로 JSONArray의 길이만큼 반복해서 String 변수에 담는다
            for(int i = 0; i < jsonArray.length(); i++){

                JSONObject item = jsonArray.getJSONObject(i);

                // 컬럼의 값들을 getString()으로 받아와서 String 변수에 저장
                // 정적 이미지는 나중에 DB에서 받아와야 한다. 일단 기본 이미지 채워넣음
                String name = item.getString(TAG_NAME);
                String user_idx = item.getString(TAG_USER_IDX);   // 운동 이름
                String profile = item.getString(TAG_PROFILE);

                // 데이터 모델 클래스 객체 선언 후 settter()로 컬럼에서 값 추출
                FriendData friendData = new FriendData();

                friendData.setF_friend_idx(user_idx);
                friendData.setF_name(name);
                friendData.setF_profile(profile);
                friendData.setF_invite(false);


                // 데이터 모델 클래스 객체를 리사이클러뷰에 연결된 ArrayList에 삽입
                mArrayList.add(friendData);

                // ArrayList에 변동이 생겼으니 어댑터에 알림
                mAdapter.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            // 에러 뜨면 결과 출력
            Log.e(TAG, "showResult : ", e);
        }

    }   // showResult() end

}
