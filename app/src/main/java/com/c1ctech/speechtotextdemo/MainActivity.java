package com.c1ctech.speechtotextdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final Integer PERMISSION_RECORD_AUDIO_REQUEST = 1;
    private SpeechRecognizer speechRecognizer;
    private EditText showText;
    private ImageButton micButton,convertButton,nextButton,backButton,langButton;
    private ImageView showImage;
    private int totalLetter;
    private String imageName="";
    private String [] numberSeries;
    private String [] letterSeries;
    private int nowLetter=0;
    private String languageLetter="i";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (isPermissionGranted()) {
            requestPermission();
        }

        showText = findViewById(R.id.edtSpeechText);
        micButton = findViewById(R.id.imgBtnMic);

        convertButton = findViewById(R.id.convertBtn);
        showImage = findViewById(R.id.showImg);

        nextButton=findViewById(R.id.nextBtn);
        backButton=findViewById(R.id.backBtn);

        langButton=findViewById(R.id.languageBtn);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {
                showText.setText("");
                showText.setHint("Listening...");
            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                micButton.setImageResource(R.drawable.ic_mic_off);
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                showText.setText(data.get(0));
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        micButton.setOnTouchListener(new View.OnTouchListener() {// mikrofon butonu işlevleri
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                //ACTION_UP: A pressed gesture has finished.
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    speechRecognizer.stopListening();
                }

                //ACTION_DOWN: A pressed gesture has started.
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    micButton.setImageResource(R.drawable.ic_mic);
                    speechRecognizer.startListening(speechRecognizerIntent);
                }

                return false;
            }
        });


        findViewById(R.id.languageBtn).setOnClickListener(new View.OnClickListener() {// metin cevirici buton işlevleri
            @Override
            public void onClick(View view) {

               if (languageLetter=="i"){
                   languageLetter="t";
                   langButton.setImageResource(getResources().getIdentifier("tr","drawable",getPackageName()));
               }
               else if(languageLetter=="t") {
                   languageLetter="i";
                   langButton.setImageResource(getResources().getIdentifier("en","drawable",getPackageName()));
               }

            }
        });


        findViewById(R.id.convertBtn).setOnClickListener(new View.OnClickListener() {// metin cevirici buton işlevleri
            @Override
            public void onClick(View view) {


                totalLetter=showText.length();

                String fullText=showText.getText().toString().toLowerCase();//konuşulmuş metin alındı//tolower ile hepsi küçültüldü

                if (totalLetter!=0){

                    numberSeries = new String[totalLetter];//alınan harflerin olacağı diziyi oluşturduk
                    getWordShredded(fullText,numberSeries,totalLetter);//harfleri ayırıp diziye koyduk

                    imageName= languageLetter+String.valueOf(getLetterNumber(numberSeries[0]));// ilk harfin ismi alındı fonksiyon aracılığı ile

                    showImage.setImageResource(getResources().getIdentifier(imageName,"drawable",getPackageName()));//ilk harf açıldı resmi
                    nowLetter=0;
                }
                else{
                    Toast.makeText(getApplicationContext(),"ses veya metin girişi yapılmadı önce bilgi giriniz! ",Toast.LENGTH_SHORT).show();//kontrol
                }

                Toast.makeText(getApplicationContext(),"Çeviri Başladı",Toast.LENGTH_SHORT).show();//kontrol


            }
        });

        findViewById(R.id.nextBtn).setOnClickListener(new View.OnClickListener() {// ileri harf resmi göster
            @Override
            public void onClick(View view) {

                if (nowLetter!=totalLetter-1&&totalLetter!=0){
                    imageName= languageLetter+String.valueOf(getLetterNumber(numberSeries[nowLetter+1]));// ilk harfin ismi alındı fonksiyon aracılığı ile
                    showImage.setImageResource(getResources().getIdentifier(imageName,"drawable",getPackageName()));//ilk harf açıldı resmi
                    nowLetter++;
                }
                else if(totalLetter==0){
                    Toast.makeText(getApplicationContext(),"ses veya metin girişi yapılmadan ileri gidilemez",Toast.LENGTH_SHORT).show();//kontrol

                }
                else{
                    Toast.makeText(getApplicationContext(),"zaten son harftesiniz ileri gidilemez",Toast.LENGTH_SHORT).show();//kontrol
                }

            }
        });

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {// geri harf resmi göster
            @Override
            public void onClick(View view) {

                if (nowLetter!=0){
                    imageName= languageLetter+String.valueOf(getLetterNumber(numberSeries[nowLetter-1]));// ilk harfin ismi alındı fonksiyon aracılığı ile
                    showImage.setImageResource(getResources().getIdentifier(imageName,"drawable",getPackageName()));//ilk harf açıldı resmi
                    nowLetter--;
                }
                else if(totalLetter==0){
                    Toast.makeText(getApplicationContext(),"ses veya metin girişi yapılmadan geri gidilemez",Toast.LENGTH_SHORT).show();//kontrol

                }
                else{
                    Toast.makeText(getApplicationContext(),"zaten ilk harftesiniz geri gidilemez",Toast.LENGTH_SHORT).show();//kontrol
                }

            }
        });



    }

    private String[] getWordShredded(String bigtext, String[] numberSeries, int textLength){//kelime parçalama fonksiyonu

        char selectedletter;

        for (int i=0;i<textLength;i++){
            //selectedletter = bigtext.substring(i, 1);
            selectedletter = bigtext.charAt(i);
            numberSeries[i]= String.valueOf(selectedletter);
        }

        return numberSeries;


    }



    private int getLetterNumber(String letter){

        int realnumber = 0;
        int alphabetNumber=0;
        if (languageLetter=="i"){
             letterSeries= new String[]{" ", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
            alphabetNumber=27;
        }
        else{
             letterSeries= new String[]{" ", "a", "b", "c", "ç", "d", "e", "f", "g", "ğ", "h", "i", "ı", "j", "k", "l", "m", "n", "o", "ö", "p", "r", "s", "ş", "t", "u", "ü", "v", "y", "z"};
            alphabetNumber=30;
        }

        for(int i=0;i<alphabetNumber;i++){
            if (letter.equals(letterSeries[i])){
                realnumber=i;
            }

        }

        return realnumber;

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }


    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_RECORD_AUDIO_REQUEST);
        }
    }

    private boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_RECORD_AUDIO_REQUEST && grantResults.length > 0) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }
    }

}