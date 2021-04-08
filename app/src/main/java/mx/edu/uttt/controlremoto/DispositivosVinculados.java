package mx.edu.uttt.controlremoto;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class DispositivosVinculados extends AppCompatActivity {

    //DEPURACIÓN LOGCAT
    private static final String TAG = "DispositivosVinculados";
    //DECLARACIÓN DE LISTVIEW
    ListView idLista;
    //STRIN QUE SE ENVIARÁ A LA ACTIVIDAD PRINCIPAL O MAINACTVITY
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    //DECLARACIÓN DE CAMPOS
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter mPairedDevicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispositivos_vinculados);
    }

    @Override
    public void onResume(){
        super.onResume();

        verficarEstadoBt();
        //INICIALIZA EL ARRAY QUE CONTENDRÁ LOS DISPOSITIVOS BLUETOOTH VINCULADOS
        mPairedDevicesArrayAdapter = new ArrayAdapter(this, R.layout.dispositivos_encontrados);
        //PRESENTA LOS DISPOSITIVOS VINCULADOS EN EL LISTVIEW
        idLista = (ListView) findViewById(R.id.idLista);
        idLista.setAdapter(mPairedDevicesArrayAdapter);
        idLista.setOnItemClickListener(mDeviceClickListener);
        //OBTIENE EL ADAPTADOR LOCAL BLUETOOTH ADAPTER
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        // Adiciona un dispositivos emparejado al array
        if (pairedDevices.size() > 0)
        {
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    }

    //SE CONFIGURA ONCLICK PARA LA LISTA
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView av, View v, int arg2, long arg3) {

            // Obtener la dirección MAC del dispositivo, que son los últimos 17 caracteres en la vista
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            finishAffinity();

            // Realiza un intent para iniciar la siguiente actividad
            // mientras toma un EXTRA_DEVICE_ADDRESS que es la dirección MAC.
            Intent intend = new Intent(DispositivosVinculados.this, MainActivity.class);
            intend.putExtra(EXTRA_DEVICE_ADDRESS, address);
            startActivity(intend);
        }
    };

    private void verficarEstadoBt(){
        //COMPRUEBA QUE EL DIPOSITIVO TIENE BLUETOOTH Y ESTA ENCENDIDO
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBtAdapter == null){
            Toast.makeText(getBaseContext(), "El dispositivo no soporta Bluetooth.", Toast.LENGTH_SHORT).show();
        }else{
            if(mBtAdapter.isEnabled()){
                Log.d(TAG, ".::Bluetooth Activado::.");
            }else{
                //SOLICITA AL USUARIO QUE ACTIVE EL BLUETOOTH
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }
}