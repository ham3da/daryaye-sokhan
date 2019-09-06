package ir.ham3da.darya.adaptors;

import android.content.Context;

import androidx.annotation.NonNull;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ir.ham3da.darya.utility.AppSettings;
import ir.ham3da.darya.ganjoor.GanjoorCat;
import ir.ham3da.darya.ganjoor.GanjoorDbBrowser;
import ir.ham3da.darya.ganjoor.GanjoorPoet;
import ir.ham3da.darya.R;


/**
 * Adapter to fill Poets and categories in RecyclerView
 */
public class AdapterRandomPoem extends PoetAndBooksAdapter<AdapterRandomPoem.PoetsAndCateItem> {
    public static final int TYPE_CHILD = 1001;

    private float textSize;

    public AdapterRandomPoem(Context context) {
        super(context);
        setItems(loadPoetsAndCate());
        AppSettings.Init(context);
        this.textSize = AppSettings.getTextSize();

    }
    /**
     * get Selected Poet Ids
     *
     * @return List<Integer>
     */
    public List<Integer> getSelectedPoets() {
        List<Integer> selectedPoets = new ArrayList<>();

        for (int i = 0; i < allItems.size(); i++) {
            if (allItems.get(i).ItemType == TYPE_HEADER && allItems.get(i).selected) {
                selectedPoets.add(allItems.get(i).ID);
            }
        }
        return selectedPoets;
    }

    /**
     * select or Unselect All Items
     *
     * @param is_selected boolean
     */
    public void selectAllItem(boolean is_selected) {
        for (int i = 0; i < allItems.size(); i++) {
            allItems.get(i).selected = is_selected;
        }
        notifyDataSetChanged();
    }


    public void selectAllChildItem(int poetId, boolean is_selected)
    {
        for (int i = 0; i < visibleItems.size(); i++)
        {
            if (visibleItems.get(i).ItemType == TYPE_CHILD && visibleItems.get(i).ParentID == poetId) {
                visibleItems.get(i).selected = is_selected;

            }
        }
      //notifyDataSetChanged();

    }


    /**
     * get Selected Category Ids
     *
     * @return List<Integer>
     */
    public List<Integer> getSelectedCategories() {
        List<Integer> selectedCategories = new ArrayList<>();
        for (int i = 0; i < allItems.size(); i++) {
            if (allItems.get(i).ItemType == TYPE_CHILD && allItems.get(i).selected) {
                selectedCategories.add(allItems.get(i).ID);
            }
        }
        return selectedCategories;
    }

    public static class PoetsAndCateItem extends PoetAndBooksAdapter.ListItem {
        public String Text;
        public int ID;
        public int ParentID;
        public int childCount;
        public int poetIndex;
        public boolean selected;

        /**
         * Add poet
         *
         * @param group
         * @param id
         * @param child_Count
         * @param poet_Index
         * @param is_selected
         */
        public PoetsAndCateItem(String group, int id, int child_Count, int poet_Index, boolean is_selected) {
            super(TYPE_HEADER, id, child_Count, poet_Index, 0);
            Text = group;
            ID = id;
            ParentID = 0;
            childCount = child_Count;
            poetIndex = poet_Index;
            selected = is_selected;
        }

        /**
         * Add book
         *
         * @param book
         * @param id
         * @param child_Count
         * @param poet_Index
         * @param parent_id
         * @param is_selected
         */
        public PoetsAndCateItem(String book, int id, int child_Count, int poet_Index, int parent_id, boolean is_selected) {
            super(TYPE_CHILD, id, child_Count, 0, parent_id);
            Text = book;
            ID = id;
            ParentID = parent_id;
            childCount = 0;
            poetIndex = 0;
            selected = is_selected;
        }
    }


    public class PoetsViewHolder extends PoetAndBooksAdapter.HeaderViewHolder {
        TextView poets_name;
        CheckBox checkbox_poet;

        LinearLayout poets_liner;

        private PoetsViewHolder(View view) {
            super(view, (ImageView) view.findViewById(R.id.item_arrow));
            poets_name = (TextView) view.findViewById(R.id.poets_name);
            checkbox_poet = (CheckBox) view.findViewById(R.id.checkbox_poet);
            poets_liner = (LinearLayout) view.findViewById(R.id.poets_liner);
            poets_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
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

            checkbox_poet.setChecked(visibleItems.get(position).selected);

            checkbox_poet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    visibleItems.get(position).selected = b;
                    selectAllChildItem(poetId, b);

                }
            });


            poets_liner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleClick();

                }
            });

        }

    }

    public class BooksViewHolder extends PoetAndBooksAdapter.ViewHolder {

        TextView books_name;
        CheckBox checkbox_book;
        LinearLayout books_liner;

        public BooksViewHolder(View view) {
            super(view);

            books_name = (TextView) view.findViewById(R.id.books_name);
            checkbox_book = (CheckBox) view.findViewById(R.id.checkbox_book);
            books_liner = (LinearLayout) view.findViewById(R.id.books_liner);

            books_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        }

        /**
         * @param position
         */
        public void bind(final int position) {
            final String book_text = visibleItems.get(position).Text;

            books_name.setText(book_text);


            final int id = visibleItems.get(position).ID;
            final int poet_id = visibleItems.get(position).ParentID;
            final View books_liner_view = books_liner;


            checkbox_book.setChecked(visibleItems.get(position).selected);

            checkbox_book.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    visibleItems.get(position).selected = b;
                }
            });


            books_liner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (id == 0) {
                        //load biography
                    } else {


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

            PoetsViewHolder PoetsViewHolder1 = new PoetsViewHolder(inflate(R.layout.item_poet_checkbox, parent));
            return PoetsViewHolder1;
        } else {
            BooksViewHolder BooksViewHolder2 = new BooksViewHolder(inflate(R.layout.item_child_books_checkbox, parent));
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

        String randomSelectedCategories = AppSettings.getRandomSelectedCategories();
        String[] stringCatIds = randomSelectedCategories.split(",");
        List<Integer> randomSelectedCategoryIds = new ArrayList<>();
        for (String catId : stringCatIds) {
            randomSelectedCategoryIds.add(Integer.valueOf(catId));
        }

        String randomSelectedPoets = AppSettings.getRandomSelectedPoets();
        String[] stringPoetIds = randomSelectedPoets.split(",");
        List<Integer> randomSelectedPoetIds = new ArrayList<>();
        for (String poetId : stringPoetIds) {
            randomSelectedPoetIds.add(Integer.valueOf(poetId));
        }



        int poet_index = 0;
        int booksCount = 0;

        if (poets.size() > 0) {
            for (GanjoorPoet Poet : poets) {
                List<GanjoorCat> categories = GanjoorDbBrowser1.getSubCats(Poet._CatID);
                booksCount += categories.size();
                poet_index++;

                boolean is_selected1;
                boolean is_selected2;

                is_selected1 = randomSelectedPoetIds.contains(Poet._ID);

                items.add(new PoetsAndCateItem(Poet._Name, Poet._ID, categories.size(), poet_index, is_selected1));

                for (GanjoorCat SubCat : categories) {

                    is_selected2 = randomSelectedCategoryIds.contains(SubCat._ID);
                    items.add(new PoetsAndCateItem(SubCat._Text, SubCat._ID, 0, 0, SubCat._ParentID, is_selected2));
                }

            }
        }


        return items;
    }
}
