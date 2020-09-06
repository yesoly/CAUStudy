package com.example.caustudy;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.commonmark.ext.gfm.strikethrough.Strikethrough;
import org.commonmark.node.Block;
import org.commonmark.node.BlockQuote;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;

import java.sql.Ref;
import java.util.Set;

import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.MarkwonSpansFactory;
import io.noties.markwon.RenderProps;
import io.noties.markwon.core.CorePlugin;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;

public class CoreActivity extends ActivityWithMenuOptions {
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Study");
    private TextView textView;
    private String study_key;
    private String mark_text;
    @NonNull
    @Override
    public MenuOptions menuOptions() {
        return MenuOptions.create()
                .add("simple", this::simple)
                .add("toast", this::toast)
                .add("alreadyParsed", this::alreadyParsed)
                .add("enabledBlockTypes", this::enabledBlockTypes);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view);
        Intent intent = getIntent();
        study_key = intent.getStringExtra("study_key");
        textView = findViewById(R.id.text_view);

        myRef.child(study_key).child("detail_info").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                if (ds.getValue().toString() != null) {
                    mark_text = ds.getValue().toString();
                    simple();
                    //enabledBlockTypes();
                    //alreadyParsed();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void step_1() {
        final Markwon markwon = Markwon.create(this);
        final Markwon markwon2 = Markwon.builder(this)
                .usePlugin(CorePlugin.create())
                .build();
    }

    /**
     * To simply apply raw (non-parsed) markdown call {@link Markwon#setMarkdown(TextView, String)}
     */
    private void simple() {
        // 여기에 그냥 친거 그대로 받아오면 될 것 같은데?
        final Markwon markwon = Markwon.builder(this)
                .usePlugin(CorePlugin.create())
                .usePlugin(StrikethroughPlugin.create())
                .build();
        
        final String markdown = mark_text;
        // this will parse raw markdown and set parsed content to specified TextView
        markwon.setMarkdown(textView, markdown);
    }


    private void toast() {

        final String markdown = mark_text;

        final Markwon markwon = Markwon.create(this);

        final Spanned spanned = markwon.toMarkdown(markdown);

        Toast.makeText(this, spanned, Toast.LENGTH_LONG).show();
    }

    /*
     * To apply already parsed markdown use {@link Markwon#setParsedMarkdown(TextView, Spanned)}
     */
    private void alreadyParsed() {

        final String markdown = mark_text;
        final Markwon markwon = Markwon.create(this);
        // parse markdown to obtain a Node
        final Node node = markwon.parse(markdown);
        // create a spanned content from parsed node
        final Spanned spanned = markwon.render(node);
        // apply parsed markdown
        markwon.setParsedMarkdown(textView, spanned);
    }

    private void enabledBlockTypes() {

        final String md = mark_text;

        final Set<Class<? extends Block>> blocks = CorePlugin.enabledBlockTypes();
        blocks.remove(BlockQuote.class);

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureParser(@NonNull Parser.Builder builder) {
                        builder.enabledBlockTypes(blocks);
                    }
                })
                .build();
        markwon.setMarkdown(textView, md);
    }
}
