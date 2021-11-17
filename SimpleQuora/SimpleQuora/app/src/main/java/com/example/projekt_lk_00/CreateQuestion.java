package com.example.projekt_lk_00;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.projekt_lk_00.pojo.Answer;
import com.example.projekt_lk_00.pojo.Greska;
import com.example.projekt_lk_00.pojo.Question;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class CreateQuestion extends AppCompatActivity {
    EditText et_question;
    String question;
    private Intent intent;
    private Context con;
    private String wsUrl = "https://racunalna-znanost.com.hr/pod_luka/question/questions.php/pitanje";
    private Greska err = new Greska();
    private Question question_obj;
    private String user_id;
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.create_question);
        et_question = findViewById(R.id.et_create_question);
        con = this;
        intent = getIntent();
        user_id = intent.getStringExtra("user_id");

    }
    public void createQuestion(View v){
        question = et_question.getText().toString();
        Gson gson = new Gson();
        Question que = new Question("1",user_id, question);
        String que_pom = gson.toJson(que, Question.class);
        new WSPregledHelper(this).execute(wsUrl,"POST",que_pom);
    }
    private static class WSPregledHelper extends AsyncTask<String, Void, String> {
        // How to use a static inner AsyncTask class
        //
        // To prevent leaks, you can make the inner class static. The problem
        // with that, though, is that you no longer have access to the Activity's
        // UI views or member variables. You can pass in a reference to the
        // Context but then you run the same risk of a memory leak. (Android can't
        // garbage collect the Activity after it closes if the AsyncTask class has
        // a strong reference to it.)
        // The solution is to make a weak reference to the Activity (or whatever
        // Context you need).
        // https://stackoverflow.com/questions/44309241/warning-this-asynctask-class-should-be-static-or-leaks-might-occur#answer-46166223
        private WeakReference<CreateQuestion> activityReference;
        // only retain a weak reference to the activity
        public WSPregledHelper(CreateQuestion context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(String... urls) {
            int br = urls.length;
            // šaljemo samo 1 parametar (1 URL), iako metoda može primiti polje parametara
            HttpURLConnection conn = null;
            try {
                // metoda prima više parametara:
                // 1. parametar - URL web servisa
                // 2. parametar - metoda (POST, PUT, DELETE)
                // 3. parametar - string s JSON porukom koja se šalje
                conn = (HttpURLConnection) new URL(urls[0]).openConnection();
                // postavljamo kodnu stranicu da bi se znakovi prikazali ispravno
                conn.setRequestProperty("Accept-Charset", "UTF-8");
                // koristi se zadane metoda (GET, POST, PUT ili DELETE)
                //conn.setRequestMethod(urls[1]);
                conn.setRequestMethod("POST");
                // POST i PUT primaju parametre u tijelu upita (OutputStream)
                if (urls[1].equals("POST") || urls[1].equals("PUT")) {
                    conn.setDoOutput(true);
                    OutputStream output = conn.getOutputStream();
                    output.write(urls[2].getBytes("UTF-8"));
                    output.close();
                    // Ako je obrada prošla u redu - dohvati povratnu poruku (InputStream) i pretvori ju u String
                    if (conn.getResponseCode() == 200 || conn.getResponseCode() == 201) {
                        // Dohvaćamo InputStream koji vraća web servis i pretvaramo ga u JSON String
                        String res = inputStreamToString(conn.getInputStream());
                        // parsiramo podatke JSON formatu u objekt tipa Users
                        Gson gson = new Gson();
                        Question question_pom = gson.fromJson(res, Question.class);
                        activityReference.get().question_obj = question_pom;
                        // metodi onPostExecute šalje se id korisnika
                        return question_pom.getQuestion_text();
                    } else {
                        // Inače se vratila greška, pa dohvati poruku greške i pretvori ju u String
                        // Koristi se ErrorStream, ane InputStream koji vraća web servis i pretvaramo ga u JSON String
                        String res = inputStreamToString(conn.getErrorStream());
                        Gson gson = new Gson();
                        activityReference.get().err = gson.fromJson(res, Greska.class);
                        return null;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                activityReference.get().err.setError(e.getMessage());
            } finally {
                if (conn != null)
                    conn.disconnect();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String answer_text){
            CreateQuestion activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;
            // Uspješna obrada
            if (answer_text != null) {
                //activity.id.setText(Integer.toString(answer_text));
                // brisanje uspješno
                Log.d("answer_text",answer_text);
                /*
                if (tid == 0){
                    activity.id.setText("");
                    activity.korisnik.setText("");
                    activity.ime.setText("");
                    activity.id.requestFocus();
                }
                else {
                    activity.id.setText(Integer.toString(activity.kor.getId()));
                    activity.korisnik.setText(activity.kor.getUsername());
                    activity.ime.setText(activity.kor.getName());
                }
                Toast.makeText(activity, "Obrada uspješna! " + tid, Toast.LENGTH_LONG).show();
                */
                Toast.makeText(activity, "Obrada uspješna! " + answer_text, Toast.LENGTH_LONG).show();

            }
            else {
                // Greška
                Toast.makeText(activity, activity.err.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private static String inputStreamToString(InputStream is){
        Scanner s = new Scanner(is).useDelimiter("\\A");
        String res = s.hasNext() ? s.next() : "";
        s.close();
        return res;
    }
}



