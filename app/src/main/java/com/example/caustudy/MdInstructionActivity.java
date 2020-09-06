package com.example.caustudy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import io.noties.markwon.Markwon;
import io.noties.markwon.core.CorePlugin;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;

public class MdInstructionActivity extends AppCompatActivity {
    String marktext;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_md_instruction);
        tv = findViewById(R.id.md_instruction);
        marktext = "# Markdown Editior  \n## 지원하는 문법 설명서  \n  \n- '#' 의 갯수(1~6개)에 따라 제목 크기  \n> # 헤더1  \n> ## 헤더2  \n> ###### 헤더6  \n\n- '-' 문장 첫 시작에 사용 시 동그라미  \n- '*, _' 문장 앞 뒤에 사용 시 *기울이기*   \n- '**, __' 문장 앞 뒤에 사용 시 **강조하기**  \n- '~~' 문장 앞 뒤에 사용 시 ~~줄 긋기~~  \n- `형광펜` 기능은 문장 앞 뒤에 ' ` '  \n- 줄 바꾸기는 스페이스바 2칸  \n> '>' 는 들여쓰기 입니다  \n";
        simple();
    }

    private void simple() {
        // 여기에 그냥 친거 그대로 받아오면 될 것 같은데?
        final Markwon markwon = Markwon.builder(this)
                .usePlugin(CorePlugin.create())
                .usePlugin(StrikethroughPlugin.create())
                .build();

        final String markdown = marktext;
        // this will parse raw markdown and set parsed content to specified TextView
        markwon.setMarkdown(tv, markdown);
    }
}
