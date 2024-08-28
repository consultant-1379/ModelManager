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
public class TBCDStringParameterDecoder extends GenericParameterDecoder {

    /**
     * @param eventParameter
     *            - event
     */
    public TBCDStringParameterDecoder(final EventParameter eventParameter) {
        super(eventParameter);
    }

    @Override
    protected String decode(final byte[] data, final int offset) {
        final byte[] tbcdValue = dataConverterHelper.getByteArrayTBCD(data, offset, eventParameter.getNumberOfBytes());

        return dataConverterHelper.getByteArrayHexStringNoF(tbcdValue, 0, tbcdValue.length);
    }

    @Override
    protected Object getDefaultValueForGenericRecord() {
        return DefaultValues.DEFAULT_STRING_VALUE;
    }

}
