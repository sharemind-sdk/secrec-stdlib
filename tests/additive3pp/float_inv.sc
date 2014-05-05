/*
 * This file is a part of the Sharemind framework.
 * Copyright (C) Cybernetica AS
 *
 * All rights are reserved. Reproduction in whole or part is prohibited
 * without the written consent of the copyright owner. The usage of this
 * code is subject to the appropriate license agreement.
 */

module float_inv;

import stdlib;
import matrix;
import additive3pp;
import a3p_matrix;
import oblivious;
import a3p_random;
import a3p_sort;
import a3p_bloom;
import x3p_string;
import x3p_aes;
import x3p_join;
import profiling;
import test_utility;


domain pd_a3p additive3pp;


template<type T>
void test_inv(T data){
    T max_absolute = 0, max_relative = 0;
    pd_a3p T[[1]] a (20) = {
        -10000,
        -1000,
        -100,
        -10,
        -5,
        -1,
        -0.8,
        -0.6,
        -0.4,
        -0.2,
        0.2,
        0.4,
        0.6,
        0.8,
        1,
        5,
        10,
        100,
        100,
        10000
    };


    T[[1]] b (20) = {
        -0.0001,
        -0.001,
        -0.01,
        -0.1,
        -0.2,
        -1,
        -1.25,
        -1.66666666666666666666666666666666666666666666666666666666666,
        -2.5,
        -5,
        5,
        2.5,
        1.66666666666666666666666666666666666666666666666666666666666,
        1.25,
        1,
        0.1,
        0.2,
        0.01,
        0.001,
        0.0001
    };

    pd_a3p T[[1]] c (20);

    c = inv(a);
    T[[1]] d (20);
    T[[1]] temp(20) = b;

    d = declassify(c) - b;

    for(uint i = 0; i < 20;++i){
        print("Inv(",declassify(a[i]),") = ",declassify(c[i])," Expected: ",b[i]);
        if(d[i] < 0){d[i] = -d[i];}
        if(temp[i] < 0){temp[i] = -temp[i];}
        print("absolute difference: ",d[i]);
        print("relative difference: ",d[i] / temp[i]);
        print("---------------------------");
    }
    max_absolute = max(d);
    max_relative = max(d / temp);

    print("TEST completed");
    print("Max absolute error: ", max_absolute);
    print("Max relative error: ", max_relative);
    test_report_error(max_relative);
}


void main(){
    print("Inv test: start");
    {
        print("Float32");
        test_inv(0::float32);
    }
    {
        print("Float64");
        test_inv(0::float64);
    }
}
