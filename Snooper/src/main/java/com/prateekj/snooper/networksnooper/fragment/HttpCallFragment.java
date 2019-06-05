package com.prateekj.snooper.networksnooper.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.view.MenuItemCompat;
import androidx.core.widget.NestedScrollView;
import androidx.core.widget.NestedScrollView.OnScrollChangeListener;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prateekj.snooper.R;
import com.prateekj.snooper.formatter.ResponseFormatterFactory;
import com.prateekj.snooper.infra.BackgroundTaskExecutor;
import com.prateekj.snooper.networksnooper.database.SnooperRepo;
import com.prateekj.snooper.networksnooper.model.Bound;
import com.prateekj.snooper.networksnooper.presenter.HttpCallFragmentPresenter;
import com.prateekj.snooper.networksnooper.viewmodel.HttpBodyViewModel;
import com.prateekj.snooper.networksnooper.views.HttpCallBodyView;
import com.prateekj.snooper.utils.Logger;

import java.util.List;

import static android.view.View.GONE;
import static android.widget.TextView.BufferType.SPANNABLE;
import static com.prateekj.snooper.networksnooper.activity.HttpCallActivity.HTTP_CALL_ID;
import static com.prateekj.snooper.networksnooper.activity.HttpCallActivity.HTTP_CALL_MODE;
import static com.prateekj.snooper.utils.CollectionUtilities.isLast;
import static java.lang.Math.min;

public class HttpCallFragment extends Fragment implements HttpCallBodyView, OnQueryTextListener, OnScrollChangeListener {

  public static final String TAG = HttpCallFragment.class.getSimpleName();
  public static final int NEXT_SET_HIGHLIGHT_SCROLL_LINE_BUFFER = 20;
  public static final int BOUNDS_HIGHLIGHT_SET_SIZE = 50;
  private int mode;
  private HttpBodyViewModel viewModel;
  private HttpCallFragmentPresenter presenter;
  private TextView payloadTextView;
  private NestedScrollView scrollView;
  private int lastBoundHighlightedIndex = 0;
  private List<Bound> bounds;
  private int ythPositionOfLastHighlightedBound;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View view = inflater.inflate(R.layout.fragment_response_body, container, false);
    viewModel = new HttpBodyViewModel();
    SnooperRepo repo = new SnooperRepo(getActivity());
    long httpCallId = getArguments().getLong(HTTP_CALL_ID);
    mode = getArguments().getInt(HTTP_CALL_MODE);
    BackgroundTaskExecutor taskExecutor = new BackgroundTaskExecutor(this.getActivity());
    presenter = new HttpCallFragmentPresenter(repo, httpCallId, this, new ResponseFormatterFactory(), taskExecutor);
    setHasOptionsMenu(true);
    return view;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    scrollView = (NestedScrollView) view.findViewById(R.id.scrollView);
    payloadTextView = (TextView) getView().findViewById(R.id.payload_text);
    super.onViewCreated(view, savedInstanceState);
    changeLoaderVisibility(View.VISIBLE);
    presenter.init(viewModel, mode);
    scrollView.setOnScrollChangeListener(this);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    MenuItem searchMenu = menu.findItem(R.id.search_menu);
    searchMenu.setVisible(true);
    ((SearchView) MenuItemCompat.getActionView(searchMenu)).setOnQueryTextListener(this);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public void onFormattingDone() {
    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(viewModel.getFormattedBody());
    payloadTextView.setText(spannableStringBuilder, SPANNABLE);
    changeLoaderVisibility(GONE);
  }

  private void changeLoaderVisibility(int visible) {
    getView().findViewById(R.id.embedded_loader).setVisibility(visible);
  }

  @Override
  public void highlightBounds(List<Bound> bounds) {
    this.bounds = bounds;
    Logger.d(TAG, "Total size: " + bounds.size());
    highlightStringFromBounds(bounds.subList(lastBoundHighlightedIndex, min(BOUNDS_HIGHLIGHT_SET_SIZE, bounds.size())));
    scrollTillYOffset(getYthPositionOfBoundInBody(bounds.get(0)));
  }

  @Override
  public void removeOldHighlightedSpans() {
    Spannable spannableString = (Spannable) payloadTextView.getText();
    BackgroundColorSpan[] backgroundSpans = spannableString.getSpans(0, spannableString.length(), BackgroundColorSpan.class);
    for (BackgroundColorSpan span : backgroundSpans) {
      spannableString.removeSpan(span);
    }
  }

  private void scrollTillYOffset(final int yOffset) {
    scrollView.post(new Runnable() {
      @Override
      public void run() {
        scrollView.scrollTo(0, yOffset);
      }
    });
  }

  @Override
  public boolean onQueryTextSubmit(String query) {
    return false;
  }

  @Override
  public boolean onQueryTextChange(String newText) {
    lastBoundHighlightedIndex = 0;
    presenter.searchInBody(newText.toLowerCase());
    return true;
  }

  @Override
  public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
    if (hasBoundsToHighlight() && needToHighlightNextSetOfBounds(scrollY)) {
      final int calculatedToIndex = lastBoundHighlightedIndex + BOUNDS_HIGHLIGHT_SET_SIZE;
      highlightStringFromBounds(bounds.subList(lastBoundHighlightedIndex + 1, min(calculatedToIndex, bounds.size())));
    }
  }

  private int getYthPositionOfBoundInBody(Bound bound) {
    int lineNumber = getLineNumber(bound.getLeft());
    return payloadTextView.getLayout().getLineTop(lineNumber);
  }

  public int getLineNumber(int indexOfFirstOccurrenceWord) {
    return payloadTextView.getLayout().getLineForOffset(indexOfFirstOccurrenceWord);
  }

  private void highlightStringFromBounds(List<Bound> bounds) {
    final Spannable text = (Spannable) payloadTextView.getText();
    payloadTextView.postDelayed(getHighlightAction(text, bounds), 5);
  }

  @NonNull
  private Runnable getHighlightAction(final Spannable text, final List<Bound> boundsCurrentSet) {
    return new Runnable() {
      @Override
      public void run() {
        for (Bound bound : boundsCurrentSet) {
          text.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.snooper_text_highlight_color)), bound.getLeft(), bound.getRight(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
          if (isLast(boundsCurrentSet, bound)) {
            ythPositionOfLastHighlightedBound = getYthPositionOfBoundInBody(bound);
            lastBoundHighlightedIndex = bounds.indexOf(bound);
          }
        }
      }
    };
  }


  private boolean needToHighlightNextSetOfBounds(int scrollY) {
    return ythPositionOfLastHighlightedBound - scrollY < NEXT_SET_HIGHLIGHT_SCROLL_LINE_BUFFER;
  }

  private boolean hasBoundsToHighlight() {
    return bounds != null && lastBoundHighlightedIndex < (bounds.size() - 1);
  }
}
