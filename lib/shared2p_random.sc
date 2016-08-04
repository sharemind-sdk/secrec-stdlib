/*
 * Copyright (C) 2016 Cybernetica
 *
 * Research/Commercial License Usage
 * Licensees holding a valid Research License or Commercial License
 * for the Software may use this file according to the written
 * agreement between you and Cybernetica.
 *
 * GNU Lesser General Public License Usage
 * Alternatively, this file may be used under the terms of the GNU Lesser
 * General Public License version 3 as published by the Free Software
 * Foundation and appearing in the file LICENSE.LGPLv3 included in the
 * packaging of this file.  Please review the following information to
 * ensure the GNU Lesser General Public License version 3 requirements
 * will be met: http://www.gnu.org/licenses/lgpl-3.0.html.
 *
 * For further information, please contact us at sharemind@cyber.ee.
 */

/**
* \cond
*/
module shared2p_random;

import shared2p;
/**
* \endcond
*/

/**
* @file
* \defgroup shared2p_random shared2p_random.sc
* \defgroup randomize randomize
*/

/** \addtogroup shared2p_random
*@{
* @brief Module with functions for randomizing values
*/


/*******************************
    randomize
********************************/


/** \addtogroup randomize
 *  @{
 *  @brief Function for randomizing values
 *  @note **D** - shared2p protection domain
 *  @note **T** - any \ref data_types "data" type
 *  @param arr - an array of any dimension
 *  @return returns an array with randomized values
 */

template <domain D : shared2p, type T, dim N>
D T[[N]] _randomize(D T[[N]] arr) {
    __syscall("shared2p::randomize_$T\_vec", __domainid(D), arr);
    return arr;
}

template <domain D : shared2p, dim N>
D bool[[N]] randomize(D bool[[N]] arr) {
    return _randomize(arr);
}

template <domain D : shared2p, dim N>
D uint8[[N]] randomize(D uint8[[N]] arr) {
    return _randomize(arr);
}

template <domain D : shared2p, dim N>
D uint16[[N]] randomize(D uint16[[N]] arr) {
    return _randomize(arr);
}

template <domain D : shared2p, dim N>
D uint32[[N]] randomize(D uint32[[N]] arr) {
    return _randomize(arr);
}

template <domain D : shared2p, dim N>
D uint[[N]] randomize(D uint[[N]] arr) {
    return _randomize(arr);
}

template <domain D : shared2p, dim N>
D int8[[N]] randomize(D int8[[N]] arr) {
    return _randomize(arr);
}

template <domain D : shared2p, dim N>
D int16[[N]] randomize(D int16[[N]] arr) {
    return _randomize(arr);
}

template <domain D : shared2p, dim N>
D int32[[N]] randomize(D int32[[N]] arr) {
    return _randomize(arr);
}

template <domain D : shared2p, dim N>
D int[[N]] randomize(D int[[N]] arr) {
    return _randomize(arr);
}

template <domain D : shared2p, dim N>
D xor_uint8[[N]] randomize(D xor_uint8[[N]] arr) {
    return _randomize(arr);
}

template <domain D : shared2p, dim N>
D xor_uint16[[N]] randomize(D xor_uint16[[N]] arr) {
    return _randomize(arr);
}

template <domain D : shared2p, dim N>
D xor_uint32[[N]] randomize(D xor_uint32[[N]] arr) {
    return _randomize(arr);
}

template <domain D : shared2p, dim N>
D xor_uint64[[N]] randomize(D xor_uint64[[N]] arr) {
    return _randomize(arr);
}

/** @}*/
/** @}*/
 
