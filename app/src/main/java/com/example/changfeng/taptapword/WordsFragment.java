package com.example.changfeng.taptapword;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import java.util.UUID;

/**
 * Created by changfeng on 2015/4/17.
 */
public class WordsFragment extends Fragment {

    private static final String TAG = "WordsFragment";

    private MaterialListView materialListView;
    private ArrayList<Word> mWords;
    private ArrayList<Word> mArchivedWords;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "OnCreateView() called");
        View view = inflater.inflate(R.layout.material_list_view, container, false);
        materialListView = (MaterialListView) view.findViewById(R.id.material_listview);

        mWords = WordLab.get(getActivity()).getWords();
        updateArchivedWords();

        updateListView();

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
                mWords.remove(mArchivedWords.get(position));
            }
        });
        return view;
    }


    private void updateListView() {
        materialListView.clear();
        if (mWords.isEmpty()) {
            SimpleCard card = new SmallImageCard(getActivity());
            card.setTitle(getString(R.string.title_no_recent_words));
            card.setDescription(getString(R.string.description_no_recent_words));
            card.setDismissible(true);
            materialListView.add(card);
        } else {
            for (Word word : mArchivedWords) {
                SimpleCard card = new SmallImageCard(getActivity());
                card.setTitle(word.getName());
                StringBuilder description = new StringBuilder();
                if (!word.getAmPhone().isEmpty()) {
                    description.append(word.getFormatAmPhone());
                }
                if (!word.getEnPhone().isEmpty()) {
                    description.append(word.getFormatEnPhone());
                }
                description.append("\n\n");
                if (!word.getMeans().isEmpty()) {
                    description.append(word.getMeans());
                }

                card.setDescription(description.toString());

                card.setTag("SIMPLE_CARD");
                card.setDismissible(true);
                materialListView.add(card);
            }
        }
    }

    private void updateArchivedWords() {
        mArchivedWords = new ArrayList<>();
        for (Word word : mWords) {
            if (word.isArchived()) {
                mArchivedWords.add(word);
            }
        }
    }

    private Word getWord(UUID uuid) {
        for (Word word : mWords) {
            if (word.getId().equals(uuid)) {
                return word;
            }
        }
        return null;
    }


}
