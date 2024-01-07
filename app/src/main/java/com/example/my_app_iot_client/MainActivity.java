package com.example.my_app_iot_client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    MQTTHelper mqttHelper;

    TextView txtHUMIDITY, txtSOID, txtTEMPERATURE;
    LabeledSwitch btnMODE, btnPUMP, btnLED, btnLIGHT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtHUMIDITY = findViewById((R.id.txtHUMIDITY));
        txtSOID = findViewById((R.id.txtSOID));
        txtTEMPERATURE = findViewById(R.id.txtTEMPERATURE);

        btnMODE = findViewById(R.id.btnMODE);
        btnPUMP = findViewById((R.id.btnPUMP));
        btnLED = findViewById(R.id.btnLED);
        btnLIGHT = findViewById(R.id.btnLIGHT);

        btnMODE.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn == true){
                    senDataMQTT("trantuanhung/feeds/mode", "1");
                }else{
                    senDataMQTT("trantuanhung/feeds/mode", "0");
                }
            }
        });

        btnPUMP.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn == true){
                    senDataMQTT("trantuanhung/feeds/pump", "1");
                }else{
                    senDataMQTT("trantuanhung/feeds/pump", "0");
                }
            }
        });

        btnLED.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn == true){
                    senDataMQTT("trantuanhung/feeds/led", "1");
                }else{
                    senDataMQTT("trantuanhung/feeds/led", "0");
                }
            }
        });

        btnLIGHT.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn == true){
                    senDataMQTT("trantuanhung/feeds/light", "1");
                }else{
                    senDataMQTT("trantuanhung/feeds/light", "0");
                }
            }
        });

        startMQTT();
    }

    public void senDataMQTT(String topic, String value){
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(false);

        byte[] b = value.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);
        }catch (MqttException e){
        }
    }

    public void startMQTT(){
        mqttHelper = new MQTTHelper(this);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("TEST", topic + " *** "+ message.toString());
                if(topic.contains("humidity")){
                    txtHUMIDITY.setText(message.toString()+"%");
                }else if(topic.contains("soil")){
                    txtSOID.setText(message.toString()+"%");
                }else if(topic.contains("temperature")){
                    txtTEMPERATURE.setText(message.toString()+"°C");
                }else if(topic.contains("mode")){
                    if(message.toString().equals("1")){
                        btnMODE.setOn(true);
                    }else{
                        btnMODE.setOn(false);
                    }
                }else if(topic.contains("led")){
                    if(message.toString().equals("1")){
                        btnLED.setOn(true);
                    }else{
                        btnLED.setOn(false);
                    }
                }else if(topic.contains("pump")){
                    if(message.toString().equals("1")){
                        btnPUMP.setOn(true);
                    }else{
                        btnPUMP.setOn(false);
                    }
                }else if(topic.contains("light")){
                    if(message.toString().equals("1")){
                        btnLIGHT.setOn(true);
                    }else{
                        btnLIGHT.setOn(false);
                    }
                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
}