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
package com.gmail.socraticphoenix.forge.randores.painter.texture.state;

import com.gmail.socraticphoenix.jlsc.value.annotation.ConversionConstructor;
import com.gmail.socraticphoenix.jlsc.value.annotation.Convert;
import com.gmail.socraticphoenix.jlsc.value.annotation.Convertible;
import com.gmail.socraticphoenix.jlsc.value.annotation.Index;

@Convertible
public class PixelState {
    @Convert(value = 0, reflect = false)
    private int x;
    @Convert(value = 1, reflect = false)
    private int y;
    @Convert(value = 2, reflect = false)
    private int tint;
    @Convert(value = 3, reflect = false)
    private int shade;
    @Convert(value = 4, reflect = false)
    private boolean vary;

    @ConversionConstructor
    public PixelState(@Index(0) int x, @Index(1) int y, @Index(2) int tint, @Index(3) int shade, @Index(4) boolean vary) {
        this.x = x;
        this.y = y;
        this.tint = tint;
        this.shade = shade;
        this.vary = vary;
    }

    public static PixelState fromString(String desc) {
        String[] pieces = desc.split(",");
        int x = Integer.parseInt(pieces[0]);
        int y = Integer.parseInt(pieces[1]);
        int tint = Integer.parseInt(pieces[2]);
        int shade = Integer.parseInt(pieces[3]);
        boolean varyHue = Boolean.parseBoolean(pieces[4]);
        return new PixelState(x, y, tint, shade, varyHue);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof PixelState) {
            PixelState state = (PixelState) other;
            return this.x == state.x && this.y == state.y && this.tint == state.tint && this.shade == state.shade && this.vary == state.vary;
        }

        return false;
    }

    public String toString() {
        return this.x + "," + this.y + "," + this.tint + "," + this.shade + "," + this.vary;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getTint() {
        return this.tint;
    }

    public void setTint(int tint) {
        this.tint = tint;
    }

    public int getShade() {
        return this.shade;
    }

    public void setShade(int shade) {
        this.shade = shade;
    }

    public boolean isVary() {
        return this.vary;
    }

    public void setVary(boolean vary) {
        this.vary = vary;
    }

}
