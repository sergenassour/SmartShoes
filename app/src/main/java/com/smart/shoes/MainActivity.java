package com.smart.shoes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smart.shoes.Models.UserModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    TextView textViewName,textViewEmail,textViewLogout;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;

    BluetoothAdapter bluetoothAdapter;
    Button buttonConnectDevice;
    Intent bluetoothIntent;
    int bluetoothRequestCode=1;

    Button buttonPairNewDevices;



    Button listen,send, listDevices;
    ListView listView;
    TextView msg_box,status;
    EditText writeMsg;


    BluetoothDevice[] btArray;

    SendReceive sendReceive;

    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING=2;
    static final int STATE_CONNECTED=3;
    static final int STATE_CONNECTION_FAILED=4;
    static final int STATE_MESSAGE_RECEIVED=5;

    int REQUEST_ENABLE_BLUETOOTH=1;

    private static final String APP_NAME = "BTChat";
    private static final UUID MY_UUID=UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
          bluetoothIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

        initUI();
        initDB();
        getUserInfo();
        initBluetooth();


    }

    private void initBluetooth() {

        findViewByIdes();
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();

        if(!bluetoothAdapter.isEnabled())
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BLUETOOTH);
        }

        implementListeners();


    }

    private void getUserInfo() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel userModel=snapshot.getValue(UserModel.class);
                textViewEmail.setText(userModel.getUserEmail());
                textViewName.setText(userModel.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    private void initDB() {
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        reference= FirebaseDatabase
                .getInstance()
                .getReference()
                .child("users")
                .child(user.getUid());

    }

    private void initUI() {
        textViewName=findViewById(R.id.textViewName);
        textViewEmail=findViewById(R.id.textViewEmail);
        textViewLogout=findViewById(R.id.textViewLogout);
        buttonConnectDevice=findViewById(R.id.buttonConnectDevice);
        buttonPairNewDevices=findViewById(R.id.buttonPairNewDevices);
      /*  buttonConnectDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             connectBluetooth();
            }
        });*/


    }

    private void connectBluetooth() {

      if(bluetoothAdapter==null){
          Toast.makeText(getApplicationContext(), "Bluetooth dose not support", Toast.LENGTH_SHORT).show();
      }else {
           if(!bluetoothAdapter.isEnabled()){
                startActivityForResult(bluetoothIntent,bluetoothRequestCode);
           }else {
               openDialogListDevices();
           }

      }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==bluetoothRequestCode){
            if(resultCode==RESULT_OK){
                Toast.makeText(getApplicationContext(), "Bluetooth enable successfully", Toast.LENGTH_SHORT).show();
                openDialogListDevices();

            }else if(resultCode==RESULT_CANCELED){
                Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
            }
        }
        
        
    }



// New Implementations


    private void implementListeners() {

        listDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Set<BluetoothDevice> bt=bluetoothAdapter.getBondedDevices();
                String[] strings=new String[bt.size()];
                btArray=new BluetoothDevice[bt.size()];
                int index=0;

                if( bt.size()>0)
                {
                    for(BluetoothDevice device : bt)
                    {
                        btArray[index]= device;
                        strings[index]=device.getName();
                        index++;
                    }
                    ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,strings);
                    listView.setAdapter(arrayAdapter);
                }
            }
        });

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServerClass serverClass=new ServerClass();
                serverClass.start();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ClientClass clientClass=new ClientClass(btArray[i]);
                clientClass.start();

                status.setText("Connecting");
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string= String.valueOf(writeMsg.getText());
                sendReceive.write(string.getBytes());
            }
        });
    }

    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what)
            {
                case STATE_LISTENING:
                    status.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    status.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    status.setText("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    status.setText("Connection Failed");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    byte[] readBuff= (byte[]) msg.obj;
                    String tempMsg=new String(readBuff,0,msg.arg1);
                    msg_box.setText(tempMsg);
                    break;
            }
            return true;
        }
    });

    private void findViewByIdes() {
        listen=(Button) findViewById(R.id.listen);
        send=(Button) findViewById(R.id.send);
        listView=(ListView) findViewById(R.id.listview);
        msg_box =(TextView) findViewById(R.id.msg);
        status=(TextView) findViewById(R.id.status);
        writeMsg=(EditText) findViewById(R.id.writemsg);
        listDevices=(Button) findViewById(R.id.listDevices);
    }

    private class ServerClass extends Thread
    {
        private BluetoothServerSocket serverSocket;

        public ServerClass(){
            try {
                serverSocket=bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME,MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run()
        {
            BluetoothSocket socket=null;

            while (socket==null)
            {
                try {
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTING;
                    handler.sendMessage(message);

                    socket=serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                }

                if(socket!=null)
                {
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTED;
                    handler.sendMessage(message);

                    sendReceive=new SendReceive(socket);
                    sendReceive.start();

                    break;
                }
            }
        }
    }

    private class ClientClass extends Thread
    {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass (BluetoothDevice device1)
        {
            device=device1;

            try {
                socket=device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run()
        {
            try {
                socket.connect();
                Message message=Message.obtain();
                message.what=STATE_CONNECTED;
                handler.sendMessage(message);

                sendReceive=new SendReceive(socket);
                sendReceive.start();

            } catch (IOException e) {
                e.printStackTrace();
                Message message=Message.obtain();
                message.what=STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }

    private class SendReceive extends Thread
    {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive (BluetoothSocket socket)
        {
            bluetoothSocket=socket;
            InputStream tempIn=null;
            OutputStream tempOut=null;

            try {
                tempIn=bluetoothSocket.getInputStream();
                tempOut=bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream=tempIn;
            outputStream=tempOut;
        }

        public void run()
        {
            byte[] buffer=new byte[1024];
            int bytes;

            while (true)
            {
                try {
                    bytes=inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes)
        {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
















    private void openDialogListDevices() {

        Dialog dialogBluetooth=new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialogBluetooth.setContentView(R.layout.list_bluetooth_devices_dialog);
        dialogBluetooth.show();
        ListView listView=dialogBluetooth.findViewById(R.id.listView);
        LinearLayout layoutEmpty=dialogBluetooth.findViewById(R.id.layoutEmpty);
        ImageView imageViewCancel=dialogBluetooth.findViewById(R.id.imageViewCancel);
        ProgressBar progressBar=dialogBluetooth.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);


        ListView listViewNewDevices=dialogBluetooth.findViewById(R.id.listViewNewDevices);
        Button buttonPairNewDevices=dialogBluetooth.findViewById(R.id.buttonPairNewDevices);




        imageViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBluetooth.dismiss();
            }
        });

        Set<BluetoothDevice> bt=bluetoothAdapter.getBondedDevices();
        String[] strings=new String[bt.size()];
        int index=0;

        for(BluetoothDevice device:bt){
            strings[index]=device.getName();
            index++;
        }
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,strings);
        listView.setAdapter(arrayAdapter);
        if(index==0){
            layoutEmpty.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }else {
            layoutEmpty.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

        ArrayList<String> stringArrayList=new ArrayList<String>();
        ArrayAdapter arrayAdapterNewDevices;



        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);


        arrayAdapterNewDevices=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,stringArrayList);
        listViewNewDevices.setAdapter(arrayAdapterNewDevices);

        BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action=intent.getAction();
                if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                    progressBar.setVisibility(View.VISIBLE);
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    progressBar.setVisibility(View.GONE);
                }else if(BluetoothDevice.ACTION_FOUND.equals(action)){
                    BluetoothDevice device2=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    stringArrayList.add(device2.getName());
                    arrayAdapterNewDevices.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), device2.getName(), Toast.LENGTH_SHORT).show();
                }
            }
        };
        buttonPairNewDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
                bluetoothAdapter.startDiscovery();
                registerReceiver(broadcastReceiver,intentFilter);
            }
        });



    }
}