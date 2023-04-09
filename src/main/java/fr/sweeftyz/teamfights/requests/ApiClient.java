package fr.sweeftyz.teamfights.requests;

import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



/*
Classe Inutile, remplacé par un serveur websockets.
 */
public class ApiClient {
    private final OkHttpClient client;
    private final ExecutorService executorService;

    public ApiClient() {
        client = new OkHttpClient();
        executorService = Executors.newFixedThreadPool(10);
    }

    public void sendGetRequest(String url, ApiCallback<String> callback) {
        Request request = new Request.Builder().url(url).build();
        sendRequest(request, callback);
    }

    public void sendPostRequest(String url, String body, ApiCallback<String> callback) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(body, mediaType);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        sendRequest(request, callback);
    }

    private void sendRequest(Request request, ApiCallback<String> callback) {
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    callback.onSuccess(responseBody);
                } else {
                    callback.onError(new IOException("Unexpected code " + response));
                }
                response.close();
            }
        });
    }

    public void close() {
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
    }

    public interface ApiCallback<T> {
        void onSuccess(T response);

        void onError(Exception e);
    }
}

