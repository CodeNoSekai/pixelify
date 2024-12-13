package node.ditzdev.pixelify.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import node.ditzdev.pixelify.MainActivity;
import node.ditzdev.pixelify.R;

public class RootFragment extends Fragment {
    
    private Button btnRequestAccess, btnContinue;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final String TAG = "RootFragment";
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        var inflate = inflater.inflate(R.layout.fragment_root, container, false);
        
        btnRequestAccess = inflate.findViewById(R.id.btnRoot);
        btnContinue = inflate.findViewById(R.id.btnContinue);
        swipeRefreshLayout = inflate.findViewById(R.id.swipeRefreshLayout);
        
        btnContinue.setEnabled(false);
        
        swipeRefreshLayout.setOnRefreshListener(() -> {
            checkRootAccessAndPermissions();
            swipeRefreshLayout.setRefreshing(false);
        });
        
        btnRequestAccess.setOnClickListener(v -> requestRootAccessAndPermissions());
        
        btnContinue.setOnClickListener(v -> {
            if (isRootAvailable() && hasWriteSecureSettingsPermission()) {
                startMainActivity();
            } else {
                Toast.makeText(requireContext(), "Root access or permission not granted", Toast.LENGTH_SHORT).show();
            }
        });
        
        checkRootAccessAndPermissions();
        
        return inflate;
    }
    
    private void requestRootAccessAndPermissions() {
        if (isRootAvailable()) {
            requestWriteSecureSettingsPermission();
        } else {
            showFailureToast("Failed to obtain root access");
        }
    }
    
    private void requestWriteSecureSettingsPermission() {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            
            String packageName = requireContext().getPackageName();
            String command = "pm grant " + packageName + " android.permission.WRITE_SECURE_SETTINGS\n";
            
            os.writeBytes(command);
            os.flush();
            os.writeBytes("exit\n");
            os.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            
            process.waitFor();
            
            if (process.exitValue() == 0 && checkWriteSecureSettingsPermission()) {
                showSuccessSnackbar("Root access and WRITE_SECURE_SETTINGS granted!");
                btnContinue.setEnabled(true);
            } else {
                Log.e(TAG, "Permission grant output: " + output);
                showFailureToast("Failed to grant WRITE_SECURE_SETTINGS");
            }
        } catch (IOException | InterruptedException e) {
            Log.e(TAG, "Error granting permission", e);
            showFailureToast("Error granting permission");
        }
    }
    
    private void checkRootAccessAndPermissions() {
        boolean hasRoot = isRootAvailable();
        boolean hasPermission = hasWriteSecureSettingsPermission();
        
        btnContinue.setEnabled(hasRoot && hasPermission);
        
        if (hasRoot && hasPermission) {
            showSuccessSnackbar("Root access and permissions are ready!");
        } else if (!hasRoot) {
            Toast.makeText(requireContext(), "Root access not available", Toast.LENGTH_SHORT).show();
        } else if (!hasPermission) {
            Toast.makeText(requireContext(), "WRITE_SECURE_SETTINGS permission not granted", Toast.LENGTH_SHORT).show();
        }
    }
    
    private boolean isRootAvailable() {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("echo Root check\n");
            os.flush();
            os.writeBytes("exit\n");
            os.flush();
            
            process.waitFor();
            return process.exitValue() == 0;
        } catch (IOException | InterruptedException e) {
            return false;
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
            Log.e(TAG, "FailedChecked", e);
            return false;
        }
    }
    
    private boolean hasWriteSecureSettingsPermission() {
        return ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.WRITE_SECURE_SETTINGS)
            == PackageManager.PERMISSION_GRANTED;
    }
    
    private void showSuccessSnackbar(String message) {
        if (getActivity() != null) {
            Snackbar.make(
                getActivity().findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_SHORT
            ).show();
        }
    }
    
    private void showFailureToast(String message) {
        Toast.makeText(
            requireContext(), 
            message, 
            Toast.LENGTH_SHORT
        ).show();
    }
    
    private void startMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }
}