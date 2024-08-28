package com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder;

import java.util.ArrayList;
import java.util.Collection;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.EventParameter;

/**
 * @author aia
 */
public class LongParameterEventArrayDecoder extends GenericEventArrayParameterDecoder {

    private final GenericParameterDecoder longParameterDecoder;

    /**
     * @param eventParameter
     *            - event
     */
    public LongParameterEventArrayDecoder(final EventParameter eventParameter) {
        super(eventParameter);
        longParameterDecoder = new LongParameterDecoder(eventParameter);
    }

    @Override
    protected Object[] initializeArray() {
        return new Long[eventParameter.getEventArrayParameterSize()];
    }

    @Override
    public Object getDecodeValueForGenericRecord(final byte[] data, final int offset) {
        final Collection<Long> result = new ArrayList<Long>(eventParameter.getEventArrayParameterSize());
        return super.getDecodeValueForGenericRecord(data, offset, result);
    }

    @Override
    protected Object decode(final byte[] data, final int offset) {
        return longParameterDecoder.decode(data, offset);
    }

    @Override
    protected Object getDefaultValueForGenericRecord() {
        return longParameterDecoder.getDefaultValueForGenericRecord();
    }

}
