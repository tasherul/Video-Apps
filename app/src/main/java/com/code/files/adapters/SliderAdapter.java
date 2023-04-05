package com.code.files.adapters;


import static com.code.files.utils.MyAppClass.getContext;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.github.islamkhsh.CardSliderAdapter;
import com.makeramen.roundedimageview.RoundedImageView;
import com.code.files.DetailsActivity;
import com.code.files.LoginActivity;
import com.oxoo.spagreen.R;
import com.code.files.WebViewActivity;
import com.code.files.models.home_content.Slide;
import com.code.files.utils.PreferenceUtils;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;


public class SliderAdapter extends CardSliderAdapter<Slide> {
    private boolean isMandatoryLogin;
    public SliderAdapter(@NotNull ArrayList<Slide> items, boolean isMandatoryLogin) {
        super(items);
        this.isMandatoryLogin = isMandatoryLogin;
    }

    @Override
    public void bindView(int i, @NotNull View view, @Nullable final Slide slide) {
        if (slide != null){
            TextView textView = view.findViewById(R.id.textView);

            textView.setText(slide.getTitle());
            RoundedImageView imageView = view.findViewById(R.id.imageview);
            Picasso.get().load(slide.getImageLink()).into(imageView);
            View lyt_parent = view.findViewById(R.id.lyt_parent);
            lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (slide.getActionType().equalsIgnoreCase("tvseries") || slide.getActionType().equalsIgnoreCase("movie")){
                        if (isMandatoryLogin){
                            if (PreferenceUtils.isLoggedIn(getContext())){
                                Intent intent=new Intent(getContext(), DetailsActivity.class);
                                intent.putExtra("vType",slide.getActionType());
                                intent.putExtra("id",slide.getActionId());

                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                getContext().startActivity(intent);
                            }else {
                                getContext().startActivity(new Intent(getContext(), LoginActivity.class));
                            }
                        }else {
                            Intent intent=new Intent(getContext(), DetailsActivity.class);
                            intent.putExtra("vType",slide.getActionType());
                            intent.putExtra("id",slide.getActionId());

                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            getContext().startActivity(intent);
                        }

                    }else if (slide.getActionType().equalsIgnoreCase("webview")){
                        Intent intent = new Intent(getContext(), WebViewActivity.class);
                        intent.putExtra("url", slide.getActionUrl());

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getContext().startActivity(intent);

                    }else if (slide.getActionType().equalsIgnoreCase("external_browser")){
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(slide.getActionUrl()));

                        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getContext().startActivity(browserIntent);

                    }else if (slide.getActionType().equalsIgnoreCase("tv")){
                        if (isMandatoryLogin){
                            if (PreferenceUtils.isLoggedIn(getContext())){
                                Intent intent=new Intent(getContext(), DetailsActivity.class);
                                intent.putExtra("vType",slide.getActionType());
                                intent.putExtra("id",slide.getActionId());

                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                getContext().startActivity(intent);
                            }else {
                                getContext().startActivity(new Intent(getContext(), LoginActivity.class));
                            }
                        }else {
                            Intent intent=new Intent(getContext(), DetailsActivity.class);
                            intent.putExtra("vType",slide.getActionType());
                            intent.putExtra("id",slide.getActionId());

                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            getContext().startActivity(intent);
                        }
                    }
                }
            });
        }
    }


    @Override
    public int getItemContentLayout(int i) {
        return R.layout.slider_item;
    }
}
