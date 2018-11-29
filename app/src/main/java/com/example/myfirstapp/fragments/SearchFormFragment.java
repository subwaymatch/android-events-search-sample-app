package com.example.myfirstapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.myfirstapp.R;
import com.example.myfirstapp.activities.EventSearchResultListActivity;
import com.example.myfirstapp.models.SearchQueryParameters;
import com.example.myfirstapp.viewadapters.KeywordAutocompleteAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFormFragment extends Fragment {
	private static final String TAG = "SearchFormFragment";

	private static final int TRIGGER_AUTO_COMPLETE = 100;
	private static final long AUTO_COMPLETE_DELAY = 300;
	private Handler handler;
	private KeywordAutocompleteAdapter keywordAutocompleteAdapter;

	private AutoCompleteTextView autoCompleteTextView;
	private Spinner searchFormCategory;
	private EditText searchFormDistance;
	private Spinner searchFormDistanceMetric;
	private RadioGroup searchFormLocationRadioGroup;
	private RadioButton searchFormUseCurrentLocation;
	private RadioButton searchFormUseOtherLocation;
	private EditText searchFormLocation;
	private Button searchFormSubmitButton;
	private Button searchFormClearButton;

	// Error message TextViews
	TextView searchFormKeywordRequired;
	TextView searchFormLocationRequired;

	public SearchFormFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @return A new instance of fragment SearchFormFragment.
	 */
	public static SearchFormFragment newInstance() {
		SearchFormFragment fragment = new SearchFormFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_search_form, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		autoCompleteTextView = view.findViewById(R.id.searchFormKeyword);
		searchFormCategory = view.findViewById(R.id.searchFormCategory);
		searchFormDistance = view.findViewById(R.id.searchFormDistance);
		searchFormDistanceMetric = view.findViewById(R.id.searchFormDistanceMetric);
		searchFormLocationRadioGroup = view.findViewById(R.id.searchFormLocationRadioGroup);
		searchFormUseCurrentLocation = view.findViewById(R.id.searchFormUseCurrentLocation);
		searchFormUseOtherLocation = view.findViewById(R.id.searchFormUseOtherLocation);
		searchFormLocation = view.findViewById(R.id.searchFormLocation);
		searchFormSubmitButton = view.findViewById(R.id.searchFormSubmitButton);
		searchFormClearButton = view.findViewById(R.id.searchFormClearButton);

		searchFormKeywordRequired = view.findViewById(R.id.searchFormKeywordRequired);
		searchFormLocationRequired = view.findViewById(R.id.searchFormLocationRequired);

		searchFormSubmitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "Submit button clicked", Toast.LENGTH_SHORT).show();
				submitSearchForm();
			}
		});

		searchFormClearButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "Clear button clicked", Toast.LENGTH_SHORT).show();
				clearSearchForm();
			}
		});

		//Setting up the adapter for AutoSuggest
		keywordAutocompleteAdapter = new KeywordAutocompleteAdapter(getActivity(),
				android.R.layout.select_dialog_item);
		autoCompleteTextView.setThreshold(3);
		autoCompleteTextView.setAdapter(keywordAutocompleteAdapter);
		autoCompleteTextView.setOnItemClickListener(
				new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
											int position, long id) {
						autoCompleteTextView.setText(keywordAutocompleteAdapter.getObject(position));
					}
				});

		autoCompleteTextView.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int
					count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
				handler.removeMessages(TRIGGER_AUTO_COMPLETE);
				handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
						AUTO_COMPLETE_DELAY);
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		handler = new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				if (msg.what == TRIGGER_AUTO_COMPLETE) {
					if (!TextUtils.isEmpty(autoCompleteTextView.getText())) {
						// makeApiCall(autoCompleteTextView.getText().toString());
						Log.d(TAG, "handleMessage: making api call for autocomplete with keyword=" + autoCompleteTextView.getText());

						RequestQueue httpRequestQueue = Volley.newRequestQueue(getActivity());

						String baseUrl = "http://ticketmaster-v1.us-west-1.elasticbeanstalk.com/api/v1.0/event/search/autocomplete";

						Uri builtUri = Uri.parse(baseUrl)
								.buildUpon()
								.appendQueryParameter("keyword", autoCompleteTextView.getText().toString())
								.build();

						String urlWithParams = builtUri.toString();

						Log.d("http", "pre-request url=" + urlWithParams);

						JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
								Request.Method.GET, urlWithParams, null, new Response.Listener<JSONArray>() {
							@Override
							public void onResponse(JSONArray response) {
								Log.d("http", "Response: " + response.toString());

								Gson gson = new Gson();
								Type listType = new TypeToken<List<String>>(){}.getType();
								List<String> keywordSuggestions = gson.fromJson(response.toString(), listType);

								Log.d(TAG, "onResponse: retrieved autocomplete entries");

								if (keywordSuggestions.size() > 0) {
									keywordAutocompleteAdapter.setData(keywordSuggestions);
									keywordAutocompleteAdapter.notifyDataSetChanged();
								}
							}

						}, new Response.ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError error) {
								Log.d("http", "onErrorResponse");
								Log.e("http", error.getMessage());
							}
						}
						);

						httpRequestQueue.add(jsonArrayRequest);
					}
				}
				return false;
			}
		});
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	private boolean validateForm() {
		boolean isValid = true;

		if (autoCompleteTextView.getText() == null || autoCompleteTextView.getText().toString().trim().isEmpty()) {
			searchFormKeywordRequired.setVisibility(View.VISIBLE);
			isValid = false;
		}

		if (searchFormUseOtherLocation.isChecked() && (searchFormLocation.getText() == null || searchFormLocation.getText().toString().trim().isEmpty())) {
			searchFormLocationRequired.setVisibility(View.VISIBLE);
			isValid = false;
		}

		return isValid;
	}

	/**
	 * Action when "Search" button is clicked
	 */
	private void submitSearchForm() {
		Log.d(TAG, "onSubmitButtonClicked: ");

		// If form is invalid, do nothing and return
		if (!validateForm()) {
			return;
		}

		SearchQueryParameters p = new SearchQueryParameters();

		p.keyword = autoCompleteTextView.getText().toString();
		p.categoryId = getResources().getStringArray(R.array.search_category_values)[searchFormCategory.getSelectedItemPosition()];
		p.distance = ((searchFormDistance.getText() == null) || searchFormDistance.getText().toString().trim().isEmpty()) ? "10" : searchFormDistance.getText().toString();
		p.distanceMetric = getResources().getStringArray(R.array.search_distance_metrics_values)[searchFormDistanceMetric.getSelectedItemPosition()];
		p.useCurrentLocation = Boolean.toString(searchFormUseCurrentLocation.isChecked());
		p.originLocation = searchFormLocation.getText().toString();
		p.userLat = 34.0266;
		p.userLng = -118.283;

		Log.d(TAG, "submitSearchForm: " + p.toString());

		Intent intent = new Intent(getActivity(), EventSearchResultListActivity.class);
		intent.putExtra("searchQueryParameters", p);
		startActivity(intent);
	}

	/**
	 * Clear search form and validation error messages
	 */
	private void clearSearchForm() {
		autoCompleteTextView.setText(null);
		searchFormCategory.setSelection(0);
		searchFormDistance.setText(null);
		searchFormLocation.setText(null);
		searchFormDistanceMetric.setSelection(0);
		searchFormLocationRadioGroup.check(R.id.searchFormUseCurrentLocation);

		searchFormKeywordRequired.setVisibility(View.GONE);
		searchFormLocationRequired.setVisibility(View.GONE);
	}


}
