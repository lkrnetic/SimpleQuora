package com.example.projekt_lk_00;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import com.example.projekt_lk_00.pojo.Greska;
import com.example.projekt_lk_00.pojo.Question;
import com.example.projekt_lk_00.pojo.Questions;

public class QuestionsView  extends AppCompatActivity {
    public ArrayList<Question> questions = new ArrayList<Question>();
    public ListView lista;
    private Greska err = new Greska();
    private ArrayAdapter<String> adapter;
    private String wsUrl, question, id_question;
    private Context con;
    public Intent intent;
    String user_id, username;
    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.questions);
        lista = (ListView) findViewById(R.id.list);
        con = this;
        lista.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>()));
        wsUrl = "https://racunalna-znanost.com.hr/pod_luka/question/questions.php";
        new WSPregledHelper(this).execute(wsUrl);
        intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        username = intent.getStringExtra("username");
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String izabrani = (String) lista.getAdapter().getItem(position);
                Integer i = position;
                Bundle bundle = new Bundle();
                question = questions.get(i).getQuestion_text();
                id_question = questions.get(i).getId();
                //Log.d("izabrani", questions.get(i).getId());
                Intent intent1 = new Intent(con, QuestionView.class);
                intent1.putExtra("question_id", id_question);
                intent1.putExtra("question_text", question);
                intent1.putExtra("user_id", user_id);
                intent1.putExtra("username", username);
                //bundle.putSerializable("questions", questions);
                intent1.putExtras(bundle);
                startActivity(intent1);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(questions.size() != 0){
            questions.clear();
            adapter.clear();
            adapter.notifyDataSetChanged();
            //question_id = intent.getStringExtra("question_id");
            //tv_question_text.setText(intent.getStringExtra("question_text"));
            new QuestionsView.WSPregledHelper(QuestionsView.this).execute(wsUrl);

        }
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create_question:
                //Intent intent = new Intent(this, Postavke.class);
                Intent intent = new Intent(QuestionsView.this, CreateQuestion.class);
                intent.putExtra("id", id_question);
                intent.putExtra("user_id", user_id);
                intent.putExtra("username", username);
                startActivityForResult(intent, 7);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private static class WSPregledHelper extends AsyncTask<String, Void, Question[]> {
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
        private WeakReference<QuestionsView> activityReference;
        // only retain a weak reference to the activity
        WSPregledHelper(QuestionsView context) {
            activityReference = new WeakReference<>(context);
        }
        @Override
        protected Question[] doInBackground(String... urls) {
            int br = urls.length;
            // šaljemo samo 1 parametar (1 URL), iako metoda može primiti polje parametara
            HttpURLConnection conn = null;
            try {
                // povezujemo se sa zadanim URL-om pomoću GET metode
                conn = (HttpURLConnection) new URL(urls[0]).openConnection();
                // postavljamo kodnu stranicu da bi se znakovi prikazali ispravno
                conn.setRequestProperty("Accept-Charset", "UTF-8");
                // vijestistimo HTTP GET metodu za dohvat
                conn.setRequestMethod("GET");
                // dohvaćamo podatke u obliku ulaznog niza
                // ako su podaci u redno dohvaćeni (HTTP kod 200)
                if (conn.getResponseCode() == 200) {
                    // pretvaramo ulazni InputStream u String
                    String res = inputStreamToString(conn.getInputStream());
                    String res2 = res.substring(2, res.length()-1);
                    //JSONObject response = new JSONObject(res);
                    Gson gson = new Gson();
                    Questions questions = gson.fromJson(res2, Questions.class);
                    // metodi onPostExecute šalje se polje objekata tipa User kako bi se
                    // lista popunila podacima pročitanih korisnika
                    return questions.getQuestions();                    // metodi onPostExecute šalje se polje objekata tipa Vijest kako bi se
                    // lista popunila podacima pročitanih vijestisnika
                } else {
                    // Inače se vratila greška, pa dohvati poruku greške i pretvori ju u String
                    // vijestisti se ErrorStream, ane InputStream koji vraća web servis i pretvaramo ga u JSON String
                    String res = inputStreamToString(conn.getErrorStream());
                    // parsiramo podatke JSON formata u objekt tipa Greska
                    Gson gson = new Gson();
                    // Ažuriramo informaciju o grešci, ako se dogodila
                    activityReference.get().err = gson.fromJson(res, Greska.class);
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null)
                    conn.disconnect();
            }
            return null;
        }
        /*
    Pomoćna metoda koja dohvaća String iz primljenog input ili error streama
        */
        private String inputStreamToString(InputStream is) {
            Scanner s = new Scanner(is).useDelimiter("\\A");
            String res = s.hasNext() ? s.next() : "";
            s.close();
            return res;
        }
        @Override
        protected void onPostExecute(Question[] rez) {
            // get a reference to the activity if it is still there
            // Dogodila se greška kod dohvata
            QuestionsView activity = activityReference.get();
            activity.adapter = (ArrayAdapter<String>) activity.lista.getAdapter();

            if (activity == null || activity.isFinishing()) return;
            // Dogodila se greška kod dohvata
            if (rez == null){
                //Toast.makeText(activity, activity.err.getError(), Toast.LENGTH_LONG).show();
                return;
            }
            // Inače ažuriraj listu
            //activity.kor = rez;
            for(int i = 0;i < rez.length; i++){
                activity.questions.add(rez[i]);
                activity.adapter.add(rez[i].getQuestion_text());
            }
            // nakon što dohvati podatke, stvara se adapter za pregled
            activity.adapter = (ArrayAdapter<String>) activity.lista.getAdapter();
            // adapter se povezuje s listom, a podaci prikazuju na ekranu
            activity.adapter.notifyDataSetChanged();
        }
    }
}