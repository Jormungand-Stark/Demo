package com.example.activitytest.Fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.activitytest.Listener.DialogWarning;
import com.example.activitytest.R;
import com.example.activitytest.Translate.TransApi;

import java.util.Objects;

import yalantis.com.sidemenu.interfaces.ScreenShotable;

public class TranslateFragment extends Fragment implements ScreenShotable{

    private static boolean flag = true; // true表英译中，false表中译英
    private static final String APP_ID = "20220329001148587";
    private static final String SECURITY_KEY = "ps_k4ImcKEWzYSIrJjks";
    private static final TransApi api = new TransApi(APP_ID, SECURITY_KEY);

    private TextView to_translate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.translate_frag, container, false);

        TextView from_language = rootView.findViewById(R.id.from_language);
        TextView to_language = rootView.findViewById(R.id.to_language);

        Button change_language = rootView.findViewById(R.id.change_language);
        change_language.setOnClickListener(v-> {
            flag = !flag;
            if (flag) { // 英译中
                from_language.setText("英文");
                to_language.setText("中文");
            }
            else { // 中译英
                from_language.setText("中文");
                to_language.setText("英文");
            }
        });

        EditText from_translate = rootView.findViewById(R.id.from_translate);
        to_translate = rootView.findViewById(R.id.to_translate);

        Button begin_translate = rootView.findViewById(R.id.begin_translate);
        begin_translate.setOnClickListener(v->{
            String src_String = from_translate.getText().toString();
            if (src_String.isEmpty()){ // 待翻译内容为空
                DialogWarning.Warning("未输入待翻译内容", getContext());
            }
            else { // 非空
                if (flag) { // 英译中
                    translate(src_String, "en", "zh");
                }
                else { // 中译英
                    translate(src_String, "zh", "en");
                }
            }
        });
        return rootView;
    }

    private void translate (String query, String from, String to) {
        new Thread(() -> { // 子线程执行逻辑，主线程负责UI
            String res = api.getTransResult(query, from, to);
            JSONObject json = (JSONObject) JSON.parse(res);
            JSONArray jsonArray = JSON.parseArray(Objects.requireNonNull(json.get("trans_result")).toString());
            StringBuilder dst = new StringBuilder();
            for (Object o : jsonArray) {
                JSONObject jsonObject = (JSONObject) o;
                dst.append("\n").append(Objects.requireNonNull(jsonObject.get("dst")));
            }
            requireActivity().runOnUiThread(() -> to_translate.setText(dst.toString()));
        }).start();
    }

    @Override
    public void takeScreenShot() {
    }

    @Override
    public Bitmap getBitmap() {
        return null;
    }
}

