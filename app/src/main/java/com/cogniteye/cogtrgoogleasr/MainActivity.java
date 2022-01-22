package com.cogniteye.cogtrgoogleasr;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final String APP_TAG = "speech-to-text";
    private static final int RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println(APP_TAG + ": SpeechRecognizer.isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        TextView editText = findViewById(R.id.tw_recognizedCommand_info);
        ImageView micButton = findViewById(R.id.iv_mic);

        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);  // number of maximum results
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, false);  // Enable this when offline
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());

        // Speech language settings
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, Locale.US);

        // Speech time settings
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 5000L);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 1000L);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 5000L);


        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                System.out.println(APP_TAG + ": onReadyForSpeech");
                micButton.setImageResource(R.drawable.microphone);
            }

            @Override
            public void onBeginningOfSpeech() {
                System.out.println(APP_TAG + ": onBeginningOfSpeech");
                editText.setHint("Listening...");
            }

            @Override
            public void onRmsChanged(float v) {
                System.out.println(APP_TAG + ": onRmsChanged( " + v + " )");
            }

            @Override
            public void onBufferReceived(byte[] bytes) {
                System.out.println(APP_TAG + ": onBufferReceived");
            }

            @Override
            public void onEndOfSpeech() {
                System.out.println(APP_TAG + ": onEndOfSpeech");
                // Restart listening here if required.
            }

            @Override
            public void onError(int i) {
                System.out.println(APP_TAG + ": onError( " + i + " ): " + getErrorText(i));
                micButton.setImageResource(R.drawable.muted_mic);
                // Restart listening here if required.
            }

            @Override
            public void onResults(Bundle bundle) {
                micButton.setImageResource(R.drawable.muted_mic);
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                System.out.println(APP_TAG + ": onResults:");
                for (String s : data) {
                    System.out.println(APP_TAG + ":  " + s);
                }
                editText.setText(data.get(0));
            }

            @Override
            public void onPartialResults(Bundle bundle) {
                System.out.println(APP_TAG + ": onPartialResults");
            }

            @Override
            public void onEvent(int i, Bundle bundle) {
                System.out.println(APP_TAG + ": onEvent( " + i + ", bundle )");
            }
        });



        micButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                System.out.println(APP_TAG + ": micButton.onTouch (thread: " + Thread.currentThread().getName() + ")");
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    System.out.println(APP_TAG + ":  ACTION_UP");
                    speechRecognizer.stopListening();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    System.out.println(APP_TAG + ":  ACTION_DOWN");
                    speechRecognizer.startListening(speechRecognizerIntent);
                    //TODO: if user says "Hey buddy" go to Image captioning Intent.
                    Intent intent = new Intent(MainActivity.this, LivePreviewActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RecordAudioRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                System.out.println(APP_TAG + ": Permission Granted");
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                System.out.println(APP_TAG + ": Permission Denied");
            }
        } else {
            System.out.println(APP_TAG + ": Permission Denied");
        }
    }

    private static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }



}
