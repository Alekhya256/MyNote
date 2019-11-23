package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AddActivity extends AppCompatActivity {

    Button button;
    GoogleSignInClient googleSignInClient;
    Button buttonnote;
    ListView listView;
    List <NoteData> list;
    NoteData noteData;
    String editApi = "http://13.233.64.181:4000/api/editnote";
    String deleteApi = "http://13.233.64.181:4000/api/deletenote";
    String View = "http://13.233.64.181:4000/api/getnotes";
    SharedPreferences sharedPreferences;
    RequestQueue requestQueue;
    Button buttonsignout;
    NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        button = findViewById(R.id.add);
        buttonnote = findViewById(R.id.listnotes);
        buttonsignout = findViewById(R.id.signout);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(AddActivity.this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(AddActivity.this, gso);

        listView = findViewById(R.id.listview);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddClicked();

            }
        });

        buttonnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = sharedPreferences.getString("userId", "");

                try {
                    viewNote(View, userId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        buttonsignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                switch (v.getId()){
                    case R.id.signout:
                        signOut();
                        break;
                }
            }
        });

    }
    public void signOut(){
        googleSignInClient.signOut().addOnCompleteListener(AddActivity.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(AddActivity.this,"signout",Toast.LENGTH_LONG).show();
                finish();;
            }
        });
    }


    public void onResume() {
        super.onResume();
        list = new ArrayList<>();
        noteAdapter = new NoteAdapter(AddActivity.this, list);
        this.listView.setAdapter(noteAdapter);
    }

    public void onEditClicked(final String editApi,String id,String title,String description) throws JSONException {



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
                Log.e("edit respose new", String.valueOf(response));

                try {
//                    JSONObject dataobj = response.getJSONObject("data");
                    String id = response.getString("_id");
                    String title = response.getString("title");
                    String description = response.getString("description");
                    Log.e("edit values",id+"\n"+title+"\n"+description);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    public class HttpRequest extends AsyncTask<String,Void,Void>{

        String id;
        int position;
        boolean isNoteDeleted;
        HttpRequest(String id,int position){
            this.id = id;
            this.position = position;
        }

        @Override
        protected Void doInBackground(String... strings) {
            Log.e("TAG","delete id is:"+id);
            JSONObject obj = new JSONObject();
            try {
                obj.put("_id",id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String request = obj.toString();
            Log.e("TAG","request is: "+request);
            try {
                URL url = new URL(deleteApi);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestProperty("Content-Type","application/json");
                httpURLConnection.setRequestMethod("DELETE");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                DataOutputStream os = new DataOutputStream(httpURLConnection.getOutputStream());
                os.writeBytes(request);
                os.flush();
                os.close();
                httpURLConnection.connect();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String tempResp = null;
                while ((tempResp = bufferedReader.readLine()) != null){
                    stringBuilder.append(tempResp);
                }
                Log.e("DELETE","delete response: "+stringBuilder.toString());
                bufferedReader.close();
               isNoteDeleted = true;
            } catch (IOException e) {
                Log.e("DELETE","DELETE error:"+e.getLocalizedMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (isNoteDeleted) {
                noteAdapter.deleteItem(position);
            }
        }
    }

    public void onDeleteClicked(final String deleteApi, String id) throws JSONException {

        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("_id", id);
        Log.d("Delete in addactivity",jsonObject.toString());



    }

    public void onAddClicked() {
        Intent intent = new Intent(AddActivity.this, Main2Activity.class);
        startActivity(intent);
    }

    public void viewNote(String get, String userId) throws JSONException {
        list = new ArrayList<NoteData>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_id", userId);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, get, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("@@@@@@@@@@@@@@@@@", String.valueOf(response));

                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        NoteData noteData = new NoteData();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String title = jsonObject.getString("title");
                        String description = jsonObject.getString("description");
                        String id =jsonObject.getString("_id");
                        noteData.setTitle(title);
                        noteData.setDescription(description);
                        noteData.setId(id);
                        list.add(noteData);

                    }
                    for (int i = 0; i < list.size(); i++) {
                        NoteData noteData = list.get(i);
                        Log.e("data", noteData.getTitle() + "\n" + noteData.getDescription()+"\n"+noteData.getId());
                    }
                    noteAdapter = new NoteAdapter(AddActivity.this, list);
                    listView.setAdapter(noteAdapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, android.view.View view, int i, long l) {
                            noteData=list.get(i);
                            Log.e("listview",noteData.getTitle());



                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }


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

    public class NoteAdapter extends BaseAdapter {
        Context context;
        private List<NoteData> list;

        public NoteAdapter(Context context, List<NoteData> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }
        public void deleteItem(int position){
            Log.e("TAG","position to delete is:"+ position);
            if (position < list.size()) {
                list.remove(position);
                notifyDataSetChanged();
            }
        }

        @Override
        public View getView(final int pos, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(context).inflate(R.layout.view_list, viewGroup, false);
            final TextView title = view.findViewById(R.id.tv_Title);
            TextView description = view.findViewById(R.id.tv_content);
            ImageButton delete = view.findViewById(R.id.delete);
            final ImageButton edit = view.findViewById(R.id.edit);
            title.setText(list.get(pos).getTitle());
            description.setText(list.get(pos).getDescription());
            edit.setTag(pos);
            delete.setTag(pos);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(android.view.View view) {
                    String id = sharedPreferences.getString("id", "");
                    new HttpRequest(list.get(pos).getId(),pos).execute(new String[]{id});

                }
            });

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(android.view.View view) {

                    int pos = (int) edit.getTag();
                    NoteData noteData = list.get(pos);
                    Log.e("edit function notedata",noteData.id);
                   // String id = sharedPreferences.getString("id",noteData.id)
                   // String title=sharedPreferences.getString("title","");
                   // String description=sharedPreferences.getString("description","testing description");
                  Intent intent = new Intent(AddActivity.this,EditScreen.class);
                  intent.putExtra("edit",  noteData);
                  startActivity(intent);
                    NoteData noteData1 = (NoteData) getIntent().getSerializableExtra("Editing");

                }
            });
            return view;

        }

    }


}
