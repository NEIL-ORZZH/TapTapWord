package com.example.changfeng.taptapword;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dexafree.materialList.cards.BigImageCard;
import com.dexafree.materialList.cards.WelcomeCard;
import com.dexafree.materialList.view.MaterialListView;

/**
 * Created by changfeng on 2015/5/4.
 */
public class HelpFragment extends Fragment {

    private static final String TAG = "HelpFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.material_list_view, container, false);
        MaterialListView materialListView = (MaterialListView) view.findViewById(R.id.material_listview);
        WelcomeCard welcomeCard = new WelcomeCard(getActivity());
        welcomeCard.setTitle(getString(R.string.title_welcome));
        welcomeCard.setTitleColor(Color.parseColor(MainActivity.SELECTED_COLOR));
        welcomeCard.setDescription(R.string.description_welcome);
        materialListView.add(welcomeCard);


        BigImageCard tutorOne = new BigImageCard(getActivity());
        tutorOne.setTitle(getString(R.string.title_tutorOne));
        tutorOne.setTitleColor(Color.parseColor(MainActivity.SELECTED_COLOR));
        tutorOne.setDrawable(R.drawable.open_reading_app);
        materialListView.add(tutorOne);

        BigImageCard tutorTwo = new BigImageCard(getActivity());
        tutorTwo.setTitle(getString(R.string.title_tutorTwo));
        tutorTwo.setTitleColor(Color.parseColor(MainActivity.SELECTED_COLOR));
        tutorTwo.setDrawable(R.drawable.select_word);
        materialListView.add(tutorTwo);

        BigImageCard tutorThree = new BigImageCard(getActivity());
        tutorThree.setTitle(getString(R.string.title_tutorThree));
        tutorThree.setTitleColor(Color.parseColor(MainActivity.SELECTED_COLOR));
        tutorThree.setDrawable(R.drawable.copy_word);
        materialListView.add(tutorThree);

        BigImageCard tutorFour = new BigImageCard(getActivity());
        tutorFour.setTitle(getString(R.string.title_tutorFour));
        tutorFour.setTitleColor(Color.parseColor(MainActivity.SELECTED_COLOR));
        tutorFour.setDrawable(R.drawable.result_word);
        materialListView.add(tutorFour);

        BigImageCard tutorFive = new BigImageCard(getActivity());
        tutorFive.setTitle(getString(R.string.title_tutorFive));
        tutorFive.setTitleColor(Color.parseColor(MainActivity.SELECTED_COLOR));
        tutorFive.setDrawable(R.drawable.refer_word);
        materialListView.add(tutorFive);

        return view;
    }
}
