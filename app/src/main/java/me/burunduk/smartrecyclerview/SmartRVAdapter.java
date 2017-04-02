package me.burunduk.smartrecyclerview;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Author: Dmitry Belousov on 02.04.2017.
 *
 * Copyright © Dmitry Belousov
 * License:  http://www.apache.org/licenses/LICENSE-2.0
 */



public class SmartRVAdapter extends RecyclerView.Adapter implements View.OnClickListener
{
    protected final List<SectionModel> sections = new ArrayList<>();
    protected final List<BaseItemModel> allItems = new ArrayList<>();
    public boolean selectionEnabled = false;



    /**********************************************************
     *                      Methods
     *********************************************************/

    public void setSections(@NonNull Collection<SectionModel> sections) {
        this.sections.clear();
        this.allItems.clear();

        this.sections.addAll(sections);

        for(final SectionModel section : sections)
            this.addSection(section);
    }

    public void addSection(@NonNull SectionModel section) {
        this.sections.add(section);
        this.allItems.add(section);

        int k = 0;

        final List<ItemModel> items = section.items;
        for (ItemModel item : items) {
            item.indexInSection = k++;
            this.allItems.add(item);
        }
    }

    public boolean addItem(@NonNull ItemModel item, int sectionIndex) {
        final int sectionsCount = this.sections.size();

        if( sectionIndex >= 0 && sectionIndex < sectionsCount ) {
            int offset = 0;
            for (int i = 0; i <= sectionIndex; ++i) {
                offset += this.sections.get(i).items.size();
            }
            offset += sectionIndex + 1;

            final SectionModel section = this.sections.get(sectionIndex);

            item.indexInSection = section.items.size();
            this.allItems.set(offset, item);

            section.items.add(item);

            return true;
        }
        return false;
    }


    /**********************************************************
     *                  RecyclerView.Adapter
     *********************************************************/

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //viewType is an position here
        return  this.allItems.get(viewType).makeViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        this.allItems.get(position).initViewHolder(holder, position);

        if(selectionEnabled) {
            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(this);
        }
    }

    @Override
    public int getItemCount() {
        return this.allItems.size();
    }

    @Override
    public void onClick(View v) {
        final Integer index = (Integer) v.getTag();

        try {
            final SelectionProvider sprovider = (SelectionProvider) this.allItems.get(index);

            sprovider.setSelected(
                    !sprovider.isSelected()
            );
            notifyItemChanged(index);
        }
        catch (Exception e) {
            Log.d("SmartRVAdapter", "Item at index " + index + "does not implement the SelectionProvider.");
        }
    }



    /**********************************************************
     *
     *                  SectionModel
     *
     *********************************************************/

    public static abstract class SectionModel<SVH extends RecyclerView.ViewHolder> extends BaseItemModel<SVH>
    {
        private final List<ItemModel> items = new ArrayList<>();

        public SectionModel(@LayoutRes int layout, @Nullable Collection<ItemModel> items) {
            super(layout);
            if(items != null) {
                this.items.addAll(items);
            }
        }


        public void addItem(@NonNull ItemModel item) {
            this.items.add(item);
        }

        @Nullable
        public ItemModel getItemAtIdex(int index) {
            return index >= 0 && index < this.items.size()
                    ? this.items.get(index)
                    : null;
        }
    }



    /**********************************************************
     *
     *                  ItemModel
     *
     *********************************************************/

    public static abstract class ItemModel<IVH extends RecyclerView.ViewHolder> extends BaseItemModel<IVH>
    {
        protected int indexInSection;

        public ItemModel(@LayoutRes int layout) {
            super(layout);
        }
    }



    /**********************************************************
     *
     *                  BaseItemModel
     *
     *********************************************************/

    public static abstract class BaseItemModel<VH extends RecyclerView.ViewHolder>
    {
        @LayoutRes
        public int layout;


        public BaseItemModel(@LayoutRes int layout) {
            this.layout = layout;
        }


        public View inflateViewHolder(@NonNull ViewGroup parent) {
            return LayoutInflater.from(
                    parent.getContext()
            ).inflate(
                    layout,
                    parent,
                    false
            );
        }

        public abstract VH makeViewHolder(@NonNull ViewGroup parent, int position);

        public abstract void initViewHolder(@NonNull VH holder, int position);
    }



    /**********************************************************
     *
     *                  SelectionProvider
     *
     *********************************************************/

    public interface SelectionProvider
    {
        void setSelected(boolean selected);
        boolean isSelected();
    }
}