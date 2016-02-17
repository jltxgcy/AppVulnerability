package com.example.nanohttpd;

import java.io.IOException;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	private SimpleServer server;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		server = new SimpleServer();
		try {
			
			// 因为程序模拟的是html放置在asset目录下，
			// 所以在这里存储一下AssetManager的指针。
			server.asset_mgr = this.getAssets();
			
			// 启动web服务
			server.start();
			
			Log.i("Httpd", "The server started.");
		} catch(IOException ioe) {
		    Log.w("Httpd", "The server could not start.");
		}
	}

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        
        if (server != null){
        	// 在程序退出时关闭web服务器
            server.stop();
    	}
        Log.w("Httpd", "The server stopped.");
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
