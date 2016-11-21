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

/** \cond */
module shared3p_keydb;

import stdlib;
import shared3p;
/** \endcond */

/**
* @file
* \defgroup shared3p_keydb shared3p_keydb.sc
* \defgroup keydb_get keydb_get
* \defgroup keydb_set keydb_set
*/

/** \addtogroup shared3p_keydb
*@{
* @brief Module with functions for randomizing values
*/

/** \addtogroup keydb_get
 *  @{
 *  @brief Get value stored in database.
 *  @note **D** - shared3p protection domain
 *  @note **T** - any \ref data_types "data" type
 *  @param key - the public key of the value.
 *  @return the value stored in the database.
 */
template <domain D : shared3p, type T>
D T keydb_get(string key) {
    uint num_bytes;
    uint obj;
    __syscall("keydb_get_size", obj, __cref key, __return num_bytes);
    uint8[[1]] bytes(num_bytes);
    __syscall("keydb_get", obj, __ref bytes);
    uint vector_size;
    __syscall ("shared3p::set_shares_$T\_vec", __domainid(D), __cref bytes, __return vector_size);
    assert(vector_size == 1);
    D T out;
    __syscall ("shared3p::set_shares_$T\_vec", __domainid(D), out, __cref bytes);
    return out;
}

template <domain D : shared3p, type T>
D T[[1]] keydb_get(string key) {
    uint num_bytes;
    uint obj;
    __syscall("keydb_get_size", obj, __cref key, __return num_bytes);
    uint8[[1]] bytes(num_bytes);
    __syscall("keydb_get", obj, __ref bytes);
    uint vector_size;
    __syscall ("shared3p::set_shares_$T\_vec", __domainid(D), __cref bytes, __return vector_size);
    D T [[1]] out (vector_size);
    __syscall ("shared3p::set_shares_$T\_vec", __domainid(D), out, __cref bytes);
    return out;
}
/** @} */

/** \addtogroup keydb_set
 *  @{
 *  @brief Store a new value in the database.
 *  @note **D** - shared3p protection domain
 *  @note **T** - any \ref data_types "data" type
 *  @param key - the public key of the value.
 *  @param value - the value to store.
 */
template <domain D : shared3p, type T>
void keydb_set(string key, D T value) {
    uint64 t_size;
    __syscall ("shared3p::get_type_size_$T", __domainid(D), __return t_size);
    uint64 num_bytes = 0;
    __syscall ("shared3p::get_shares_$T\_vec", __domainid(D), value, __return num_bytes);
    uint8[[1]] bytes(num_bytes);
    __syscall ("shared3p::get_shares_$T\_vec", __domainid(D), value, __ref bytes);
    uint64 array = 1;
    __syscall("keydb_set", array, __cref key, __cref bytes);
}

template <domain D : shared3p, type T>
void keydb_set(string key, D T[[1]] value) {
    uint64 t_size;
    __syscall ("shared3p::get_type_size_$T", __domainid(D), __return t_size);
    uint64 num_bytes = 0;
    __syscall ("shared3p::get_shares_$T\_vec", __domainid(D), value, __return num_bytes);
    uint8[[1]] bytes(num_bytes);
    __syscall ("shared3p::get_shares_$T\_vec", __domainid(D), value, __ref bytes);
    uint64 array = 1;
    __syscall("keydb_set", array, __cref key, __cref bytes);
}
/** @} */

/** @} */
