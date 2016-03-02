package vfediuchko.pins.test.db;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import vfediuchko.pins.test.SimplePins;
import vfediuchko.pins.test.db.model.Pin;


public class PinRepository {
    Realm realm = Realm.getInstance(SimplePins.getAppContext());

    public void getAllUserPins(Context context, String userId, IRealmResultCallback callback) {
        RealmQuery query = realm.where(Pin.class).equalTo("userId", userId);
        RealmResults<Pin> results = query.findAll();
        if (callback != null)
            callback.onSuccess(results);
    }

    public int getNextKey() {
        return realm.where(Pin.class).max("id").intValue() + 1;
    }

    public void removePin(int id, IRealmDeleteItemCallback callback) {
        realm.beginTransaction();

        Pin pin = realm.where(Pin.class).equalTo("id", id).findFirst();
        pin.removeFromRealm();
        realm.commitTransaction();
        if (callback != null)
            callback.onSuccess();
    }
}


