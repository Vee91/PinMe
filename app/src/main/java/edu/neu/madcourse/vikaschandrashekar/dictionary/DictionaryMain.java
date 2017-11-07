package edu.neu.madcourse.vikaschandrashekar.dictionary;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import edu.neu.madcourse.vikaschandrashekar.R;

/**
 * Created by cvikas on 2/1/2017.
 */

public class DictionaryMain extends Activity {

    private char firstChar;
    String output = "";
    MediaPlayer mMediaPlayer;
    final List<String> showList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.dictionary_main);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.title_bar);
        final TextView appTitle = (TextView) findViewById(R.id.app_title);
        if(appTitle != null){
            appTitle.setTextColor(Color.WHITE);
            appTitle.setText(R.string.dictionary_title);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EditText enterWord = (EditText)findViewById(R.id.word);
        enterWord.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() >= 3){
                    setListView(s.toString().toLowerCase(),showList);
                }

            }
        });
    }

    private void setListView(String s, List<String> showList){
        try {
            if(s.charAt(0) != firstChar) {
                firstChar = s.charAt(0);
                output = Expander.expand(this, s.charAt(0));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(output.length()>0) {
            List<String> outputList = Arrays.asList(output.split(Pattern.quote("\\")));
            if(outputList.contains(s) && !(showList.contains(s))){
                showList.add(s);
                mMediaPlayer = MediaPlayer.create(this, R.raw.beep);
                mMediaPlayer.setVolume(0.5f, 0.5f);
                mMediaPlayer.setLooping(false);
                mMediaPlayer.start();
                String[] showWords = showList.toArray(new String[showList.size()]);
                ListView foundWords = (ListView) findViewById(R.id.foundWords);
                ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, showWords);
                foundWords.setAdapter(adapter);
            }
        }
    }

    public void clearSearch(View view){
        showList.clear();
        String[] showWords = new String[0];
        ListView foundWords = (ListView) findViewById(R.id.foundWords);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, showWords);
        foundWords.setAdapter(adapter);
        EditText searchWord = (EditText) findViewById(R.id.word);
        searchWord.setText("");
    }

    public void returnToMenu(View view) {
        finish();
    }

    public void getAcknowledgements(View view) {
        Intent intent = new Intent(this, Acknowledgements.class);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
    }

}
