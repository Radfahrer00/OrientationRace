package com.example.orientationrace;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class CompassView extends View {
    private Paint paint;
    private float azimuth; // Current azimuth angle

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.0f);
        azimuth = 0.0f; // Initial azimuth
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2;

        // Draw the compass circle
        canvas.drawCircle(width / 2, height / 2, radius, paint);

        // Calculate the position of the arrow based on the azimuth
        float arrowX = (float) (width / 2 + radius * Math.sin(Math.toRadians(azimuth)));
        float arrowY = (float) (height / 2 - radius * Math.cos(Math.toRadians(azimuth)));

        // Draw the arrow (triangle)
        Path path = new Path();
        path.moveTo(arrowX, arrowY - 20); // Top
        path.lineTo(arrowX - 10, arrowY + 20); // Bottom left
        path.lineTo(arrowX + 10, arrowY + 20); // Bottom right
        path.close(); // Close the path to form a triangle

        canvas.drawPath(path, paint);
    }

    // Setter method to update the azimuth
    public void setAzimuth(float azimuth) {
        this.azimuth = azimuth;
    }
}

