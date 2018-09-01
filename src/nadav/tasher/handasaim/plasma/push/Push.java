package nadav.tasher.handasaim.plasma.push;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class Push {
    public static final String maintainer = "http://nockio.com/h/x/push/test/";

    public static void getPushes(OnFetch onFetch) {
        try {
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("filter", new JSONArray().put(0).toString())
                    .build();
            Request request = new Request.Builder()
                    .url(maintainer)
                    .post(requestBody)
                    .build();
            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            if (response != null) {
                String json = response.body().string();
                JSONObject object = new JSONObject(json);
                if (object.getString("mode").equals("client")) {
                    if (object.getBoolean("approved")) {
                        onFetch.onFetch(object.getJSONArray("pushes"));
                    }
                }
                response.close();
            }
            System.out.println("Push Refresh");
        } catch (Exception e) {
            e.printStackTrace();
            onFetch.onFetch(new JSONArray());
        }
    }

    public interface OnFetch {
        void onFetch(JSONArray pushes);
    }
}
