package com.example.projekt_lk_00;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.projekt_lk_00.pojo.Answer;
import com.example.projekt_lk_00.pojo.Answers;
import com.example.projekt_lk_00.pojo.Greska;
import com.example.projekt_lk_00.pojo.Question;
import com.example.projekt_lk_00.pojo.Questions;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
public class QuestionView extends ListActivity implements Serializable {
    private Context context;
    public Intent intent;
    String question_text, question_id, user_id, username;
    private ArrayList<Question> question_pom;
    private TextView tv_question_text;
    private String wsUrl = "https://www.racunalna-znanost.com.hr/pod_luka/question/questions.php";
    private ListView lista;
    //private ArrayAdapter<String> adapter;
    private Greska err = new Greska();
    //public ArrayList<Answer> answers = new ArrayList<Answer>();
    private Answer[] answers = null;
    private MojAdapter adapter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question);
        context = this;
        //moj_adapter = new MojAdapter(this, answers);
        intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        username = intent.getStringExtra("username");
        Bundle bundle = getIntent().getExtras();
        tv_question_text = findViewById(R.id.tv_question_text);
        question_id = intent.getStringExtra("question_id");
        tv_question_text.setText(intent.getStringExtra("question_text"));
        wsUrl = wsUrl + "?id=" + question_id + "/answers";
        new WSPregledHelper(this).execute(wsUrl);
        String pomocni = wsUrl + "?id=" + question_id + "/answers";
        //lista = findViewById(R.id.comments_list);


        /*
        lista = findViewById(R.id.comments_list);
        lista.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>()));
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        */
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create_question:
                //Intent intent = new Intent(this, Postavke.class);
                Intent intent = new Intent(QuestionView.this, CreateQuestion.class);
                startActivityForResult(intent, 7);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        adapter.notifyDataSetChanged();
        new QuestionView.WSPregledHelper(QuestionView.this).execute(wsUrl);
        /*
        if(answers.length != 0){
            for(int i = 0;i< adapter.getCount();i++){
                adapter.remove(answers[i]);
            }
            adapter.notifyDataSetChanged();
            Arrays.fill(answers, null);
            //question_id = intent.getStringExtra("question_id");
            //tv_question_text.setText(intent.getStringExtra("question_text"));
            new QuestionView.WSPregledHelper(QuestionView.this).execute(wsUrl);

        }
        */
    }
    public void createAnswer(View v){
        Intent intent2 = new Intent(this, CreateAnswer.class);
        intent2.putExtra("user_id", user_id);
        intent2.putExtra("username", username);
        intent2.putExtra("question_id", question_id);
        startActivity(intent2);
    }
    @Override
    public void onBackPressed(){
        Intent returnIntent = new Intent();
        Bundle bundle2 = new Bundle();
        //bundle2.putSerializable("article", article_pom);
        //returnIntent.putExtra("br_str",br_str);
        //skrolaj  = intent.getStringExtra("skrolaj");
        //Log.d("skro", skrolaj);
        //returnIntent.putExtra("skrolaj",skrolaj);
        returnIntent.putExtras(bundle2);
        setResult(RESULT_OK,returnIntent);
        finish();
    }
    private static class WSPregledHelper extends AsyncTask<String, Void, Answer[]> {
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
        private WeakReference<QuestionView> activityReference;
        // only retain a weak reference to the activity
        public WSPregledHelper(QuestionView context) {
            activityReference = new WeakReference<>(context);
        }
        @Override
        protected Answer[] doInBackground(String... urls) {
            int br = urls.length;
            // šaljemo samo 1 parametar (1 URL), iako metoda može primiti polje parametara
            HttpURLConnection conn = null;
            try {
                // povezujemo se sa zadanim URL-om pomoću GET metode
                conn = (HttpURLConnection) new URL(urls[0]).openConnection();
                // postavljamo kodnu stranicu da bi se znakovi prikazali ispravno
                conn.setRequestProperty("Accept-Charset", "UTF-8");
                // vijestistimo HTTP GET metodurlsu za dohvat
                conn.setRequestMethod("GET");
                // dohvaćamo podatke u obliku ulaznog niza
                // ako su podaci u redno dohvaćeni (HTTP kod 200)
                if (conn.getResponseCode() == 200 ) {
                    String res = inputStreamToString(conn.getInputStream());
                    String res2 = res.substring(2, res.length()-1);
                    //JSONObject response = new JSONObject(res);
                    Gson gson = new Gson();
                    Answers answers = gson.fromJson(res2, Answers.class);
                    // metodi onPostExecute šalje se polje objekata tipa User kako bi se
                    // lista popunila podacima pročitanih korisnika
                    return answers.getAnswers();
                }
                else {
                    // Inače se vratila greška, pa dohvati poruku greške i pretvori ju u String
                    // Koristi se ErrorStream, ane InputStream koji vraća web servis i pretvaramo ga u JSON String
                    String res = inputStreamToString(conn.getErrorStream());
                    // parsiramo podatke JSON formata u objekt tipa Greska
                    Gson gson = new Gson();
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
        protected void onPostExecute(Answer[] rez) {
            // get a reference to the activity if it is still there
            // Dogodila se greška kod dohvata
            QuestionView activity = activityReference.get();
            //activity.adapter = (ArrayAdapter<String>) activity.lista.getAdapter();

            if (activity == null || activity.isFinishing()) return;
            // Dogodila se greška kod dohvata
            if (rez == null){
                Toast.makeText(activity, activity.err.getError(), Toast.LENGTH_LONG).show();
                return;
            }
            activity.answers = rez;
            activity.adapter = new MojAdapter(activity.context, activity.answers);
            activity.setListAdapter(activity.adapter);
            // Inače ažuriraj listu
            //activity.kor = rez;
            /*
            for(int i = 0;i < rez.length; i++){
                activity.answers.add(rez[i]);
                //activity.adapter.add(rez[i].getAnswer_text());
            }
            */
            // nakon što dohvati podatke, stvara se adapter za pregled
            //activity.adapter = (ArrayAdapter<String>) activity.lista.getAdapter();
            // adapter se povezuje s listom, a podaci prikazuju na ekranu
            //activity.adapter.notifyDataSetChanged();
        }
    }
}
