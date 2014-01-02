/*
 * This file is a part of the Sharemind framework.
 * Copyright (C) Cybernetica AS
 *
 * All rights are reserved. Reproduction in whole or part is prohibited
 * without the written consent of the copyright owner. The usage of this
 * code is subject to the appropriate license agreement.
 */

/** \cond */
module a3p_statistics_distribution;

import a3p_sort;
import a3p_statistics_common;
import a3p_statistics_summary;
import additive3pp;
import stdlib;
/** \endcond */


/**
 * @file
 * \defgroup a3p_statistics_distribution a3p_statistics_distribution.sc
 * \defgroup histogram histogram
 */

/** \addtogroup <a3p_statistics_distribution>
 *  @{
 *  @brief Functions for visualising the distribution of data.
 */


/** \cond */
// Sturges' formula calculates the number of breaks in the histogram
// k = ceiling (log2(n) + 1)
uint64 _getNoOfBreaks (uint64 sizeData){
    uint64 k = 1;

    for (uint powOf2 = 2; powOf2 < sizeData; powOf2 = powOf2 * 2) {
        k++;
    }

    return (k + 1);
}

// Gives the sequence of breaks
// NOTE: Currently not used
float64[[1]] _getExactBreaksHist (uint64 min, uint64 max, uint64 noOfBreaks) {
    float64 stepSize = (float64)(max - min) / (float64) noOfBreaks;
    float64[[1]] breaks (noOfBreaks + 1);
    for (uint i = 0; i < noOfBreaks; i = i + 1){
        breaks[i] = (float64) min + stepSize * (float64) i;
    }
    breaks[noOfBreaks] = (float64) max;
    return breaks;
}


template<type T>
T[[1]] _getApproximateBreaksHist (T min, T max, uint64 noOfBreaks) {
    uint64 stepSize = 0;
    uint64 difference = (uint)(max - min);

    if (difference % noOfBreaks == 0) {
        stepSize  = difference / noOfBreaks;
    } else {
        stepSize  = (difference / noOfBreaks) + 1;
    }

    T[[1]] breaks (noOfBreaks + 1);
    for (uint i = 0; i < noOfBreaks + 1; i = i + 1) {
        breaks[i] = min + (T)(i * stepSize);
    }

    return breaks;
}

template<domain D, type T>
D T[[1]] _countElementsHist (D T[[1]] data, T[[1]] breaks) {
    uint64 sizeBreaks = size (breaks);
    uint64 sizeData = size (data);
    assert (sizeBreaks > 1);
    T stepSize = breaks[1] - breaks[0];
    
    //Technically the value is last index + 1 (which is size of the vector).
    //Move the variable here, because it's used already here and makes things more readable.
    uint lastIndex = (sizeBreaks - 1) * sizeData;
    D T[[1]] compA (lastIndex), compB (lastIndex), compC (lastIndex);
    D T[[1]] compRes1 (lastIndex), compRes2 (lastIndex);
    
    uint64 currentIndex;    //Again, for better readability.
    for (uint i = 0; i < sizeBreaks - 1; i = i + 1){
        for (uint j = 0; j < sizeData; j = j + 1){
            currentIndex = i * sizeData + j;
            
            compA[currentIndex] = data[j];
            compB[currentIndex] = breaks[i];
            compC[currentIndex] = breaks[i + 1];
        }
    }
    
    compRes1[0:sizeData] = (T) (compA[0:sizeData] >= compB[0:sizeData]);
    //This and next compRes2 are calculated exactly the same way. Why split it up?
    compRes2[0:sizeData] = (T) (compA[0:sizeData] <= compC[0:sizeData]);
    compRes1[sizeData:lastIndex] = (T) (compA[sizeData:lastIndex] > compB[sizeData:lastIndex]);
    compRes2[sizeData:lastIndex] = (T) (compA[sizeData:lastIndex] <= compC[sizeData:lastIndex]);

    return sum (compRes1 * compRes2, sizeBreaks - 1);
}

template<domain D, type T>
D T[[2]] _histogram (D T[[1]] data, D bool[[1]] isAvailable) {

    D T[[1]] cutData = cut (data, isAvailable);
    uint64 sizeData = size (cutData);

    if (sizeData < 5) {
        D T[[2]] result;
        return result;
    }

    uint64 noBreaks = _getNoOfBreaks (sizeData);
    D T min = min (cutData);
    D T max = max (cutData);

    // Declassify min and max because you will see them on the histogram anyway
    T[[1]] breaks = _getApproximateBreaksHist (declassify (min), declassify (max), noBreaks);

    // Count the elements according to the breaks
    D T[[1]] counts = _countElementsHist(cutData, breaks);
    D T[[2]] result (2, size(breaks));
    result[0,:] = breaks;
    result[1, : size(breaks) - 1] = counts;

    return result;
}


/*
 * Private.
 */
template<domain D, type T>
D T[[2]] _discrete_distribution_count (D T[[1]] data, D bool[[1]] isAvailable, D T min, D T max, D T stepSize) {
    //This is parallel.
    
    D T[[1]] cutData = cut (data, isAvailable);
    
    uint sizeData = size (cutData);
    
    //Why exactly 5? Should it be bigger than 5?
    if (sizeData < 5) {
        D T[[2]] result;
        return result;
    }
    
    //Number of "columns" (different possible values).
    uint cols = (uint)declassify ((max - min)) + 1;
    //Already declassifying stuff (matrix size).
    D T[[2]] result (2, cols) = stepSize;
    
    uint compSize = sizeData * cols;
    //Vectors for parallel computations.
    D T[[1]] compA (compSize), compB (compSize), compC (compSize);
    
    uint startIndex, endIndex;  //Tmp for loop.
    D T colVal = 0;
    //compA contains cols times cutData vector. (val1, val2, val3, val1, val2, val3)
    //compB contains sizeData times values from "columns". (col1, col1, col1, col2, col2, col2)
    //While we're at it we can also populate first row of result matrix.
    //  (As we don't have {1..10} and { x * x | x <- {1..10}} syntax YET, have to do it with loops.)
    for (uint i = 0; i < cols; ++i)
    {
        startIndex = i * sizeData;
        endIndex = (i + 1) * sizeData;
        compA[startIndex:endIndex] = cutData;
        //So here we declassify stepSize. Problem? No?
        colVal = min + (T)(i * (uint)declassify(stepSize));
        compB[startIndex:endIndex] = colVal;
        
        result[0,i] = colVal;
    }
    
    //Here we get match between cutData value and distribution class value.
    compC = (T)(compA == compB);
    
    //We need to get "cols" sums. This way compC is split into "cols" arrays (each containing sizeData elements).
    result[1,:] = sum (compC, cols);
    
    return result;
}

/** \endcond */


/** \addtogroup <histogram>
 *  @{
 *  @brief Create a histogram
 *  @note **D** - any protection domain
 *  @note Supported types - \ref int32 "int32" / \ref int64 "int64"
 *  @param data - input vector
 *  @param isAvailable - vector indicating which elements of the input vector are available
 *  @return returns a matrix where the first row contains histogram
 *  bin boundaries and the second row contains counts for each bin
 *  (note that the last element of the second row is always 0 because
 *  for n bins we have n+1 boundaries). An element x belongs to a bin
 *  with boundaries (a, b) if a < x <= b.
 */
template<domain D>
D int32[[2]] histogram (D int32[[1]] data, D bool[[1]] isAvailable) {
    return _histogram (data, isAvailable);
}

template<domain D>
D int64[[2]] histogram (D int64[[1]] data, D bool[[1]] isAvailable) {
    return _histogram (data, isAvailable);
}



//Private distribution count.
/*
 * Taking a wild guess, that in most of the use cases stepsize is 1, so can omit that from parameters.
 */
template<domain D>
D int32[[2]] discrete_distribution_count (D int32[[1]] data, D bool[[1]] isAvailable, D int32 min, D int32 max) {
    //No better idea at the moment.
    D int32 one = 1;
    return discrete_distribution_count (data, isAvailable, min, max, one);
}

template<domain D>
D int64[[2]] discrete_distribution_count (D int64[[1]] data, D bool[[1]] isAvailable, D int64 min, D int64 max) {
    //No better idea at the moment.
    D int64 one = 1;
    return discrete_distribution_count (data, isAvailable, min, max, one);
}

/*
 * More possible versions:
 * a) instead of stepSize give vector of possible values.
 */
template<domain D>
D int32[[2]] discrete_distribution_count (D int32[[1]] data, D bool[[1]] isAvailable, D int32 min, D int32 max, D int32 stepSize) {
    return _discrete_distribution_count (data, isAvailable, min, max, stepSize);
}

template<domain D>
D int64[[2]] discrete_distribution_count (D int64[[1]] data, D bool[[1]] isAvailable, D int64 min, D int64 max, D int64 stepSize) {
    return _discrete_distribution_count (data, isAvailable, min, max, stepSize);
}

/** @} */
/** @} */

