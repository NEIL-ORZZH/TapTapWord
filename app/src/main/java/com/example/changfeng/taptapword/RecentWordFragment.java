package com.example.changfeng.taptapword;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dexafree.materialList.cards.SimpleCard;
import com.dexafree.materialList.cards.SmallImageCard;
import com.dexafree.materialList.controller.OnDismissCallback;
import com.dexafree.materialList.controller.RecyclerItemClickListener;
import com.dexafree.materialList.model.Card;
import com.dexafree.materialList.model.CardItemView;
import com.dexafree.materialList.view.MaterialListView;

import java.util.ArrayList;

public class RecentWordFragment extends Fragment {

    private ArrayList<Word> mWords;
    MaterialListView materialListView;

    @Override
    public void onPause() {
        super.onPause();
        WordLab.get(getActivity()).saveWords();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_simple, container, false);
        View view = inflater.inflate(R.layout.material_list_view, container, false);
        materialListView = (MaterialListView) view.findViewById(R.id.material_listview);

        mWords = WordLab.get(getActivity()).getWords();

        for (Word word : mWords) {

            SimpleCard card = new SmallImageCard(getActivity());
            card.setTitle(word.getName());
            StringBuilder description = new StringBuilder();
            if (word.hasAmPhone()) {
                description.append("美:[");
                description.append(word.getAmPhone());
                description.append("]");
            }
            if (word.hasEnPhone()) {
                description.append("英:[");
                description.append(word.getEnPhone());
                description.append("]");
            }
            description.append("\n\n");
            if (word.hasMeans()) {
                description.append(word.getMeans());
            }

            card.setDescription(description.toString());

            card.setTag("SIMPLE_CARD");
            card.setDismissible(true);
            materialListView.add(card);
        }

        materialListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(CardItemView cardItemView, int i) {

            }

            @Override
            public void onItemLongClick(CardItemView cardItemView, int i) {

            }
        });

        materialListView.setOnDismissCallback(new OnDismissCallback() {
            @Override
            public void onDismiss(Card card, int position) {

                // Recover the tag linked to the Card
                String tag = card.getTag().toString();

                // Show a toast
                Toast.makeText(getActivity().getApplicationContext(), "You have dismissed a " + tag, Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

}