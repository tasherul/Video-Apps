package com.code.files.adapters;

import android.app.Activity;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.code.files.DetailsActivity;
import com.code.files.LoginActivity;
import com.code.files.YoutubeActivity;
import com.code.files.database.config.ConfigViewModel;
import com.code.files.utils.SubscriptionDialog;
import com.oxoo.spagreen.R;
import com.code.files.SubscriptionActivity;
import com.code.files.models.CommonModels;
import com.code.files.utils.ItemAnimation;
import com.code.files.utils.PreferenceUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class LiveTvHomeAdapter extends RecyclerView.Adapter<LiveTvHomeAdapter.OriginalViewHolder> implements SubscriptionDialog.OnAdLoadingCallback {
    private List<CommonModels> items = new ArrayList<>();
    private Activity ctx;
    private String fromActivity;
    private int lastPosition = -1;
    private boolean on_attach = true;
    private int animation_type = 2;
    private boolean isMandatoryLogin = false;
    private ConfigViewModel configViewModel;
    private CommonModels selectedObj;
    private SubscriptionDialog subscriptionDialog;

    public LiveTvHomeAdapter(Activity context, ConfigViewModel configViewModel, List<CommonModels> items, String fromActivity, boolean isMandatoryLogin) {
        this.items = items;
        ctx = context;
        this.fromActivity = fromActivity;
        this.isMandatoryLogin = isMandatoryLogin;
        this.configViewModel = configViewModel;
        subscriptionDialog = new SubscriptionDialog(context, configViewModel);
        subscriptionDialog.setOnAdLoadingCallback(this);

    }


    @Override
    public LiveTvHomeAdapter.OriginalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LiveTvHomeAdapter.OriginalViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_live_tv_home, parent, false);
        vh = new LiveTvHomeAdapter.OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(LiveTvHomeAdapter.OriginalViewHolder holder, final int position) {

        final CommonModels obj = items.get(position);
        selectedObj = obj;

        holder.name.setText(obj.getTitle());
        Picasso.get().load(obj.getImageUrl()).into(holder.image);

        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check app config data
                if (obj.isPaid.equalsIgnoreCase("1")) {
                    if (!PreferenceUtils.isActivePlan(ctx)) {
                        subscriptionDialog.showDialog();
                    }
                }else {
                    playContent(obj,false);
                }
            }
        });

        setAnimation(holder.itemView, position);

    }

    private void playContent(CommonModels obj, boolean fromAds) {

        Log.e("----------", "play content: " + obj.getStremURL() );
        Intent intent;
        if (obj.getServerType().equalsIgnoreCase("youtube_live") || obj.getServerType().equalsIgnoreCase("youtube")){
            intent = new Intent(ctx, YoutubeActivity.class);
            intent.putExtra("url", obj.getStremURL());
        }else {
            intent = new Intent(ctx, DetailsActivity.class);
            intent.putExtra("vType", obj.getVideoType());
            intent.putExtra("id", obj.getId());
            intent.putExtra("fromAds", fromAds);
            if (fromActivity.equals(DetailsActivity.TAG)) {
                boolean castSession = ((DetailsActivity) ctx).getCastSession();
                //Toast.makeText(ctx, "castSession in"+castSession, Toast.LENGTH_SHORT).show();
                intent.putExtra("castSession", castSession);
            }

        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ctx.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onAdShowedSuccessfully() {
        playContent(selectedObj, true);
    }

    @Override
    public void onFailedToShow() {

    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView name;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.image);
            name = v.findViewById(R.id.name);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                on_attach = false;
                super.onScrollStateChanged(recyclerView, newState);
            }

        });

        super.onAttachedToRecyclerView(recyclerView);
    }

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, animation_type);
            lastPosition = position;
        }
    }

}