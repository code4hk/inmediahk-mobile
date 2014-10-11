package net.inmediahk.reader.Util;

import android.net.http.AndroidHttpClient;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.params.HttpConnectionParams;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class OkHTTPClient {
    private OkHttpClient client = new OkHttpClient();

    public final String get(String path) {
        try {
            Request request = new Request.Builder()
                    .url(path)
                    .build();

            Response response = client.newCall(request).execute();

            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

	public String post(String path, ArrayList<NameValuePair> list, String encode){
		AndroidHttpClient client = null;
		try {
			UrlEncodedFormEntity entity=new UrlEncodedFormEntity(list, encode);

			HttpPost httpPost = new HttpPost(path);
			httpPost.setEntity(entity);

			client = AndroidHttpClient.newInstance("");

			HttpConnectionParams.setConnectionTimeout(client.getParams(), 3000);
			HttpConnectionParams.setSoTimeout(client.getParams(), 5000);

			HttpResponse httpResponse = client.execute(httpPost);

			if(httpResponse.getStatusLine().getStatusCode()==200){
				InputStream inputStream=httpResponse.getEntity().getContent();
				return toString(inputStream,encode);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(client != null) {
				client.close();
			}
		}
		return null;
	}
	
	private String toString(InputStream inputStream, String encode) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len;
		String result="";
		if (inputStream != null) {
			try {
				while ((len = inputStream.read(data)) != -1) {
					outputStream.write(data,0,len);
				}
				result=new String(outputStream.toByteArray(),encode);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}