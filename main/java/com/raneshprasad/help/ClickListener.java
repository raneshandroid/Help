package com.raneshprasad.help;

/**
 * Created by anubhaprasad on 3/25/17.
 */

import android.view.View;

/**
 * Created by VMac on 06/01/17.
 */

public interface ClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}