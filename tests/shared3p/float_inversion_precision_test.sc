/*
 * This file is a part of the Sharemind framework.
 * Copyright (C) Cybernetica AS
 *
 * All rights are reserved. Reproduction in whole or part is prohibited
 * without the written consent of the copyright owner. The usage of this
 * code is subject to the appropriate license agreement.
 */

module float_precision;

import stdlib;
import matrix;
import shared3p;
import shared3p_matrix;
import oblivious;
import shared3p_random;
import shared3p_sort;
import shared3p_bloom;
import shared3p_string;
import shared3p_aes;
import shared3p_join;
import profiling;
import test_utility;


/****************************************************
*****************************************************
*****************************************************
*****/      public uint test_amount = 10;       /****
******  increase for more accurate percentages  *****
*****************************************************
*****************************************************
*****************************************************/


domain pd_shared3p shared3p;

template<type T>
T random_float(T data){
    T rand = 1;
    pd_shared3p uint32 temp;
    pd_shared3p int8 temp2;
    T scalar;
    T scalar2;
    for(uint i = 0; i < 2; ++i){
        scalar = 0;
        while(scalar == 0 || scalar2 == 0){
            scalar = (T) declassify(randomize(temp));
            scalar2 = (T) declassify(randomize(temp2));
        }
        if((i % 2) == 0){
            rand *= scalar;
            rand *= scalar2;
        }
        else{
            rand /= scalar;
            rand /= scalar2;
        }
    }
    return rand;
}

template<domain D:shared3p,type T>
D T[[1]] random(D T[[1]] data){
    uint x_shape = size(data);
    T[[1]] rand (x_shape) = 1;
    pd_shared3p uint32[[1]] temp (x_shape);
    pd_shared3p int8[[1]] temp2 (x_shape);
    T[[1]] scalar (x_shape);
    T[[1]] scalar2 (x_shape);
    for(uint i = 0; i < 2; ++i){
        scalar[0] = 0;
        while(any(scalar == 0) || any(scalar2 == 0)){
            scalar = (T) declassify(randomize(temp));
            scalar2 = (T) declassify(randomize(temp2));
        }
        if((i % 2) == 0){
            rand *= scalar;
            rand *= scalar2;
        }
        else{
            rand /= scalar;
            rand /= scalar2;
        }
    }
    pd_shared3p T[[1]] result = rand;
    return result;
}

template<domain D:shared3p,type T>
D T[[2]] random(D T[[2]] data){
    uint x_shape = shape(data)[0];
    uint y_shape = shape(data)[1];
    T[[2]] rand (x_shape,y_shape) = 1;
    pd_shared3p uint32[[2]] temp (x_shape,y_shape);
    pd_shared3p int8[[2]] temp2 (x_shape,y_shape);
    T[[2]] scalar (x_shape,y_shape);
    T[[2]] scalar2 (x_shape,y_shape);
    for(uint i = 0; i < 2; ++i){
        scalar[0,0] = 0;
        while(any(scalar == 0) || any(scalar2 == 0)){
            scalar = (T) declassify(randomize(temp));
            scalar2 = (T) declassify(randomize(temp2));
        }
        if((i % 2) == 0){
            rand *= scalar;
            rand *= scalar2;
        }
        else{
            rand /= scalar;
            rand /= scalar2;
        }
    }
    pd_shared3p T[[2]] result = rand;
    return result;
}

template<domain D:shared3p,type T>
D T[[3]] random(D T[[3]] data){
    uint x_shape = shape(data)[0];
    uint y_shape = shape(data)[1];
    uint z_shape = shape(data)[2];
    T[[3]] rand (x_shape,y_shape,z_shape) = 1;
    pd_shared3p uint32[[3]] temp (x_shape,y_shape,z_shape);
    pd_shared3p int8[[3]] temp2 (x_shape,y_shape,z_shape);
    T[[3]] scalar (x_shape,y_shape,z_shape);
    T[[3]] scalar2 (x_shape,y_shape,z_shape);
    for(uint i = 0; i < 2; ++i){
        scalar[0,0,0] = 0;
        while(any(scalar == 0) || any(scalar2 == 0)){
            scalar = (T) declassify(randomize(temp));
            scalar2 = (T) declassify(randomize(temp2));
        }
        if((i % 2) == 0){
            rand *= scalar;
            rand *= scalar2;
        }
        else{
            rand /= scalar;
            rand /= scalar2;
        }
    }
    pd_shared3p T[[3]] result = rand;
    return result;
}

template<type T>
void test_inversion(T data){
    T max_absolute = 0, max_relative = 0;
    pd_shared3p T[[1]] a (8) = {
        10.49934588656477,
        58.83904325241103,
        6.459133708592741,
        12.30305849067902,
        5.672880010454444,
        0.5154542504192215,
        -33.07334086055091,
        -8.146960136185919
    };
    T[[1]] b (8) = {
        0.095244028609403699186052996064777215723445856946584784926884,
        0.016995517682198601980202808696548131280981723968423205692767,
        0.154819523037536122962576052147431363914115676938109787699726,
        0.081280601954190079255736827142709095552187175689689325695726,
        0.176277305029741292975640471523912602826018007231098313451172,
        1.940036383804566626673218567731497610520720266582510220379170,
        -0.03023583266705227484615820064040857182654732612020244551028,
        -0.12274516915313642586555771859977852412995381036589904796001
    };
    pd_shared3p T[[1]] c (8);

    c = inv(a);
    T[[1]] d (8);
    T[[1]] temp(8) = b;

    d = declassify(c) - b;

    for(uint i = 0; i < 8;++i){
        if(d[i] < 0){d[i] = -d[i];}
        if(temp[i] < 0){temp[i] = -temp[i];}
    }
    max_absolute = max(d);
    max_relative = max(d / temp);

    print("TEST completed");
    print("Max absolute error: ", max_absolute);
    print("Max relative error: ", max_relative);
    test_report_error(max_relative);
}

void main(){

    print("Precision test: start");

    print("TEST 5: Float32/64 inversion precision");
    {
        print("Float32");
        test_inversion(0::float32);
    }
    {
        print("Float64");
        test_inversion(0::float64);
    }

    print("Test finished!");
}
