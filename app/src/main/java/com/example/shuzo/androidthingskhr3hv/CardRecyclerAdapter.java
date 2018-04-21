package com.example.shuzo.androidthingskhr3hv;

// TODO : kotlinで書き直す

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;


class CardRecyclerAdapter extends RecyclerView.Adapter<CardRecyclerAdapter.VH> {
    private final String TAG = CardRecyclerAdapter.class.getSimpleName();
    private Context context;
    private SupportSerialServo supportSerialServo;

    public CardRecyclerAdapter(Context context, SupportSerialServo supportSerialServo) {
        this.context = context;
        this.supportSerialServo = supportSerialServo;
    }


    @Override
    public int getItemCount() {
        return 17;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView portNum;
        SeekBar rotateSeekBar;

        public VH(View v) {
            super(v);
            portNum = (TextView) v.findViewById(R.id.port_number);
            rotateSeekBar = (SeekBar) v.findViewById(R.id.pwm_seekbar);

            // 最初はこれらのViewは表示しない
            rotateSeekBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.layout_card, parent, false);
        VH holder = new VH(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final VH holder, final int position) {
        holder.portNum.setText(String.valueOf(position + 1));
        holder.rotateSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(final SeekBar seekBar, int progress, boolean fromUser) {
                supportSerialServo.toRotate((byte) position, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
