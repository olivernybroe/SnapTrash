package dk.snaptrash.snaptrash.Utils;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class UnderlineTextView extends android.support.v7.widget.AppCompatTextView {

    public UnderlineTextView(Context context) {
        super(context);
        this.setPaintFlags(this.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    public UnderlineTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setPaintFlags(this.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    public UnderlineTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setPaintFlags(this.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }
}
