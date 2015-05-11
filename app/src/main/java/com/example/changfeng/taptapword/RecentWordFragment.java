package com.example.changfeng.taptapword;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private boolean isActionMode = false;
    private ArrayList<Integer> selectedItemPositions;

    MaterialListView materialListView;

    @Override
    public void onPause() {
        super.onPause();
        WordLab.get(getActivity()).saveWords();
//        Log.d(TAG, getActivity().getFileStreamPath(WordLab.FILENAME).getAbsolutePath());
    }


    @Override
    public void onResume() {
        super.onResume();
        updateListView(Color.WHITE);
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
                if (mRecentWords.isEmpty()) {
                    Toast.makeText(getActivity(), getString(R.string.title_no_recent_words), Toast.LENGTH_SHORT).show();
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
                    currentWordId = mRecentWords.get(i).getUUID();
                    Intent intent = new Intent(getActivity(), WordActivity.class);
                    intent.putExtra(WordActivity.EXTRA_WORD_NAME, mRecentWords.get(i).getName());
                    intent.putExtra(WordActivity.EXTRA_WORD_PHONE, mRecentWords.get(i).getFormatPhones());
                    intent.putExtra(WordActivity.EXTRA_WORD_MEANS, mRecentWords.get(i).getMeans());
                    intent.putExtra(WordActivity.EXTRA_WORD_ARCHIVED, mRecentWords.get(i).isArchived());
                    startActivityForResult(intent, REQUEST_WORD);
                }

            }

            @Override
            public void onItemLongClick(CardItemView cardItemView, int i) {
                if (!isActionMode) {
                    isActionMode = true;
                    getActivity().startActionMode(mActionModeCallback);
                    cardItemView.setBackgroundColor(Color.parseColor(MainActivity.SELECTED_COLOR));
                    if (!mRecentWords.isEmpty()) {
                        selectedItemPositions.add(i);
                    }
                }
            }
        });

        materialListView.setOnDismissCallback(new OnDismissCallback() {
            @Override
            public void onDismiss(Card card, int position) {
                if (!mRecentWords.isEmpty()) {
                    showToast(getString(R.string.message_archive_success), Toast.LENGTH_SHORT);
                }
                archiveWord(position);
                updateRecentWords();
                updateListView(Color.WHITE);

            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.d(TAG, "OnActivityResult() called" + " requestCode:" + requestCode + " resultCode:" + resultCode);
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

                showToast(getString(R.string.message_edit_success), Toast.LENGTH_SHORT);
//                Log.d(TAG, word.getName() + " " + word.getAmPhone() + " " + word.getEnPhone() + " " + word.getMeans());
            }
        }

    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            isActionMode = true;
            selectedItemPositions = new ArrayList<>();
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_recent_words_menu, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    deleteWords(selectedItemPositions);
                    showToast(getString(R.string.message_delete_success), Toast.LENGTH_SHORT);
//                    Log.d(TAG, "delete words:" + selectedItemPositions);
                    mode.finish();
                    break;
                case R.id.action_archive:
                    archiveWords(selectedItemPositions);
                    showToast(getString(R.string.message_archive_success), Toast.LENGTH_SHORT);
//                    Log.d(TAG, "archived words Positions:" + selectedItemPositions);
                    mode.finish();
                    break;
                case R.id.action_select_all:
                    selectAll();
                    checkAll();
                    break;
                default:
                    return false;
            }
            return  true;
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
//            Log.d(TAG, "onDestroyActionMode() called");
            updateRecentWords();
            updateListView(Color.WHITE);
            isActionMode = false;
            selectedItemPositions = null;

        }
    };

    private void updateListView(int color) {
        materialListView.removeAllViews();
        materialListView.clear();

        if (mRecentWords.isEmpty()) {
            SimpleCard card = new SmallImageCard(getActivity());
            card.setTitle(getString(R.string.title_no_recent_words));
            card.setDescription(getString(R.string.description_no_recent_words));
            card.setTag("SIMPLE_CARD");
            card.setDismissible(true);
            card.setBackgroundColor(color);
            card.setTitleColor(Color.parseColor(MainActivity.SELECTED_COLOR));
            card.setDescriptionColor(Color.parseColor(MainActivity.WORD_TEXT_COLOR));
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
                card.setBackgroundColor(color);
                card.setTitleColor(Color.parseColor(MainActivity.SELECTED_COLOR));
                card.setDescriptionColor(Color.parseColor(MainActivity.WORD_TEXT_COLOR));
                materialListView.add(card);
            }
        }
        materialListView.setBackgroundColor(Color.WHITE);
    }

    private void updateRecentWords() {
        mRecentWords = new ArrayList<>();
        for (Word word : mWords) {
            if (!word.isArchived()) {
                mRecentWords.add(word);
            }
        }
    }

    private Word getWord(UUID uuid){
        for (Word word : mWords) {
            if (word.getUUID().equals(uuid)) {
                return word;
            }
        }
        return null;
    }

    private void archiveWord(int position) {
        if (!mRecentWords.isEmpty()) {
            getWord(mRecentWords.get(position).getUUID()).setArchived(true);
        }
    }

    private void archiveWords(ArrayList<Integer> positions) {
        if (positions.isEmpty()) {
            return;
        }
        for (Integer position : positions) {
            archiveWord(position);
        }
    }

    private void deleteWord(int position) {
        if (!mWords.isEmpty()) {
            mWords.remove(mRecentWords.get(position));
        }
    }

    private void deleteWords(ArrayList<Integer> positions) {
//        Log.d(TAG,"deleteWords() called positions:" + positions);
        if (positions.isEmpty()) {
            return;
        }
        for (Integer position : positions) {
            deleteWord(position);
        }
    }

    private void selectAll() {
        selectedItemPositions.clear();
        for (int i = 0; i < mRecentWords.size(); i++) {
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