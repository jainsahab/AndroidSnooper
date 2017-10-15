package com.prateekj.snooper;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.prateekj.snooper.dbreader.activity.DatabaseListActivity;
import com.prateekj.snooper.networksnooper.activity.HttpCallListActivity;

public class SnooperFlowSelectionDialog extends DialogFragment {
  public static final String DEFAULT_FRAGMENT_TAG = SnooperFlowSelectionDialog.class.getSimpleName();

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.snooper_flow_selection_dialog, container, false);
    setClickHandlers(view);
    return view;
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Dialog dialog = super.onCreateDialog(savedInstanceState);
    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    return dialog;
  }

  private void setClickHandlers(View view) {
    view.findViewById(R.id.network_history_selection).setOnClickListener(navigateTo(HttpCallListActivity.class));
    view.findViewById(R.id.database_reader_selection).setOnClickListener(navigateTo(DatabaseListActivity.class));
    view.findViewById(R.id.close_icon).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
      }
    });
  }

  private View.OnClickListener navigateTo(final Class<? extends Activity> activityClass) {
    return new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getActivity(), activityClass);
        getActivity().startActivity(intent);
        dismiss();
      }
    };
  }
}
