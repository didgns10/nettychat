package com.example.nettychat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nettychat.SignRetrofit.ApiClient;
import com.example.nettychat.SignRetrofit.ApiInterface;
import com.example.nettychat.SignRetrofit.ProfileClass;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignActivity  extends AppCompatActivity {

    private EditText et_name;
    private Button btn_start;
    private ImageView imgv_profile;
    private EditText et_email;
    private EditText et_pw;
    private TextView tv_result;

    private String name;
    private String email;
    private String pw;
    private String profileimg;
    private Boolean signok;
    private Bitmap bitmap;

    private static final int IMG_REQUEST = 777;
    private static String IP_ADDRESS = "13.125.232.78";
    private static String TAG = "phptest";
    private SharedPreferences sf;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);


        et_email = (EditText) findViewById(R.id.et_femail);
        et_name = (EditText) findViewById(R.id.et_name);
        et_pw = (EditText)findViewById(R.id.et_pw);
        btn_start = (Button)findViewById(R.id.btn_start);
        tv_result = (TextView)findViewById(R.id.tv_result);
        imgv_profile = (ImageView)findViewById(R.id.imgv_profile);

        //저장된 값을 불러오기 위해 같은 네임파일을 찾음.
        sf = getSharedPreferences("LOGIN",MODE_PRIVATE);
        email = sf.getString("et_email","");
        Log.e("login",email);



        //이미지프로필을 클릭하면
        imgv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seletImage();
            }
        });

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadImage();


            }
        });

    }
    //프로필 버튼을 클릭하게 되면 앨범에서 선택하게 해주는 함수
    private void seletImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMG_REQUEST);
    }

    // 레트로핏을 통한 프로필 업로드 과정
    private void uploadImage(){


        name = et_name.getText().toString().trim();
        email = et_email.getText().toString().trim();
        pw = et_pw.getText().toString().trim();
        profileimg = imageToString();


        Log.e("로그0",name);
        Log.e("로그0",email);
        Log.e("로그0",pw);
        Log.e("로그0",profileimg);

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<ProfileClass> call = apiInterface.uploadImage1(email,name,pw,profileimg);

        call.enqueue(new Callback<ProfileClass>() {
            @Override
            public void onResponse(Call<ProfileClass> call, Response<ProfileClass> response) {

                ProfileClass profileClass = response.body();
                Log.e("로그0",response.body().getResponse()+"");

                if(profileClass.getResponse().equals("no")){
                    Toast.makeText(SignActivity.this,"빈칸없이 채워주세요",Toast.LENGTH_SHORT).show();
                }else if(profileClass.getResponse().equals("yes")){
                    //성공했을경우 회원가입 성공이라는 쉐어드에 담아 메인으로 넘어가게끔 한다.
                    SharedPreferences grant = getSharedPreferences("LOGIN", MODE_PRIVATE);
                    SharedPreferences.Editor editor = grant.edit();
                    editor.putString("et_email", email);
                    editor.apply();


                    Toast.makeText(SignActivity.this,"회원가입을 완료했습니다.",Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SignActivity.this, NavigationActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(SignActivity.this,"회원가입에 실패하셨습니다.",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ProfileClass> call, Throwable t) {

            }
        });

    }

    //앨범에서 가져오는 데이터값들
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMG_REQUEST && resultCode==RESULT_OK && data!=null){
            Uri path = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),path);
                bitmap = Bitmap.createScaledBitmap(bitmap,700,900,true);
                imgv_profile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //이미지의 절대경로를 비트맥 형식을 스트링 값으로바꿔주는 함수
    private String imageToString(){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgByte,Base64.DEFAULT);
    }



}
