/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.DefaultValues;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.EventParameter;

/**
 * @author aia
 */
public class DoubleParameterDecoder extends GenericParameterDecoder {

    /**
     * @param eventParameter
     *            - event
     */
    public DoubleParameterDecoder(final EventParameter eventParameter) {
        super(eventParameter);
    }

    @Override
    protected Object decode(final byte[] data, final int offset) {
        return dataConverterHelper.getByteArrayDouble(data, offset, eventParameter.getNumberOfBytes());
    }

    @Override
    protected Object getDefaultValueForGenericRecord() {
        return DefaultValues.DEFAULT_DOUBLE_VALUE;
    }

}
