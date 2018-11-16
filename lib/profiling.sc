/*
 * Copyright (C) Cybernetica
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
module profiling;
/** \endcond */

/**
 * @file
 * \defgroup module_profiling profiling.sc
 * \defgroup profiling_new_section_type newSectionType
 * \defgroup profiling_start_section startSection
 * \defgroup profiling_end_section endSection
 * \defgroup profiling_flush_profile_log flushProfileLog
 */

/** \addtogroup module_profiling
 * @{
 * @brief Module for profiling SecreC programs
 */

/**
 * \addtogroup profiling_new_section_type
 * @{
 * @brief Create a new profiling section.
 * @param name - name of the section as it will appear in the
 * profiling log
 * @return returns section type identifier
 */
uint32 newSectionType (string name) {
    uint32 out;
    __syscall("ProcessProfiler_newSectionType", __cref name, __ref out);
    return out;
}
/** @} */

/**
 * \addtogroup profiling_start_section
 * @{
 * @brief Start a profiling section.
 * @param stype - section type identifier
 * @param n - size of the data processed in this section
 * @return returns section identifier
 */
uint32 startSection (uint32 stype, uint n) {
    uint32 out;
    __syscall("ProcessProfiler_startSection", stype, n, __ref out);
    return out;
}
/** @} */

/**
 * \addtogroup profiling_end_section
 * @{
 * @brief End a profiling section.
 * @param section_id - section identifier
 */
void endSection (uint32 section_id) {
    __syscall("ProcessProfiler_endSection", section_id);
}
/** @} */

/**
 * \addtogroup profiling_flush_profile_log
 * @{
 * @brief Flush profiling log.
 */
void flushProfileLog () {
    __syscall("ProcessProfiler_flushLog");
}
/** @} */
/** @} */
