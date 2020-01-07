package ir.ham3da.darya.adaptors;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ir.ham3da.darya.ActivityInfo2;
import ir.ham3da.darya.ui.main.MainPoetsFragment;
import ir.ham3da.darya.utility.AppFontManager;
import ir.ham3da.darya.utility.AppSettings;
import ir.ham3da.darya.Bungee;
import ir.ham3da.darya.ActivityCate;
import ir.ham3da.darya.ganjoor.GanjoorCat;
import ir.ham3da.darya.ganjoor.GanjoorDbBrowser;
import ir.ham3da.darya.ganjoor.GanjoorPoet;
import ir.ham3da.darya.ActivityMain;
import ir.ham3da.darya.R;
import ir.ham3da.darya.utility.MyDialogs;

/**
 * Adapter to fill Poets and categories in RecyclerView
 */
public class AdapterPoetsExpand extends PoetAndBooksAdapter<AdapterPoetsExpand.PoetsAndCateItem> {
    public static final int TYPE_CHILD = 1001;

    private float textSize;
    private MainPoetsFragment mainPoetsFragment;
    int fontId;

    public AdapterPoetsExpand(Context context, MainPoetsFragment mainPoetsFragment1) {
        super(context);
        setItems(loadPoetsAndCate());

        AppSettings.Init(context);
        this.textSize = AppSettings.getTextSize();

        mainPoetsFragment = mainPoetsFragment1;
        fontId = AppSettings.getPoemsFont();

    }

    public static class PoetsAndCateItem extends PoetAndBooksAdapter.ListItem {
        public String Text;
        public int ID;
        public int ParentID;
        public int childCount;
        public int poetIndex;
        public int listType; // 0: poet, 1: categories, 2: poems

        /**
         * Add poet
         *
         * @param group
         * @param id
         * @param child_Count
         * @param poet_Index
         */
        public PoetsAndCateItem(String group, int id, int child_Count, int poet_Index) {
            super(TYPE_HEADER, id, child_Count, poet_Index, 0);
            Text = group;
            ID = id;
            ParentID = 0;
            childCount = child_Count;
            poetIndex = poet_Index;
            listType = 0;

        }

        /**
         * Add book
         *
         * @param book
         * @param id
         * @param child_Count
         * @param poet_Index
         * @param parent_id
         * @param list_type   int 0: poet, 1: categories, 2: poems , 3: biographi
         */
        public PoetsAndCateItem(String book, int id, int child_Count, int poet_Index, int parent_id, int list_type) {
            super(TYPE_CHILD, id, child_Count, 0, parent_id);
            Text = book;
            ID = id;
            ParentID = parent_id;
            childCount = 0;
            poetIndex = 0;
            listType = list_type;
        }
    }


    public class PoetsViewHolder extends PoetAndBooksAdapter.HeaderViewHolder {
        TextView poets_name, books_count;
        ImageButton moreOptions;
        ImageView avatar_poet;

        LinearLayout poets_liner;

        private PoetsViewHolder(View view) {
            super(view, (ImageView) view.findViewById(R.id.item_arrow));


            poets_name = view.findViewById(R.id.poets_name);
            books_count = view.findViewById(R.id.books_count);
            avatar_poet = view.findViewById(R.id.avatar_poet);
            poets_liner = view.findViewById(R.id.poets_liner);

            moreOptions = view.findViewById(R.id.moreOptions);

            poets_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            AppFontManager.setFont(mContext, poets_name, fontId);
        }

        public void bind(final int position) {
            super.bind(position);

            final int poetId = visibleItems.get(position).ID;
            AppSettings.Init(mContext);
            if (AppSettings.getPoetListIndexStatus()) {
                String poetIndexStr = String.format(Locale.getDefault(), "%d", visibleItems.get(position).poetIndex);
                String poetText_str = poetIndexStr + ". " + visibleItems.get(position).Text;
                poets_name.setText(poetText_str);

            } else {
                poets_name.setText(visibleItems.get(position).Text);
            }

            String books = visibleItems.get(position).childCount > 1 ? mContext.getString(R.string.books) : mContext.getString(R.string.book);

            int childCount = visibleItems.get(position).childCount;

            String childCountStr = String.format(Locale.getDefault(), "%d", childCount);

            books_count.setText(childCountStr);

            poets_liner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleClick();
                }
            });

            moreOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    showMenu(view, poetId, position, poets_liner);
                }
            });

        }

    }

    /**
     * show More Menu for an item
     *
     * @param view
     * @param poetId
     * @param position
     */
    public void showMenu(final View view, final int poetId, final int position, final LinearLayout parentLayer) {
        //creating a popup menu
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        //inflating menu from xml resource
        popup.inflate(R.menu.poet_option_menu);

        //adding click listener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.poem_delete:
                        deleteItem(poetId, position);
                        break;
                    case R.id.poet_open:
                        parentLayer.performClick();
                        break;
                }
                return false;
            }
        });

        //displaying the popup
        popup.show();
    }


    private void deleteItem(final int poetId, final int position) {

        MyDialogs MyDialogs1 = new MyDialogs(mContext);

        PoetsAndCateItem poetsAndCateItem =
                visibleItems.get(position);
        String ques = String.format(mContext.getString(R.string.poet_delete_ques), "<b>" + poetsAndCateItem.Text + "</b>");

        final Dialog yesNoDialog = MyDialogs1.YesNoDialog(ques, mContext.getDrawable(R.drawable.ic_delete_white_24dp), true);

        Button noBtn = yesNoDialog.findViewById(R.id.noBtn);
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yesNoDialog.dismiss();
            }
        });

        Button yesBtn = yesNoDialog.findViewById(R.id.yesBtn);
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GanjoorDbBrowser GanjoorDbBrowser1 = new GanjoorDbBrowser(mContext);
                if (isExpanded(position)) {
                    collapseItems(position, false);
                }
                yesNoDialog.dismiss();

                GanjoorPoet ganjoorPoet = GanjoorDbBrowser1.getPoet(poetId);

                GanjoorDbBrowser1.DeletePoet(ganjoorPoet);

                mainPoetsFragment.refreshList(position);


            }
        });
        yesNoDialog.show();
    }

    /**
     * reIndex Items
     */
    private void reIndexItems() {
        int index = 0;
        for (PoetsAndCateItem poetsAndCateItem : visibleItems) {
            index++;
            poetsAndCateItem.poetIndex = index;
        }

        notifyDataSetChanged();
    }

    public class BooksViewHolder extends PoetAndBooksAdapter.ViewHolder {

        TextView books_name;
        ImageView avatar_book;
        LinearLayout books_liner;

        public BooksViewHolder(View view) {
            super(view);

            books_name = view.findViewById(R.id.books_name);
            avatar_book = view.findViewById(R.id.avatar_book);
            books_liner = view.findViewById(R.id.books_liner);

            books_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            AppFontManager.setFont(mContext, books_name, fontId);
        }

        private void showBiography(View view, int poet_id) {

            GanjoorDbBrowser GanjoorDbBrowser1 = new GanjoorDbBrowser(mContext);
            GanjoorPoet ganjoorPoet = GanjoorDbBrowser1.getPoet(poet_id);

            Intent intent = new Intent(view.getContext(), ActivityInfo2.class);

            intent.putExtra("title1", mContext.getString(R.string.short_introduction));
            intent.putExtra("title2", ganjoorPoet._Name);
            intent.putExtra("text", ganjoorPoet._Bio);


            mContext.startActivity(intent);
            Bungee.card(mContext);
        }


        /**
         * @param position
         */
        public void bind(int position) {
            final String book_text = visibleItems.get(position).Text;

            books_name.setText(book_text);


            final int id = visibleItems.get(position).ID;
            final int poet_id = visibleItems.get(position).ParentID;
            final int list_type = visibleItems.get(position).listType;

            final View books_liner_view = books_liner;

            books_liner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (list_type == 3) {
                        showBiography(v, poet_id);
                    } else {

                        Intent intent = new Intent(v.getContext(), ActivityCate.class);
                        intent.putExtra("cate_id", id);
                        intent.putExtra("fromCate", false);

                        mContext.startActivity(intent);
                        Bungee.card(mContext);
                    }
                }
            });


        }


    }


    /**
     * @param parent
     * @param viewType
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == TYPE_HEADER) {

            PoetsViewHolder PoetsViewHolder1 = new PoetsViewHolder(inflate(R.layout.item_poet, parent));
            return PoetsViewHolder1;
        } else {
            BooksViewHolder BooksViewHolder2 = new BooksViewHolder(inflate(R.layout.item_child_books, parent));
            return BooksViewHolder2;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull PoetAndBooksAdapter.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                ((PoetsViewHolder) holder).bind(position);
                break;
            case TYPE_CHILD:

            default:
                ((BooksViewHolder) holder).bind(position);
                break;
        }
    }


    /**
     * load Poets And their Categories(books)
     *
     * @return Void
     */
    private List<PoetsAndCateItem> loadPoetsAndCate() {
        List<PoetsAndCateItem> items = new ArrayList<>();
        GanjoorDbBrowser GanjoorDbBrowser1 = new GanjoorDbBrowser(mContext);
        List<GanjoorPoet> poets = GanjoorDbBrowser1.getPoets();

        int poet_index = 0;
        int booksCount = 0;

        if (poets.size() > 0) {
            for (GanjoorPoet Poet : poets) {

                List<GanjoorCat> categories = GanjoorDbBrowser1.getSubCats(Poet._CatID);
                booksCount += categories.size();

                poet_index++;

                if (categories.size() > 0) {
                    items.add(new PoetsAndCateItem(Poet._Name, Poet._ID, categories.size(), poet_index));
                    items.add(new PoetsAndCateItem(mContext.getString(R.string.short_introduction), 0, 0, 0, Poet._ID, 3));
                    for (GanjoorCat SubCat : categories) {
                        items.add(new PoetsAndCateItem(SubCat._Text, SubCat._ID, 0, 0, SubCat._ParentID, 1));
                    }
                }

                int poemsCount = GanjoorDbBrowser1.getPoemsCount(Poet._CatID);
                if (poemsCount > 0) {
                    if (categories.size() == 0) {
                        items.add(new PoetsAndCateItem(Poet._Name, Poet._ID, 1, poet_index));
                        items.add(new PoetsAndCateItem(mContext.getString(R.string.short_introduction), 0, 0, 0, Poet._ID, 3));
                    }
                    items.add(new PoetsAndCateItem(mContext.getString(R.string.nocategory), Poet._CatID, 0, 0, Poet._ID, 2));
                    booksCount++;
                } else {
                    if (categories.size() == 0) {
                        items.add(new PoetsAndCateItem(Poet._Name, Poet._ID, 0, poet_index));
                        items.add(new PoetsAndCateItem(mContext.getString(R.string.short_introduction), 0, 0, 0, Poet._ID, 3));
                    }
                }


            }
        }
        GanjoorDbBrowser1.CloseDatabase();


        ActivityMain activity = (ActivityMain) mContext;
        if (activity != null) {
            activity.booksCount = booksCount;
        }
        return items;
    }
}
