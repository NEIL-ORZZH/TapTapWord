package com.example.changfeng.taptapword;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

public class RecentWordFragment extends Fragment {

    private static final String TAG = "RecentWordFragment";

    private static final int REQUEST_WORD = 1;
    private UUID currentWordId;
    private ArrayList<Word> mWords;
    private ArrayList<Word> mRecentWords = new ArrayList<>();
    MaterialListView materialListView;

    @Override
    public void onPause() {
        super.onPause();
        WordLab.get(getActivity()).saveWords();
    }


    @Override
    public void onResume() {
        super.onResume();
        updateListView();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_simple, container, false);
        View view = inflater.inflate(R.layout.material_list_view, container, false);
        materialListView = (MaterialListView) view.findViewById(R.id.material_listview);

        mWords = WordLab.get(getActivity()).getWords();
        updateRecentWords();

        materialListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(CardItemView cardItemView, int i) {
                currentWordId = mRecentWords.get(i).getId();
                Intent intent = new Intent(getActivity(), WordActivity.class);
                intent.putExtra(WordActivity.EXTRA_WORD_NAME, mRecentWords.get(i).getName());
                intent.putExtra(WordActivity.EXTRA_WORD_PHONE, mRecentWords.get(i).getFormatPhones());
                intent.putExtra(WordActivity.EXTRA_WORD_MEANS, mRecentWords.get(i).getMeans());
                intent.putExtra(WordActivity.EXTRA_WORD_ARCHIVED, mRecentWords.get(i).isArchived());
                startActivityForResult(intent, REQUEST_WORD);
            }

            @Override
            public void onItemLongClick(CardItemView cardItemView, int i) {
                cardItemView.setBackgroundColor(Color.BLUE);
            }
        });

        materialListView.setOnDismissCallback(new OnDismissCallback() {
            @Override
            public void onDismiss(Card card, int position) {

                // Recover the tag linked to the Card
                String tag = card.getTag().toString();

                // Show a toast
                Toast.makeText(getActivity().getApplicationContext(), "You have dismissed a " + tag, Toast.LENGTH_SHORT).show();
                getWord(mRecentWords.get(position).getId()).setArchived(true);
                updateRecentWords();

            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "OnActivityResult() called" + " requestCode:" + requestCode + " resultCode:" + resultCode);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_WORD) {
            if (getWord(currentWordId) != null) {
                Word word = getWord(currentWordId);
                word.setName(data.getStringExtra(WordActivity.EXTRA_WORD_NAME));
                word.setArchived(data.getBooleanExtra(WordActivity.EXTRA_WORD_ARCHIVED, false));
                word.setMeans(data.getStringExtra(WordActivity.EXTRA_WORD_MEANS));
                String phone = data.getStringExtra(WordActivity.EXTRA_WORD_PHONE).trim();

                String amPhone = phone.substring(phone.indexOf('[') + 1, phone.indexOf(']'));
                String emPhone = phone.substring(phone.lastIndexOf('[') + 1, phone.lastIndexOf(']'));
                word.setAmPhone(amPhone);
                word.setEnPhone(emPhone);

                updateRecentWords();

                Log.d(TAG, word.getName() + " " + word.getAmPhone() + " " + word.getEnPhone() + " " + word.getMeans());
            }
        }

    }

    private void updateListView() {
        materialListView.clear();

        if (mRecentWords.isEmpty()) {
            SimpleCard card = new SmallImageCard(getActivity());
            card.setTitle(getString(R.string.title_no_recent_words));
            card.setDescription(getString(R.string.description_no_recent_words));
            card.setDismissible(true);
            materialListView.add(card);
        } else {
            for (Word word : mRecentWords) {
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

    private void updateRecentWords() {
        mRecentWords = new ArrayList<>();
        for (Word word : mWords) {
            if (!word.isArchived()) {
                mRecentWords.add(word);
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