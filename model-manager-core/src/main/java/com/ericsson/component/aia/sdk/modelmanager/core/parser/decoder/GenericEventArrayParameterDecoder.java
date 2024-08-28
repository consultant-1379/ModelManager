package com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder;

import java.util.Collection;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.EventParameter;

/**
 * @author aia
 */
public abstract class GenericEventArrayParameterDecoder extends GenericParameterDecoder {

    /**
     * @param eventParameter
     *            - event
     */
    public GenericEventArrayParameterDecoder(final EventParameter eventParameter) {
        super(eventParameter);
    }

    @Override
    protected abstract Object decode(final byte[] data, final int offset);

    /**
     * @return object array
     */
    protected abstract Object[] initializeArray();

    @Override
    public Object getDecodeValue(final byte[] data, final int offset) {
        final Object[] result = initializeArray();
        int offsetToUse = offset;
        for (int i = 0; i < eventParameter.getEventArrayParameterSize(); i++) {
            result[i] = super.getDecodeValue(data, offsetToUse);
            offsetToUse = super.adjustOffset(offsetToUse);
        }
        return result;
    }

    /**
     * @param data
     *            - data
     * @param offset
     *            - offset
     * @param result
     *            - result
     * @return object
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object getDecodeValueForGenericRecord(final byte[] data, final int offset, final Collection result) {
        int offsetToUse = offset;
        for (int i = 0; i < eventParameter.getEventArrayParameterSize(); i++) {
            final Object decodeValue = super.getDecodeValueForGenericRecord(data, offsetToUse);
            result.add(decodeValue);
            offsetToUse = super.adjustOffset(offsetToUse);
        }
        return result;
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

}
