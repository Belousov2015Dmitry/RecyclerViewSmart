package me.burunduk.smartrecyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import java.util.HashMap;

import static android.support.annotation.Dimension.DP;


/**
 * Author: Dmitry Belousov on 02.04.2017.
 *
 * Copyright © Dmitry Belousov
 * License:  http://www.apache.org/licenses/LICENSE-2.0
 */


public class IndexDecorator extends RecyclerView.ItemDecoration
{
    //Props:
    @Dimension(unit = DP) public int paddingLeft = 0;
    @Dimension(unit = DP) public int paddingTop = 0;
    @ColorInt public int textColor = Color.WHITE;
    @Dimension(unit = DP) public int fontSize = 24;
    public Typeface typeface;
    public boolean logEnabled = true;

    //Vars:
    private final Context context;
    private final Paint textPaint = new Paint();
    private final HashMap<Character, Boolean> indexDrawn = new HashMap<>();

    private int fontSizePx = 0;
    private int paddingTopPx = 0;
    private int paddingLeftPx = 0;



    /**********************************************************
     *
     *                  Initialization
     *
     *********************************************************/

    public IndexDecorator(Context context) {
        super();
        this.context = context;

        applySettings();
    }

    public IndexDecorator(
            Context context,
            @ColorInt int textColor,
            int fontSize,
            Typeface typeface,
            @Dimension(unit = DP) int paddingLeft,
            @Dimension(unit = DP) int paddingTop)
    {
        super();
        this.context = context;
        this.textColor = textColor;
        this.fontSize = fontSize;
        this.paddingLeft = paddingLeft;
        this.paddingTop = paddingTop;
        this.typeface = typeface;

        applySettings();
    }



    /**********************************************************
     *
     *                      Methods
     *
     ********************************************************/

    public void applySettings() {
        this.fontSizePx = dpToPx(context, this.fontSize);
        this.paddingTopPx = dpToPx(context, this.paddingTop);
        this.paddingLeftPx = dpToPx(context, this.paddingLeft);

        this.textPaint.setAntiAlias(true);
        this.textPaint.setTypeface(this.typeface);
        this.textPaint.setColor(this.textColor);
        this.textPaint.setTextSize(this.fontSizePx);
    }

    public static  int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        );
    }



    /**********************************************************
     *
     *                      OnDraw
     *
     *********************************************************/


    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        indexDrawn.clear();

        final int cnt = parent.getChildCount();

        for (int i = 0; i < cnt; ++i) {
            final View view = parent.getChildAt(i);
            final RecyclerView.ViewHolder viewHolder = parent.getChildViewHolder(view);

            try {
                final IndexProvider indexProvider = (IndexProvider) viewHolder;
                final char indexChar = indexProvider.getIndexChar();

                final Boolean drawn = indexDrawn.get(indexChar);

                if(indexChar != 0 && (drawn == null || !drawn)) {
                    indexDrawn.put(indexChar, Boolean.TRUE);

                    final int top = parent.getTop() + this.fontSizePx + this.paddingTopPx;
                    int y = view.getTop() + this.fontSizePx;

                    try {
                        final View nextView = parent.getChildAt(i + 1);
                        final RecyclerView.ViewHolder nextViewHolder = parent.getChildViewHolder(nextView);

                        final IndexProvider nextIndexProvider = (IndexProvider) nextViewHolder;
                        final char nextIndexChar = nextIndexProvider.getIndexChar();

                        if(y < top && nextIndexChar == indexChar)
                            y = top;
                    }
                    catch (Exception ex) {
                        if(logEnabled)
                            ex.printStackTrace();
                    }

                    c.drawText(
                            new char[] { indexChar },
                            0, 1,   //index, count
                            paddingLeftPx, y + this.paddingTopPx, //x, y
                            textPaint
                    );
                }
            }
            catch (Exception e) {
                if(logEnabled)
                    e.printStackTrace();
            }
        }
    }



    /**********************************************************
     *
     *                  IndexProvider
     *
     *********************************************************/

    public interface IndexProvider
    {
        char getIndexChar();
    }
}