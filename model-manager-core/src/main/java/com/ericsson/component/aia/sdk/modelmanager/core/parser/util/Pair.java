/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.component.aia.sdk.modelmanager.core.parser.util;

import java.io.Serializable;

/**
 * @author aia
 * @param <F>
 *            - F
 * @param <S>
 *            - S
 */
public final class Pair<F, S> implements Serializable {

    private static final long serialVersionUID = 1L;

    private F first;

    private S second;

    /**
     * Default Constructor.
     *
     * @param first
     *            - first
     * @param second
     *            - second
     */
    public Pair(final F first, final S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    public void setFirst(final F first) {
        this.first = first;
    }

    public void setSecond(final S second) {
        this.second = second;
    }

    @Override
    public String toString() {
        return first + "->" + second;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (first == null ? 0 : first.hashCode());
        result = prime * result + (second == null ? 0 : second.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Pair) {
            final Pair<?, ?> other = (Pair<?, ?>) obj;
            return first == other.first && second == other.second
                    || first != null && first.equals(other.first) && second != null && second.equals(other.second);
        }
        return false;
    }
}
