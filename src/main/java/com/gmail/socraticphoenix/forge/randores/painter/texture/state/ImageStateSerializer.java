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

import com.gmail.socraticphoenix.jlsc.JLSCArray;
import com.gmail.socraticphoenix.jlsc.JLSCException;
import com.gmail.socraticphoenix.jlsc.serialization.JLSCSerializer;
import com.gmail.socraticphoenix.jlsc.skeleton.JLSCVerifier;
import com.gmail.socraticphoenix.jlsc.skeleton.JLSCVerifiers;
import com.gmail.socraticphoenix.jlsc.value.JLSCValue;

public class ImageStateSerializer implements JLSCSerializer<ImageState> {

    @Override
    public Class<ImageState> result() {
        return ImageState.class;
    }

    @Override
    public JLSCVerifier verifier() {
        return JLSCVerifiers.array(JLSCVerifiers.convertible(PixelState.class));
    }

    @Override
    public boolean canSerialize(Object object) {
        return object instanceof ImageState;
    }

    @Override
    public JLSCValue serialize(JLSCValue value) throws JLSCException {
        ImageState state = value.getAs(ImageState.class).get();
        JLSCArray array = new JLSCArray();
        for(PixelState state1 : state.getStates()) {
            array.add(state1);
        }
        return JLSCValue.of(array);
    }

    @Override
    public JLSCValue deSerialize(JLSCValue value) throws JLSCException {
        JLSCArray array = value.getAsArray().get();
        ImageState state = new ImageState();
        for(JLSCValue value1 : array) {
            state.add(value1.getAs(PixelState.class).get());
        }
        return JLSCValue.serialized(state, value.getProperties(), value.getTypeSpecifier(), value);
    }

}
