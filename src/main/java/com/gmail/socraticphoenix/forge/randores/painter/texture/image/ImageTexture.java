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
package com.gmail.socraticphoenix.forge.randores.painter.texture.image;

import com.gmail.socraticphoenix.jlsc.value.annotation.ConversionConstructor;
import com.gmail.socraticphoenix.jlsc.value.annotation.Convert;
import com.gmail.socraticphoenix.jlsc.value.annotation.Convertible;
import com.gmail.socraticphoenix.jlsc.value.annotation.Index;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Convertible
public class ImageTexture implements Texture {
    @Convert(value = 0, reflect = false)
    private File imageFile;
    @Convert(value = 1, reflect = false)
    private Type type;
    private BufferedImage image;

    @ConversionConstructor
    public ImageTexture(@Index(0) File imageFile, @Index(1) Type type) {
        this.imageFile = imageFile;
        this.type = type;
    }

    public void load() throws IOException {
        this.image = ImageIO.read(this.imageFile);
    }

    public File getFile() {
        return this.imageFile;
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public Type getType() {
        return this.type;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ImageTexture && this.imageFile.equals(((ImageTexture) other).imageFile) && this.type == ((ImageTexture) other).type;
    }

    public enum Type {
        BLOCK,
        ITEM,
        GUI
    }

}
