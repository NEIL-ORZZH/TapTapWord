package com.example.changfeng.taptapword;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dexafree.materialList.cards.BigImageCard;
import com.dexafree.materialList.cards.WelcomeCard;
import com.dexafree.materialList.view.MaterialListView;

/**
 * Created by changfeng on 2015/4/17.
 */
public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.material_list_view, container, false);
        MaterialListView materialListView = (MaterialListView) view.findViewById(R.id.material_listview);
        WelcomeCard card1 = new WelcomeCard(getActivity());

        card1.setTitle("单词忍者");
        card1.setTitleColor(Color.parseColor(MainActivity.SELECTED_COLOR));
        card1.setDescription("Spy into words for you!\n\nPowered by changfeng\nEmail:changfeng1050@hotmail.com");
        card1.setDescriptionColor(Color.parseColor(MainActivity.WORD_TEXT_COLOR));
        materialListView.add(card1);

        return view;
    }
}
