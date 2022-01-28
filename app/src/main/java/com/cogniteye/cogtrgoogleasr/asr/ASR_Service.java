package com.cogniteye.cogtrgoogleasr.asr;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.cogniteye.cogtrgoogleasr.MainActivity;

import java.util.ArrayList;
import java.util.Locale;

public class ASR_Service extends Service implements RecognitionListener {

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    private SpeechRecognizer speechRecognizer;
    private static final int RecordAudioRequestCode = 1;


    @Override
    public void onCreate() {
        Log.v("TestService", "onCreate");

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("TestService", "onStart1");
        Toast.makeText(this,"start Service.",Toast.LENGTH_SHORT).show();
        Log.v("TestService", "onStart2");
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        speechRecognizer.setRecognitionListener(this);

        final Intent voice = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        voice.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 40);  // number of maximum results
        voice.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");
        voice.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, false);  // Enable this when offline
        voice.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());

        // Speech language settings
        voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        voice.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, Locale.US);

        // Speech time settings
        voice.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 5000L);
        voice.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 100000L);
        voice.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 50000L);


        speechRecognizer.startListening(voice);

        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        Log.v("TestService", "onBind");
        return null;
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.v("TestService", "onReady");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.v("TestService", "onPartial");
    }

    @Override
    public void onRmsChanged(float v) {
        Log.v("TestService", "onRMS");
    }

    @Override
    public void onBufferReceived(byte[] bytes) {
        Log.v("TestService", "onBuffer");
    }

    @Override
    public void onEndOfSpeech() {
        Log.v("TestService", "onEndOfSpeech");
    }

    @Override
    public void onError(int i) {
        Log.v("TestService", "onError: " + i);
    }

    @Override
    public void onResults(Bundle bundle) {
       // ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        Log.v("TestService", "onResult: " + bundle);

    }

    @Override
    public void onPartialResults(Bundle bundle) {
        Log.v("TestService", "onPartial: " + bundle);
    }

    @Override
    public void onEvent(int i, Bundle bundle) {
        Log.v("TestService", "onEvent");
    }



}
