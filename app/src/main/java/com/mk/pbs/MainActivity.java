package com.mk.pbs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mk.pbs.net.SocketClient;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Button postBtn;
    private List<RecordInfo> messages = new ArrayList<>();
    private PostDialog postDialog;
    private DialogCancel dialogCancel;
    private RecordInfo recordInfo ;
    private static final int MY_PERMISSIONS_INTERNET_CONTACTS = 0x11;
    private String[] requestPermissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions();
        }

        postBtn = findViewById(R.id.post);
        postDialog = new PostDialog(this);

        for (int i = 0; i <= 10; i++) {
            recordInfo = new RecordInfo();
            recordInfo.setMsg("message " + i);
            recordInfo.setReportNo(i + "");
            messages.add(recordInfo);
        }


        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postDialog.setMessage("post list ?")
                        .setPositive("YES")
                        .setNegtive("NO")
                        .setOnClickBottomListener(new PostDialog.OnClickBottomListener() {
                            @Override
                            public void onPositiveClick() {
                                Log.d(TAG, "POSTOSNG");
                                postRemote();
                            }

                            @Override
                            public void onNegtiveClick() {
                                if (dialogCancel != null) {
                                    dialogCancel.cancelDialog();
                                }
                                postDialog.dismiss();
                            }
                        }).show();
            }
        });

    }

    private boolean sendMessageCallBack = false;
    private String sendMessageNo = "";


    private void postRemote() {
        if (messages.isEmpty()) {
            return;
        }
        SocketClient client = new SocketClient();
        client.socketConnect(connected -> {
            Log.d(TAG, "connect state " + connected);
            if (!connected) {
                client.disConnect();
                return;
            }else {
                remotePost(client);
            }
        },false);

        client.setSocketCallBack(new SocketClient.SocketCallBack() {
            @Override
            public void callBackMessage(String message) {
                Log.d(TAG, "connect call back " );
                if (message.equals("OK")) {
//                    sendMessageCallBack = false;
                }
            }

            @Override
            public void callErrorMessage(String errorMessage) {
                Log.d(TAG, "connect call back error" );
            }
        });

        class sendMes implements Runnable{
            private int messageNum = 0;
            @Override
            public void run() {
                while (true) {
                    synchronized (this) {
                        if (messageNum < messages.size() ) {

                        }
                    }
                }
            }
        }
    }
    private void remotePost(SocketClient client){
        for (int i = 10; i < messages.size(); i++) {
            if (postDialog != null) {
                postDialog.setChangeMessage(i + "/" + messages.size());
            }
            Log.d(TAG, "send Msg " + messages.get(i).toString());
            client.sendMessage(messages.get(i).toString()+" message " +"\u001c");
//            if (!sendMessageCallBack) {
//                sendMessageNo = messages.get(i).getReportNo();
//
//
//                sendMessageCallBack = true;
//            }




        }
//        client.disConnect();
    }

    public void setDialogCancel(DialogCancel dialogCancel) {
        this.dialogCancel = dialogCancel;
    }


    interface DialogCancel {
        void cancelDialog();
    }







    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, requestPermissions[1]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET)) {
                Log.i(TAG, "shouldShowRequestPermissionRationale");
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.INTERNET},
                        MY_PERMISSIONS_INTERNET_CONTACTS);

            } else {
                Log.i(TAG, "requestPermissions");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.INTERNET},
                        MY_PERMISSIONS_INTERNET_CONTACTS);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_INTERNET_CONTACTS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "onRequestPermissionsResult granted");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Log.i(TAG, "onRequestPermissionsResult denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
        }
    }

}