package com.example.dxdrush.beziercurveforslowfastview;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dxdrush on 10/10/2017.
 */

public class BezierCurveForSlowFastView extends View {

    private final static int MIN_SPACE = 65;
    private final static int MAX_CONTROL_POINT = 8;
    private final static int MIN_CONTROL_POINT = 3;
    private Point startPoint = new Point();
    private Point endPoint = new Point();
    private Point defaultControlPoint1 = new Point();
    private Point defaultControlPoint2 = new Point();
    private Paint bezierLinePaint;
    private Paint controlPointPaint;
    private Paint axleLinePaint;
    private Paint decroPaint;
    private Path bezierLinePath;
    private int bezierLineColor = Color.parseColor("#333333");
    private int controlPointColor = Color.parseColor("#333333");
    private int axleLineColor = Color.parseColor("#F2F2F2");
    private int decroColor = Color.parseColor("#858585");
    private List<Point> controlPointList = new ArrayList<>();
    private int index = -1;
    private boolean isFirstDraw = true;
    private long actionDownTime = 0;

    private Point actionDownPoint = new Point();

    private boolean addPointFlag;
    private boolean deletePointFlag;

    private WeakReference<Context> contextWeakReference;

    private int DP1;
    private int DP2;
    private int DP4;
    private int DP10;

    public BezierCurveForSlowFastView(Context context) {
        this(context, null);
    }

    public BezierCurveForSlowFastView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BezierCurveForSlowFastView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        contextWeakReference = new WeakReference<Context>(context);
        initData();
        initPaint();
    }

    private void initData() {

        DP1 = ScreenUtils.dp2px(contextWeakReference.get(), 1);
        DP2 = ScreenUtils.dp2px(contextWeakReference.get(), 2);
        DP4 = ScreenUtils.dp2px(contextWeakReference.get(), 4);
        DP10 = ScreenUtils.dp2px(contextWeakReference.get(), 10);
    }

    private void initPaint() {

        //曲线
        bezierLinePaint = new Paint();
        bezierLinePaint.setAntiAlias(true);
        bezierLinePaint.setDither(true);
        bezierLinePaint.setColor(bezierLineColor);
        bezierLinePaint.setStyle(Paint.Style.STROKE);
        bezierLinePaint.setStrokeWidth(DP2);

        //控制圆点
        controlPointPaint = new Paint();
        controlPointPaint.setAntiAlias(true);
        controlPointPaint.setDither(true);
        controlPointPaint.setStyle(Paint.Style.STROKE);
        controlPointPaint.setStrokeWidth(DP10);
        controlPointPaint.setColor(controlPointColor);

        //控制圆点装饰线
        decroPaint = new Paint();
        decroPaint.setStyle(Paint.Style.STROKE);
        decroPaint.setStrokeWidth(DP1);
        decroPaint.setColor(decroColor);

        //中轴线
        axleLinePaint = new Paint();
        axleLinePaint.setAntiAlias(true);
        axleLinePaint.setDither(true);
        axleLinePaint.setStyle(Paint.Style.STROKE);
        axleLinePaint.setStrokeWidth(DP2);
        axleLinePaint.setColor(axleLineColor);

        bezierLinePath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isFirstDraw) {
            controlPointList.add(startPoint);
            controlPointList.add(defaultControlPoint1);
            controlPointList.add(defaultControlPoint2);
            controlPointList.add(endPoint);
            isFirstDraw = false;
        }

        canvas.drawLine(0, getMeasuredHeight() / 2, getMeasuredWidth(), getMeasuredHeight() / 2, axleLinePaint);

        for (int i = 0; i < controlPointList.size(); i++) {
            canvas.drawCircle(controlPointList.get(i).x, controlPointList.get(i).y, DP10 / 2, controlPointPaint);

            for (int j = 0; j < 2; j++) {
                canvas.drawLine(controlPointList.get(i).x - DP4, controlPointList.get(i).y + (2 * j - 1) * DP2, controlPointList.get(i).x + DP4, controlPointList.get(i).y + (2 * j - 1) * DP2, decroPaint);
            }

            /**
             * 利用三阶贝塞尔曲线
             * 以每两个控制点为起始，终止节点
             * 在起始终止节点范围内找到三个构造贝塞尔曲线的数据点
             * 用cubicTo 绘制起始终止节点间的曲线连接线
             * 以此类推
             */
            bezierLinePath.reset();
            if (i < (controlPointList.size() - 1)) {
                bezierLinePath.moveTo(controlPointList.get(i).x, controlPointList.get(i).y); //移至起点
                bezierLinePath.cubicTo((controlPointList.get(i).x + controlPointList.get(i + 1).x) / 2, controlPointList.get(i).y, (controlPointList.get(i).x + controlPointList.get(i + 1).x) / 2, controlPointList.get(i + 1).y, controlPointList.get(i + 1).x, controlPointList.get(i + 1).y);
                canvas.drawPath(bezierLinePath, bezierLinePaint);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        startPoint.x = DP10;
        startPoint.y = getMeasuredHeight() / 2;
        endPoint.x = getMeasuredWidth() - DP10;
        endPoint.y = getMeasuredHeight() / 2;
        defaultControlPoint1.x = (startPoint.x + endPoint.x) / 3;
        defaultControlPoint1.y = (startPoint.y + endPoint.y) / 2;
        defaultControlPoint2.x = ((startPoint.x + endPoint.x) / 3) * 2;
        defaultControlPoint2.y = (startPoint.y + endPoint.y) / 2;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                index = -1;
                for (int i = 0; i < controlPointList.size(); i++) {
                    if (Math.abs(controlPointList.get(i).x - event.getX()) < DP10 && Math.abs(controlPointList.get(i).y - event.getY()) < DP10) {
                        index = i;
                    }
                }
                actionDownTime = System.currentTimeMillis();
                actionDownPoint.x = (int) event.getX();
                actionDownPoint.y = (int) event.getY();
                addPointFlag = true;
                deletePointFlag = true;
                break;

            case MotionEvent.ACTION_MOVE:
                if (index != -1) {
                    if (Math.abs(actionDownPoint.x - event.getX()) > DP10 || Math.abs(actionDownPoint.y - event.getY()) > DP10) {
                        deletePointFlag = false;
                    }
                    if (deletePointFlag && (System.currentTimeMillis() - actionDownTime) > 1000 && controlPointList.size() > MIN_CONTROL_POINT) {

                        final AlertDialog.Builder normalDialog =
                                new AlertDialog.Builder(contextWeakReference.get());
                        normalDialog.setTitle("删除控制点");
                        normalDialog.setMessage("你确定要删除该控制点吗?");
                        normalDialog.setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        controlPointList.remove(index);
                                        invalidate();
                                    }
                                });
                        normalDialog.setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                        normalDialog.show();

                        deletePointFlag = false;

                    }
                    Point movePoint;
                    if (index == 0 || index == controlPointList.size() - 1) {
                        movePoint = new Point(controlPointList.get(index).x, (int) event.getY());
                        if (event.getY() > DP10 && event.getY() < (getMeasuredHeight() - DP10)) {
                            controlPointList.set(index, movePoint);
                            invalidate();
                        }
                    } else {
                        movePoint = new Point((int) event.getX(), (int) event.getY());
                        if (event.getX() - controlPointList.get(index - 1).x > MIN_SPACE
                                && controlPointList.get(index + 1).x - event.getX() > MIN_SPACE
                                && event.getY() > DP10
                                && event.getY() < (getMeasuredHeight() - DP10)) {
                            controlPointList.set(index, movePoint);
                            invalidate();
                        }
                    }
                } else {
                    if (Math.abs(actionDownPoint.x - event.getX()) > DP10 || Math.abs(actionDownPoint.y - event.getY()) > DP10) {
                        addPointFlag = false;
                    }
                    if (addPointFlag && (System.currentTimeMillis() - actionDownTime) > 1000) {
                        for (int i = 0; i < controlPointList.size() - 1; i++) {
                            if (event.getX() - controlPointList.get(i).x > MIN_SPACE
                                    && controlPointList.get(i + 1).x - event.getX() > MIN_SPACE) {
                                if (controlPointList.size() < MAX_CONTROL_POINT) {
                                    Point longClickEventPoint = new Point((int) event.getX(), (int) event.getY());
                                    controlPointList.add((i + 1), longClickEventPoint);
                                    invalidate();
                                    break;
                                } else {
                                    final AlertDialog.Builder normalDialog =
                                            new AlertDialog.Builder(contextWeakReference.get());
                                    normalDialog.setTitle("控制点最大限制提示");
                                    normalDialog.setMessage("最多只能添加" + MAX_CONTROL_POINT + "个控制点，当前控制点数已达到上限，无法继续添加");
                                    normalDialog.setPositiveButton("确定",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            });
                                    normalDialog.show();
                                    break;
                                }
                            }
                        }
                        addPointFlag = false;
                    }
                    if (event.getX() > DP10 && event.getX() < (getMeasuredWidth() - DP10)) {
                        invalidate();
                    }
                }
                break;
        }
        return true;
    }
}
