package com.code.files.bottomshit;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Space;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.code.files.StripePaymentActivity;
import com.code.files.database.config.ConfigViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.oxoo.spagreen.R;
import com.code.files.database.DatabaseHelper;
import com.code.files.network.model.config.PaymentConfig;

public class PaymentBottomShitDialog extends BottomSheetDialogFragment {

    public static final String PAYPAL = "paypal";
    public static final String STRIP = "strip";
    public static final String RAZOR_PAY = "razorpay";
    public static final String OFFLINE_PAY = "offline_pay";

    private OnBottomShitClickListener bottomShitClickListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_payment_bottom_shit, container,
                false);

        ConfigViewModel configViewModel = new ViewModelProvider(getActivity()).get(ConfigViewModel.class);
        PaymentConfig config = configViewModel.getConfigData().getPaymentConfig();
        CardView paypalBt, stripBt, razorpayBt, offlineBtn;
        paypalBt = view.findViewById(R.id.paypal_btn);
        stripBt = view.findViewById(R.id.stripe_btn);
        razorpayBt = view.findViewById(R.id.razorpay_btn);
        offlineBtn = view.findViewById(R.id.offline_btn);
        Space space = view.findViewById(R.id.space2);
        Space space4 = view.findViewById(R.id.space4);

        if (!config.getPaypalEnable()) {
            paypalBt.setVisibility(View.GONE);
            space.setVisibility(View.GONE);
        }

        if (!config.getStripeEnable()) {
            stripBt.setVisibility(View.GONE);
            space.setVisibility(View.GONE);
        }
        if (!config.getRazorpayEnable()) {
            razorpayBt.setVisibility(View.GONE);
        }
        if (!config.isOfflinePaymentEnable()){
            offlineBtn.setVisibility(View.GONE);
            space4.setVisibility(View.GONE);
        }

        paypalBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomShitClickListener.onBottomShitClick(PAYPAL);
            }
        });

        stripBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomShitClickListener.onBottomShitClick(STRIP);
            }
        });

        razorpayBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomShitClickListener.onBottomShitClick(RAZOR_PAY);
            }
        });

        offlineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomShitClickListener.onBottomShitClick(OFFLINE_PAY);
            }
        });


        return view;

    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            bottomShitClickListener = (OnBottomShitClickListener) context;
        } catch (Exception e) {
            throw new ClassCastException(context.toString() + " must be implemented");
        }

    }

    public interface OnBottomShitClickListener {
        void onBottomShitClick(String paymentMethodName);
    }

}

