package vfediuchko.pins.test.ui;

import vfediuchko.pins.test.db.model.Pin;


public interface IOnPinClickListener {
    void onClick(Pin data);
    void onLongClick(Pin data);

}
