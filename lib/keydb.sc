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
module keydb;

import stdlib;
/** \endcond */

struct ScanCursor {
    uint cursor;
    string key;
}

void keydb_connect(string host) {
    __syscall ("keydb_connect", __cref host);
}

void keydb_disconnect() {
    __syscall ("keydb_disconnect");
}

template <type T>
T keydb_get(string key, T proxy) {
    uint num_bytes;
    uint obj;
    uint t_size = sizeof(proxy);
    __syscall("keydb_get_size", obj, __cref key, __return num_bytes);
    assert(num_bytes == t_size);
    T out;
    __syscall("keydb_get", obj, __ref out);
    return out;
}

template <type T>
T[[1]] keydb_get(string key, T[[1]] proxy) {
    uint num_bytes;
    uint obj;
    T dummy;
    uint t_size = sizeof(dummy);
    __syscall("keydb_get_size", obj, __cref key, __return num_bytes);
    assert(num_bytes % t_size == 0);
    T[[1]] out(num_bytes / t_size);
    __syscall("keydb_get", obj, __ref out);
    return out;
}

template <type T>
void keydb_set(string key, T value) {
    uint64 array = 0;
    __syscall("keydb_set", array,  __cref key, __cref value);
}

template <type T>
void keydb_set(string key, T[[1]] value) {
    uint64 array = 1;
    __syscall("keydb_set", array, __cref key, __cref value);
}

ScanCursor keydb_scan(string pattern) {
    string key;
    uint cursor = 0;
    __syscall("keydb_scan", __ref cursor, __cref pattern, __return key);
    ScanCursor sc;
    sc.cursor = cursor;
    sc.key = key;
    return sc;
}

ScanCursor keydb_scan_next(uint cursor) {
    ScanCursor sc;
    string fake;
    string key;
    __syscall("keydb_scan", __ref cursor, __cref fake, __return key);
    sc.cursor = cursor;
    sc.key = key;
    return sc;
}

bool keydb_clean(string pattern) {
    bool ok;
    __syscall("keydb_clean", __cref pattern, __return ok);
    return ok;
}
