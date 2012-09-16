package org.chaotisch.hqapp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

import android.app.Application;


public class hqapp extends Application {
		private static hqapp thisone = null;
		private boolean identityLoaded = false;
		
		public boolean isIdentityLoaded() {
			try {
				if(identityLoaded && sshobj.getIdentityNames().size() >0)
					return true;
			} catch (JSchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}


		public void setIdentityLoaded(boolean identityLoaded) {
			this.identityLoaded = identityLoaded;
		}


		private JSch sshobj;
		
		public JSch getSshobj() {
			return sshobj;
		}


		@Override
		  public void onCreate() {
		    super.onCreate();
		    // TODO Put your application initialization code here.
		    thisone =this;
		    this.sshobj = new JSch();
		    
		    
		  }
		
		
		static synchronized hqapp getHqapp()
		{
			if(thisone == null)
			{
				new hqapp();
			}
			return thisone;
			
		}

		
		
}
