/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 socraticphoenix@gmail.com
 * Copyright (c) 2016 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.gmail.socraticphoenix.forge.randores.painter.util;

import java.awt.Color;

public class ColorUtil {

    public static double distanceSquared(Color c1, Color c2) {
        double rmean = (c1.getRed() + c2.getRed()) / 2;
        int r = c1.getRed() - c2.getRed();
        int g = c1.getGreen() - c2.getGreen();
        int b = c1.getBlue() - c2.getBlue();
        double wR = 2 + rmean / 256;
        double wG = 4.0;
        double wB = 2 + (255 - rmean) / 256;
        return wR * r * r + wG * g * g + wB * b * b;
    }

    public static double distance(Color c1, Color c2) {
        return Math.sqrt(ColorUtil.distanceSquared(c1, c2));
    }

    public static boolean distanceWithinThreshold(Color c1, Color c2, double threshold) {
        return ColorUtil.distanceSquared(c1, c2) < (threshold * threshold);
    }

    public static double generalBrightnessSquared(Color c1) {
        int r = c1.getRed();
        int g = c1.getGreen();
        int b = c1.getBlue();
        return 0.241 * r * r + 0.691 * g * g + 0.068 * b * b;
    }

    public static double generalBrightness(Color c1) {
        return Math.sqrt(ColorUtil.generalBrightnessSquared(c1));
    }

    public static float relativeBrightness(Color c1) {
        return ColorUtil.hsb(c1)[2] * 255f;
    }

    public static float[] hsb(Color c1) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), hsb);
        return hsb;
    }

    public static int scaledGeneralBrightness(Color c1) {
        return (int) Math.round(ColorUtil.generalBrightness(c1) / 256 * 10);
    }

}
