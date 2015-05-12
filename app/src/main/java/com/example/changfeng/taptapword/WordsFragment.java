package com.example.changfeng.taptapword;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dexafree.materialList.cards.SimpleCard;
import com.dexafree.materialList.cards.SmallImageCard;
import com.dexafree.materialList.controller.OnDismissCallback;
import com.dexafree.materialList.controller.RecyclerItemClickListener;
import com.dexafree.materialList.model.Card;
import com.dexafree.materialList.model.CardItemView;
import com.dexafree.materialList.view.MaterialListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by changfeng on 2015/4/17.
 */
public class WordsFragment extends Fragment {

    private static final String TAG = "WordsFragment";

    private static final int REQUEST_WORD = 2;

    private MaterialListView materialListView;
    private ArrayList<Word> mWords;
    private ArrayList<Word> mArchivedWords;
    private Word currentWord;

    private long currentWordId;

    private boolean isActionMode = false;
    private ArrayList<Integer> selectedItemPositions = new ArrayList<>();





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "OnCreateView() called");
        View view = inflater.inflate(R.layout.fragment_words, container, false);
        materialListView = (MaterialListView) view.findViewById(R.id.material_listview);

        mArchivedWords = WordManger.get(getActivity()).getArchivedWords();

        updateListView();


        materialListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(CardItemView cardItemView, int i) {
                if (mArchivedWords.isEmpty()) {
                    Toast.makeText(getActivity(), getString(R.string.title_no_archived_words), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isActionMode) {
                    if (isSelected(i)) {
                        selectedItemPositions.remove(i);
                        cardItemView.setBackgroundColor(Color.WHITE);
                    } else {
                        selectedItemPositions.add(i);
                        cardItemView.setBackgroundColor(Color.parseColor(MainActivity.SELECTED_COLOR));
                    }
                } else {
                    currentWordId = mArchivedWords.get(i).getId();

                    currentWord = mArchivedWords.get(i);
                    Intent intent = new Intent(getActivity(), WordActivity.class);
                    intent.putExtra(WordActivity.EXTRA_WORD_NAME, currentWord.getName());
                    intent.putExtra(WordActivity.EXTRA_WORD_PHONE, currentWord.getFormatPhones());
                    intent.putExtra(WordActivity.EXTRA_WORD_MEANS, currentWord.getMeans());
                    intent.putExtra(WordActivity.EXTRA_WORD_ARCHIVED, currentWord.isArchived());
                    startActivityForResult(intent, REQUEST_WORD);
                }
            }

            @Override
            public void onItemLongClick(CardItemView cardItemView, int i) {
                if (!isActionMode) {
                    isActionMode = true;
                    getActivity().startActionMode(mActionModeCallback);
                    cardItemView.setBackgroundColor(Color.parseColor(MainActivity.SELECTED_COLOR));
                    selectedItemPositions.add(i);
                    if (!mArchivedWords.isEmpty()) {
                        selectedItemPositions.add(i);
                    }
                }
            }
        });

        materialListView.setOnDismissCallback(new OnDismissCallback() {
            @Override
            public void onDismiss(Card card, int position) {

                if (!mArchivedWords.isEmpty()) {
                    WordManger.get(getActivity()).deleteWord(mArchivedWords.get(position));
                    mArchivedWords.remove(position);
                    showToast(getString(R.string.message_delete_success), Toast.LENGTH_SHORT);
                }
                updateListView();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mArchivedWords = WordManger.get(getActivity()).getArchivedWords();
        updateListView();
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            isActionMode = true;
            selectedItemPositions.clear();
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_words_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    deleteWords(selectedItemPositions);
                    showToast(getString(R.string.message_delete_success), Toast.LENGTH_SHORT);
                    mode.finish();
                    break;
                case R.id.action_unarchive:
                    unArchiveWords(selectedItemPositions);
                    showToast(getString(R.string.message_unarchive_success), Toast.LENGTH_SHORT);
                    mode.finish();
                    break;
                case R.id.action_select_all:
                    selectAll();
                    checkAll();
                default:
                    return false;
            }
            return  true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
//            Log.d(TAG, "OnDestroyActionMode() called");
            updateArchivedWords();
            updateListView();
            isActionMode = false;
            selectedItemPositions.clear();
        }
    };


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.d(TAG, "OnActivityResult() called" + " requestCode:" + requestCode + " resultCode:" + resultCode);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_WORD) {
            currentWord.setName(data.getStringExtra(WordActivity.EXTRA_WORD_NAME));
            currentWord.setArchived(data.getBooleanExtra(WordActivity.EXTRA_WORD_ARCHIVED, false));
            currentWord.setMeans(data.getStringExtra(WordActivity.EXTRA_WORD_MEANS));
            String phone = data.getStringExtra(WordActivity.EXTRA_WORD_PHONE).trim();

            String amPhone = phone.substring(phone.indexOf('[') + 1, phone.indexOf(']'));
            String emPhone = phone.substring(phone.lastIndexOf('[') + 1, phone.lastIndexOf(']'));
            currentWord.setAmPhone(amPhone);
            currentWord.setEnPhone(emPhone);

            WordManger.get(getActivity()).updateWord(currentWord);
            if (!currentWord.isArchived()) {
                mArchivedWords.remove(currentWord);
            }

            showToast(getString(R.string.message_edit_success), Toast.LENGTH_SHORT);
        }

    }

    private void updateListView() {
        materialListView.removeAllViews();
        materialListView.clear();

        if (mArchivedWords.isEmpty()) {
            SimpleCard card = new SmallImageCard(getActivity());
            card.setTitle(getString(R.string.title_no_archived_words));
            card.setDescription(getString(R.string.description_no_archived_words));
            card.setTag("SIMPLE_CARD");
            card.setDismissible(true);
            card.setBackgroundColor(Color.WHITE);
            card.setTitleColor(Color.parseColor(MainActivity.SELECTED_COLOR));
            card.setDescriptionColor(Color.parseColor(MainActivity.WORD_TEXT_COLOR));
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
                card.setBackgroundColor(Color.WHITE);
                card.setTitleColor(Color.parseColor(MainActivity.SELECTED_COLOR));
                card.setDescriptionColor(Color.parseColor(MainActivity.WORD_TEXT_COLOR));
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
            if (word.getUUID().equals(uuid)) {
                return word;
            }
        }
        return null;
    }

    private void unArchiveWord(int position) {
        if (!mArchivedWords.isEmpty()) {
            getWord(mArchivedWords.get(position).getUUID()).setArchived(false);
        }
    }

    private void unArchiveWords(ArrayList<Integer> positions) {
        if (positions.isEmpty()) {
            return;
        }
        for (Integer position : positions) {
            unArchiveWord(position);
        }
    }

    private void deleteWord(int position) {
        if (!mWords.isEmpty()) {
            mWords.remove(mArchivedWords.get(position));
        }
    }

    private void deleteWords(ArrayList<Integer> positions) {
        if (positions.isEmpty()) {
            return;
        }

        for (Integer position : positions) {
            deleteWord(position);
        }
    }


    private void selectAll() {
        selectedItemPositions.clear();
        for (int i = 0; i < mArchivedWords.size(); i++) {
            selectedItemPositions.add(i);
        }
    }

    private void checkAll() {
        materialListView.setBackgroundColor(Color.parseColor(MainActivity.SELECTED_COLOR));

    }

    private boolean isSelected(int position) {
        for (int pos : selectedItemPositions) {
            if (pos == position) {
                return true;
            }
        }
        return false;
    }

    private void showToast(String message, int duration) {
        Toast.makeText(getActivity(), message, duration).show();
    }
}
