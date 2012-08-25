package org.chaotisch.hqapp;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.nio.CharBuffer;
import java.util.Properties;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.jce.KeyPairGenRSA;

public class MainActivity extends Activity {
	/** Called when the activity is first created. */
	Button button;
	Button btnaltkey;
	Session session;
	ByteArrayOutputStream baos;
	ByteArrayInputStream bais;
	Channel channel;
	final static String PRIVKEYFILE = "private.key";
	final static String PUBKEYFILE = "public.key";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		bais = new ByteArrayInputStream(new byte[1000]);
		button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final Runnable r = new Runnable()
				{
				    public void run() 
				    {
				    	onSSH("31", "strom");
				        Log.i(this.getClass().getName(),"fertig SSHed");
				    }
				};
				r.run();
				// onCommand(arg0);
			}
			
		});
		btnaltkey = (Button) findViewById(R.id.button2);
		btnaltkey.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				sharekey();
			}

		});
		
		File file = getFileStreamPath(PRIVKEYFILE);
		if(!file.exists()) {
			final Runnable r = new Runnable()
			{
			    public void run() 
			    {
			        makeakey();
			        Log.i(this.getClass().getName(),"Key created!");
			    }
			};
			r.run();
		}

	}

	public void onSSH(String myaction, String username) {
//		String username = "strom";
//		String password = "testpassword";
		String host = "192.168.2.10"; // sample ip address
			JSch jsch = new JSch();
			try {
				
				File file = getFileStreamPath(PRIVKEYFILE);
				jsch.addIdentity(file.getAbsolutePath(), "solong");
				session = jsch.getSession(username, host, 22);
//				session.setPassword(password);
				Properties properties = new Properties();
				properties.put("StrictHostKeyChecking", "no");
				session.setConfig(properties);
				session.connect(30000);
				channel = session.openChannel("shell");
				channel.setOutputStream(System.out);
				PrintStream shellStream = new PrintStream(
						channel.getOutputStream());
				channel.connect();
				
				shellStream.println(myaction);
				shellStream.flush();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				channel.disconnect();
				session.disconnect();
			} catch (JSchException e) {
				// TODO Auto-generated catch block
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public void sharekey() {
        // ##### Read the file back in #####
        
        /* We have to use the openFileInput()-method
         * the ActivityContext provides.
         * Again for security reasons with
         * openFileInput(...) */
        try{
		FileInputStream fIn = openFileInput(PUBKEYFILE);
        BufferedReader fInBuffer= new BufferedReader(new InputStreamReader(fIn));

		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(android.content.Intent.EXTRA_TEXT, fInBuffer.readLine());
		startActivity(Intent.createChooser(intent,
				"Select an action for sharing"));
		fInBuffer.close();
	}catch (IOException ioe) {
        ioe.printStackTrace();
	}
	}
	public void makeakey() {
		JSch jsch = new JSch();
		int type = KeyPair.RSA;
		try {
			KeyPair kpair = KeyPair.genKeyPair(jsch, type);
			kpair.setPassphrase("solong");

			Log.i(this.getClass().getName(),"Finger print: " + kpair.getFingerPrint());
			OutputStream out = new OutputStream() {
				private StringBuilder string = new StringBuilder();

				@Override
				public void write(int b) throws IOException {
					this.string.append((char) b);

				}

				public String toString() {
					return this.string.toString();
				}
			};
			
			kpair.writePublicKey(out,"Hq app key");
			
				FileOutputStream fOut = openFileOutput(PRIVKEYFILE,
						MODE_PRIVATE);
				kpair.writePrivateKey(fOut);

				fOut.close();
				
				
				FileOutputStream fOut2 = openFileOutput(PUBKEYFILE,
						MODE_PRIVATE);
				kpair.writePublicKey(fOut2,"Hq app key");

				fOut2.close();
			
			kpair.dispose();
			
		} catch (Exception e) {
			System.out.println(e);
		}

	}
}
