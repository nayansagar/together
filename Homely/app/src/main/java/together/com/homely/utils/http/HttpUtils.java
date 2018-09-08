package together.com.homely.utils.http;

import android.net.Uri;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sagar on 4/1/2017.
 */
public class HttpUtils {

    private static HttpUtils httpUtils = new HttpUtils();

    private static HttpClient httpClient = new DefaultHttpClient();

    private static final String hostName = "http://192.168.56.1:8080/";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private HttpUtils(){}

    public static HttpUtils getInstance(){
        if(httpUtils == null){
            synchronized (httpUtils){
                httpUtils = new HttpUtils();
            }
        }
        return httpUtils;
    }

    public Map<String, Object> createUser(String phoneNumber, String name) throws IOException {
        HttpPost httpPost = new HttpPost(hostName+"users");
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", name);
        requestBody.put("phone", phoneNumber);
        String request = objectMapper.writeValueAsString(requestBody);
        httpPost.setEntity(new StringEntity(request));
        httpPost.addHeader("Content-Type", "application/json");
        HttpResponse httpResponse = httpClient.execute(httpPost);
        if(httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
            throw new RuntimeException("Failed to create user for request");
        }
        String responseBody = convertStreamToString(httpResponse.getEntity().getContent());
        return objectMapper.readValue(responseBody, Map.class);
    }

    private String convertStreamToString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[100];
        int nRead = inputStream.read(buf);
        baos.write(buf, 0, nRead);
        baos.flush();
        return new String(baos.toByteArray());
    }

    public String createFamily(String userId, String fName, List<Map<String, String>> invitesList) throws IOException {
        Map<String, Object> invites = new HashMap<>();
        invites.put("name", fName);
        invites.put("members", invitesList);

        HttpPost httpPost = new HttpPost(hostName+"users/"+userId+"/family");
        String request = objectMapper.writeValueAsString(invites);
        httpPost.setEntity(new StringEntity(request));
        httpPost.addHeader("Content-Type", "application/json");
        HttpResponse httpResponse = httpClient.execute(httpPost);
        if(httpResponse.getStatusLine().getStatusCode() != 201){
            throw new RuntimeException("Failed to create family, status : "+httpResponse.getStatusLine().getStatusCode());
        }
        return httpResponse.getHeaders("Location")[0].getValue();
    }

    public void joinFamily(String userId, String familyId) throws IOException {
        HttpPost httpPost = new HttpPost(hostName+"users/"+userId+"/families/"+familyId);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        if(httpResponse.getStatusLine().getStatusCode() != 200){
            throw new RuntimeException("Failed to add user to family, status : "+httpResponse.getStatusLine().getStatusCode());
        }
    }

    public String uploadContent(byte[] content, String contentType) throws IOException {
        HttpPost httpPost = new HttpPost(hostName+"content");
        httpPost.setEntity(new ByteArrayEntity(content));
        httpPost.addHeader("Content-Type", contentType);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        if(httpResponse.getStatusLine().getStatusCode() != 201){
            throw new RuntimeException("Failed to upload content, status : "+httpResponse.getStatusLine().getStatusCode());
        }
        return httpResponse.getHeaders("Location")[0].getValue();
    }

    /*public String uploadContentMultipart(Uri contentUri, String contentType) throws IOException {
        HttpPost httpPost = new HttpPost(hostName+"content");
        FileBody filebodyVideo = new FileBody(new File(contentUri.getPath()), contentType, "any");
        MultipartEntityBuilder reqEntityBuilder = MultipartEntityBuilder.create();
        reqEntityBuilder.addPart("videoFile", filebodyVideo);
        httpPost.setEntity(reqEntityBuilder.build());
        HttpResponse httpResponse = httpClient.execute(httpPost);
        if(httpResponse.getStatusLine().getStatusCode() != 201){
            throw new RuntimeException("Failed to upload content, status : "+httpResponse.getStatusLine().getStatusCode());
        }
        return httpResponse.getHeaders("Location")[0].getValue();
    }*/



    public byte[] downloadContent(String contentId) throws IOException {
        ByteArrayOutputStream baos = null;
        try{
            HttpGet httpGet = new HttpGet(hostName+contentId);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if(httpResponse.getStatusLine().getStatusCode() != 200){
                throw new RuntimeException("Failed to download content, status : "+httpResponse.getStatusLine().getStatusCode());
            }
            baos = new ByteArrayOutputStream();
            httpResponse.getEntity().writeTo(baos);
            return baos.toByteArray();
        }finally {
            baos.close();
        }

    }
}
