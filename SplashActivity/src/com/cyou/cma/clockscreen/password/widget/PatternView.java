/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cyou.cma.clockscreen.password.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Debug;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.cynad.cma.locker.R;
import com.cyou.cma.clockscreen.util.LockPatternUtils;
import com.cyou.cma.clockscreen.util.Util;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.TypeEvaluator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

/**
 * Displays and detects the user's unlock attempt, which is a drag of a finger
 * across 9 regions of the screen.
 * Is also capable of displaying a static pattern in "in progress", "wrong" or
 * "correct" states.
 */
public class PatternView extends View {
    // Aspect to use when rendering this view
    private static final int ASPECT_SQUARE = 0; // View will be the minimum of width/height
    private static final int ASPECT_LOCK_WIDTH = 1; // Fixed width; height will be minimum of (w,h)
    private static final int ASPECT_LOCK_HEIGHT = 2; // Fixed height; width will be minimum of (w,h)

    // Vibrator pattern for creating a tactile bump
    private static final long[] DEFAULT_VIBE_PATTERN = {
            0, 1, 40, 41
    };

    private static final boolean PROFILE_DRAWING = false;
    private boolean mDrawingProfilingStarted = false;

    private Paint mPaint = new Paint();
    private Paint mPathPaint = new Paint();

    /**
     * How many milliseconds we spend animating each circle of a lock pattern
     * if the animating mode is set. The entire animation should take this
     * constant * the length of the pattern to complete.
     */
    private static final int MILLIS_PER_CIRCLE_ANIMATING = 700;

    private OnPatternListener mOnPatternListener;
    private ArrayList<Cell> mPattern = new ArrayList<Cell>(9);

    /**
     * Lookup table for the circles of the pattern we are currently drawing.
     * This will be the cells of the complete pattern unless we are animating,
     * in which case we use this to hold the cells we are drawing for the in
     * progress animation.
     */
    private boolean[][] mPatternDrawLookup = new boolean[3][3];

    /**
     * the in progress point:
     * - during interaction: where the user's finger is
     * - during animation: the current tip of the animating line
     */
    private float mInProgressX = -1;
    private float mInProgressY = -1;

    private long mAnimatingPeriodStart;

    private DisplayMode mPatternDisplayMode = DisplayMode.Wrong;
    private boolean mInputEnabled = true;
    private boolean mInStealthMode = false;
    private boolean mTactileFeedbackEnabled = true;
    private boolean mPatternInProgress = false;

// private float mDiameterFactor = 0.5f;
// private float mHitFactor = 0.6f;

    private float mDiameterFactor = 0.5f;
    private float mHitFactor = 0.6f;
    private float mSquareWidth;
    private float mSquareHeight;

    private Bitmap mBitmapCircleDefault;
    private Bitmap mBitmapCircleGreen;
    private Bitmap mBitmapCircleRed;

    private final Path mCurrentPath = new Path();
    private final Rect mInvalidate = new Rect();

    private int mBitmapWidth;
    private int mBitmapHeight;

    private Vibrator vibe; // Vibrator for creating tactile feedback

    private int mAspect;
    private boolean mAnimate;

    /**
     * Represents a cell in the 3 X 3 matrix of the unlock pattern view.
     */
    public static class Cell {
        int row;
        int column;
        float centerX;
        float centerY;
        public String tag;
        ObjectAnimator mAnimator;
        float scale;

        // keep # objects limited to 9
        static Cell[][] sCells = new Cell[3][3];
        static float[][] offsetRato = {
                {
                        2, 2, 2
                }, {
                        3, 4, 5
                }, {
                        4, 5, 6
                }
        };
        static float[][] startTimes = {
                {
                        0, 0, 0
                }, {
                        0, 1, 2
                }, {
                        1, 2, 3
                }
        };

        static float[][] scales = {
                {
                        0.1f, 0.06f, 0.04f
                }, {
                        0.05f, 0.1f, 0.2f
                }, {
                        0.1f, 0.2f, 0.4f
                }
        };

// static int[][] alphas = {
// {
// (int) (255 * 0.8f), (int) (255 * 0.8f), (int) (255 * 0.8f)
// }, {
// (int) (255 * 0.7f), (int) (255 * 0.7f), (int) (255 * 0.7f)
// }, {
// (int) (255 * 0.7f), (int) (255 * 0.7f), (int) (255 * 0.7f)
// }
// };
        static {
            Util.Logjb("jiangbin", "jiangbin cell init");
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    sCells[i][j] = new Cell(i, j);
                    sCells[i][j].tag = "" + (i * 3 + (j + 1));
                }
            }
        }

// static float[] offset = {
// 0.05f, 0.1f, 0.2f
// };

        static float[][] haha = {
                {
                        7.0f / 53.f, 9.f / 53.f, 11.f / 53.f
                }, {
                        18.f / 53.f, 21.f / 53.f, 34.f / 53.f
                }, {
                        27.f / 53.f, 33.f / 53.f, 47.f / 53.f
                }
        };

        /**
         * @param row The row of the cell.
         * @param column The column of the cell.
         */
        private Cell(int row, int column) {
            checkRange(row, column);
            this.row = row;
            this.column = column;
            this.scale = scales[row][column];
        }

        public int getRow() {
            return row;
        }

        public int getColumn() {
            return column;
        }

        public static final long DURATION = 500;

        public static void setMeasure(float width, float height, final PatternView patternView) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    sCells[i][j].centerX = j * width + width / 2f;
                    sCells[i][j].centerY = i * height + height / 2f;

                    if (patternView.mAnimate) {
                        Cell endCell = new Cell(i, j);
                        endCell.centerX = sCells[i][j].centerX;
                        endCell.centerY = sCells[i][j].centerY;
                        endCell.scale = 1.0f;
                        Cell startCell = new Cell(i, j);
                        startCell.centerX = sCells[i][j].centerX;
                        startCell.centerY = sCells[i][j].centerY + haha[i][j] * height;
                        ObjectAnimator objectAnimator = ObjectAnimator.ofObject(sCells[i][j], "animator",
                                new MyEvaluator(i, j, startTimes[i][j], offsetRato[i][j] - startTimes[i][j]),
                                startCell,
                                endCell);
                        objectAnimator.setDuration(DURATION);
                        objectAnimator.start();
                        objectAnimator.addUpdateListener(new AnimatorUpdateListener() {

                            @Override
                            public void onAnimationUpdate(ValueAnimator arg0) {

                                patternView.invalidate();
                            }
                        });
                        sCells[i][j].mAnimator = objectAnimator;
                    }
                }
            }
        }

        static class MyEvaluator implements TypeEvaluator<Cell> {
            Cell cell;
            float startTime;
            float costTime;
            float duration = 6;

            public MyEvaluator(int i, int j, float startTime, float costTime) {
                cell = new Cell(i, j);
                this.startTime = startTime;
                this.costTime = costTime;
            }

            @Override
            public Cell evaluate(float frator, Cell startValue, Cell endValue) {

                if (frator < 1.0f * startTime / duration) {
                    startValue.scale = 0;
                    return startValue;
                } else if (frator <= (startTime + costTime) / duration) {
                    float newFrator = (frator * duration - startTime) / costTime;

                    cell.centerX = startValue.centerX;
                    cell.centerY = startValue.centerY + newFrator * (endValue.centerY - startValue.centerY);
// cell.alpha = startValue.alpha + (int) (newFrator * (endValue.alpha - startValue.alpha));
                    cell.scale = startValue.scale + newFrator * (endValue.scale - startValue.scale);
                    return cell;
                } else {
                    return endValue;
                }

            }

        }

        public void setAnimator(Cell cell) {
            this.centerY = cell.centerY;
// this.alpha = cell.alpha;
            this.scale = cell.scale;
        }

        /**
         * @param row The row of the cell.
         * @param column The column of the cell.
         */
        public static synchronized Cell of(int row, int column) {
            checkRange(row, column);
            return sCells[row][column];
        }

        private static void checkRange(int row, int column) {
            if (row < 0 || row > 2) {
                throw new IllegalArgumentException("row must be in range 0-2");
            }
            if (column < 0 || column > 2) {
                throw new IllegalArgumentException("column must be in range 0-2");
            }
        }

        public String toString() {
            return "(row=" + row + ",clmn=" + column + ")";
        }
    }

    /**
     * How to display the current pattern.
     */
    public enum DisplayMode {

        /**
         * The pattern drawn is correct (i.e draw it in a friendly color)
         */
        Correct,

        /**
         * Animate the pattern (for demo, and help).
         */
        Animate,

        /**
         * The pattern is wrong (i.e draw a foreboding color)
         */
        Wrong
    }

    /**
     * The call back interface for detecting patterns entered by the user.
     */
    public static interface OnPatternListener {

        /**
         * A new pattern has begun.
         */
        void onPatternStart();

        /**
         * The pattern was cleared.
         */
        void onPatternCleared();

        /**
         * The user extended the pattern currently being drawn by one cell.
         * 
         * @param pattern The pattern with newly added cell.
         */
        void onPatternCellAdded(List<Cell> pattern);

        /**
         * A pattern was detected from the user.
         * 
         * @param pattern The pattern.
         */
        void onPatternDetected(List<Cell> pattern);
    }

    public PatternView(Context context) {
        this(context, null);
    }

    public PatternView(Context context, AttributeSet attrs) {
        super(context, attrs);
        vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.lockpatternview);
// final String aspect = "square";
        final String aspect = a.getString(R.styleable.lockpatternview_aspect);
        mAnimate = a.getBoolean(R.styleable.lockpatternview_animate, false);
        Util.Logjb("PatternView", "PatternView aspect--->" + aspect + " mAnimate-->" + mAnimate);

        if ("square".equals(aspect)) {
            mAspect = ASPECT_SQUARE;
        } else if ("lock_width".equals(aspect)) {
            mAspect = ASPECT_LOCK_WIDTH;
        } else if ("lock_height".equals(aspect)) {
            mAspect = ASPECT_LOCK_HEIGHT;
        } else {
            mAspect = ASPECT_SQUARE;
        }

        setClickable(true);
        int color = a.getColor(R.styleable.lockpatternview_paintcolor, Color.WHITE
                );
        mPathPaint.setAntiAlias(true);
        mPathPaint.setDither(true);
        mPathPaint.setColor(color); // TODO this should be from the style
        mPathPaint.setAlpha(153);// 60%透明
        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setStrokeJoin(Paint.Join.ROUND);
        mPathPaint.setStrokeCap(Paint.Cap.ROUND);

        // lot's of bitmaps!
// mBitmapBtnDefault = getBitmapFor(R.drawable.pattern_btn_code_lock_default);
// mBitmapBtnTouched = getBitmapFor(R.drawable.pattern_btn_code_lock_touched);
        int btn_normal_drawable = a.getResourceId(R.styleable.lockpatternview_normaldrawable
                , 0);
// a.getResourceId(index, defValue)
// a.getd
        int btn_pressed_drawable = a.getResourceId(R.styleable.lockpatternview_pressdrawable, 0);
        mBitmapCircleDefault = getBitmapFor(btn_normal_drawable);
        mBitmapCircleGreen = getBitmapFor(btn_pressed_drawable);
        mBitmapCircleRed = getBitmapFor(R.drawable.btn_code_lock_wrong);

        // we assume all bitmaps have the same size
        mBitmapWidth = mBitmapCircleDefault.getWidth();
        mBitmapHeight = mBitmapCircleDefault.getHeight();
        a.recycle();
        // allow vibration pattern to be customized
// mVibePattern = loadVibratePattern(com.android.internal.R.array.config_virtualKeyVibePattern);
//    setBackgroundColor(Color.RED);
    }

    private Bitmap getBitmapFor(int resId) {
        return BitmapFactory.decodeResource(getContext().getResources(), resId);
    }

    /**
     * @return Whether the view is in stealth mode.
     */
    public boolean isInStealthMode() {
        return mInStealthMode;
    }

    /**
     * @return Whether the view has tactile feedback enabled.
     */
    public boolean isTactileFeedbackEnabled() {
        return mTactileFeedbackEnabled;
    }

    /**
     * Set whether the view is in stealth mode. If true, there will be no
     * visible feedback as the user enters the pattern.
     * 
     * @param inStealthMode Whether in stealth mode.
     */
    public void setInStealthMode(boolean inStealthMode) {
        mInStealthMode = inStealthMode;
    }

    /**
     * Set whether the view will use tactile feedback. If true, there will be
     * tactile feedback as the user enters the pattern.
     * 
     * @param tactileFeedbackEnabled Whether tactile feedback is enabled
     */
    public void setTactileFeedbackEnabled(boolean tactileFeedbackEnabled) {
        mTactileFeedbackEnabled = tactileFeedbackEnabled;
    }

    /**
     * Set the call back for pattern detection.
     * 
     * @param onPatternListener The call back.
     */
    public void setOnPatternListener(
            OnPatternListener onPatternListener) {
        mOnPatternListener = onPatternListener;
    }

    /**
     * Set the pattern explicitely (rather than waiting for the user to input
     * a pattern).
     * 
     * @param displayMode How to display the pattern.
     * @param pattern The pattern.
     */
    public void setPattern(DisplayMode displayMode, List<Cell> pattern) {
        mPattern.clear();
        mPattern.addAll(pattern);
        clearPatternDrawLookup();
        for (Cell cell : pattern) {
            mPatternDrawLookup[cell.getRow()][cell.getColumn()] = true;
        }

        setDisplayMode(displayMode);
    }

    /**
     * Set the display mode of the current pattern. This can be useful, for
     * instance, after detecting a pattern to tell this view whether change the
     * in progress result to correct or wrong.
     * 
     * @param displayMode The display mode.
     */
    public void setDisplayMode(DisplayMode displayMode) {
        mPatternDisplayMode = displayMode;
        if (displayMode == DisplayMode.Animate) {
            if (mPattern.size() == 0) {
                throw new IllegalStateException("you must have a pattern to "
                        + "animate if you want to set the display mode to animate");
            }
            mAnimatingPeriodStart = SystemClock.elapsedRealtime();
            final Cell first = mPattern.get(0);
// mInProgressX = getCenterXForColumn(first.getColumn());
// mInProgressY = getCenterYForRow(first.getRow(), first.getColumn());
            mInProgressX = first.centerX;
            mInProgressY = first.centerY;
            clearPatternDrawLookup();
        }
        invalidate();
    }

    /**
     * Clear the pattern.
     */
    public void clearPattern() {
        if(!mPatternInProgress)
        resetPattern();
    }

    /**
     * Reset all pattern state.
     */
    private void resetPattern() {
        mPattern.clear();
        clearPatternDrawLookup();
        mPatternDisplayMode = DisplayMode.Correct;
        invalidate();
    }

    /**
     * Clear the pattern lookup table.
     */
    private void clearPatternDrawLookup() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                mPatternDrawLookup[i][j] = false;
            }
        }
    }

    /**
     * Disable input (for instance when displaying a message that will
     * timeout so user doesn't get view into messy state).
     */
    public void disableInput() {
        mInputEnabled = false;
    }

    /**
     * Enable input.
     */
    public void enableInput() {
        mInputEnabled = true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        final int width = w;
        mSquareWidth = width / 3.0f;

        final int height = h;
        mSquareHeight = height / 3.0f;
        Util.Logjb("jiangbin", "jiangbin cell onSizeChanged");
        Cell.setMeasure(mSquareWidth, mSquareHeight, this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int viewWidth = (int) (3 * ( 63* getResources().getDisplayMetrics().density + getResources()
                .getDimension(R.dimen.pattern_margin)));
        setMeasuredDimension(viewWidth, viewWidth);

    }

    /**
     * Determines whether the point x, y will add a new point to the current
     * pattern (in addition to finding the cell, also makes heuristic choices
     * such as filling in gaps based on current pattern).
     * 
     * @param x The x coordinate.
     * @param y The y coordinate.
     */
    private Cell detectAndAddHit(float x, float y) {
        final Cell cell = checkForNewHit(x, y);
        if (cell != null) {

            // check for gaps in existing pattern
            Cell fillInGapCell = null;
            final ArrayList<Cell> pattern = mPattern;
            if (!pattern.isEmpty()) {
                final Cell lastCell = pattern.get(pattern.size() - 1);
                int dRow = cell.row - lastCell.row;
                int dColumn = cell.column - lastCell.column;

                int fillInRow = lastCell.row;
                int fillInColumn = lastCell.column;

                if (Math.abs(dRow) == 2 && Math.abs(dColumn) != 1) {
                    fillInRow = lastCell.row + ((dRow > 0) ? 1 : -1);
                }

                if (Math.abs(dColumn) == 2 && Math.abs(dRow) != 1) {
                    fillInColumn = lastCell.column + ((dColumn > 0) ? 1 : -1);
                }

                fillInGapCell = Cell.of(fillInRow, fillInColumn);
            }

            if (fillInGapCell != null &&
                    !mPatternDrawLookup[fillInGapCell.row][fillInGapCell.column]) {
                addCellToPattern(fillInGapCell);
            }
            addCellToPattern(cell);
            if (mTactileFeedbackEnabled) {
                vibe.vibrate(DEFAULT_VIBE_PATTERN, -1); // Generate tactile feedback
// vibe.vibrate(100);
            }
            return cell;
        }
        return null;
    }

    private void addCellToPattern(Cell newCell) {
        mPatternDrawLookup[newCell.getRow()][newCell.getColumn()] = true;
        mPattern.add(newCell);
        if (mOnPatternListener != null) {
            mOnPatternListener.onPatternCellAdded(mPattern);
        }
    }

    // helper method to find which cell a point maps to
    private Cell checkForNewHit(float x, float y) {

        final int rowHit = getRowHit(y);
        if (rowHit < 0) {
            return null;
        }
        final int columnHit = getColumnHit(x);
        if (columnHit < 0) {
            return null;
        }

        if (mPatternDrawLookup[rowHit][columnHit]) {
            return null;
        }
        return Cell.of(rowHit, columnHit);
    }

    /**
     * Helper method to find the row that y falls into.
     * 
     * @param y The y coordinate
     * @return The row that y falls in, or -1 if it falls in no row.
     */
    private int getRowHit(float y) {

        final float squareHeight = mSquareHeight;
        float hitSize = squareHeight * mHitFactor;

        float offset = (squareHeight - hitSize) / 2f;
        for (int i = 0; i < 3; i++) {

            final float hitTop = offset + squareHeight * i;
            if (y >= hitTop && y <= hitTop + hitSize) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Helper method to find the column x fallis into.
     * 
     * @param x The x coordinate.
     * @return The column that x falls in, or -1 if it falls in no column.
     */
    private int getColumnHit(float x) {
        final float squareWidth = mSquareWidth;
        float hitSize = squareWidth * mHitFactor;

        float offset = (squareWidth - hitSize) / 2f;
        for (int i = 0; i < 3; i++) {

            final float hitLeft = offset + squareWidth * i;
            if (x >= hitLeft && x <= hitLeft + hitSize) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!mInputEnabled || !isEnabled()) {
            return false;
        }

        final float x = motionEvent.getX();
        final float y = motionEvent.getY();
        Cell hitCell;
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                resetPattern();
                hitCell = detectAndAddHit(x, y);
                if (hitCell != null && mOnPatternListener != null) {
                    mPatternInProgress = true;
                    mPatternDisplayMode = DisplayMode.Correct;
                    mOnPatternListener.onPatternStart();
                } else if (mOnPatternListener != null) {
                    mPatternInProgress = false;
                    mOnPatternListener.onPatternCleared();
                }
                if (hitCell != null) {
// final float startX = getCenterXForColumn(hitCell.column);
// final float startY = getCenterYForRow(hitCell.row, hitCell.column);
                    final float startX = hitCell.centerX;
                    final float startY = hitCell.centerY;
                    final float widthOffset = mSquareWidth / 2f;
                    final float heightOffset = mSquareHeight / 2f;

                    invalidate((int) (startX - widthOffset), (int) (startY - heightOffset),
                            (int) (startX + widthOffset), (int) (startY + heightOffset));
                }
                mInProgressX = x;
                mInProgressY = y;
                if (PROFILE_DRAWING) {
                    if (!mDrawingProfilingStarted) {
                        Debug.startMethodTracing("LockPatternDrawing");
                        mDrawingProfilingStarted = true;
                    }
                }
                return true;
            case MotionEvent.ACTION_UP:
                // report pattern detected
                if (!mPattern.isEmpty() && mOnPatternListener != null) {
                    mPatternInProgress = false;
                    mOnPatternListener.onPatternDetected(mPattern);
                    invalidate();
                }
                if (PROFILE_DRAWING) {
                    if (mDrawingProfilingStarted) {
                        Debug.stopMethodTracing();
                        mDrawingProfilingStarted = false;
                    }
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                final int patternSizePreHitDetect = mPattern.size();
                hitCell = detectAndAddHit(x, y);
                final int patternSize = mPattern.size();
                if (hitCell != null && (mOnPatternListener != null) && (patternSize == 1)) {
                    mPatternInProgress = true;
                    mOnPatternListener.onPatternStart();
                }
                // note current x and y for rubber banding of in progress
                // patterns
                final float dx = Math.abs(x - mInProgressX);
                final float dy = Math.abs(y - mInProgressY);
                if (dx + dy > mSquareWidth * 0.01f) {
                    float oldX = mInProgressX;
                    float oldY = mInProgressY;

                    mInProgressX = x;
                    mInProgressY = y;

                    if (mPatternInProgress && patternSize > 0) {
                        final ArrayList<Cell> pattern = mPattern;
                        final float radius = mSquareWidth * mDiameterFactor * 0.5f;

                        final Cell lastCell = pattern.get(patternSize - 1);

// float startX = getCenterXForColumn(lastCell.column);
// float startY = getCenterYForRow(lastCell.row, lastCell.column);
                        float startX = lastCell.centerX;
                        float startY = lastCell.centerY;
                        float left;
                        float top;
                        float right;
                        float bottom;

                        final Rect invalidateRect = mInvalidate;

                        if (startX < x) {
                            left = startX;
                            right = x;
                        } else {
                            left = x;
                            right = startX;
                        }

                        if (startY < y) {
                            top = startY;
                            bottom = y;
                        } else {
                            top = y;
                            bottom = startY;
                        }

                        // Invalidate between the pattern's last cell and the current location
                        invalidateRect.set((int) (left - radius), (int) (top - radius),
                                (int) (right + radius), (int) (bottom + radius));

                        if (startX < oldX) {
                            left = startX;
                            right = oldX;
                        } else {
                            left = oldX;
                            right = startX;
                        }

                        if (startY < oldY) {
                            top = startY;
                            bottom = oldY;
                        } else {
                            top = oldY;
                            bottom = startY;
                        }

                        // Invalidate between the pattern's last cell and the previous location
                        invalidateRect.union((int) (left - radius), (int) (top - radius),
                                (int) (right + radius), (int) (bottom + radius));

                        // Invalidate between the pattern's new cell and the pattern's previous cell
                        if (hitCell != null) {
                            startX = hitCell.centerX;
                            startY = hitCell.centerY;
                            if (patternSize >= 2) {
                                // (re-using hitcell for old cell)
                                hitCell = pattern.get(patternSize - 1
                                        - (patternSize - patternSizePreHitDetect));
                                oldX = hitCell.centerX;
                                oldY = hitCell.centerY;
                                if (startX < oldX) {
                                    left = startX;
                                    right = oldX;
                                } else {
                                    left = oldX;
                                    right = startX;
                                }

                                if (startY < oldY) {
                                    top = startY;
                                    bottom = oldY;
                                } else {
                                    top = oldY;
                                    bottom = startY;
                                }
                            } else {
                                left = right = startX;
                                top = bottom = startY;
                            }

                            final float widthOffset = mSquareWidth / 2f;
                            final float heightOffset = mSquareHeight / 2f;

                            invalidateRect.set((int) (left - widthOffset),
                                    (int) (top - heightOffset), (int) (right + widthOffset),
                                    (int) (bottom + heightOffset));
                        }

                        invalidate(invalidateRect);
                    } else {
                        invalidate();
                    }
                }
                return true;
            case MotionEvent.ACTION_CANCEL:
                resetPattern();
                if (mOnPatternListener != null) {
                    mPatternInProgress = false;
                    mOnPatternListener.onPatternCleared();
                }
                if (PROFILE_DRAWING) {
                    if (mDrawingProfilingStarted) {
                        Debug.stopMethodTracing();
                        mDrawingProfilingStarted = false;
                    }
                }
                return true;
        }
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final ArrayList<Cell> pattern = mPattern;
        final int count = pattern.size();
        final boolean[][] drawLookup = mPatternDrawLookup;

        if (mPatternDisplayMode == DisplayMode.Animate) {

            // figure out which circles to draw

            // + 1 so we pause on complete pattern
            final int oneCycle = (count + 1) * MILLIS_PER_CIRCLE_ANIMATING;
            final int spotInCycle = (int) (SystemClock.elapsedRealtime() -
                    mAnimatingPeriodStart) % oneCycle;
            final int numCircles = spotInCycle / MILLIS_PER_CIRCLE_ANIMATING;

            clearPatternDrawLookup();
            for (int i = 0; i < numCircles; i++) {
                final Cell cell = pattern.get(i);
                drawLookup[cell.getRow()][cell.getColumn()] = true;
            }

            // figure out in progress portion of ghosting line

            final boolean needToUpdateInProgressPoint = numCircles > 0
                    && numCircles < count;

            if (needToUpdateInProgressPoint) {
                final float percentageOfNextCircle =
                        ((float) (spotInCycle % MILLIS_PER_CIRCLE_ANIMATING)) /
                                MILLIS_PER_CIRCLE_ANIMATING;

                final Cell currentCell = pattern.get(numCircles - 1);
// final float centerX = getCenterXForColumn(currentCell.column);
// final float centerY = getCenterYForRow(currentCell.row, currentCell.column);
                final float centerX = currentCell.centerX;
                final float centerY = currentCell.centerY;
                final Cell nextCell = pattern.get(numCircles);
                final float dx = percentageOfNextCircle *
                        (nextCell.centerX - centerX);
                final float dy = percentageOfNextCircle *
                        (nextCell.centerY - centerY);
                mInProgressX = centerX + dx;
                mInProgressY = centerY + dy;
            }
            invalidate();
        }

// float radius = (squareWidth * mDiameterFactor * 0.5f);
        mPathPaint.setStrokeWidth(4.0f * getResources().getDisplayMetrics().density);

        final Path currentPath = mCurrentPath;
        currentPath.rewind();

        // TODO: the path should be created and cached every time we hit-detect a cell
        // only the last segment of the path should be computed here
        // draw the path of the pattern (unless the user is in progress, and
        // we are in stealth mode)
        final boolean drawPath = (!mInStealthMode || mPatternDisplayMode == DisplayMode.Wrong);
        if (drawPath) {
            boolean anyCircles = false;
            for (int i = 0; i < count; i++) {
                Cell cell = pattern.get(i);

                // only draw the part of the pattern stored in
                // the lookup table (this is only different in the case
                // of animation).
                if (!drawLookup[cell.row][cell.column]) {
                    break;
                }
                anyCircles = true;

                float centerX = cell.centerX;
                float centerY = cell.centerY;
                if (i == 0) {
                    currentPath.moveTo(centerX, centerY);
                } else {
                    currentPath.lineTo(centerX, centerY);
                }
            }

            // add last in progress section
            if ((mPatternInProgress || mPatternDisplayMode == DisplayMode.Animate)
                    && anyCircles) {
                currentPath.lineTo(mInProgressX, mInProgressY);
            }
            canvas.drawPath(currentPath, mPathPaint);
        }

        // draw the circles

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Cell cell = Cell.of(i, j);
                drawCircle(canvas, cell, drawLookup[i][j]);
            }
        }

        // draw the arrows associated with the path (unless the user is in progress, and
        // we are in stealth mode)
        boolean oldFlag = (mPaint.getFlags() & Paint.FILTER_BITMAP_FLAG) != 0;
// mPaint.setFilterBitmap(filter)
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true); // draw with higher quality since we render with transforms
        if (drawPath) {
            for (int i = 0; i < count - 1; i++) {
                Cell cell = pattern.get(i);
                Cell next = pattern.get(i + 1);

                // only draw the part of the pattern stored in
                // the lookup table (this is only different in the case
                // of animation).
                if (!drawLookup[next.row][next.column]) {
                    break;
                }

            }
        }
        mPaint.setFilterBitmap(oldFlag); // restore default flag
    }

    /**
     * @param canvas
     * @param leftX
     * @param topY
     * @param partOfPattern Whether this circle is part of the pattern.
     */
    private void drawCircle(Canvas canvas, Cell cell, boolean partOfPattern) {
        Bitmap outerCircle;
// Bitmap innerCircle;

        if (!partOfPattern || (mInStealthMode && mPatternDisplayMode != DisplayMode.Wrong)) {
            // unselected circle
            outerCircle = mBitmapCircleDefault;
// innerCircle = mBitmapBtnDefault;
        } else if (mPatternInProgress) {
            // user is in middle of drawing a pattern
            outerCircle = mBitmapCircleGreen;
// innerCircle = mBitmapBtnTouched;
        } else if (mPatternDisplayMode == DisplayMode.Wrong) {
            // the pattern is wrong
            outerCircle = mBitmapCircleRed;
// innerCircle = mBitmapBtnDefault;
        } else if (mPatternDisplayMode == DisplayMode.Correct ||
                mPatternDisplayMode == DisplayMode.Animate) {
            // the pattern is correct
            outerCircle = mBitmapCircleGreen;
// innerCircle = mBitmapBtnDefault;
        } else {
            throw new IllegalStateException("unknown display mode " + mPatternDisplayMode);
        }

        int bitmapWidth = outerCircle.getWidth();
        int bitmapHeight = outerCircle.getHeight();
        if (mAnimate) {
            Matrix matrix = new Matrix();
            matrix.setTranslate(cell.centerX - bitmapWidth * cell.scale / 2, cell.centerY - bitmapHeight
                    * cell.scale / 2);
            matrix.preScale(cell.scale, cell.scale);
            canvas.drawBitmap(outerCircle, matrix, mPaint);
        } else {
            canvas.drawBitmap(outerCircle, cell.centerX - bitmapWidth / 2, cell.centerY - bitmapHeight / 2,
                    mPaint);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState,
                LockPatternUtils.patternToString(mPattern),
                mPatternDisplayMode.ordinal(),
                mInputEnabled, mInStealthMode, mTactileFeedbackEnabled, mAnimate);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        final SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setPattern(
                DisplayMode.Correct,
                LockPatternUtils.stringToPattern(ss.getSerializedPattern()));
        mPatternDisplayMode = DisplayMode.values()[ss.getDisplayMode()];
        mInputEnabled = ss.isInputEnabled();
        mInStealthMode = ss.isInStealthMode();
        mTactileFeedbackEnabled = ss.isTactileFeedbackEnabled();
        mAnimate = ss.isAnimate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Util.Logjb("PatternView", "PatternView onAttachedToWindow");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Util.Logjb("PatternView", "PatternView onDetachedFromWindow");
    }

    /**
     * The parecelable for saving and restoring a lock pattern view.
     */
    private static class SavedState extends BaseSavedState {

        private final String mSerializedPattern;
        private final int mDisplayMode;
        private final boolean mInputEnabled;
        private final boolean mInStealthMode;
        private final boolean mTactileFeedbackEnabled;
        private final boolean mAnimate;

        /**
         * Constructor called from {@link PatternView#onSaveInstanceState()}
         */
        private SavedState(Parcelable superState, String serializedPattern, int displayMode,
                boolean inputEnabled, boolean inStealthMode, boolean tactileFeedbackEnabled, boolean animate) {
            super(superState);
            mSerializedPattern = serializedPattern;
            mDisplayMode = displayMode;
            mInputEnabled = inputEnabled;
            mInStealthMode = inStealthMode;
            mTactileFeedbackEnabled = tactileFeedbackEnabled;
            mAnimate = animate;
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            mSerializedPattern = in.readString();
            mDisplayMode = in.readInt();
            mInputEnabled = (Boolean) in.readValue(null);
            mInStealthMode = (Boolean) in.readValue(null);
            mTactileFeedbackEnabled = (Boolean) in.readValue(null);
            mAnimate = (Boolean) in.readValue(null);
// in.readb
        }

        public boolean isAnimate() {
            return mAnimate;
        }

        public String getSerializedPattern() {
            return mSerializedPattern;
        }

        public int getDisplayMode() {
            return mDisplayMode;
        }

        public boolean isInputEnabled() {
            return mInputEnabled;
        }

        public boolean isInStealthMode() {
            return mInStealthMode;
        }

        public boolean isTactileFeedbackEnabled() {
            return mTactileFeedbackEnabled;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(mSerializedPattern);
            dest.writeInt(mDisplayMode);
            dest.writeValue(mInputEnabled);
            dest.writeValue(mInStealthMode);
            dest.writeValue(mTactileFeedbackEnabled);
            dest.writeValue(mAnimate);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    public void startAnimator() {
        try {

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    Cell.sCells[i][j].mAnimator.start();
                }
            }
        } catch (Exception e) {
        }
    }

// @Override
// public void setVisibility(int visibility) {
// super.setVisibility(visibility);
// Util.Logjb("PatternView", "PatternView setVisibility");
//
// if (visibility == View.VISIBLE) {
// startAnimator();
// }
// }
}
