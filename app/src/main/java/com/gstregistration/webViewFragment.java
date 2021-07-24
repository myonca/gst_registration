package com.gstregistration;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.MailTo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class webViewFragment extends Fragment {
    private String urlToOpen;

    private static String file_type = "*/*";    // file types to be allowed for upload
    private boolean multiple_files = true;         // allowing multiple file upload

    private static final String TAG = MainActivity.class.getSimpleName();

    private String cam_file_data = null;        // for storing camera file information
    private ValueCallback<Uri> file_data;       // data/header received after file selection
    private ValueCallback<Uri[]> file_path;     // received file(s) temp. location

    private final static int file_req_code = 1;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= 21) {
            Uri[] results = null;

            /*-- if file request cancelled; exited camera. we need to send null value to make future attempts workable --*/
            if (resultCode == Activity.RESULT_CANCELED) {
                if (requestCode == file_req_code) {
                    file_path.onReceiveValue(null);
                    return;
                }
            }

            /*-- continue if response is positive --*/
            if (resultCode == RESULT_OK) {
                if (requestCode == file_req_code) {
                    if (null == file_path) {
                        return;
                    }

                    ClipData clipData;
                    String stringData;
                    try {
                        clipData = intent.getClipData();
                        stringData = intent.getDataString();
                    } catch (Exception e) {
                        clipData = null;
                        stringData = null;
                    }

                    if (clipData == null && stringData == null && cam_file_data != null) {
                        results = new Uri[]{Uri.parse(cam_file_data)};
                    } else {
                        if (clipData != null) { // checking if multiple files selected or not
                            final int numSelectedFiles = clipData.getItemCount();
                            results = new Uri[numSelectedFiles];
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                results[i] = clipData.getItemAt(i).getUri();
                            }
                        } else {
                            results = new Uri[]{Uri.parse(stringData)};
                        }
                    }
                }
            }
            file_path.onReceiveValue(results);
            file_path = null;
        } else {
            if (requestCode == file_req_code) {
                if (null == file_data) return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                file_data.onReceiveValue(result);
                file_data = null;
            }
        }
    }


    @SuppressLint({"SetJavaScriptEnabled", "WrongViewCast"})


    public webViewFragment(String urlToOpen) {
        this.urlToOpen = urlToOpen;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1: {
                    webViewGoBack();
                }
                break;
            }
        }
    };
    private WebView wv;
    private AdView adView;
    AdRequest adRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_web_view, container, false);
        wv = (WebView) v.findViewById(R.id.myWV);

        wv.loadUrl(this.urlToOpen);
        WebSettings settings = wv.getSettings();
        settings.setJavaScriptEnabled(true);
        wv.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        final ProgressDialog progressBar = ProgressDialog.show(getActivity(), "", "Loading...");

//        adView = (AdView) v.findViewById(R.id.adView);
//        adRequest = new AdRequest.Builder().build();
//        adView.loadAd(adRequest);

        wv.setWebChromeClient(new WebChromeClient() {

            /*--
            openFileChooser is not a public Android API and has never been part of the SDK.
            handling input[type="file"] requests for android API 16+; I've removed support below API 21 as it was failing to work along with latest APIs.
            --*/
        /*    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                file_data = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType(file_type);
                if (multiple_files) {
                    i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                }
                startActivityForResult(Intent.createChooser(i, "File Chooser"), file_req_code);
            }
        */
            /*-- handling input[type="file"] requests for android API 21+ --*/
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (file_permission() && Build.VERSION.SDK_INT >= 21) {
                    file_path = filePathCallback;
                    Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    contentSelectionIntent.setType(file_type);
                    if (multiple_files) {
                        contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    }
                    Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                    chooserIntent.putExtra(Intent.EXTRA_TITLE, "File chooser");
                    startActivityForResult(chooserIntent, file_req_code);
                    return true;
                } else {
                    return false;
                }
            }
        });


        wv.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("tel:")) {
                    Intent tel = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    startActivity(tel);
                    return true;
                } else if (url.contains("mailto:")) {
                    MailTo mailTo = MailTo.parse(url);

                    Intent mailIntent = sendEmail(getContext(), mailTo.getTo(), mailTo.getSubject(), mailTo.getBody()); // I added these extra parameters just incase you need to send those
                    getContext().startActivity(Intent.createChooser(mailIntent,
                            "Send Email Using: "));
                    view.reload();
                    return true;

                } else if (url != null && url.startsWith("whatsapp://")) {
                    view.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    view.goBack();
                    return true;
                }else {
                    view.loadUrl(url);
                    return true;
                }
            }

            public void onPageFinished(WebView view, String url) {
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }

            private Intent sendEmail(Context context, String email, String subject, String body) {
                Uri uri = Uri.parse("mailto:" + email)
                        .buildUpon()
                        .appendQueryParameter("subject", subject)
                        .appendQueryParameter("body", body)
                        .build();
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                return intent;
            }
        });

        wv.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == MotionEvent.ACTION_UP
                        && wv.canGoBack()) {
                    handler.sendEmptyMessage(1);
                    return true;
                }
                return false;
            }

        });
        return v;

    }

    /*-- callback reporting if error occurs --*/
    public class Callback extends WebViewClient {
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//            Toast.makeText(getApplicationContext(), "Failed loading app!", Toast.LENGTH_SHORT).show();
        }
    }

    /*-- checking and asking for required file permissions --*/
    public boolean file_permission() {
        return true;
//        if(Build.VERSION.SDK_INT >=23 && (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
//            return false;
//        }else{
//            return true;
//        }
    }

    /*-- creating new image file here --*/
    private File create_image() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    /*-- creating new video file here --*/
    private File create_video() throws IOException {
        @SuppressLint("SimpleDateFormat")
        String file_name = new SimpleDateFormat("yyyy_mm_ss").format(new Date());
        String new_name = "file_" + file_name + "_";
        File sd_directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(new_name, ".3gp", sd_directory);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    private void webViewGoBack() {
        wv.goBack();
    }
}
