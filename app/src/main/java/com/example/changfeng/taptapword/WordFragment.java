package com.example.changfeng.taptapword;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dexafree.materialList.cards.SmallImageCard;
import com.dexafree.materialList.view.MaterialListView;

/**
 * Created by changfeng on 2015/4/17.
 */
public class WordFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.material_list_view, container, false);
        MaterialListView materialListView = (MaterialListView) view.findViewById(R.id.material_listview);
        SmallImageCard card = new SmallImageCard(getActivity());
        card.setDescription("Hello word");
        card.setTitle("word");
        card.setDrawable(R.drawable.ic_launcher);

        materialListView.add(card);
        materialListView.add(card);
        materialListView.add(card);
        materialListView.add(card);
        materialListView.add(card);
        materialListView.add(card);
        materialListView.add(card);
        materialListView.add(card);
        materialListView.add(card);
        materialListView.add(card);
        materialListView.add(card);
        materialListView.add(card);
        materialListView.add(card);
        materialListView.add(card);
        materialListView.add(card);


        return view;
    }
}
