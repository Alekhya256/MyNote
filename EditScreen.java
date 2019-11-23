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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EditScreen extends AppCompatActivity {
    String editApi = "http://13.233.64.181:4000/api/editnote";
    EditText editTextTitle,editTextDescription;
    Button buttonSave, buttonCancel, buttonBack;
    SharedPreferences sharedPreferences;
    RequestQueue requestQueue;

    NoteData noteData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noteData= (NoteData) getIntent().getSerializableExtra("edit");
        Log.e("clickonedit",noteData.id);
        setContentView(R.layout.activity_edit_screen);
        editTextTitle = findViewById(R.id.et_title1);
        editTextDescription = findViewById(R.id.et_content1);
        buttonSave = findViewById(R.id.save);
        buttonCancel = findViewById(R.id.cancel);
        buttonBack = findViewById(R.id.btn_back);
        editTextTitle.setText(noteData.getTitle());
        editTextDescription.setText(noteData.getDescription());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(EditScreen.this);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = editTextTitle.getText().toString();
                String text = editTextDescription.getText().toString();

                try {
                    onEditClicked(editApi,noteData.id,title,text);
                    // Log.e("edited",id+"\n"+title+"\n"+description);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancleClicked();
            }

            private void onCancleClicked() {
                String title = editTextTitle.getText().toString();
                String text = editTextDescription.getText().toString();
            }
        });
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditScreen.this,Main2Activity.class);
                startActivity(intent);
                EditScreen.this.finish();
            }
        });

    }
    public void onEditClicked(final String editApi,String id,String title,String description) throws
            JSONException {



        final List<NoteData> list=new ArrayList<NoteData>();
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("_id",id);
        jsonObject.put("title", title);
        jsonObject.put("description", description);
        Log.d("AddActivity",jsonObject.toString());
        Log.e("id : ",id);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, editApi, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("edit respose newtttt", String.valueOf(response));

                try {
//                    JSONObject dataobj = response.getJSONObject("data");
                    //String id = response.getString("_id");
                    String title = response.getString("title");
                    String description = response.getString("description");
                    Log.e("edit values","\n"+title+"\n"+description);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finish();
//
//                for (int i =0;i<list.size();i++){
//
//                    NoteData noteData=list.get(i);
//                    Log.e("success",noteData.getId()+ "\n" + noteData.getTitle()+ "\n" + noteData.getDescription());
//
//                }


//                Intent intent = new Intent(AddActivity.this, Main2Activity.class);
//                intent.putExtra("title",);
//                intent.putExtra("description", );
//                startActivity(intent);


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

}

