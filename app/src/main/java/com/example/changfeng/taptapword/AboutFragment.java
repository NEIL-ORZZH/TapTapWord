package com.example.changfeng.taptapword;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dexafree.materialList.cards.BigImageCard;
import com.dexafree.materialList.view.MaterialListView;

/**
 * Created by changfeng on 2015/4/17.
 */
public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.material_list_view, container, false);
        MaterialListView materialListView = (MaterialListView) view.findViewById(R.id.material_listview);
        BigImageCard card = new BigImageCard(getActivity());
        card.setDescription("Powered by changfeng\nchangfeng1050@hotmail.com");
        card.setTitle("TapTapWord");
        card.setDrawable(R.drawable.git_hub);

        materialListView.add(card);
        return view;
    }
}
