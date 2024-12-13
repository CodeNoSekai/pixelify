package node.ditzdev.pixelify;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import node.ditzdev.pixelify.adapter.GetStartupPagerAdapter;

public class GetStartup extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_get_startup);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        GetStartupPagerAdapter adapter = new GetStartupPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("ADB Shell"); break;
                case 1: tab.setText("Brevent"); break;
                case 2: tab.setText("Shizuku"); break;
                case 3: tab.setText("ROOT"); break;
            }
        }).attach();
    }
}