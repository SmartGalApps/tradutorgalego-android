package es.galapps.android.tradutorgalego.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SlidingDrawer;

public class CustomSlidingDrawer extends SlidingDrawer {

    public CustomSlidingDrawer(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);

        int orientation = attrs.getAttributeIntValue("android", "orientation",
                ORIENTATION_VERTICAL);
        this.mTopOffset = attrs.getAttributeIntValue("android", "topOffset", 0);
        this.mVertical = (orientation == SlidingDrawer.ORIENTATION_VERTICAL);
    }

    public CustomSlidingDrawer(Context context, AttributeSet attrs) {

        super(context, attrs);

        int orientation = attrs.getAttributeIntValue("android", "orientation",
                ORIENTATION_VERTICAL);
        this.mTopOffset = attrs.getAttributeIntValue("android", "topOffset", 0);
        this.mVertical = (orientation == SlidingDrawer.ORIENTATION_VERTICAL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthSpecMode == MeasureSpec.UNSPECIFIED
                || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            throw new RuntimeException(
                    "SlidingDrawer cannot have UNSPECIFIED dimensions");
        }

        final View handle = getHandle();
        final View content = getContent();
        measureChild(handle, widthMeasureSpec, heightMeasureSpec);

        if (this.mVertical) {
            int height = heightSpecSize - handle.getMeasuredHeight()
                    - this.mTopOffset;
            content.measure(widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(height, heightSpecMode));
            heightSpecSize = handle.getMeasuredHeight() + this.mTopOffset
                    + content.getMeasuredHeight();
            widthSpecSize = content.getMeasuredWidth();
            if (handle.getMeasuredWidth() > widthSpecSize) widthSpecSize = handle
                    .getMeasuredWidth();
        } else {
            int width = widthSpecSize - handle.getMeasuredWidth()
                    - this.mTopOffset;
            getContent().measure(
                    MeasureSpec.makeMeasureSpec(width, widthSpecMode),
                    heightMeasureSpec);
            widthSpecSize = handle.getMeasuredWidth() + this.mTopOffset
                    + content.getMeasuredWidth();
            heightSpecSize = content.getMeasuredHeight();
            if (handle.getMeasuredHeight() > heightSpecSize) heightSpecSize = handle
                    .getMeasuredHeight();
        }

        setMeasuredDimension(widthSpecSize, heightSpecSize);
    }

    private boolean mVertical;
    private int mTopOffset;
}