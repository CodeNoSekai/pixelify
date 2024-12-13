package node.ditzdev.pixelify;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorHandlerActivity extends AppCompatActivity {
    private static final String EXTRA_ERROR = "extra_error";

    public static Intent createErrorIntent(Context context, Throwable throwable) {
        Intent intent = new Intent(context, ErrorHandlerActivity.class);
        intent.putExtra(EXTRA_ERROR, getStackTraceString(throwable));
        intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    private static String getStackTraceString(Throwable throwable) {
        if (throwable == null) {
            return "";
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_handler);
        
        String errorMessage = getIntent().getStringExtra(EXTRA_ERROR);
        TextView errorTextView = findViewById(R.id.error_text_view);
        errorTextView.setText(errorMessage);
        
        findViewById(R.id.btn_restart)
                .setOnClickListener(
                        v -> {
                            Intent intent = new Intent(this, Splash.class);
                            intent.addFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            | Intent.FLAG_ACTIVITY_NEW_TASK
                                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            System.exit(0);
                        });
        
        findViewById(R.id.btnCopy)
                .setOnClickListener(v -> {
                    ClipboardManager clipboard = (ClipboardManager) 
                        getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Error Log", errorMessage);
                    clipboard.setPrimaryClip(clip);
                    Snackbar.make(v, "Error Log Copied", Snackbar.LENGTH_SHORT).show();
                });
    }

    public static void setDefaultUncaughtExceptionHandler(Context context) {
        Thread.setDefaultUncaughtExceptionHandler(
                (thread, throwable) -> {
                    Intent errorIntent = createErrorIntent(context, throwable);
                    context.startActivity(errorIntent);
                    System.exit(1);
                });
    }
}