package goduoel.com.kakaointern.presentation.listener;

import android.view.View;

public interface OnItemClickListener<T> {
    void onItemClick(View view, T item);
}
