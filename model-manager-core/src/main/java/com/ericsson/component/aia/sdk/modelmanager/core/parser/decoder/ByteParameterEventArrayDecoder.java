package com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder;

import java.nio.ByteBuffer;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.DefaultValues;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.EventParameter;

/**
 * @author aia
 */
public class ByteParameterEventArrayDecoder extends GenericParameterDecoder {

    private final GenericParameterDecoder byteParameterDecoder;

    /**
     * @param eventParameter
     *            - event
     */
    public ByteParameterEventArrayDecoder(final EventParameter eventParameter) {
        super(eventParameter);
        byteParameterDecoder = new ByteParameterDecoder(eventParameter);
    }

    @Override
    public Object getDecodeValue(final byte[] data, final int offset) {
        final Object[] result = new Byte[eventParameter.getEventArrayParameterSize()];
        int offsetToUse = offset;
        for (int i = 0; i < eventParameter.getEventArrayParameterSize(); i++) {
            result[i] = super.getDecodeValue(data, offsetToUse);
            offsetToUse = super.adjustOffset(offsetToUse);
        }
        return result;
    }

    @Override
    public Object getDecodeValueForGenericRecord(final byte[] data, final int offset) {
        final byte[] result = new byte[eventParameter.getEventArrayParameterSize()];
        int offsetToUse = offset;
        for (int i = 0; i < eventParameter.getEventArrayParameterSize(); i++) {
            result[i] = (byte) super.getDecodeValueForGenericRecord(data, offsetToUse);
            offsetToUse = super.adjustOffset(offsetToUse);
        }
        return ByteBuffer.wrap(result);
    }

    @Override
    public int adjustOffset(final int offset) {
        int adjustedOffset = offset;
        if ((eventParameter.isUseValid() || eventParameter.isOptional()) && !eventParameter.isValidLTEembeddedbitFlag()) {
            adjustedOffset += 1 * eventParameter.getEventArrayParameterSize();
        }
        adjustedOffset += (eventParameter.getNumberOfBytes() * eventParameter.getEventArrayParameterSize());
        return adjustedOffset;
    }

    @Override
    protected Object decode(final byte[] data, final int offset) {
        return byteParameterDecoder.decode(data, offset);
    }

    @Override
    protected Object getDefaultValueForGenericRecord() {
        return DefaultValues.DEFAULT_BYTE_VALUE;
    }
}
