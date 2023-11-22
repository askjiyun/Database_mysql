package com.cookandroid.database_mysql;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private static String TAG = "phplogin";
    private static final String TAG_JSON = "users";
    private static final String TAG_NAME = "username";
    private static final String TAG_PASS = "passwords";
    private static final String TAG_EMAIL = "emails";
    private static final String TAG_STD = "studentid";
    private TextView mTextViewResult;
    ArrayList<HashMap<String, String>> mArrayList;
    ListView mListViewList;
    private EditText mEditTextStd, mEditTextPass;
    Button btn_login, btn_register;
    private String mJsonString;
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView loginText = (TextView) findViewById(R.id.loginText);
        ImageView loginImage = (ImageView) findViewById(R.id.loginImage);
        CheckBox rememberCheckBox = (CheckBox) findViewById(R.id.rememberCheckBox);
        TextView forgotText = (TextView) findViewById(R.id.forgotText);

        mTextViewResult = (TextView)findViewById(R.id.textView_main_result);
        mListViewList = (ListView) findViewById(R.id.listView_main_list);
        mEditTextStd = (EditText) findViewById(R.id.et_std);
        mEditTextPass = (EditText) findViewById(R.id.et_pass);

        mEditTextPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

//join us 버튼 클릭 시 회원가입 화면으로 이동
        btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });


//login 버튼 클릭 시
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                mArrayList.clear();


                GetData task = new GetData();
                task.execute( mEditTextStd.getText().toString(), mEditTextPass.getText().toString());

            }
        });


        mArrayList = new ArrayList<>();

    }

    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(LoginActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG, "response - " + result);

            if (result == null){
                mTextViewResult.setText(errorString);
            }
            else {
                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String userStd = (String)params[0];
            String userPassword = (String)params[1];

            String serverURL = "http://10.0.2.2:80/Login.php";
            String postParameters = "studentid=" + userStd + "&passwords=" + userPassword;


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

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


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }

    private void showResult(){

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String username = item.getString(TAG_NAME);
                String userPassword = item.getString(TAG_PASS);
                String email = item.getString(TAG_EMAIL);
                String studentid = item.getString(TAG_STD);

                HashMap<String,String> hashMap = new HashMap<>();

                hashMap.put(TAG_NAME, username);
                hashMap.put(TAG_STD, studentid);
                hashMap.put(TAG_EMAIL, email);
                hashMap.put(TAG_PASS, userPassword);

                mArrayList.add(hashMap);


                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                dialog = builder.setMessage("Login.")
                        .setNegativeButton("Success", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 로그인 성공 시에 수행할 작업을 여기에 추가
                                // 예: 특정 화면으로 이동, 다른 작업 수행 등
                            }
                        })
                        .create();
                dialog.show();

                return;
            }


            ListAdapter adapter = new SimpleAdapter(
                    LoginActivity.this, mArrayList, R.layout.user_list,
                    new String[]{TAG_NAME, TAG_PASS, TAG_EMAIL, TAG_STD},
                    new int[]{R.id.textView_list_name, R.id.textView_list_pass, R.id.textView_list_email, R.id.textView_list_std}
            );

            mListViewList.setAdapter(adapter);

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }


    }
}