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

@page operatordefinitions Operator definitions
@brief Operator definitions in the SecreC language.

@section operatordefinitions Operator definitions

When defining a protection domain kind we must also define operators if we wish to compute with private values from that kind. Operator definitions are almost like normal function definitions with a special name. For example, we can use a template function with a template domain argument to write a definition for all domains of the same kind. The following is an example of the multiplication operator.

Listing 1

    template <domain D : shared3p>
    D uint64[[1]] operator * (D uint64[[1]] x, D uint64[[1]] y) {
        D uint64[[1]] res (size (x));
        __syscall ("shared3p::mul_uint64_vec", __domainid (D), x, y, res);
        return res;
    }

The compiler can implicitly reshape matrices and scalars into vectors and use a definition with vector arguments. It can also classify one of the arguments if it is public. Thus only a definition with two private vector arguments is required. It is possible to overload a definition. That is, write a definition for some specific combination of argument types. For example, when the second argument is public, we can write the following definition.

Listing 2

    template <domain D : shared3p>
    D uint64[[1]] operator * (D uint64[[1]] x, uint64[[1]] y) {
        __syscall ("shared3p::mulc_uint64_vec", __domainid (D), x, __cref y, x);
        return x;
    }

This definition will be preferred by the compiler when the supplied second argument is public.

Note that it is not possible to add new operators. Only built-in operators can appear after the **operator** keyword.

We must also define type conversions (cast operators) similarly. An example follows.

Listing 3

    template <domain D : shared3p>
    D bool[[1]] cast (D uint64[[1]] x) {
        D bool[[1]] res (size (x));
        __syscall ("shared3p::conv_uint64_to_bool_vec", __domainid (D), x, res);
        return res;
    }

*/
