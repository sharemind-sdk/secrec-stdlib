/*
 * Copyright (C) 2015 Cybernetica
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

@page data_types Data types
@brief Different primitive data types of the SecreC language.

\anchor bool
\anchor uint8
\anchor uint16
\anchor uint32
\anchor uint64
\anchor int8
\anchor int16
\anchor int32
\anchor int64
\anchor float32
\anchor float64
\anchor string
\anchor uint
\anchor int
\anchor float

@section public-pdk Public protection domain

| Type        | Minimum value        | Maximum value        | Maximum negative value | Minimum positive value |
| :---------: | :------------------: | :------------------: | :--------------------: | :--------------------: |
| string      | NA                   | NA                   | NA                     | NA                     |
| bool        | NA                   | NA                   | NA                     | NA                     |
| uint8       | 0                    | 255                  | NA                     | NA                     |
| uint16      | 0                    | 65535                | NA                     | NA                     |
| uint32      | 0                    | 4294967295           | NA                     | NA                     |
| uint64      | 0                    | 18446744073709551615 | NA                     | NA                     |
| int8        | -128                 | 127                  | NA                     | NA                     |
| int16       | -32768               | 32767                | NA                     | NA                     |
| int32       | -2147483648          | 2147483647           | NA                     | NA                     |
| int64       | -9223372036854775808 | 9223372036854775807  | NA                     | NA                     |
| float32 | -3.402823 &times; 10<sup>38</sup> | 3.402823 &times; 10<sup>38</sup> | -2.802597 &times; 10<sup>-45</sup> | 2.802597 &times; 10<sup>-45</sup> |
| float64 | -1.797693 &times; 10<sup>308</sup> | 1.797693 &times; 10<sup>308</sup> | -4.940656 &times; 10<sup>-324</sup> | 4.940656 &times; 10<sup>-324</sup> |

@note **uint** is an alias for **uint64**
@note **int** is an alias for **int64**
@note **float** is an alias for **float32**

@section shared3p-pdk shared3p data types

\anchor xor_uint8
\anchor xor_uint16
\anchor xor_uint32
\anchor xor_uint64
\anchor fix32
\anchor fix64

| Type        | Respective public type | Minimum value        | Maximum value        | Maximum negative value | Minimum positive value |
| :---------: | :--------------------: | :------------------: | :------------------: | :--------------------: | :--------------------: |
| bool        | bool                   | NA                   | NA                   | NA                     | NA                     |
| uint8       | uint8                  | 0                    | 255                  | NA                     | NA                     |
| uint16      | uint16                 | 0                    | 65535                | NA                     | NA                     |
| uint32      | uint32                 | 0                    | 4294967295           | NA                     | NA                     |
| uint64      | uint64                 | 0                    | 18446744073709551615 | NA                     | NA                     |
| int8        | int8                   | -128                 | 127                  | NA                     | NA                     |
| int16       | int16                  | -32768               | 32767                | NA                     | NA                     |
| int32       | int32                  | -2147483648          | 2147483647           | NA                     | NA                     |
| int64       | int64                  | -9223372036854775808 | 9223372036854775807  | NA                     | NA                     |
| xor_uint8   | uint8                  | 0                    | 255                  | NA                     | NA                     |
| xor_uint16  | uint16                 | 0                    | 65535                | NA                     | NA                     |
| xor_uint32  | uint32                 | 0                    | 4294967295           | NA                     | NA                     |
| xor_uint64  | uint64                 | 0                    | 18446744073709551615 | NA                     | NA                     |
| float32 | float32 | ≈ -2.4 &times; 10<sup>4932</sup> | ≈ 2.4 &times; 10<sup>4932</sup> | ≈ -1.7 &times; 10<sup>-4932</sup> | ≈ 1.7 &times; 10<sup>-4932</sup> |
| float64 | float64 | ≈ -2.4 &times; 10<sup>4932</sup> | ≈ 2.4 &times; 10<sup>4932</sup> | ≈ -1.7 &times; 10<sup>-4932</sup> | ≈ 1.7 &times; 10<sup>-4932</sup> |
| fix32 | float32 | -32768.0 | 32767.99998474121 | -1.52587890625 &times; 10<sup>-5</sup> | 1.52587890625 &times; 10<sup>-5</sup> |
| fix64 | float64 | -2.147483648 &times; 10<sup>9</sup> | 2.14748364799999999976716935634613037109375 &times; 10<sup>9</sup> | -2.3283064365386962890625 &times; 10<sup>-10</sup> | 2.3283064365386962890625 &times; 10<sup>-10</sup> |

*/
