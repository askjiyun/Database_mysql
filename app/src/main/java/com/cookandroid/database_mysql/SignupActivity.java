package com.cookandroid.database_mysql;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignupActivity extends AppCompatActivity {

    private static String IP_ADDRESS = "서버 IP주소";
    private static String TAG = "phpsignup";
    private EditText mEditTextName;
    private EditText mEditTextPassword;
    private EditText mEditTextEmail;
    private EditText mEditTextStudentid;
    private TextView mTextViewResult;
    private boolean validate=false;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mEditTextName = (EditText)findViewById(R.id.et_name);
        mEditTextPassword = (EditText)findViewById(R.id.et_pass);
        mEditTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        mEditTextEmail = (EditText)findViewById(R.id.et_email);
        mEditTextStudentid = (EditText)findViewById(R.id.et_std);
        mTextViewResult = (TextView)findViewById(R.id.textView_result);

        mTextViewResult.setMovementMethod(new ScrollingMovementMethod());

//가입하기 버튼 클릭시
        Button buttonInsert = (Button)findViewById(R.id.btn_register);
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Name = mEditTextName.getText().toString();
                String Std = mEditTextStudentid.getText().toString();
                String Email = mEditTextEmail.getText().toString();
                String Password = mEditTextPassword.getText().toString();

                InsertData task = new InsertData();
                task.execute("http://10.0.2.2:80/Signup.php", Name,Std,Email,Password);

                mEditTextName.setText("");
                mEditTextStudentid.setText("");
                mEditTextEmail.setText("");
                mEditTextPassword.setText("");


                AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                dialog = builder.setMessage("축하합니다.회원가입에 성공하셨습니다!")
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                SignupActivity.this.startActivity(intent);
                            }
                        })

                        .create();
                dialog.show();

            }
        });

//로그인 버튼 클릭시 로그인 화면으로 이동
        Button btn_login=(Button)findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent =new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(SignupActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG, "POST response  - " + result);

        }

        @Override
        protected String doInBackground(String... params) {

            String username = (String) params[1];
            String studentid = (String) params[2];
            String emails = (String) params[3];
            String passwords = (String) params[4];

            String serverURL = (String) params[0];
            String postParameters = "username=" + username + "&studentid=" + studentid + "&emails=" + emails + "&passwords=" + passwords;


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
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
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
}