package vfediuchko.pins.test.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import io.realm.RealmResults;
import vfediuchko.pins.test.R;
import vfediuchko.pins.test.db.model.Pin;


public class PinHolder extends RecyclerView.ViewHolder {
    private Context context;
    private TextView title;
    private View itemView;

    public PinHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
        this.itemView = itemView;
        title = (TextView) itemView.findViewById(R.id.title);

    }

    public void setData(final Pin data, final IOnPinClickListener onPinClickListener) {
        fillItem(data);
        setListener(data, onPinClickListener);
    }

    private void fillItem(Pin pin) {
        title.setText(pin.getTitle());
    }

    private void setListener(final Pin pin, final IOnPinClickListener onPinClickListener) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPinClickListener.onClick(pin);
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onPinClickListener.onLongClick(pin);
                return true;
            }
        });

    }

}
