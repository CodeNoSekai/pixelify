package node.ditzdev.pixelify.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import node.ditzdev.pixelify.fragments.ADBShellFragment;
import node.ditzdev.pixelify.fragments.BreventFragment;
import node.ditzdev.pixelify.fragments.ShizukuFragment;
import node.ditzdev.pixelify.fragments.RootFragment;

public class GetStartupPagerAdapter extends FragmentStateAdapter {
    public GetStartupPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new ADBShellFragment();
            case 1: return new BreventFragment();
            case 2: return new ShizukuFragment();
            case 3: return new RootFragment();
            default: return new ADBShellFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}