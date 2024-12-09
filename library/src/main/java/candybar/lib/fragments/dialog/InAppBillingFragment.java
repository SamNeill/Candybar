package candybar.lib.fragments.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.afollestad.materialdialogs.MaterialDialog;
import com.danimahardhika.android.helpers.core.utils.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import candybar.lib.R;
import candybar.lib.adapters.dialog.InAppBillingAdapter;
import candybar.lib.helpers.TypefaceHelper;
import candybar.lib.items.InAppBilling;
import candybar.lib.preferences.Preferences;
import candybar.lib.utils.InAppBillingClient;
import candybar.lib.utils.BillingProcessor;

public class InAppBillingFragment extends DialogFragment {

    private static final String TAG = "candybar.dialog.inapp.billing";
    private static final String TYPE = "type";
    private static final String KEY = "key";
    private static final String PRODUCT_ID = "product_id";
    private static final String PRODUCT_COUNT = "product_count";

    private ListView mListView;
    private ProgressBar mProgress;
    private String mKey;
    private int mType;
    private String[] mProductsId;
    private int[] mProductsCount;
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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(requireActivity());
        builder.customView(R.layout.fragment_inapp_dialog, false);
        builder.typeface(TypefaceHelper.getMedium(requireActivity()), TypefaceHelper.getRegular(requireActivity()));
        builder.title(mType == InAppBilling.DONATE ? R.string.navigation_view_donate :
                R.string.premium_request);
        MaterialDialog dialog = builder.build();
        dialog.show();

        mListView = dialog.getCustomView().findViewById(R.id.inapp_list);
        mProgress = dialog.getCustomView().findViewById(R.id.progress);

        // Get product details
        mBillings = new ArrayList<>();
        InAppBillingClient.get(requireActivity()).getProcessor().queryProducts(
            Arrays.asList(mProductsId),
            new BillingProcessor.QueryProductsCallback() {
                @Override
                public void onSuccess(List<String> products) {
                    if (getActivity() == null) return;
                    
                    mProgress.setVisibility(View.GONE);
                    
                    for (int i = 0; i < products.size(); i++) {
                        String productId = products.get(i);
                        InAppBilling billing;
                        if (mType == InAppBilling.DONATE) {
                            billing = new InAppBilling(productId);
                        } else {
                            billing = new InAppBilling(productId, mProductsCount[i]);
                        }
                        mBillings.add(billing);
                    }
                    
                    mListView.setAdapter(new InAppBillingAdapter(getActivity(), mBillings));
                }

                @Override
                public void onError(String error) {
                    if (getActivity() == null) return;
                    dismiss();
                    LogUtil.e("Failed to load product details: " + error);
                }
            }
        );

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args == null) throw new IllegalArgumentException("No arguments found");
        
        mType = args.getInt(TYPE);
        mKey = args.getString(KEY);
        mProductsId = args.getStringArray(PRODUCT_ID);
        if (args.containsKey(PRODUCT_COUNT)) {
            mProductsCount = args.getIntArray(PRODUCT_COUNT);
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if (Preferences.get(requireActivity()).getInAppBillingType() == mType) {
            Preferences.get(requireActivity()).setInAppBillingType(-1);
        }
        super.onDismiss(dialog);
    }
}
