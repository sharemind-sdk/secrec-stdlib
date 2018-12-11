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
module trace;
/** \endcond */

/**
 * @file
 * \defgroup trace trace.sc
 * \defgroup trace_operation traceOperation
 * \defgroup trace_load traceLoad
 * \defgroup trace_load_column traceLoad(column)
 * \defgroup trace_save traceSave
 */

/** \addtogroup trace
 * @{
 * @brief Module for tracing operations in sharemind-trace format
 */

/**
 * \addtogroup trace_operation
 * @{
 * @brief Trace operation
 * @param program - program name
 * @param operation - operation name
 */
void traceOperation(string program, string operation) {
    print("program=\"$program\",operation=\"$operation\"");
}
/** @} */

/**
 * \addtogroup trace_load_column
 * @{
 * @brief Trace column load
 * @param ds - data source name
 * @param table - table name
 * @param column - column name
 */
void traceLoad(string ds, string table, string column) {
    print("load-table,ds=\"$ds\",name=\"$table\",column=\"$column\"");
}
/** @} */

/**
 * \addtogroup trace_load
 * @{
 * @brief Trace table load
 * @param ds - data source name
 * @param table - table name
 */
void traceLoad(string ds, string table) {
    print("load-table,ds=\"$ds\",name=\"$table\"");
}
/** @} */

/**
 * \addtogroup trace_save
 * @{
 * @brief Trace table save
 * @param ds - data source name
 * @param table - table name
 */
void traceSave(string ds, string table) {
    print("save-table,ds=\"$ds\",name=\"$table\"");
}
/** @} */

/** @} */
/** @} */
