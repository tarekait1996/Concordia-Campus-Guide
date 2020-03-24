package com.example.concordia_campus_guide.Fragments.POIFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.concordia_campus_guide.Adapters.PoiAdapter;
import com.example.concordia_campus_guide.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.widget.ViewPager2;

public class POIFragment extends Fragment {

    private ViewPager2 poiVP;
    private POIFragmentViewModel mViewModel;

    public static POIFragment newInstance() {
        return new POIFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.p_o_i_fragment, container, false);

        initComponents(rootView);

        return rootView;
    }

    private void initComponents(View rootView) {
        poiVP = rootView.findViewById(R.id.poiVp);
        setupViewPager();
    }

    private void setupViewPager() {
        List<String> services = new ArrayList<>();
        services.add("class");
        services.add("libraries");
        services.add("lounges");
        services.add("bathroom");
        services.add("coffee shop");
        services.add("class");
        services.add("carotte");
        services.add("concombre");
        services.add("tomate");
        services.add("coffee shop");
        services.add("tomate");
        services.add("coffee shop");
        PoiAdapter poiViewPagerAdapter = new PoiAdapter(getContext(), services);
        poiVP.setAdapter(poiViewPagerAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(POIFragmentViewModel.class);
        // TODO: Use the ViewModel
    }

}