package vfediuchko.pins.test.db;

import io.realm.RealmResults;
import vfediuchko.pins.test.db.model.Pin;


public interface IRealmResultCallback {
    void onSuccess(RealmResults<Pin> realmResults);
}
