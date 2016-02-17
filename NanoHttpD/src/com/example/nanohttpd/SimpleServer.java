package com.example.nanohttpd;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import android.content.res.AssetManager;
import android.util.Log;


public class SimpleServer extends NanoHTTPD {
	AssetManager asset_mgr;
	
    public SimpleServer() {
    	// �˿���8088��Ҳ����˵Ҫͨ��http://127.0.0.1:8088���õ���
        super(8088);
    }

    public Response serve(String uri, Method method, 
            Map<String, String> header,
            Map<String, String> parameters,
            Map<String, String> files)
    {
        int len = 0;  
		byte[] buffer = null;
		Log.d("jltxgcy", header.get("remote-addr"));
    	
		// Ĭ�ϴ����url���ԡ�/����ͷ�ģ���Ҫɾ����������ͱ���˾���·��
    	String file_name = uri.substring(1);
    	
    	// Ĭ�ϵ�ҳ�������趨Ϊindex.html
    	if(file_name.equalsIgnoreCase("")){
    		file_name = "index.html";
    	}

    	try {
			
    		//ͨ��AssetManagerֱ�Ӵ��ļ����ж�ȡ����
			InputStream in = asset_mgr.open(file_name, AssetManager.ACCESS_BUFFER);
			
			//���赥����ҳ�ļ���С��������1MB
		 	buffer = new byte[1024*1024];  
	        
		 	int temp=0;
	        while((temp=in.read())!=-1){
	        	buffer[len]=(byte)temp;  
	            len++;  
	        }
		    in.close();  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	// ����ȡ�����ļ����ݷ��ظ������
        return new NanoHTTPD.Response(new String(buffer,0,len));

    }
}