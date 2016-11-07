package code.source.es.newbluetooth.Activity;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import code.source.es.newbluetooth.R;
import code.source.es.newbluetooth.Service.ScanService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button scanButton,lineTestButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        startService(new Intent(this,ScanService.class));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this,ScanService.class));
    }

    private void initView(){
        scanButton=(Button)findViewById(R.id.scanButton);
        lineTestButton=(Button)findViewById(R.id.lineButton);
        scanButton.setOnClickListener(this);
        lineTestButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent=null;
        switch (v.getId()){
            case R.id.scanButton:
                intent=new Intent(this,ScanActivity.class);
                break;
            case R.id.lineButton:
                intent=new Intent(this,LineTestActivity.class);
                break;
        }
        if(intent!=null)
            startActivity(intent);
    }




}
