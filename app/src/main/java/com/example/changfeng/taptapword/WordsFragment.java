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
    private Word currentWord;
    private ArrayList<Word> selectedWords;
    private ArrayList<Word> mArchivedWords = new ArrayList<>();
    private boolean isActionMode = false;

    private MaterialListView materialListView;

    @Override
    public void onResume() {
        super.onResume();
        mArchivedWords = WordManger.get(getActivity()).getArchivedWords();
        updateListView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "OnCreateView() called");
        View view = inflater.inflate(R.layout.fragment_words, container, false);
        materialListView = (MaterialListView) view.findViewById(R.id.material_listview);

        mArchivedWords = WordManger.get(getActivity()).getArchivedWords();

        materialListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(CardItemView cardItemView, int i) {
                if (mArchivedWords.isEmpty()) {
                    Toast.makeText(getActivity(), getString(R.string.title_no_archived_words), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isActionMode) {
                    if (selectedWords.contains(mArchivedWords.get(i))) {
                        selectedWords.remove(mArchivedWords.get(i));
                        cardItemView.setBackgroundColor(Color.WHITE);
                    } else {
                        selectedWords.add(mArchivedWords.get(i));
                        cardItemView.setBackgroundColor(Color.parseColor(MainActivity.SELECTED_COLOR));
                    }
                } else {
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
                    selectedWords = new ArrayList<>();
                    if (!mArchivedWords.isEmpty()) {
                        selectedWords.add(mArchivedWords.get(i));
                        cardItemView.setBackgroundColor(Color.parseColor(MainActivity.SELECTED_COLOR));
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


    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            isActionMode = true;
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
                    if (!selectedWords.isEmpty()) {
                        deleteWords();
                        showToast(getString(R.string.message_delete_success), Toast.LENGTH_SHORT);
                    }
                    mode.finish();
                    break;
                case R.id.action_unarchive:
                    if (!selectedWords.isEmpty()) {
                        unArchiveWords();
                        showToast(getString(R.string.message_unarchive_success), Toast.LENGTH_SHORT);
                    }
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

        @Override
        public void onDestroyActionMode(ActionMode mode) {
//            Log.d(TAG, "OnDestroyActionMode() called");
            updateListView();
            isActionMode = false;
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
        materialListView.setBackgroundColor(Color.WHITE);
    }

    private void unArchiveWords() {
        if (selectedWords.isEmpty()) {
            return;
        }

        for (Word word : selectedWords) {
            word.setArchived(false);
            WordManger.get(getActivity()).updateWord(word);
        }
        mArchivedWords.removeAll(selectedWords);
    }

    private void deleteWords() {
//        Log.d(TAG,"deleteWords() called positions:" + positions);
        if (selectedWords.isEmpty()) {
            return;
        }
        for (Word word : selectedWords) {
            WordManger.get(getActivity()).deleteWord(word);
        }
        mArchivedWords.removeAll(selectedWords);
    }

    private void selectAll() {
        if (mArchivedWords.isEmpty()) {
            return;
        }
        selectedWords = mArchivedWords;
    }

    private void checkAll() {
        if (mArchivedWords.isEmpty()) {
            return;
        }
        materialListView.setBackgroundColor(Color.parseColor(MainActivity.SELECTED_COLOR));

    }
    private void showToast(String message, int duration) {
        Toast.makeText(getActivity(), message, duration).show();
    }
}
