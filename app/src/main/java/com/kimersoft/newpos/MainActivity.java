package com.kimersoft.newpos;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.morefun.ypossdk.emv.card.CardProc;
import com.morefun.ypossdk.misc.Misc;
import com.morefun.ypossdk.pub.Api;
import com.morefun.ypossdk.pub.CommEnum;
import com.morefun.ypossdk.pub.listener.StartCSwiperListener;
import com.morefun.ypossdk.pub.param.BeepParam;
import com.morefun.ypossdk.pub.param.StartCSwiperParam;
import com.morefun.ypossdk.pub.result.StartCSwiperResult;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Api mfapi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);
        Button button4 = (Button) findViewById(R.id.button4);
        Button button5 = (Button) findViewById(R.id.button5);
        Button button6 = (Button) findViewById(R.id.button6);
        Button button7 = (Button) findViewById(R.id.button7);
        Button button8 = (Button) findViewById(R.id.button8);

        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
        button7.setOnClickListener(this);
        button8.setOnClickListener(this);

        mfapi = Api.Create(MainActivity.this);
    }

    @Override
    protected void onDestroy() {
        mfapi.Destory();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button2:
                Toast.makeText(this, "PBOC card reader", Toast.LENGTH_SHORT).show();
                PBOC();
                break;
            case R.id.button3:
                Swingcard();
                Toast.makeText(this, "Card reader+plus contact less", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button4:
                Beep();
                Toast.makeText(this, "Beep button4", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button5:
                Toast.makeText(this, "SerialNumber "+ mfapi.GetSn(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.button6:
                Toast.makeText(this, "CUPDeviceSn "+mfapi.GetCUPDeviceSn(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.button7:
                Toast.makeText(this, "Version "+mfapi.GetVersion(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.button8:

                Toast.makeText(this, "button 8", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void Beep() {
        BeepParam param = new BeepParam();
        param.setTime(300);
        mfapi.Beep( param );
    }

    private  void PBOC(){
        //ac.showwait(id,"Executing,Please wait");
        new AlertDialog.Builder(this,R.style.AlertDialog_AppCompat)
                .setTitle("Reading the card")
                .setMessage("Please Wait)")
                .setCancelable(false)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mfapi.StopScanner();
                        mfapi.Cancel();
                    }
                })
                .show();

        StartCSwiperParam sp = new StartCSwiperParam();
        sp.setAmount("000000000100");//Setting amount,Units
        sp.setTransType(CommEnum.SDK_TRANS_TYPE.SDK_FUNC_SALE);//Transaction Type
        sp.setTrackenc(true);//Whether to enable track encryption
        sp.setIcFlag(true);//Use an IC card (card mode 3)
        sp.setMagFlag(true);//Use magnetic stripe card (card mode 2)
        sp.setRfFlag(false); // set true for contact less (card mode 4)

        mfapi.StartCSwiper( sp , new StartCSwiperListener() {
            @Override
            public void OnReturn(StartCSwiperResult ret) {
                StringBuilder sb = new StringBuilder();
                sb.append( "result:" + ret.getError().toString() + "\n" );
                sb.append( "Card mode:" + ret.getCardType() + "\n" );
                sb.append( "Main account number:" + ret.getPan() + "\n"  );
                sb.append( "Card is valid:" + ret.getExpData()  + "\n" );
                sb.append( "Service code:" + ret.getServiceCode() + "\n"   );
                sb.append( "track one length:" + ret.getTrack1Len() + "\n"  );
                sb.append( "Track one message:" + ret.getsTrack1()  + "\n" );
                sb.append( "track two length:" + ret.getTrack2Len() + "\n"  );
                sb.append( "Track two message:" + ret.getsTrack2()  + "\n" );
                sb.append( "track three length:" + ret.getTrack3Len() + "\n"  );
                sb.append( "Track three message:" + ret.getsTrack3() + "\n"  );
                sb.append( "Card serial number:" + ret.getPansn() + "\n"  );
                sb.append( "IC card data:" + Misc.hex2asc(ret.getTlvData() , ret.getDatalen()*2, 0 )  + "\n"   );

                //ac.blockmsg(id,sb.toString());

                new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog_AppCompat)
                        .setTitle("Information back")
                        .setMessage(sb.toString())
                        .setCancelable(false)
                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CardProc.CardProcEnd();
                            }
                        })
                        .show();
            }
        });
    }

    private void Swingcard() {
        StartCSwiperParam sp = new StartCSwiperParam();
        sp.setAmount("000000000100"); //Set the amount, unit points
        sp.setTransType(CommEnum.SDK_TRANS_TYPE. SDK_FUNC_SALE ); //Transaction Type
        sp.setTrackenc(true);//Whether to enable track encryption
        sp.setIcFlag(true);//Use an IC card (card mode 3)
        sp.setMagFlag(true);//Use magnetic stripe card (card mode 2)
        sp.setRfFlag(true); // set true for contact less (card mode 4)
        mfapi.StartCSwiper( sp , new StartCSwiperListener() {
            @Override
            public void OnReturn(StartCSwiperResult ret) {
                StringBuilder sb = new StringBuilder();
                sb.append( "result:" + ret.getError().toString() + "\n" );
                sb.append( "Card mode:" + ret.getCardType() + "\n" );
                sb.append( "Main account number:" + ret.getPan() + "\n"  );
                sb.append( "Card is valid:" + ret.getExpData()  + "\n" );
                sb.append( "Service code:" + ret.getServiceCode() + "\n"   );
                sb.append( "track one length:" + ret.getTrack1Len() + "\n"  );
                sb.append( "Track one message:" + ret.getsTrack1()  + "\n" );
                sb.append( "track two length:" + ret.getTrack2Len() + "\n"  );
                sb.append( "Track two message:" + ret.getsTrack2()  + "\n" );
                sb.append( "track three length:" + ret.getTrack3Len() + "\n"  );
                sb.append( "Track three message:" + ret.getsTrack3() + "\n"  );
                sb.append( "Card serial number:" + ret.getPansn() + "\n"  );
                sb.append( "IC card data:" + Misc. hex2asc (ret.getTlvData() ,
                        ret.getDatalen(), 0 )  + "\n"   );

                Log.d("Log", "SwingCard Result: " + sb.toString());
                CardProc.CardProcEnd(); // ends swiper scanner
            }
        });
    }
}
