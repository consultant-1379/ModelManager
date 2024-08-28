package com.ericsson.component.aia.sdk.modelmanager.core.parser.util;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder.Asn1ContentByteArrayParameterDecoder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder.BooleanParameterDecoder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder.BooleanStructureArrayParameterDecoder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder.ByteArrayByteArrayParameterDecoder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder.ByteArrayStructureParameterDecoder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder.ByteParameterDecoder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder.ByteParameterEventArrayDecoder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder.DottedDecimalStringParameterDecoder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder.DottedDecimalStructureArrayStringParameterDecoder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder.DoubleParameterDecoder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder.DoubleStructureArrayParameterDecoder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder.GenericParameterDecoder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder.IBCDParameterDecoder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder.IBCDStructureArrayParameterDecoder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder.LongParameterDecoder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder.LongParameterEventArrayDecoder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder.LongStructureArrayParameterDecoder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder.StringArrayIPv6ParameterDecoder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder.StringIPv6ParameterDecoder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder.StringParameterDecoder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder.StringStructureArrayParameterDecoder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder.TBCDStringParameterDecoder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder.THREE_3_GPPStringParameterDecoder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder.UnsignedIntegerEventArrayParameterDecoder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder.UnsignedIntegerParameterDecoder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder.UnsignedIntegerStructureArrayParameterDecoder;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.EventParameter;

/**
 * @author aia
 */
public enum ParserType {
    SHORT(2007), SHORT_ARRAY(2008), BYTE(2009) {
        @Override
        public GenericParameterDecoder getStructParameterDecoder(final EventParameter eventParameter) {
            return new ByteArrayStructureParameterDecoder(eventParameter);
        }

        @Override
        public GenericParameterDecoder getParameterDecoder(final EventParameter eventParameter) {
            return new ByteParameterDecoder(eventParameter);
        }

        @Override
        public GenericParameterDecoder getEventArrayByteParameterDecoder(final EventParameter eventParameter) {
            return new ByteParameterEventArrayDecoder(eventParameter);
        }
    },
    BYTE_ARRAY(2010) {
        private boolean isCellTraceAsn1Parameter(final EventParameter eventParameter) {
            return eventParameter.getName().endsWith("MESSAGE_CONTENTS") && eventParameter.isVariableLength();
        }

        @Override
        public GenericParameterDecoder getParameterDecoder(final EventParameter eventParameter) {
            if (isCellTraceAsn1Parameter(eventParameter)) {
                return new Asn1ContentByteArrayParameterDecoder(eventParameter);
            }
            return new ByteArrayByteArrayParameterDecoder(eventParameter);
        }

    },
    LONG(2011) {
        @Override
        public GenericParameterDecoder getStructParameterDecoder(final EventParameter eventParameter) {
            return new LongStructureArrayParameterDecoder(eventParameter);
        }

        @Override
        public GenericParameterDecoder getParameterDecoder(final EventParameter eventParameter) {
            return new LongParameterDecoder(eventParameter);
        }

        @Override
        public GenericParameterDecoder getEventArrayByteParameterDecoder(final EventParameter eventParameter) {
            return new LongParameterEventArrayDecoder(eventParameter);
        }
    },
    LONG_ARRAY(2012),
    BOOLEAN(2013) {
        @Override
        public GenericParameterDecoder getStructParameterDecoder(final EventParameter eventParameter) {
            return new BooleanStructureArrayParameterDecoder(eventParameter);
        }

        @Override
        public GenericParameterDecoder getParameterDecoder(final EventParameter eventParameter) {
            return new BooleanParameterDecoder(eventParameter);
        }

    },
    BOOLEAN_ARRAY(2014),
    STRING(2015) {
        @Override
        public GenericParameterDecoder getStructParameterDecoder(final EventParameter eventParameter) {
            return new StringStructureArrayParameterDecoder(eventParameter);
        }

        @Override
        public GenericParameterDecoder getParameterDecoder(final EventParameter eventParameter) {
            return new StringParameterDecoder(eventParameter);
        }
    },
    STRING_ARRAY(2016),
    DOUBLE(2017) {
        @Override
        public GenericParameterDecoder getStructParameterDecoder(final EventParameter eventParameter) {
            return new DoubleStructureArrayParameterDecoder(eventParameter);
        }

        @Override
        public GenericParameterDecoder getParameterDecoder(final EventParameter eventParameter) {
            return new DoubleParameterDecoder(eventParameter);
        }
    },
    FLOAT(2019),
    FLOAT_ARRAY(2020),
    INTEGER(2021) {
        @Override
        public GenericParameterDecoder getStructParameterDecoder(final EventParameter eventParameter) {
            return new UnsignedIntegerStructureArrayParameterDecoder(eventParameter);
        }

        @Override
        public GenericParameterDecoder getParameterDecoder(final EventParameter eventParameter) {
            return new UnsignedIntegerParameterDecoder(eventParameter);
        }

        @Override
        public GenericParameterDecoder getEventArrayByteParameterDecoder(final EventParameter eventParameter) {
            return new UnsignedIntegerEventArrayParameterDecoder(eventParameter);
        }
    },
    IBCD(3026) {
        @Override
        public GenericParameterDecoder getStructParameterDecoder(final EventParameter eventParameter) {
            return new IBCDStructureArrayParameterDecoder(eventParameter);
        }

        @Override
        public GenericParameterDecoder getParameterDecoder(final EventParameter eventParameter) {
            return new IBCDParameterDecoder(eventParameter);
        }
    },
    TBCD(3035) {

        @Override
        public GenericParameterDecoder getParameterDecoder(final EventParameter eventParameter) {
            return new TBCDStringParameterDecoder(eventParameter);
        }
    },
    IPADDRESS(3036) {
        @Override
        public GenericParameterDecoder getStructParameterDecoder(final EventParameter eventParameter) {
            return new DottedDecimalStructureArrayStringParameterDecoder(eventParameter);
        }

        @Override
        public GenericParameterDecoder getParameterDecoder(final EventParameter eventParameter) {
            return new DottedDecimalStringParameterDecoder(eventParameter);
        }
    },
    IPADDRESSV6(3037) {
        @Override
        public GenericParameterDecoder getStructParameterDecoder(final EventParameter eventParameter) {
            return new StringArrayIPv6ParameterDecoder(eventParameter);
        }

        @Override
        public GenericParameterDecoder getParameterDecoder(final EventParameter eventParameter) {
            return new StringIPv6ParameterDecoder(eventParameter);
        }
    },
    DNSNAME(3038) {

        @Override
        public GenericParameterDecoder getParameterDecoder(final EventParameter eventParameter) {
            return new THREE_3_GPPStringParameterDecoder(eventParameter);
        }
    },
    HEXSTRING(3039),
    CCSTRING(3040),
    OTHER(9999);

    final int identity;

    /**
     * @param identity
     *            - identity
     */
    ParserType(final int identity) {
        this.identity = identity;
    }

    /**
     * gets Identity
     *
     * @return the identity
     */
    public int getIdentity() {
        return identity;
    }

    /**
     * getStructParameterDecoder
     *
     * @param eventParameter
     *            - the event parser
     * @return the parameter decoder
     */
    public GenericParameterDecoder getStructParameterDecoder(final EventParameter eventParameter) {
        return null;
    }

    /**
     * getParameterDecoder
     *
     * @param eventParameter
     *            - the event parser
     * @return the parameter decoder
     */
    public GenericParameterDecoder getParameterDecoder(final EventParameter eventParameter) {
        return null;
    }

    /**
     * getEventArrayByteParameterDecoder
     *
     * @param eventParameter
     *            - the event parser
     * @return the parameter decoder
     */
    public GenericParameterDecoder getEventArrayByteParameterDecoder(final EventParameter eventParameter) {
        return null;
    }

}
