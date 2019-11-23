package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Main2Activity extends AppCompatActivity {
    EditText editTexttitle, editTextContent;
    Button buttonSave, buttonCancel, buttonBack;
    //Button buttonnotes;
    SharedPreferences sharedPreferences;
   // SharedPreferences.Editor editor;
    //private String noteType = "create";
    //private String id,title="",desc="";
    String create = "http://13.233.64.181:4000/api/createnote";
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // if (getIntent().getExtras().getString("type")!=null){
          //  noteType = getIntent().getExtras().getString("type");
           // id = getIntent().getExtras().getString("id");
          //  title = getIntent().getExtras().getString("title");
          //  desc = getIntent().getExtras().getString("desc");
       // }
        setContentView(R.layout.activity_main2);
        editTexttitle = findViewById(R.id.et_title);
        editTextContent = findViewById(R.id.et_content);
        buttonSave = findViewById(R.id.save);
        buttonCancel = findViewById(R.id.cancel);
        buttonBack = findViewById(R.id.btn_back);


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Main2Activity.this);
      //  editor = sharedPreferences.edit();
        //editTexttitle.setText(title);
       // editTextContent.setText(desc);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main2Activity.this, AddActivity.class);
                startActivity(intent);
                Main2Activity.this.finish();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelClicked();
            }
        });


        buttonSave.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                String title = editTexttitle.getText().toString();
                String text = editTextContent.getText().toString();
                String userId = sharedPreferences.getString("userId", "");


                try {
                    myApi(create, title, text, userId);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }


            // buttonnotes.setOnClickListener(new View.OnClickListener() {
            //  @Override
            // public void onClick(View view) {
            //     Intent intent = new Intent(Main2Activity.this, AddActivity.class);
            // startActivity(intent);


        });
    }


        private void myApi(String count, String title, String text, String userId) throws JSONException {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_id", userId);
            jsonObject.put("title", title);
            jsonObject.put("description", text);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, count, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("@@@@@@@@@@@@", String.valueOf(response));

                    try {
                        JSONObject dataobj = response.getJSONObject("data");
                        String userId = dataobj.getString("user_id");
                        String title = dataobj.getString("title");
                        String description = dataobj.getString("description");
                        String id = dataobj.getString("_id");

                        Log.e("notedatataa", userId + "\n" + title + "\n" + description + "\n" + id);
                       // editor.putString("id",id);
                       // editor.apply();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    finish();
                    Toast.makeText(Main2Activity.this, "Saved", Toast.LENGTH_LONG).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Error", String.valueOf(error));

                }
            });

            requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonObjectRequest);
        }

        public void onCancelClicked() {
            this.finish();
        }



}

