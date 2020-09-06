package com.example.caustudy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.MetricAffectingSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.caustudy.Models.FirebaseID;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.commonmark.parser.InlineParserFactory;
import org.commonmark.parser.Parser;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import io.noties.debug.AndroidLogDebugOutput;
import io.noties.debug.Debug;
import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.SoftBreakAddsNewLinePlugin;
import io.noties.markwon.core.spans.EmphasisSpan;
import io.noties.markwon.core.spans.StrongEmphasisSpan;
import io.noties.markwon.editor.AbstractEditHandler;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;
import io.noties.markwon.editor.MarkwonEditorUtils;
import io.noties.markwon.editor.PersistedSpans;
import io.noties.markwon.editor.handler.EmphasisEditHandler;
import io.noties.markwon.editor.handler.StrongEmphasisEditHandler;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.inlineparser.BangInlineProcessor;
import io.noties.markwon.inlineparser.EntityInlineProcessor;
import io.noties.markwon.inlineparser.HtmlInlineProcessor;
import io.noties.markwon.inlineparser.MarkwonInlineParser;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import io.noties.markwon.linkify.LinkifyPlugin;


public class PostActivity2 extends ActivityWithMenuOptions implements View.OnClickListener {

    private String pendingInput;
    String markdown_text;

    private Context mContext;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private EditText mTitle, editText;
    private FirebaseUser userAuth;
    private Map<String, Object> data = new HashMap<>();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private String user_name;
    private String study_key, study_name, email, name, id;
    DatabaseReference studyRef = FirebaseDatabase.getInstance().getReference("Study");


    @NonNull
    @Override
    public MenuOptions menuOptions() {
        return MenuOptions.create()
                .add("simpleProcess", this::simple_process)
                .add("simplePreRender", this::simple_pre_render)
                .add("customPunctuationSpan", this::custom_punctuation_span)
                .add("additionalEditSpan", this::additional_edit_span)
                .add("additionalPlugins", this::additional_plugins)
                .add("multipleEditSpans", this::multiple_edit_spans)
                .add("multipleEditSpansPlugin", this::multiple_edit_spans_plugin)
                .add("pluginRequire", this::plugin_require)
                .add("pluginNoDefaults", this::plugin_no_defaults)
                .add("heading", this::heading)
                .add("newLine", this::newLine);
    }

    @Override
    protected void beforeOptionSelected(@NonNull String option) {
        // we cannot _clear_ editText of text-watchers without keeping a reference to them...
        pendingInput = editText != null
                ? editText.getText().toString()
                : null;

        createView();
    }

    @Override
    protected void afterOptionSelected(@NonNull String option) {
        if (!TextUtils.isEmpty(pendingInput)) {
            editText.setText(pendingInput);
        }
    }

    public void setmContext(Context context) {
        mContext = context;
    }

    private void createView() {
        setContentView(R.layout.activity_post);

        Intent intent = getIntent();
        study_key = intent.getStringExtra("study_key");
        study_name = intent.getStringExtra("study_name");

        mAuth = FirebaseAuth.getInstance();
        userAuth = mAuth.getCurrentUser();
        mTitle = findViewById(R.id.post_title_edit);
        DatabaseReference databaseReference_user = firebaseDatabase.getReference("사용자");

        if (userAuth != null) {
            Log.d("test","userAuth ok");
            databaseReference_user.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (userAuth.getEmail().equals(ds.child("email").getValue().toString())) {
                            user_name = ds.child("username").getValue().toString();
                        }
                        else {
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        findViewById(R.id.post_save_button).setOnClickListener(this);
        this.editText = findViewById(R.id.post_contents_edit);

        initBottomBar();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createView();
        Debug.init(new AndroidLogDebugOutput(true));

        multiple_edit_spans();
    }

    @Override
    public void onClick(View v) {
        if(mAuth.getCurrentUser() != null) {
            String postId = mStore.collection(FirebaseID.post).document().getId();
            Log.d("PostActivity : postId",postId);
            Log.d("PostActivity : FirebaseID.post : ",FirebaseID.post);

            data = new HashMap<>();
            // Data input part
            data.put("content", editText.getText().toString());
            data.put("title", mTitle.getText().toString());
            data.put("author_uid", mAuth.getCurrentUser().getUid());


            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat mFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String time = mFormat.format(date);


            data.put("time_stamp", FieldValue.serverTimestamp());
            data.put("user_name",this.user_name);
            //mStore.collection(FirebaseID.post).document(postId).set(data, SetOptions.merge());

            String title = mTitle.getText().toString();

            studyRef.child(study_key).child("assignment_list").child(title).child("title").setValue(mTitle.getText().toString());
            studyRef.child(study_key).child("next_assignment").child("title").setValue(mTitle.getText().toString());
            studyRef.child(study_key).child("assignment_list").child(title).child("content").setValue(editText.getText().toString());
            studyRef.child(study_key).child("assignment_list").child(title).child("user_name").setValue(this.user_name);
            studyRef.child(study_key).child("assignment_list").child(title).child("date").setValue(time).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    ((JMainActivity2)JMainActivity2.mContext).set_listview();

                }
            });
            Log.d("test","Auth success");
            finish();
        } else {
            Log.d("test","Auth fail");
        }
    }

    private void simple_process() {
        // Process highlight in-place (right after text has changed)

        // obtain Markwon instance
        final Markwon markwon = Markwon.create(this);

        // create editor
        final MarkwonEditor editor = MarkwonEditor.create(markwon);

        // set edit listener
        editText.addTextChangedListener(MarkwonEditorTextWatcher.withProcess(editor));
    }

    private void simple_pre_render() {
        // Process highlight in background thread

        final Markwon markwon = Markwon.create(this);
        final MarkwonEditor editor = MarkwonEditor.create(markwon);

        editText.addTextChangedListener(MarkwonEditorTextWatcher.withPreRender(
                editor,
                Executors.newCachedThreadPool(),
                editText));
    }

    private void custom_punctuation_span() {
        // Use own punctuation span

        final MarkwonEditor editor = MarkwonEditor.builder(Markwon.create(this))
                .punctuationSpan(EditorActivity.CustomPunctuationSpan.class, EditorActivity.CustomPunctuationSpan::new)
                .build();

        editText.addTextChangedListener(MarkwonEditorTextWatcher.withProcess(editor));
    }

    private void additional_edit_span() {
        // An additional span is used to highlight strong-emphasis

        final MarkwonEditor editor = MarkwonEditor.builder(Markwon.create(this))
                .useEditHandler(new AbstractEditHandler<StrongEmphasisSpan>() {
                    @Override
                    public void configurePersistedSpans(@NonNull PersistedSpans.Builder builder) {
                        // Here we define which span is _persisted_ in EditText, it is not removed
                        //  from EditText between text changes, but instead - reused (by changing
                        //  position). Consider it as a cache for spans. We could use `StrongEmphasisSpan`
                        //  here also, but I chose Bold to indicate that this span is not the same
                        //  as in off-screen rendered markdown
                        builder.persistSpan(EditorActivity.Bold.class, EditorActivity.Bold::new);
                    }

                    @Override
                    public void handleMarkdownSpan(
                            @NonNull PersistedSpans persistedSpans,
                            @NonNull Editable editable,
                            @NonNull String input,
                            @NonNull StrongEmphasisSpan span,
                            int spanStart,
                            int spanTextLength) {
                        // Unfortunately we cannot hardcode delimiters length here (aka spanTextLength + 4)
                        //  because multiple inline markdown nodes can refer to the same text.
                        //  For example, `**_~~hey~~_**` - we will receive `**_~~` in this method,
                        //  and thus will have to manually find actual position in raw user input
                        final MarkwonEditorUtils.Match match =
                                MarkwonEditorUtils.findDelimited(input, spanStart, "**", "__");
                        if (match != null) {
                            editable.setSpan(
                                    // we handle StrongEmphasisSpan and represent it with Bold in EditText
                                    //  we still could use StrongEmphasisSpan, but it must be accessed
                                    //  via persistedSpans
                                    persistedSpans.get(EditorActivity.Bold.class),
                                    match.start(),
                                    match.end(),
                                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            );
                        }
                    }

                    @NonNull
                    @Override
                    public Class<StrongEmphasisSpan> markdownSpanType() {
                        return StrongEmphasisSpan.class;
                    }
                })
                .build();

        editText.addTextChangedListener(MarkwonEditorTextWatcher.withProcess(editor));
    }

    private void additional_plugins() {
        final Markwon markwon = Markwon.builder(this)
                .usePlugin(StrikethroughPlugin.create())
                .build();
        final MarkwonEditor editor = MarkwonEditor.create(markwon);
        editText.addTextChangedListener(MarkwonEditorTextWatcher.withProcess(editor));
    }

    private void multiple_edit_spans() {
        // for links to be clickable
        editText.setMovementMethod(LinkMovementMethod.getInstance());
        final InlineParserFactory inlineParserFactory = MarkwonInlineParser.factoryBuilder()
                // no inline images will be parsed
                .excludeInlineProcessor(BangInlineProcessor.class)
                // no html tags will be parsed
                .excludeInlineProcessor(HtmlInlineProcessor.class)
                // no entities will be parsed (aka `&amp;` etc)
                .excludeInlineProcessor(EntityInlineProcessor.class)
                .build();

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(StrikethroughPlugin.create())
                .usePlugin(LinkifyPlugin.create())
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureParser(@NonNull Parser.Builder builder) {

                        // disable all commonmark-java blocks, only inlines will be parsed
//                        builder.enabledBlockTypes(Collections.emptySet());

                        builder.inlineParserFactory(inlineParserFactory);
                    }
                })
                .usePlugin(SoftBreakAddsNewLinePlugin.create())
                .build();

        final LinkEditHandler.OnClick onClick = (widget, link) -> markwon.configuration().linkResolver().resolve(widget, link);

        final MarkwonEditor editor = MarkwonEditor.builder(markwon)
                .useEditHandler(new EmphasisEditHandler())
                .useEditHandler(new StrongEmphasisEditHandler())
                .useEditHandler(new StrikethroughEditHandler())
                .useEditHandler(new CodeEditHandler())
                .useEditHandler(new BlockQuoteEditHandler())
                .useEditHandler(new LinkEditHandler(onClick))
                .build();

//        editText.addTextChangedListener(MarkwonEditorTextWatcher.withProcess(editor));
        editText.addTextChangedListener(MarkwonEditorTextWatcher.withPreRender(
                editor, Executors.newSingleThreadExecutor(), editText));
    }

    private void multiple_edit_spans_plugin() {
        // inline parsing is configured via MarkwonInlineParserPlugin

        // for links to be clickable
        editText.setMovementMethod(LinkMovementMethod.getInstance());

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(StrikethroughPlugin.create())
                .usePlugin(LinkifyPlugin.create())
                .usePlugin(MarkwonInlineParserPlugin.create(builder -> {
                    builder
                            .excludeInlineProcessor(BangInlineProcessor.class)
                            .excludeInlineProcessor(HtmlInlineProcessor.class)
                .excludeInlineProcessor(EntityInlineProcessor.class);
    }))
            .build();

    final LinkEditHandler.OnClick onClick = (widget, link) -> markwon.configuration().linkResolver().resolve(widget, link);

    final MarkwonEditor editor = MarkwonEditor.builder(markwon)
            .useEditHandler(new EmphasisEditHandler())
            .useEditHandler(new StrongEmphasisEditHandler())
            .useEditHandler(new StrikethroughEditHandler())
            .useEditHandler(new CodeEditHandler())
            .useEditHandler(new BlockQuoteEditHandler())
            .useEditHandler(new LinkEditHandler(onClick))
            .build();

        editText.addTextChangedListener(MarkwonEditorTextWatcher.withPreRender(
    editor, Executors.newSingleThreadExecutor(), editText));
}

    private void newLine() {
        final Markwon markwon = Markwon.create(this);
        final MarkwonEditor editor = MarkwonEditor.create(markwon);
        final TextWatcher textWatcher = MarkdownNewLine.wrap(MarkwonEditorTextWatcher.withProcess(editor));
        editText.addTextChangedListener(textWatcher);
    }

    private void plugin_require() {
        // usage of plugin from other plugins
        final Markwon markwon = Markwon.builder(this)
                .usePlugin(MarkwonInlineParserPlugin.create())
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configure(@NonNull Registry registry) {
                        registry.require(MarkwonInlineParserPlugin.class)
                                .factoryBuilder()
                                .excludeInlineProcessor(HtmlInlineProcessor.class);
                    }
                })
                .build();

        editText.setMovementMethod(LinkMovementMethod.getInstance());

        final MarkwonEditor editor = MarkwonEditor.create(markwon);

        editText.addTextChangedListener(MarkwonEditorTextWatcher.withPreRender(
                editor, Executors.newSingleThreadExecutor(), editText));
    }

    private void plugin_no_defaults() {
        // a plugin with no defaults registered

        final Markwon markwon = Markwon.builder(this)
                .usePlugin(MarkwonInlineParserPlugin.create(MarkwonInlineParser.factoryBuilderNoDefaults()))
//                .usePlugin(MarkwonInlineParserPlugin.create(MarkwonInlineParser.factoryBuilderNoDefaults(), factoryBuilder -> {
//                    // if anything, they can be included here
////                    factoryBuilder.includeDefaults()
//                }))
                .build();

        final MarkwonEditor editor = MarkwonEditor.create(markwon);

        editText.addTextChangedListener(MarkwonEditorTextWatcher.withPreRender(
                editor, Executors.newSingleThreadExecutor(), editText));
    }

    private void heading() {
        final Markwon markwon = Markwon.create(this);
        final MarkwonEditor editor = MarkwonEditor.builder(markwon)
                .useEditHandler(new HeadingEditHandler())
                .build();

        editText.addTextChangedListener(MarkwonEditorTextWatcher.withPreRender(
                editor, Executors.newSingleThreadExecutor(), editText));
    }

    private void initBottomBar() {
        // all except block-quote wraps if have selection, or inserts at current cursor position

        final Button bold = findViewById(R.id.bold);
        final Button italic = findViewById(R.id.italic);
        final Button strike = findViewById(R.id.strike);
        final Button quote = findViewById(R.id.quote);
        final Button code = findViewById(R.id.code);

        addSpan(bold, new StrongEmphasisSpan());
        addSpan(italic, new EmphasisSpan());
        addSpan(strike, new StrikethroughSpan());

        bold.setOnClickListener(new EditorActivity.InsertOrWrapClickListener(editText, "**"));
        italic.setOnClickListener(new EditorActivity.InsertOrWrapClickListener(editText, "_"));
        strike.setOnClickListener(new EditorActivity.InsertOrWrapClickListener(editText, "~~"));
        code.setOnClickListener(new EditorActivity.InsertOrWrapClickListener(editText, "`"));
        quote.setOnClickListener(v -> {

            final int start = editText.getSelectionStart();
            final int end = editText.getSelectionEnd();

            if (start < 0) {
                return;
            }

            if (start == end) {
                editText.getText().insert(start, "> ");
            } else {
                // wrap the whole selected area in a quote
                final List<Integer> newLines = new ArrayList<>(3);
                newLines.add(start);

                final String text = editText.getText().subSequence(start, end).toString();
                int index = text.indexOf('\n');
                while (index != -1) {
                    newLines.add(start + index + 1);
                    index = text.indexOf('\n', index + 1);
                }

                for (int i = newLines.size() - 1; i >= 0; i--) {
                    editText.getText().insert(newLines.get(i), "> ");
                }
            }
        });
    }

    private static void addSpan(@NonNull TextView textView, Object... spans) {
        final SpannableStringBuilder builder = new SpannableStringBuilder(textView.getText());
        final int end = builder.length();
        for (Object span : spans) {
            builder.setSpan(span, 0, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setText(builder);
    }

    private static class InsertOrWrapClickListener implements View.OnClickListener {

        private final EditText editText;
        private final String text;

        InsertOrWrapClickListener(@NonNull EditText editText, @NonNull String text) {
            this.editText = editText;
            this.text = text;
        }

        @Override
        public void onClick(View v) {
            final int start = editText.getSelectionStart();
            final int end = editText.getSelectionEnd();

            if (start < 0) {
                return;
            }

            if (start == end) {
                // insert at current position
                editText.getText().insert(start, text);
            } else {
                editText.getText().insert(end, text);
                editText.getText().insert(start, text);
            }
        }
    }

    private static class CustomPunctuationSpan extends ForegroundColorSpan {
        CustomPunctuationSpan() {
            super(0xFFFF0000); // RED
        }
    }

    private static class Bold extends MetricAffectingSpan {
        public Bold() {
            super();
        }

        @Override
        public void updateDrawState(TextPaint tp) {
            update(tp);
        }

        @Override
        public void updateMeasureState(@NonNull TextPaint textPaint) {
            update(textPaint);
        }

        private void update(@NonNull TextPaint paint) {
            paint.setFakeBoldText(true);
        }
    }
}
