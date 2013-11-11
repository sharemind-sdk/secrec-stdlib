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

	D T[[1]] compA ((sizeBreaks - 1) * sizeData), compB ((sizeBreaks - 1) * sizeData), compC ((sizeBreaks - 1) * sizeData);
	D T[[1]] compRes1 ((sizeBreaks - 1) * sizeData), compRes2 ((sizeBreaks - 1) * sizeData);

	for (uint i = 0; i < sizeBreaks - 1; i = i + 1){
		for (uint j = 0; j < sizeData; j = j + 1){
			compA[i * sizeData + j] = data[j];
			compB[i * sizeData + j] = breaks[i];
			compC[i * sizeData + j] = breaks[i + 1];
		}
	}

	uint lastIndex = (sizeBreaks - 1) * sizeData;
	compRes1[0:sizeData] = (T) (compA[0:sizeData] >= compB[0:sizeData]);
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
 *  (note that the first row is longer by 1 element)
 */
template<domain D>
D int32[[2]] histogram (D int32[[1]] data, D bool[[1]] isAvailable) {
    return _histogram (data, isAvailable);
}

template<domain D>
D int64[[2]] histogram (D int64[[1]] data, D bool[[1]] isAvailable) {
    return _histogram (data, isAvailable);
}
/** @} */
/** @} */

