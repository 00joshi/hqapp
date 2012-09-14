package org.chaotisch.hqapp;

import java.io.FileOutputStream;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.KeyPair;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;

public class CreateKey extends Activity {
	final static String PRIVKEYFILE = "private.key";
	final static String PUBKEYFILE = "public.key";
	int genkeylength = 1024;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_key);
		SeekBar yourSeekBar=(SeekBar) findViewById(R.id.seekBar1);
		yourSeekBar.setOnSeekBarChangeListener(new yourListener());

	}
	
	private class yourListener implements SeekBar.OnSeekBarChangeListener {
		TextView Keylength = (TextView) findViewById(R.id.seekBarValue);

        public void onProgressChanged(SeekBar seekBar, int progress,
                boolean fromUser) {
                            // Log the progress
            Log.d("DEBUG", "Schlüssellänge: "+progress);
                            //set textView's text
            
            		findViewById(R.id.seekBarValue);
           Keylength.setText(String.valueOf(progress));
           genkeylength = progress;
        }

        public void onStartTrackingTouch(SeekBar seekBar) {}

        public void onStopTrackingTouch(SeekBar seekBar) { }
    }
	public boolean buttonMakeKey(View View) {
			final EditText fldpassphrase = (EditText) findViewById(R.id.Passphrase);
			final String genpassphrase = fldpassphrase.getText().toString();
			final EditText fldnickname = (EditText) findViewById(R.id.editText2);
			final String gennickname =  fldnickname.getText().toString();
			Log.i(this.getClass().getName(), "Key generated for " + gennickname);
			final Runnable r = new Runnable() {
			public void run() {					
			JSch jsch = new JSch();
			int type = KeyPair.RSA;
			try {
				KeyPair kpair = KeyPair.genKeyPair(jsch, type,genkeylength);
				kpair.setPassphrase(genpassphrase);
				Log.i(this.getClass().getName(),
				"Finger print: " + kpair.getFingerPrint());
				FileOutputStream fOut = openFileOutput(PRIVKEYFILE, MODE_PRIVATE);
				kpair.writePrivateKey(fOut);
				fOut.close();

				FileOutputStream fOut2 = openFileOutput(PUBKEYFILE, MODE_PRIVATE);
				kpair.writePublicKey(fOut2, gennickname);

				fOut2.close();
				kpair.dispose();
				
		        Intent a = new Intent(CreateKey.this,MainActivity.class);
		        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		        startActivity(a);

				
				} catch (Exception e) {
						System.out.println(e);
						}

			}
		};
		r.run();
		return true;
	}

}
