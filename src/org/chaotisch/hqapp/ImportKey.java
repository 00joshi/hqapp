package org.chaotisch.hqapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.app.Activity;
import android.content.Intent;

public class ImportKey extends Activity {
	final static String PRIVKEYFILE = "private.key";
	final static String PUBKEYFILE = "public.key";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_import_key);
	}

	public void savekey(View view) {
		
		EditText inputpub = (EditText) findViewById(R.id.fldpubkey);
		EditText inputpriv = (EditText) findViewById(R.id.fldprivkey);
		
		byte[] bufpub = inputpub.getText().toString().getBytes();
		byte[] bufpriv = inputpriv.getText().toString().getBytes();
		Log.i(this.getClass().getName(), "saving imported key");
		File filepriv = new File(PRIVKEYFILE);
		if (filepriv.delete()){
		Log.i(this.getClass().getName(),"Deleted" + PRIVKEYFILE);
		}
		File filepub = new File(PUBKEYFILE);
		if(filepub.delete()){
		Log.i(this.getClass().getName(),"Deleted" + PUBKEYFILE);
		}
		try {
			FileOutputStream fOut = openFileOutput(PRIVKEYFILE, MODE_PRIVATE);
			for (int i = 0; i < bufpriv.length; i++) {
				fOut.write(bufpriv[i]);
			}
			fOut.close();

			FileOutputStream fOut2 = openFileOutput(PUBKEYFILE, MODE_PRIVATE);
			for (int i = 0; i < bufpub.length; i++) {
				fOut2.write(bufpub[i]);
			}
			fOut2.close();
			
	        Intent a = new Intent(ImportKey.this,MainActivity.class);
	        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
	        startActivity(a);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
