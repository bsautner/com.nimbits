package com.nimbits.android.surface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.view.*;
import com.nimbits.cloudplatform.Nimbits;

/**
 * Author: Benjamin Sautner
 * Date: 2/26/13
 * Time: 7:47 PM
 */
public class MainSurfaceView extends SurfaceView implements SurfaceHolder.Callback, GestureDetector.OnGestureListener {

    private View mView;
    private final SurfaceListener listener;
    private static GestureDetector gestureScanner;
    private float viewWidth;
    private float viewHeight;

    public MainSurfaceView(final Context aContext, final SurfaceListener surfaceListener) {

        super(aContext);
        this.listener = surfaceListener;
        setLongClickable(true);
        gestureScanner = new GestureDetector(getContext(), this);



    }
    private void startDrawing() {
        MainLoopThread loopThread = new MainLoopThread(this);
        loopThread.setRunning(true);
        loopThread.start();
    }
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        return gestureScanner.onTouchEvent(event);
    }

    public interface SurfaceListener {

        void onClick();

        void onMeasure();

    }

    @Override
    protected void onDraw(final Canvas canvas) {

        super.onDraw(canvas);


    }
    public void resume() {
        setLongClickable(true);
        final ViewTreeObserver vto = this.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new SurfaceGlobalLayoutListener());
    }

    private class MainLoopThread {
        public static final int MAX_FLING = 3000;
        private static final float FLING_DAMPER = 0.01F;
        private final MyThread thread = new MyThread();
        private volatile MainSurfaceView view;
        private boolean running = false;
        private static final float timefactor = 0.1f;


        public MainLoopThread(MainSurfaceView surface) {
            MainLoopThread.this.view = surface;
        }

        public void setRunning(boolean run) {
            running = run;
        }

        void doDraw(Canvas c) {
            if (c != null) {
            if (Nimbits.tree != null && ! Nimbits.tree.isEmpty()) {
                c.drawColor(0, PorterDuff.Mode.CLEAR);
                Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
                p.setColor(Color.RED);
                p.setStrokeWidth(1);

                c.drawCircle(viewWidth/2, viewHeight/2, 40F, p);


                Paint lightGreyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                lightGreyPaint.setColor(Color.LTGRAY);
                lightGreyPaint.setStrokeWidth(1);

                c.drawCircle(100F, 100F, 10F, lightGreyPaint);
            }
        }
        }

        private void updatePhysics() {



        }


        public void start() {
            thread.start();
        }

        private class MyThread extends Thread {
            @Override
            public void run() {
                while (running) {
                    Canvas c = null;
                    try {
                        c = view.getHolder().lockCanvas();
                        synchronized (view.getHolder()) {
                            // view.onDraw(c);

                            updatePhysics();
                            doDraw(c);
                        }
                    } catch (IllegalArgumentException ex) {

                        running = false;

                    } finally {
                        if (c != null) {
                            view.getHolder().unlockCanvasAndPost(c);
                        }
                    }


                }
            }
        }
    }


    private class SurfaceGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {
          //  if (EpgLandscapeDimensions.guideStartTime == 0) {
                measureGuide();
                startDrawing();
          //  }


        }

        private void measureGuide() {
            // int rows;
            long guideStartTime;

           viewWidth = getMeasuredWidth();
            viewHeight = getMeasuredHeight();

           // int rows = (int) (viewHeight * EpgLandscapeDimensions.ROW_VIEW_RATIO);
            //int rowHeight = viewHeight / rows;
            //int channelWidth = (int) (viewWidth * EpgLandscapeDimensions.PERCENT_OF_SCREEN_FOR_CHANNELS);

//            Calendar halfs = Calendar.getInstance();
//            EpgLandscapeDimensions.zeroOutCalendar(halfs);
//            guideStartTime = halfs.getTimeInMillis();
//            EpgLandscapeDimensions.minuteWidth = ((viewWidth / EpgLandscapeDimensions.MINUTES_ON_SCREEN));
//            EpgLandscapeDimensions.guideStartTime = halfs.getTimeInMillis();
//            halfs.add(Calendar.DAY_OF_YEAR, 1);
//            EpgLandscapeDimensions.guideMaxFuture = halfs.getTimeInMillis();
//            EpgLandscapeDimensions.topMenuHeight = Paints.epgTopTimeRangeFontPaint.getTextSize();
//            EpgLandscapeDimensions.rowHeight = rowHeight;
//            EpgLandscapeDimensions.viewWidth = viewWidth;
//            EpgLandscapeDimensions.viewHeight = viewHeight;
//            EpgLandscapeDimensions.channelWidth = channelWidth;
//
//            EpgLandscapeDimensions.liveStreamIndicatorWidth = Paints.fontPaint.measureText(EpgLandscapeDimensions.liveStream);
//            EpgLandscapeDimensions.liveStreamIndicatorHeight = Paints.fontPaint.getTextSize();
//            channelSurfaceYRange = Range.closed(rowHeight, viewHeight);
//            List<Channel> channels = getChannelsFromSurfaceRange(channelSurfaceYRange);
//            listingsOnScreenChunk = ListingChunk.getInstance(Range.closed(guideStartTime, guideStartTime + EpgLandscapeDimensions.MS_TO_LOAD), channels);
//
//            generateChannelRanges(rowHeight);
//            listener.onMeasure();
//            //  listener.onListingChunkRequest(listingsOnScreenChunk);
//            processMovement(0F, 0F);
       // }


    }
}


}