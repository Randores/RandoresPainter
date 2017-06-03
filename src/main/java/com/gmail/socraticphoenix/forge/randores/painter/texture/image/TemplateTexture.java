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

import com.gmail.socraticphoenix.forge.randores.painter.texture.state.ImageState;
import com.gmail.socraticphoenix.jlsc.serialization.annotation.Name;
import com.gmail.socraticphoenix.jlsc.serialization.annotation.Serializable;
import com.gmail.socraticphoenix.jlsc.serialization.annotation.SerializationConstructor;
import com.gmail.socraticphoenix.jlsc.serialization.annotation.Serialize;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Serializable
public class TemplateTexture implements Texture {
    @Serialize(value = "state", reflect = false)
    private ImageState state;
    @Serialize(value = "file", reflect = false)
    private File imageFile;
    private BufferedImage image;

    @SerializationConstructor
    public TemplateTexture(@Name("file") File imageFile, @Name("state") ImageState state) {
        this.imageFile = imageFile;
        this.state = state;
    }

    public ImageState getState() {
        return this.state;
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

    @Override
    public boolean equals(Object other) {
        return other instanceof TemplateTexture && this.imageFile.equals(((TemplateTexture) other).imageFile) && this.state.equals(((TemplateTexture) other).state);
    }

}
