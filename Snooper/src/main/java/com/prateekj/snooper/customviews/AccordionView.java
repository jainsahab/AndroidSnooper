package com.prateekj.snooper.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prateekj.snooper.R;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.view.LayoutInflater.from;

public class AccordionView extends LinearLayout {
  public static int COLLAPSE = 0;
  public static int EXPAND = 1;

  private View bodyView;
  private int state;
  private int headerText;
  private View headerView;

  public AccordionView(Context context) {
    super(context);
  }

  public AccordionView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initAttributes(attrs);
  }

  public AccordionView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initAttributes(attrs);
  }

  @TargetApi(LOLLIPOP)
  public AccordionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    initAttributes(attrs);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    initViews();
  }

  public void onStateChange() {
    bodyView.setVisibility(state == COLLAPSE ? View.GONE : View.VISIBLE);
    ImageView statusIcon = (ImageView) headerView.findViewById(R.id.state_icon);
    statusIcon.setImageResource(state == COLLAPSE ? R.drawable.arrow_left_white : R.drawable.arrow_down_white);
  }

  private void initAttributes(AttributeSet attributeSet) {
    TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attributeSet, R.styleable.AccordionView, 0, 0);
    state = typedArray.getInteger(R.styleable.AccordionView_state, COLLAPSE);
    headerText = typedArray.getResourceId(R.styleable.AccordionView_headerText, R.string.accordion_header);
    typedArray.recycle();
  }

  private void initViews() {
    setOrientation(VERTICAL);
    headerView = from(getContext()).inflate(R.layout.accordion_view_heading, this, false);
    ((TextView) headerView.findViewById(R.id.header_text)).setText(headerText);
    addView(headerView, 0);
    bodyView = findViewWithTag(getContext().getString(R.string.accordion_body));
    onStateChange();
    headerView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        toggleState();
        onStateChange();
      }
    });
  }

  private void toggleState() {
    state = state == COLLAPSE ? EXPAND : COLLAPSE;
  }

}
