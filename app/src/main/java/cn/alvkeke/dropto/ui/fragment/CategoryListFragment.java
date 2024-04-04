package cn.alvkeke.dropto.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

import cn.alvkeke.dropto.R;
import cn.alvkeke.dropto.data.Category;
import cn.alvkeke.dropto.ui.adapter.CategoryListAdapter;
import cn.alvkeke.dropto.ui.intf.ListNotification;

public class CategoryListFragment extends Fragment implements ListNotification {

    public interface AttemptListener {
        enum Attempt {
            CREATE,
            DETAIL,
            EXPAND,
        }
        void onAttemptRecv(AttemptListener.Attempt attempt, Category category);
        void onErrorRecv(String errorMessage);
    }

    private AttemptListener listener;
    private CategoryListAdapter categoryListAdapter;
    private ArrayList<Category> categories;

    public CategoryListFragment() {
    }

    public void setListener(AttemptListener listener) {
        this.listener = listener;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = requireContext();

        RecyclerView rlCategory = view.findViewById(R.id.rlist_category);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_category);
        View statusBar = view.findViewById(R.id.category_list_status_bar);
        View navigationBar = view.findViewById(R.id.category_list_navigation_bar);
        setSystemBarHeight(view, statusBar, navigationBar);

        toolbar.inflateMenu(R.menu.category_toolbar);
        toolbar.setOnMenuItemClickListener(new CategoryMenuListener());

        categoryListAdapter = new CategoryListAdapter(categories);
        categoryListAdapter.setCategories(categories);

        rlCategory.setAdapter(categoryListAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        rlCategory.setLayoutManager(layoutManager);
        rlCategory.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        categoryListAdapter.setItemClickListener(new onListItemClick());

    }

    private void setSystemBarHeight(View parent, View status, View navi) {
        parent.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @NonNull
            @Override
            public WindowInsets onApplyWindowInsets(@NonNull View view, @NonNull WindowInsets insets) {
                int statusHei, naviHei;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    statusHei = insets.getInsets(WindowInsets.Type.statusBars()).top;
                    naviHei = insets.getInsets(WindowInsets.Type.navigationBars()).bottom;
                    status.getLayoutParams().height = statusHei;
                    navi.getLayoutParams().height = naviHei;
                }
                return insets;
            }
        });
    }

    class CategoryMenuListener implements Toolbar.OnMenuItemClickListener {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int menuId = item.getItemId();
            if (menuId == R.id.category_menu_item_add) {
                listener.onAttemptRecv(AttemptListener.Attempt.CREATE, null);
            } else if (menuId == R.id.category_menu_item_edit) {
                listener.onErrorRecv("Try edit categories");
            } else {
                listener.onErrorRecv("Unknown menu id: " + menuId);
                return false;
            }
            return true;
        }
    }

    class onListItemClick implements CategoryListAdapter.OnItemClickListener {

        @Override
        public void onItemClick(int index, View v) {
            Category e = categories.get(index);
            listener.onAttemptRecv(AttemptListener.Attempt.EXPAND, e);
        }

        @Override
        public boolean onItemLongClick(int index, View v) {
            Category e = categories.get(index);
            listener.onAttemptRecv(AttemptListener.Attempt.DETAIL, e);
            return true;
        }
    }

    @Override
    public void notifyItemListChanged(ListNotification.Notify notify, int index, Object object) {
        Category category = (Category) object;
        if (notify != Notify.REMOVED && categories.get(index) != category) {
            listener.onErrorRecv("target Category not exist");
            return;
        }

        switch (notify) {
            case CREATED:
                categoryListAdapter.notifyItemInserted(index);
                categoryListAdapter.notifyItemRangeChanged(index,
                        categoryListAdapter.getItemCount()-index);
                break;
            case UPDATED:
                categoryListAdapter.notifyItemChanged(index);
                break;
            case REMOVED:
                categoryListAdapter.notifyItemRemoved(index);
                categoryListAdapter.notifyItemRangeChanged(index,
                        categoryListAdapter.getItemCount()-index);
                break;
            default:
        }
    }

}