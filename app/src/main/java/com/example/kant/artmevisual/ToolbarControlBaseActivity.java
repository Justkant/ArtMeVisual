package com.example.kant.artmevisual;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by Quentin on 27/02/2015.
 * EpiAndroid Project.
 */
public abstract class ToolbarControlBaseActivity<S extends Scrollable> extends BaseActivity implements ObservableScrollViewCallbacks {

    private FloatingActionButton mFab;
    private Toolbar mToolbar;
    private View mContainer;
    private S mScrollable;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mToolbar = getActionBarToolbar();
        mFab = (FloatingActionButton) findViewById(R.id.normal_plus);
        mScrollable = createScrollable();
        mContainer = createContainer();
        mScrollable.setScrollViewCallbacks(this);
    }

    protected abstract View createContainer();

    protected abstract S createScrollable();

    @Override
    public void onScrollChanged(int i, boolean b, boolean b2) {
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        if (scrollState == ScrollState.UP) {
            if (toolbarIsShown()) {
                hideToolbar();
            }
            if (mFab != null && fabIsShown()) {
                hideFab();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (toolbarIsHidden()) {
                showToolbar();
            }
            if (mFab != null && fabIsHidden()) {
                showFab();
            }
        }
    }

    private boolean toolbarIsShown() {
        return ViewHelper.getTranslationY(mToolbar) == 0;
    }

    private boolean toolbarIsHidden() {
        return ViewHelper.getTranslationY(mToolbar) == -mToolbar.getHeight();
    }

    private void showToolbar() {
        moveToolbar(0);
    }

    private void hideToolbar() {
        moveToolbar(-mToolbar.getHeight());
    }

    private boolean fabIsShown() {
        return ViewHelper.getTranslationY(mFab) == 0;
    }

    private boolean fabIsHidden() {
        return ViewHelper.getTranslationY(mFab) == mFab.getHeight() + ((FrameLayout.LayoutParams) mFab.getLayoutParams()).bottomMargin;
    }

    private void showFab() {
        moveFab(0);
    }

    private void hideFab() {
        moveFab(mFab.getHeight() + ((FrameLayout.LayoutParams) mFab.getLayoutParams()).bottomMargin);
    }

    private void moveToolbar(float toTranslationY) {
        if (ViewHelper.getTranslationY(mToolbar) == toTranslationY) {
            return;
        }
        ValueAnimator animator = ValueAnimator.ofFloat(ViewHelper.getTranslationY(mToolbar), toTranslationY).setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float translationY = (float) animation.getAnimatedValue();
                ViewHelper.setTranslationY(mToolbar, translationY);
                ViewHelper.setTranslationY(mContainer, translationY);
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mContainer.getLayoutParams();
                lp.height = (int) -translationY + getScreenHeight() - lp.topMargin;
                mContainer.requestLayout();
            }
        });
        animator.start();
    }

    private void moveFab(float toTranslationY) {
        if (ViewHelper.getTranslationY(mFab) == toTranslationY) {
            return;
        }
        ValueAnimator animator = ValueAnimator.ofFloat(ViewHelper.getTranslationY(mFab), toTranslationY).setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float translationY = (float) animation.getAnimatedValue();
                ViewHelper.setTranslationY(mFab, translationY);
            }
        });
        animator.start();
    }
}
