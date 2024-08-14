package com.example.home;

import android.content.Context;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SOSFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sos, container, false);

        // Find the TextView
        TextView sosText = view.findViewById(R.id.sos_text);

        // Create Alpha Animation for fade in and fade out
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(1000);
        fadeOut.setRepeatMode(Animation.REVERSE);
        fadeOut.setRepeatCount(Animation.INFINITE);

        // Start the animation
        sosText.startAnimation(fadeOut);

        // Start vibration
        vibrate();

        return view;
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            // Vibrate for 500 milliseconds
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                // Deprecated in API 26, but still works for earlier versions
                vibrator.vibrate(500);
            }
        }
    }
}
