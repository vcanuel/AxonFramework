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

import org.axonframework.common.ObjectUtils;
import org.axonframework.common.TypeReference;
import org.axonframework.conversion.ChainingContentTypeConverter;
import org.axonframework.conversion.Converter;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.junit.jupiter.*;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Test class validating the {@link ConversionAwareMessage}.
 *
 * @author Jakob Hatzl
 */
@ExtendWith(MockitoExtension.class)
class ConversionAwareMessageTest extends MessageTestSuite<ConversionAwareMessage> {

    private Converter converter;

    @BeforeEach
    void setUp() {
        converter = spy(new ChainingContentTypeConverter());
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(converter);
    }

    @Test
    void payloadAsClassInvokesConverter() {
        String exStringPayload = "payload";
        byte[] exBytePayload = exStringPayload.getBytes(StandardCharsets.UTF_8);
        ConversionAwareMessage exMessage = buildMessage(exBytePayload);

        // test
        String acPayload = exMessage.payloadAs(String.class);

        assertThat(acPayload).isEqualTo(exStringPayload);
        verify(converter).convert(eq(exBytePayload), eq((Type) String.class));
    }

    @Test
    void payloadAsTypeRefInvokesConverter() {
        String exStringPayload = "payload";
        byte[] exBytePayload = exStringPayload.getBytes(StandardCharsets.UTF_8);
        ConversionAwareMessage exMessage = buildMessage(exBytePayload);

        // test
        String acPayload = exMessage.payloadAs(new TypeReference<String>() {
        });

        assertThat(acPayload).isEqualTo(exStringPayload);
        verify(converter).convert(eq(exBytePayload), eq((Type) String.class));
    }

    @Test
    void payloadAsDoesNotInterfereWithDelegateConversionCaching() {
        String exStringPayload = "payload";
        byte[] exBytePayload = exStringPayload.getBytes(StandardCharsets.UTF_8);
        ConversionAwareMessage exMessage = buildMessage(exBytePayload);

        // test
        String acPayload = exMessage.payloadAs(String.class);
        String acPayload2 = exMessage.payloadAs(String.class);

        assertThat(acPayload).isEqualTo(exStringPayload);
        assertThat(acPayload2).isEqualTo(exStringPayload);
        verify(converter, times(1)).convert(eq(exBytePayload), eq((Type) String.class));
    }

    @Override
    protected ConversionAwareMessage buildDefaultMessage() {
        Message delegate =
                new GenericMessage(TEST_IDENTIFIER, TEST_TYPE, TEST_PAYLOAD, TEST_PAYLOAD_TYPE, TEST_METADATA);
        return new ConversionAwareMessage(delegate, converter);
    }

    @Override
    protected <P> ConversionAwareMessage buildMessage(@Nullable P payload) {
        GenericMessage delegate = new GenericMessage(new MessageType(ObjectUtils.nullSafeTypeOf(payload)),
                                                     payload,
                                                     Metadata.emptyInstance());
        return new ConversionAwareMessage(delegate, converter);
    }
}
