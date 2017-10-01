package com.prateekj.snooper.networksnooper.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.SearchView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
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

import java.util.List;

import static android.view.View.GONE;
import static android.widget.TextView.BufferType.SPANNABLE;
import static com.prateekj.snooper.networksnooper.activity.HttpCallActivity.HTTP_CALL_ID;
import static com.prateekj.snooper.networksnooper.activity.HttpCallActivity.HTTP_CALL_MODE;

public class HttpCallFragment extends Fragment implements HttpCallBodyView, SearchView.OnQueryTextListener {

  private int mode;
  private HttpBodyViewModel viewModel;
  private HttpCallFragmentPresenter presenter;
  private TextView payloadTextView;
  private NestedScrollView scrollView;

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
    Log.d("myTag", "Total size: " + bounds.size());
    highlightStringEfficiently(bounds.subList(0, Math.min(50, bounds.size())));
    scrollTillLine(getLineNumber(bounds.get(0).getLeft()));
  }

  @Override
  public void removeOldHighlightedSpans() {
    Spannable spannableString = (Spannable) payloadTextView.getText();
    BackgroundColorSpan[] backgroundSpans = spannableString.getSpans(0, spannableString.length(), BackgroundColorSpan.class);
    for (BackgroundColorSpan span : backgroundSpans) {
      spannableString.removeSpan(span);
    }
  }

  private void scrollTillLine(final int lineNumber) {
    scrollView.post(new Runnable() {
      @Override
      public void run() {
        int y = payloadTextView.getLayout().getLineTop(lineNumber);
        scrollView.scrollTo(0, y);
      }
    });
  }


  public int getLineNumber(int indexOfFirstOccurrenceWord) {
    return payloadTextView.getLayout().getLineForOffset(indexOfFirstOccurrenceWord);
  }

  private void highlightStringEfficiently(List<Bound> bounds) {
    Log.d("myTag", "Highlighting bounds " + bounds.size());
    final Spannable text = (Spannable) payloadTextView.getText();
    payloadTextView.postDelayed(getHighlightAction(text, bounds, 0), 5);
  }

  @NonNull
  private Runnable getHighlightAction(final Spannable text, final List<Bound> bounds, final int index) {
    return new Runnable() {
      @Override
      public void run() {
        Bound bound = bounds.get(index);
        text.setSpan(new BackgroundColorSpan(Color.YELLOW), bound.getLeft(), bound.getRight(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (index + 1 < bounds.size()) {
          payloadTextView.postDelayed(getHighlightAction(text, bounds, index + 1), 2);
        }
      }
    };
  }

  @Override
  public boolean onQueryTextSubmit(String query) {
    return false;
  }

  @Override
  public boolean onQueryTextChange(String newText) {
    presenter.searchInBody(newText.toLowerCase());
    return true;
  }
}
