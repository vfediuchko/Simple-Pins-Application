package vfediuchko.pins.test.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.Profile;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import vfediuchko.pins.test.PreferenceStorage;
import vfediuchko.pins.test.R;

public class MainActivity extends AppCompatActivity {
    public static final int FRAGMENT_MAP = 1;
    public static final int FRAGMENT_MY_PINS = 2;
    public static final int FRAGMENT_HELP = 3;
    public static final int LOGOUT = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addDrawer(toolbar);
        switchToPins();

    }

    void switchToPins() {
        Fragment fragment = new FragmentMyPins();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_layout, fragment)
                .commit();
    }

    void addDrawer(Toolbar toolbar) {
        Profile profile = Profile.getCurrentProfile();
        View drawer = getLayoutInflater().inflate(R.layout.drawer_header, null);
        if (null != profile) {
            Glide.with(this)
                    .load(profile.getProfilePictureUri(100, 100))
                    .into((ImageView) drawer.findViewById(R.id.userImage));
            ((TextView) drawer.findViewById(R.id.name)).setText(profile.getName());
        }
        new Drawer()T
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(drawer)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_map).withIcon(FontAwesome.Icon.faw_home),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_my_pins).withIcon(FontAwesome.Icon.faw_map_marker),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_help).withIcon(FontAwesome.Icon.faw_android),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_log_out).withIcon(FontAwesome.Icon.faw_lock)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        Fragment fragment = null;
                        switch (position) {
                            case FRAGMENT_MAP:
                                fragment = new FragmentMap();
                                break;
                            case FRAGMENT_MY_PINS:
                                fragment = new FragmentMyPins();
                                break;
                            case FRAGMENT_HELP:
                                fragment = new FragmentHelp();
                                break;
                            case LOGOUT:
                                PreferenceStorage.saveActiveUserId("");

                                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                startActivity(intent);
                                break;
                        }
                        if (null == fragment) return;

                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.content_layout, fragment, fragment.getClass().getSimpleName())
                                .commit();
                    }
                })
                .withSelectedItem(FRAGMENT_MAP)
                .build();
    }


}