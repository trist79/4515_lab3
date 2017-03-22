package edu.temple.a4515_lab3;

import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;

import static android.nfc.NdefRecord.createMime;

public class MainActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback {


    public String publickey, privatekey, encrypted, decrypted;
    byte [] encryptedBytes, decryptedBytes;
    KeyPair kp1;
    public NfcAdapter mNfcAdapter;
    Context context;
    int x = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context=this;

        TextView pu = (TextView) findViewById(R.id.publickey);
        TextView pr = (TextView) findViewById(R.id.privatekey);
        pu.setMovementMethod(new ScrollingMovementMethod());
        pr.setMovementMethod(new ScrollingMovementMethod());

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
        }
        else {
            mNfcAdapter.setNdefPushMessageCallback(this, this);
        }

        final Button generateB = (Button) findViewById(R.id.generate);
        generateB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
                    KeyPair kp = kpg.generateKeyPair();
                    kp1 = kp;
                    TextView pu = (TextView) findViewById(R.id.publickey);
                    TextView pr = (TextView) findViewById(R.id.privatekey);

                    pu.setMovementMethod(new ScrollingMovementMethod());
                    pr.setMovementMethod(new ScrollingMovementMethod());
                    publickey = kp.getPublic().toString();
                    privatekey = kp.getPrivate().toString();

                    pu.setText(publickey);
                    pr.setText(privatekey);



                    NdefMessage msg1 = new NdefMessage(
                            new NdefRecord[] { createMime(
                                    "edu.temple.a4515_lab3", publickey.getBytes())


                                    //NdefRecord.createApplicationRecord("com.example.android.beam")
                            });

                    mNfcAdapter.setNdefPushMessage(msg1, MainActivity.this);


                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }




        });

        final Button encryptB = (Button) findViewById(R.id.encrypt);
        encryptB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                try {
                    Cipher cipher1 = Cipher.getInstance("RSA");
                    cipher1.init(Cipher.ENCRYPT_MODE, kp1.getPrivate());
                    EditText editText1 = (EditText) findViewById(R.id.decryptedtext);
                    String test = (String) editText1.getText().toString();
                    encryptedBytes = cipher1.doFinal(test.getBytes());
                    encrypted = new String(encryptedBytes);

                    TextView decrypted = (TextView) findViewById(R.id.decryptedtext2);
                    decrypted.setText("Encrypted!");

                    NdefMessage msg1 = new NdefMessage(
                            new NdefRecord[] { createMime(
                                    "edu.temple.a4515_lab3", encryptedBytes)


                                    //NdefRecord.createApplicationRecord("com.example.android.beam")
                            });

                    mNfcAdapter.setNdefPushMessage(msg1, MainActivity.this);

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

        });



        final Button decryptB = (Button) findViewById(R.id.decrypt);
        decryptB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                try {

                    TextView decrypted1 = (TextView) findViewById(R.id.decryptedtext2);
                    decrypted1.setText("Encrypted!");


                    Cipher cipher2 = Cipher.getInstance("RSA");
                    cipher2.init(Cipher.DECRYPT_MODE, kp1.getPublic());
                    decryptedBytes = cipher2.doFinal(encryptedBytes);
                    decrypted = new String(decryptedBytes);
                    TextView decrypted3 = (TextView) findViewById(R.id.decryptedtext2);
                    decrypted3.setText(decrypted);


                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

        });









    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {

        NdefMessage msg = new NdefMessage(
                new NdefRecord[] { createMime(
                        "edu.temple.a4515_lab3", encryptedBytes)


                        //NdefRecord.createApplicationRecord("com.example.android.beam")
                });
        return msg;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    void processIntent(Intent intent) {
        if(x == 0){
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES);
            // only one message sent during the beam
            NdefMessage msg = (NdefMessage) rawMsgs[0];
            publickey = new String(msg.getRecords()[0].getPayload());
            x++;
        }

        else{
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES);
            // only one message sent during the beam
            NdefMessage msg = (NdefMessage) rawMsgs[0];
            encryptedBytes = new String(msg.getRecords()[0].getPayload()).getBytes();


            try {

                TextView decrypted1 = (TextView) findViewById(R.id.decryptedtext2);
                Cipher cipher2 = Cipher.getInstance("RSA");
                cipher2.init(Cipher.DECRYPT_MODE, kp1.getPublic());
                decryptedBytes = cipher2.doFinal(encryptedBytes);
                decrypted = new String(decryptedBytes);
                TextView decrypted3 = (TextView) findViewById(R.id.decryptedtext2);
                decrypted3.setText(decrypted);


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
