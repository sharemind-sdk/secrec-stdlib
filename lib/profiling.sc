/*
 * This file is a part of the Sharemind framework.
 * Copyright (C) Cybernetica AS
 *
 * All rights are reserved. Reproduction in whole or part is prohibited
 * without the written consent of the copyright owner. The usage of this
 * code is subject to the appropriate license agreement.
 */

/**
* @file
* \cond
*/
module profiling;

uint32 newSectionType (string name) {
    uint32 out;
    __syscall("ProcessProfiler_newSectionType", __cref name, __ref out);
    return out;
}

uint32 startSection (uint32 stype, uint n) {
    uint32 out;
    __syscall("ProcessProfiler_startSection", stype, n, __ref out);
    return out;
}

void endSection (uint32 section_id) {
    __syscall("ProcessProfiler_endSection", section_id);
}


/**
* \endcond
*/
