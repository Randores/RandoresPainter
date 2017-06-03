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
package com.gmail.socraticphoenix.forge.randores.painter.translation;

import com.gmail.socraticphoenix.forge.randores.painter.PainterApp;
import com.gmail.socraticphoenix.jlsc.value.annotation.ConversionConstructor;
import com.gmail.socraticphoenix.jlsc.value.annotation.Convert;
import com.gmail.socraticphoenix.jlsc.value.annotation.Convertible;
import com.gmail.socraticphoenix.jlsc.value.annotation.Index;

import java.io.IOException;
import java.util.List;

@Convertible
public class Translation {
    @Convert(value = 0, reflect = false)
    public String key;
    @Convert(value = 1, reflect = false)
    public String value;
    @Convert(value = 2, reflect = false)
    public Type type;

    @ConversionConstructor
    public Translation(@Index(0) String key, @Index(1) String value, @Index(2) Type type) {
        this.key = key;
        this.value = value;
        this.type = type;
    }

    public Translation getEnglish() throws IOException {
        List<Translation> english = PainterApp.getEnglishTranslations();
        return english.stream().filter(t -> t.key.equals(this.key)).findFirst().get();
    }

    public enum Type {
        ASSETS,
        RANDORES
    }

}
