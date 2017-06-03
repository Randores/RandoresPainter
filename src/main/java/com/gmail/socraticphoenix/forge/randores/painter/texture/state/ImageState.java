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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class ImageState {
    private List<PixelState> states;

    public ImageState(List<PixelState> states) {
        this.states = states;
    }

    public ImageState() {
        this.states = new ArrayList<>();
    }

    public static ImageState fromString(String desc) {
        try {
            ImageState state = new ImageState();
            BufferedReader reader = new BufferedReader(new StringReader(desc));
            String line;
            while ((line = reader.readLine()) != null) {
                state.add(PixelState.fromString(line));
            }
            return state;
        } catch (IOException e) {
            throw new IllegalStateException("String reader threw IOException", e);
        }
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ImageState && this.states.equals(((ImageState) other).states);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (PixelState state : this.states) {
            builder.append(state.toString()).append(System.lineSeparator());
        }
        return builder.toString();
    }

    public List<PixelState> getStates() {
        return this.states;
    }

    public boolean contains(int x, int y) {
        return this.states.stream().filter(c -> c.getX() == x && c.getY() == y).findFirst().isPresent();
    }

    public void remove(int x, int y) {
        this.states.removeIf(c -> c.getX() == x && c.getY() == y);
    }

    public PixelState get(int x, int y) {
        return this.states.stream().filter(c -> c.getY() == y && c.getX() == x).findFirst().orElse(null);
    }

    public void add(PixelState state) {
        this.states.removeIf(current -> current.getX() == state.getX() && current.getY() == state.getY());
        this.states.add(state);
    }

}
