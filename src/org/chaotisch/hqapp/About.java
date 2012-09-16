package org.chaotisch.hqapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.widget.TextView;

public class About extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        TextView version = (TextView) findViewById(R.id.Version);
        PackageInfo pInfo;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		
			version.setMovementMethod(LinkMovementMethod.getInstance());

        version.setText(Html.fromHtml("Version "+pInfo.versionName+"<br />For new versions visit: <a href='http://ccc-ffm.de'>ccc-ffm.de</a>"));
    } catch (NameNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_about, menu);
        return true;
    }*/
}
