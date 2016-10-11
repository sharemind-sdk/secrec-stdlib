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

import stdlib;
import matrix;
import shared3p;
import shared3p_matrix;
import shared3p_random;
import test_utility;

domain pd_shared3p shared3p;

template<domain D : shared3p,type T>
bool test_transpose(D T data){
    bool result = true;
    D T[[2]] mat (6,6);
    mat = randomize(mat);
    D T[[2]] mat2 = transpose(mat);
    for(uint i = 0; i < 6; ++i){
        if(!all(declassify(mat[:,i]) == declassify(mat2[i,:]))){
            result = false;
        }
    }

    return result;
}

template<domain D : shared3p,type T>
bool test_transpose2(D T data){
    bool result = true;
    D T[[3]] mat (5,5,5);
    mat = randomize(mat);
    D T[[3]] mat2 = transpose(mat);
    for(uint i = 0; i < 5; ++i){
        if(!all(declassify(mat[:,i,:]) == declassify(mat2[:,:,i]))){
            result = false;
        }
    }

    return result;
}

// Positive definite Hermitian matrices for testing matrix inversion.
float64[[1]] posDefHermitian6 = {4.5147070473921, -0.548380003035885, 0.553262791858168, -0.343522423728657, 0.64642556474659, 0.0441899740628056, -0.548380003035885, 7.93198803060617, 0.00105763875724463, -0.242650049532099, -0.0943384086681789, -0.0508849008926296, 0.553262791858168, 0.00105763875724464, 1.29159156209203, 0.188999897798622, 0.239639290809394, 0.0643942773432498, -0.343522423728657, -0.242650049532099, 0.188999897798622, 4.89894288034871, 0.171894063048936, -0.0109990933088925, 0.64642556474659, -0.094338408668179, 0.239639290809394, 0.171894063048936, 3.619482155148, 0.0866843911626045, 0.0441899740628056, -0.0508849008926295, 0.0643942773432498, -0.0109990933088925, 0.0866843911626045, 4.32260410926593};

float64[[1]] posDefHermitian3 = {2.7496186920348, -0.0154905694999501, 0.461515862970774, -0.0154905694999503, 5.03332414138181, 3.09870646924035, 0.461515862970774, 3.09870646924035, 7.56719250437758};

template<domain D : shared3p, type T>
bool test_cholInv(D T proxy, float64[[1]] data) {
    uint n = (uint) sqrt((float64) size(data));
    D T[[2]] X(n, n) = (T) reshape(data, n, n);
    T[[2]] I = declassify(matrixMultiplication(X, choleskyInverse(X)));
    for (uint i = 0; i < n; ++i) {
        I[i, i] -= 1;
    }
    return isNegligible(sum(reshape(I, size(data))));
}

template<domain D : shared3p, type T>
bool test_borderingInv(D T proxy, float64[[1]] data) {
    uint n = (uint) sqrt((float64) size(data));
    D T[[2]] X(n, n) = (T) reshape(data, n, n);
    T[[2]] I = declassify(matrixMultiplication(X, borderingInverse(X)));
    for (uint i = 0; i < n; ++i) {
        I[i, i] -= 1;
    }
    return isNegligible(sum(reshape(I, size(data))));
}

void main(){
    string test_prefix = "Matrix transpose 2D and 3D";
    { pd_shared3p bool t; test(test_prefix, test_transpose(t), t); }
    { pd_shared3p uint8 t; test(test_prefix, test_transpose(t), t); }
    { pd_shared3p uint16 t; test(test_prefix, test_transpose(t), t); }
    { pd_shared3p uint32 t; test(test_prefix, test_transpose(t), t); }
    { pd_shared3p uint64 t; test(test_prefix, test_transpose(t), t); }
    { pd_shared3p int8 t; test(test_prefix, test_transpose(t), t); }
    { pd_shared3p int16 t; test(test_prefix, test_transpose(t), t); }
    { pd_shared3p int32 t; test(test_prefix, test_transpose(t), t); }
    { pd_shared3p int64 t; test(test_prefix, test_transpose(t), t); }
    { pd_shared3p xor_uint8 t; test(test_prefix, test_transpose(t), t); }
    { pd_shared3p xor_uint16 t; test(test_prefix, test_transpose(t), t); }
    { pd_shared3p xor_uint32 t; test(test_prefix, test_transpose(t), t); }
    { pd_shared3p xor_uint64 t; test(test_prefix, test_transpose(t), t); }

    test_prefix = "Matrix transpose 2D and 3D (2)";
    { pd_shared3p bool t; test(test_prefix, test_transpose2(t), t); }
    { pd_shared3p uint8 t; test(test_prefix, test_transpose2(t), t); }
    { pd_shared3p uint16 t; test(test_prefix, test_transpose2(t), t); }
    { pd_shared3p uint32 t; test(test_prefix, test_transpose2(t), t); }
    { pd_shared3p uint64 t; test(test_prefix, test_transpose2(t), t); }
    { pd_shared3p int8 t; test(test_prefix, test_transpose2(t), t); }
    { pd_shared3p int16 t; test(test_prefix, test_transpose2(t), t); }
    { pd_shared3p int32 t; test(test_prefix, test_transpose2(t), t); }
    { pd_shared3p int64 t; test(test_prefix, test_transpose2(t), t); }
    { pd_shared3p xor_uint8 t; test(test_prefix, test_transpose2(t), t); }
    { pd_shared3p xor_uint16 t; test(test_prefix, test_transpose2(t), t); }
    { pd_shared3p xor_uint32 t; test(test_prefix, test_transpose2(t), t); }
    { pd_shared3p xor_uint64 t; test(test_prefix, test_transpose2(t), t); }

    test_prefix = "Matrix inverse using Cholesky decomposition (3x3)";
    { pd_shared3p float32 t; test(test_prefix, test_cholInv(t, posDefHermitian3), t); }
    { pd_shared3p float64 t; test(test_prefix, test_cholInv(t, posDefHermitian3), t); }

    test_prefix = "Matrix inverse using Cholesky decomposition (6x6)";
    { pd_shared3p float32 t; test(test_prefix, test_cholInv(t, posDefHermitian6), t); }
    { pd_shared3p float64 t; test(test_prefix, test_cholInv(t, posDefHermitian6), t); }

    test_prefix = "Matrix inverse using bordering (3x3)";
    { pd_shared3p float32 t; test(test_prefix, test_borderingInv(t, posDefHermitian3), t); }
    { pd_shared3p float64 t; test(test_prefix, test_borderingInv(t, posDefHermitian3), t); }

    test_prefix = "Matrix inverse using bordering (6x6)";
    { pd_shared3p float32 t; test(test_prefix, test_borderingInv(t, posDefHermitian6), t); }
    { pd_shared3p float64 t; test(test_prefix, test_borderingInv(t, posDefHermitian6), t); }

    test_report();
}
