package node.ditzdev.pixelify.fragments;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import rikka.shizuku.Shizuku;

public class ShizukuFragment extends Fragment {

  private static final int REQUEST_CODE_SHIZUKU = 1;
  private static final int REQUEST_CODE_WRITE_SECURE_SETTINGS = 2;

  private Button btnContinue, btnConnect;
  private SwipeRefreshLayout swipeRefreshLayout;

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    var inflate = inflater.inflate(R.layout.fragment_shizuku, container, false);
    swipeRefreshLayout = inflate.findViewById(R.id.swipeRefreshLayout);
    btnContinue = inflate.findViewById(R.id.btnContinue);
    btnConnect = inflate.findViewById(R.id.btnConnect);
    Button linkShizuku = inflate.findViewById(R.id.btnlinkShizuku);
    Button linkTutorial = inflate.findViewById(R.id.btnTutorialShizuku);

    swipeRefreshLayout.setOnRefreshListener(
        () -> {
          checkPermissionStatus();
          swipeRefreshLayout.setRefreshing(false);
        });

    linkShizuku.setOnClickListener(
        v -> {
          Intent intent =
              new Intent(
                  Intent.ACTION_VIEW,
                  Uri.parse(
                      "https://play.google.com/store/apps/details?id=moe.shizuku.privileged.api"));
          startActivity(intent);
        });

    linkTutorial.setOnClickListener(
        v -> {
          Intent intent =
              new Intent(
                  Intent.ACTION_VIEW,
                  Uri.parse("https://youtu.be/LuFiQ_FaiD4?si=TemZ1V9BDX0afoZ4"));
          startActivity(intent);
        });

    btnContinue.setOnClickListener(
        v -> {
          if (hasWriteSecureSettingsPermission()) {
            startMainActivity();
          } else {
            Toast.makeText(
                    requireContext(),
                    "Permission not granted. Please use another way.",
                    Toast.LENGTH_SHORT)
                .show();
          }
        });

    btnConnect.setOnClickListener(v -> connectShizuku());

    return inflate;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    checkPermissionStatus();
  }

  private void connectShizuku() {
    if (Shizuku.pingBinder()) {
      if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
        requestWriteSecureSettingsPermission();
      } else {
        Shizuku.requestPermission(REQUEST_CODE_SHIZUKU);
      }
    } else {
      Toast.makeText(requireContext(), "Shizuku service not found", Toast.LENGTH_SHORT).show();
    }
  }

  private void requestWriteSecureSettingsPermission() {
    try {
      Shizuku.newProcess(new String[]{
          "pm", "grant", requireContext().getPackageName(), 
          "android.permission.WRITE_SECURE_SETTINGS"
      }, null, null);
      
      if (checkWriteSecureSettingsPermission()) {
        showSuccessSnackbarAndNavigate();
      }
    } catch (Exception e) {
      Log.e("ShizukuPermission", "FailedPermissionShizukuProvider", e);
      Toast.makeText(requireContext(), "Failed to Grant Permission", Toast.LENGTH_SHORT).show();
    }
  }

  private boolean checkWriteSecureSettingsPermission() {
    try {
      PackageManager packageManager = requireContext().getPackageManager();
      return packageManager.checkPermission(
          "android.permission.WRITE_SECURE_SETTINGS", 
          requireContext().getPackageName()
      ) == PackageManager.PERMISSION_GRANTED;
    } catch (Exception e) {
      Log.e("PermissionCheck", "FailedCheckedPermission", e);
      return false;
    }
  }

  private void showSuccessSnackbarAndNavigate() {
    if (getActivity() != null) {
      Snackbar.make(getActivity().findViewById(android.R.id.content), 
          "Access \"WRITE_SECURE_SETTINGS\" is Granted", 
          Snackbar.LENGTH_LONG
      ).show();

      getActivity().findViewById(android.R.id.content).postDelayed(() -> {
        startMainActivity();
      }, 1500);
    }
  }

  private void checkPermissionStatus() {
    boolean hasPermission = hasWriteSecureSettingsPermission();
    btnContinue.setEnabled(hasPermission);
    if (getActivity() != null) {
      if (hasPermission) {
        Snackbar.make(
                getActivity().findViewById(android.R.id.content),
                "Permission Granted!",
                Snackbar.LENGTH_SHORT)
            .show();
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

  private final Shizuku.OnRequestPermissionResultListener mRequestPermissionResultCallback = 
    (requestCode, grantResult) -> {
      if (grantResult == PackageManager.PERMISSION_GRANTED) {
        if (requestCode == REQUEST_CODE_SHIZUKU) {
          requestWriteSecureSettingsPermission();
        } else if (requestCode == REQUEST_CODE_WRITE_SECURE_SETTINGS) {
          if (checkWriteSecureSettingsPermission()) {
            showSuccessSnackbarAndNavigate();
          }
        }
      } else {
        Toast.makeText(requireContext(), "Shizuku's permission was denied", Toast.LENGTH_SHORT).show();
      }
    };

  @Override
  public void onStart() {
    super.onStart();
    Shizuku.addRequestPermissionResultListener(mRequestPermissionResultCallback);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Shizuku.removeRequestPermissionResultListener(mRequestPermissionResultCallback);
  }
}