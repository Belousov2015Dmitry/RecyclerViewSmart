package me.burunduk.smartrecyclerview.example;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import me.burunduk.smartrecyclerview.IndexDecorator;
import me.burunduk.smartrecyclerview.R;
import me.burunduk.smartrecyclerview.SmartRVAdapter;

/**
 * Created by Dmitry Belousov on 02.04.2017.
 */

public class MainActivity extends AppCompatActivity
{
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.recyclerView = (RecyclerView) findViewById(R.id.activity_main_recycler);

        this.recyclerView.setLayoutManager(
                new LinearLayoutManager(
                        this,
                        LinearLayoutManager.VERTICAL,
                        false
                )
        );

        final SmartRVAdapter adapter = new SmartRVAdapter();

        final List<SmartRVAdapter.ItemModel> itemsForSection0 = new LinkedList<SmartRVAdapter.ItemModel>() {{
            add(new MyItemModel("AName 0"));
            add(new MyItemModel("AName 1"));
            add(new MyItemModel("AName 2"));
            add(new MyItemModel("AName 3"));
            add(new MyItemModel("AName 4"));
            add(new MyItemModel("AName 5"));
        }};

        final List<SmartRVAdapter.ItemModel> itemsForSection1 = new LinkedList<SmartRVAdapter.ItemModel>() {{
            add(new MyItemModel("BName 0"));
            add(new MyItemModel("BName 1"));
        }};

        final List<SmartRVAdapter.ItemModel> itemsForSection2 = new LinkedList<SmartRVAdapter.ItemModel>() {{
            add(new MyItemModel("CName 0"));
            add(new MyItemModel("CName 1"));
            add(new MyItemModel("CName 2"));
            add(new MyItemModel("CName 3"));
            add(new MyItemModel("CName 4"));
            add(new MyItemModel("CName 5"));
            add(new MyItemModel("CName 6"));
            add(new MyItemModel("CName 7"));
        }};


        final List<SmartRVAdapter.SectionModel> sections = new LinkedList<SmartRVAdapter.SectionModel>() {{
            add(new MySectionModel(itemsForSection0, "ASection 0"));
            add(new MySectionModel(itemsForSection1, "BSection 1"));
            add(new MySectionModel(itemsForSection2, "CSection 2"));
        }};

        this.recyclerView.setAdapter(adapter);

        final IndexDecorator itemDecorator = new IndexDecorator(getApplicationContext());
        itemDecorator.paddingLeft = 8;
        itemDecorator.typeface = Typeface.DEFAULT_BOLD;
        itemDecorator.applySettings();
        this.recyclerView.addItemDecoration(itemDecorator);

        adapter.setSections(sections);
        adapter.selectionEnabled = true;
        adapter.notifyDataSetChanged();
    }
}



/**********************************************************
 *
 *                  View Holders
 *
 *********************************************************/

class MySectionHolder extends RecyclerView.ViewHolder
{
    public TextView textView;

    public MySectionHolder(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.section_layout_textView);
    }
}

class MyItemHolder extends RecyclerView.ViewHolder implements IndexDecorator.IndexProvider
{
    public TextView textView;


    public MyItemHolder(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.item_layout_textView);
    }

    @Override
    public char getIndexChar() {
        final String name = textView.getText().toString();
        return name.length() > 0 ? name.charAt(0) : 0;
    }
}



/**********************************************************
 *
 *                  Item Models
 *
 *********************************************************/

class MySectionModel extends SmartRVAdapter.SectionModel<MySectionHolder>
{
    public String title;

    public MySectionModel(@Nullable Collection<SmartRVAdapter.ItemModel> items, String title) {
        super(R.layout.section_layout, items);
        this.title = title;
    }

    public MySectionModel(@LayoutRes int layout, @Nullable Collection<SmartRVAdapter.ItemModel> items) {
        super(layout, items);
    }

    @Override
    public MySectionHolder makeViewHolder(@NonNull ViewGroup parent, int position) {
        return new MySectionHolder(
                this.inflateViewHolder(parent)
        );
    }

    @Override
    public void initViewHolder(@NonNull MySectionHolder holder, int position) {
        holder.textView.setText(title);
    }
}

class MyItemModel extends SmartRVAdapter.ItemModel<MyItemHolder> implements SmartRVAdapter.SelectionProvider
{
    public String name;
    private boolean selected;

    public MyItemModel(String name) {
        super(R.layout.item_layout);
        this.name = name;
    }

    public MyItemModel(@LayoutRes int layout) {
        super(layout);
    }

    @Override
    public MyItemHolder makeViewHolder(@NonNull ViewGroup parent, int position) {
        return new MyItemHolder(
                super.inflateViewHolder(parent)
        );
    }

    @Override
    public void initViewHolder(@NonNull MyItemHolder holder, int position) {
        holder.textView.setText(name);
        holder.itemView.setBackgroundColor(
                selected ? Color.LTGRAY : Color.GREEN
        );
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }
}