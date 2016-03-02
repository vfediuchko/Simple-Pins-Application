package vfediuchko.pins.test.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import io.realm.Realm;
import io.realm.RealmResults;
import vfediuchko.pins.test.PreferenceStorage;
import vfediuchko.pins.test.R;
import vfediuchko.pins.test.db.IRealmDeleteItemCallback;
import vfediuchko.pins.test.db.IRealmResultCallback;
import vfediuchko.pins.test.db.PinRepository;
import vfediuchko.pins.test.db.model.Pin;


public class FragmentMyPins extends Fragment {
    private PinsAdapter adapter;

    private void switchToMap(Bundle bundle) {
        Fragment fragment = new FragmentMap();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    private IOnPinClickListener iOnPinClickListener = new IOnPinClickListener() {
        @Override
        public void onClick(Pin pin) {
            Bundle bundle = new Bundle();
            bundle.putString(FragmentMap.MARKER_TITLE, pin.getTitle());
            bundle.putDouble(FragmentMap.LATITUDE, pin.getLatitude());
            bundle.putDouble(FragmentMap.LONGITUDE, pin.getLongitude());
            switchToMap(bundle);

        }

        @Override
        public void onLongClick(final Pin data) {

            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle(getString(R.string.remove_title));
            alert.setPositiveButton(getString(R.string.remove), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    PinRepository pinRepository = new PinRepository();
                    pinRepository.removePin(data.getId(), removeCallback);
                }
            });

            alert.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });

            alert.show();
        }
    };
    private IRealmDeleteItemCallback removeCallback = new IRealmDeleteItemCallback() {
        @Override
        public void onSuccess() {
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_list, container, false);
        RecyclerView recycler = (RecyclerView) v.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PinsAdapter(getContext(), iOnPinClickListener);

        recycler.setAdapter(adapter);
        fillData();
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToMap(null);
            }
        });
        return v;
    }

    private void fillData() {
        String userName = PreferenceStorage.getActiveUserId();
        PinRepository pinRepository = new PinRepository();
        pinRepository.getAllUserPins(getActivity(), userName, new IRealmResultCallback() {
            @Override
            public void onSuccess(RealmResults<Pin> realmResults) {
                adapter.setData(realmResults);
            }
        });
    }
}
