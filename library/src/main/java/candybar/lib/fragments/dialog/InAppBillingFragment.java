package candybar.lib.fragments.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.afollestad.materialdialogs.MaterialDialog;
import com.danimahardhika.android.helpers.core.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import candybar.lib.R;
import candybar.lib.adapters.dialog.InAppBillingAdapter;
import candybar.lib.helpers.TypefaceHelper;
import candybar.lib.items.InAppBilling;
import candybar.lib.items.BillingProcessor;
import candybar.lib.preferences.Preferences;
import candybar.lib.utils.InAppBillingClient;
import candybar.lib.utils.listeners.InAppBillingListener;

public class InAppBillingFragment extends DialogFragment {

    private static final String TAG = "candybar.dialog.inapp.billing";
    private static final String TYPE = "type";
    private static final String KEY = "key";
    private static final String PRODUCT_ID = "product_id";
    private static final String PRODUCT_COUNT = "product_count";

    private ListView mInAppList;
    private ProgressBar mProgress;
    private String mKey;
    private int mType;
    private String[] mProductsId;
    private int[] mProductsCount;
    private InAppBillingAdapter mAdapter;
    private List<InAppBilling> mBillings;

    private static InAppBillingFragment newInstance(int type, String key, String[] productId, int[] productCount) {
        InAppBillingFragment fragment = new InAppBillingFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TYPE, type);
        bundle.putString(KEY, key);
        bundle.putStringArray(PRODUCT_ID, productId);
        if (productCount != null)
            bundle.putIntArray(PRODUCT_COUNT, productCount);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static void showInAppBillingDialog(@NonNull FragmentManager fm, int type, String key,
                                            String[] productId, int[] productCount) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag(TAG);
        if (prev != null) {
            ft.remove(prev);
        }

        try {
            DialogFragment dialog = InAppBillingFragment.newInstance(type, key, productId, productCount);
            dialog.show(ft, TAG);
        } catch (IllegalArgumentException | IllegalStateException ignored) {
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getInt(TYPE);
            mKey = getArguments().getString(KEY);
            mProductsId = getArguments().getStringArray(PRODUCT_ID);
            mProductsCount = getArguments().getIntArray(PRODUCT_COUNT);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(requireActivity());
        builder.title(mType == InAppBilling.DONATE ?
                R.string.navigation_view_donate : R.string.premium_request)
                .customView(R.layout.fragment_inapp_dialog, false)
                .typeface(TypefaceHelper.getMedium(requireActivity()), TypefaceHelper.getRegular(requireActivity()))
                .positiveText(mType == InAppBilling.DONATE ? R.string.donate : R.string.premium_request_buy)
                .negativeText(R.string.close)
                .onPositive((dialog, which) -> {
                    try {
                        InAppBillingListener listener = (InAppBillingListener) requireActivity();
                        listener.onInAppBillingSelected(
                                mType, mAdapter.getSelectedProduct());
                    } catch (Exception ignored) {
                    }
                    dismiss();
                })
                .onNegative((dialog, which) ->
                        Preferences.get(requireActivity()).setInAppBillingType(-1))
                .backgroundColorAttr(R.attr.cb_cardBackground)
                .titleColorAttr(R.attr.cb_primaryText)
                .contentColorAttr(R.attr.cb_secondaryText)
                .positiveColorAttr(R.attr.cb_colorAccent)
                .negativeColorAttr(R.attr.cb_secondaryText)
                .dividerColorAttr(R.attr.cb_dividerList);
        MaterialDialog dialog = builder.build();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        setCancelable(false);

        View customView = dialog.getCustomView();
        mInAppList = (ListView) customView.findViewById(R.id.inapp_list);
        mProgress = (ProgressBar) customView.findViewById(R.id.progress);

        if (savedInstanceState != null) {
            mType = savedInstanceState.getInt(TYPE);
            mKey = savedInstanceState.getString(KEY);
            mProductsId = savedInstanceState.getStringArray(PRODUCT_ID);
            mProductsCount = savedInstanceState.getIntArray(PRODUCT_COUNT);
        }

        // Get product details
        mBillings = new ArrayList<>();
        InAppBillingClient.get(requireActivity()).getProcessor().queryProducts(
            java.util.Arrays.asList(mProductsId),
            new BillingProcessor.QueryProductsCallback() {
                @Override
                public void onSuccess(List<String> products) {
                    if (getActivity() == null) return;
                    
                    mProgress.setVisibility(View.GONE);
                    
                    for (int i = 0; i < products.size(); i++) {
                        String productId = products.get(i);
                        InAppBilling billing = mProductsCount != null ?
                            new InAppBilling(productId, mProductsCount[i]) :
                            new InAppBilling(productId);
                        mBillings.add(billing);
                    }
                    
                    mAdapter = new InAppBillingAdapter(getActivity(), mBillings);
                    mInAppList.setAdapter(mAdapter);
                }

                @Override
                public void onError(String error) {
                    if (getActivity() == null) return;
                    
                    mProgress.setVisibility(View.GONE);
                    dismiss();
                    Preferences.get(getActivity()).setInAppBillingType(-1);

                    Toast.makeText(getActivity(), R.string.billing_load_product_failed,
                            Toast.LENGTH_LONG).show();
                    
                    LogUtil.e("Failed to load product details: " + error);
                }
            });

        return dialog;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(TYPE, mType);
        outState.putString(KEY, mKey);
        outState.putStringArray(PRODUCT_ID, mProductsId);
        outState.putIntArray(PRODUCT_COUNT, mProductsCount);
        super.onSaveInstanceState(outState);
    }
}
