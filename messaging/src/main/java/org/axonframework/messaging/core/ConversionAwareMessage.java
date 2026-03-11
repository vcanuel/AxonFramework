/*
 * Copyright (c) 2010-2026. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.axonframework.messaging.core;

import org.axonframework.common.Assert;
import org.axonframework.common.TypeReference;
import org.axonframework.conversion.Converter;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * A {@link MessageDecorator} implementation, that makes a message aware of its appropriate {@link Converter} for
 * invoking {@link #payloadAs(Class)} and {@link #payloadAs(TypeReference)} without specifying a converter explicitly.
 *
 * @author Jakob Hatzl
 * @since 5.1.0
 */
public class ConversionAwareMessage extends MessageDecorator {

    private final Converter converter;

    /**
     * Constructs a {@code ConversionAwareMessage} for the given delegate @{@link Message} with the desired default
     * {@link Converter} for payload conversion.
     *
     * @param delegate  the delegate @{@link Message}
     * @param converter the {@link Converter} intended default converter for payload conversion
     */
    public ConversionAwareMessage(Message delegate, Converter converter) {
        super(delegate);
        Assert.notNull(converter, () -> "converter may not be null");
        this.converter = converter;
    }

    @Override
    public @Nullable <T> T payloadAs(Class<T> type) {
        return payloadAs(type, this.converter);
    }

    @Override
    public Message withMetadata(Map<String, String> metadata) {
        return new ConversionAwareMessage(delegate().withMetadata(metadata), converter);
    }

    @Override
    public Message andMetadata(Map<String, @Nullable String> metadata) {
        return new ConversionAwareMessage(delegate().andMetadata(metadata), converter);
    }

    @Override
    public Message withConvertedPayload(Type type, Converter converter) {
        Message withConvertedPayload = delegate().withConvertedPayload(type, converter);
        if (delegate().equals(withConvertedPayload)) {
            return this;
        } else {
            return new ConversionAwareMessage(withConvertedPayload, this.converter);
        }
    }
}
