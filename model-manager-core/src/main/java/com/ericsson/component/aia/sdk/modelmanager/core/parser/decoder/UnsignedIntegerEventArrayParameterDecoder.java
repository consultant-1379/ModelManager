package com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder;

import java.util.ArrayList;
import java.util.Collection;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.EventParameter;

/**
 * @author aia
 */
public class UnsignedIntegerEventArrayParameterDecoder extends GenericEventArrayParameterDecoder {

    private final GenericParameterDecoder unsignedIntegerParameterDecoder;

    /**
     * @param eventParameter
     *            - event
     */
    public UnsignedIntegerEventArrayParameterDecoder(final EventParameter eventParameter) {
        super(eventParameter);
        unsignedIntegerParameterDecoder = new UnsignedIntegerParameterDecoder(eventParameter);
    }

    @Override
    protected Object[] initializeArray() {
        return new Integer[eventParameter.getEventArrayParameterSize()];
    }

    @Override
    public Object getDecodeValueForGenericRecord(final byte[] data, final int offset) {
        final Collection<Integer> result = new ArrayList<Integer>(eventParameter.getEventArrayParameterSize());
        return super.getDecodeValueForGenericRecord(data, offset, result);
    }

    @Override
    protected Object decode(final byte[] data, final int offset) {
        return unsignedIntegerParameterDecoder.decode(data, offset);
    }

    @Override
    protected Object getDefaultValueForGenericRecord() {
        return unsignedIntegerParameterDecoder.getDefaultValueForGenericRecord();
    }

}
