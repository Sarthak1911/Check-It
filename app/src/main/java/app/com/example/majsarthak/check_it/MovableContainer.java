package app.com.example.majsarthak.check_it;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.widget.TableRow;

@CoordinatorLayout.DefaultBehavior(MoveUp.class)

public class MovableContainer extends TableRow
{
    public MovableContainer(Context context)
    {
        super(context);
    }

    public MovableContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

}
