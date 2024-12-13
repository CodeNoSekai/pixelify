package node.ditzdev.pixelify.fragments;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import node.ditzdev.pixelify.ErrorHandlerActivity;
import node.ditzdev.pixelify.MainActivity;
import node.ditzdev.pixelify.R;

public class ADBShellFragment extends Fragment {
    private Button btnContinue;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_adb_shell, container, false);
        ErrorHandlerActivity.setDefaultUncaughtExceptionHandler(requireContext());

        swipeRefreshLayout = inflate.findViewById(R.id.swipeRefreshLayout);
        btnContinue = inflate.findViewById(R.id.btnContinue);
        Button linkAdb = inflate.findViewById(R.id.btnLinkAdb);
        Button copy = inflate.findViewById(R.id.btnCopy);
        copy.setTooltipText("Copy Command Here");
        linkAdb.setTooltipText("Open Link Download");

        swipeRefreshLayout.setOnRefreshListener(
                () -> {
                    checkPermissionStatus();
                    swipeRefreshLayout.setRefreshing(false);
                });

        copy.setOnClickListener(
                v -> {
                    ClipboardManager clipboard =
                            (ClipboardManager)
                                    requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip =
                            ClipData.newPlainText(
                                    "Command Shell: ",
                                    "adb shell pm grant node.ditzdev.pixelify android.permission.WRITE_SECURE_SETTINGS");
                    clipboard.setPrimaryClip(clip);
                    Snackbar.make(v, "Command Shell Copied", Snackbar.LENGTH_SHORT).show();
                });

        linkAdb.setOnClickListener(
                v -> {
                    Intent intent =
                            new Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(
                                            "https://developer.android.com/tools/releases/platform-tools"));
                    startActivity(intent);
                });

        btnContinue.setOnClickListener(
                v -> {
                    if (hasWriteSecureSettingsPermission()) {
                        startMainActivity();
                    } else {
                        Toast.makeText(
                                        requireContext(),
                                        "Permission not granted. Please use ADB command.",
                                        Toast.LENGTH_SHORT)
                                .show();
                    }
                });

        return inflate;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkPermissionStatus();
    }

    private void checkPermissionStatus() {
        boolean hasPermission = hasWriteSecureSettingsPermission();
        btnContinue.setEnabled(hasPermission);

        if (getActivity() != null) {
            if (hasPermission) {
                Snackbar.make(getActivity().findViewById(android.R.id.content), "Permission Granted!", Snackbar.LENGTH_SHORT).show();
            } else {
            }
        }
    }

    private boolean hasWriteSecureSettingsPermission() {
        return ContextCompat.checkSelfPermission(
                        requireContext(), Manifest.permission.WRITE_SECURE_SETTINGS)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void startMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }
}
