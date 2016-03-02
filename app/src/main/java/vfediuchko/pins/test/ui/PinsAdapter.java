package vfediuchko.pins.test.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import io.realm.RealmResults;
import vfediuchko.pins.test.R;
import vfediuchko.pins.test.db.model.Pin;

public class PinsAdapter extends RecyclerView.Adapter<PinHolder> {
    private Context context;
    private IOnPinClickListener onPinClickListener;

    public PinsAdapter(Context context, IOnPinClickListener onPinClickListener) {
        this.context = context;
        this.onPinClickListener = onPinClickListener;
    }

    @Override
    public PinHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PinHolder(context, LayoutInflater.from(context).inflate(R.layout.item_pin, parent, false));
    }

    @Override
    public void onBindViewHolder(PinHolder holder, int position) {
        holder.setData(data.get(position), onPinClickListener);

    }

    @Override
    public int getItemCount() {
        if (data != null)
            return data.size();
        return 0;
    }

    private RealmResults<Pin> data;

    public void setData(RealmResults<Pin> data) {
        this.data = data;
        notifyDataSetChanged();
    }

}
