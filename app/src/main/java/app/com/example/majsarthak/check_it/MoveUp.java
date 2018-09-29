package app.com.example.majsarthak.check_it;

import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;


public class MoveUp extends CoordinatorLayout.Behavior<View>
{
    private static boolean  SNACKBAR_BEHAVIOUR_ENABLED;

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        
        return SNACKBAR_BEHAVIOUR_ENABLED && dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {

        float translationY = Math.min(0, dependency.getTranslationY() - dependency.getHeight());

        child.setTranslationY(translationY);

        return true;
    }

    static
    {
        SNACKBAR_BEHAVIOUR_ENABLED = Build.VERSION.SDK_INT >= 11;
    }

}

