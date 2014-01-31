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
import oblivious;
import stdlib;
/** \endcond */


/**
 * @file
 * \defgroup a3p_statistics_distribution a3p_statistics_distribution.sc
 * \defgroup histogram histogram
 * \defgroup discreteDistributionCount discreteDistributionCount
 * \defgroup discreteDistributionCount_stepSize discreteDistributionCount(with stepSize)
 * \defgroup heatmap heatmap
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


template<domain D, type T>
D T[[2]] _discreteDistributionCount (D T[[1]] data, D bool[[1]] isAvailable, D T min, D T max, D T stepSize) {

    D T[[1]] cutData = cut (data, isAvailable);

    uint sizeData = size (cutData);

    // Why exactly 5? Should it be bigger than 5?
    if (sizeData < 5) {
        D T[[2]] result;
        return result;
    }

    // Number of "columns" (different possible values).
    uint cols = (uint)declassify ((max - min)) + 1;
    // Already declassifying something (matrix size).
    D T[[2]] result (2, cols) = stepSize;

    uint compSize = sizeData * cols;
    // Vectors for parallel computations.
    D T[[1]] compA (compSize), compB (compSize), compC (compSize);

    uint startIndex, endIndex;  // Tmp for loop.
    D T colVal = 0;
    // compA contains cols times cutData vector. (val1, val2, val3, val1, val2, val3)
    // compB contains sizeData times values from "columns". (col1, col1, col1, col2, col2, col2)
    // While we're at it we can also populate first row of result matrix.
    // (As we don't have {1..10} and { x * x | x <- {1..10}} syntax YET, have to do it with loops.)
    for (uint i = 0; i < cols; ++i)
    {
        startIndex = i * sizeData;
        endIndex = (i + 1) * sizeData;
        compA[startIndex:endIndex] = cutData;
        // Here we declassify stepSize. Problem? No? Probably should make stepSize parameter public.
        colVal = min + (T)(i * (uint)declassify(stepSize));
        compB[startIndex:endIndex] = colVal;

        result[0,i] = colVal;
    }

    // Here we get match between cutData value and distribution class value.
    compC = (T)(compA == compB);

    // We need to get "cols" sums. This way compC is split into "cols" arrays (each containing sizeData elements).
    result[1,:] = sum (compC, cols);

    return result;
}

template<type T>
T _max (T x, T y) {
    T[[1]] z = {x, y};
    return max (z);
}

template <domain D : additive3pp, type T, type UT>
D T[[2]] _heatmap (D T[[1]] x,
                   D T[[1]] y,
                   D bool[[1]] xIsAvailable,
                   D bool[[1]] yIsAvailable,
                   UT unsignedBuddy)
{
    assert (size (x) == size (y));

    uint dataSize = size (x);

    // If either data vector has a missing sample then we are missing
    // a point on the plot.
    D bool[[1]] isAvailable = xIsAvailable && yIsAvailable;

    // Cut two samples with one mask.
    D T[[2]] mat (dataSize, 2);
    mat[:,0] = x;
    mat[:,1] = y;
    mat = cut (mat, isAvailable);

    T xmin = declassify (min (mat[:,0]));
    T xmax = declassify (max (mat[:,0]));
    T ymin = declassify (min (mat[:,1]));
    T ymax = declassify (max (mat[:,1]));

    uint s = _getNoOfBreaks (dataSize);
    /* max (1, (xmax - xmin) / s) will hopefully give us such a step
     * size that we'll have s steps but if the data range is too small
     * (classifiers for example) then we'll have as many steps as there
     * are possible values.
     */
    uint xstep = max (1 :: uint, (uint) (xmax - xmin) / s);
    uint ystep = max (1 :: uint, (uint) (ymax - ymin) / s);

    // z will be a matrix where each element counts the number of
    // points falling in a specific bin. The bins are sequential, ie
    // element (a, b) is the a-th bin on the x-axis and b-th bin on
    // the y-axis.
    uint rows = (uint) (ymax - ymin) / ystep + 1;
    uint columns = (uint) (xmax - xmin) / xstep + 1;
    D T[[2]] z (rows, columns);

    // For each data point we'll find coordinates of its bin in z
    dataSize = shape (mat)[0];

    D T[[1]] xy (2 * dataSize);
    xy[:dataSize] = mat[:,0];
    xy[dataSize:] = mat[:,1];

    uint[[1]] steps (2 * dataSize);
    steps[:dataSize] = xstep;
    steps[dataSize:] = ystep;

    T[[1]] mins (2 * dataSize);
    mins[:dataSize] = xmin;
    mins[dataSize:] = ymin;

    D uint[[1]] bins (2 * dataSize);
    // TODO: replace with a single cast when we have T -> uint64.
    bins = (uint) (UT) (xy - mins) / steps;

    // Increment bin counts
    for (uint i = 0; i < dataSize; i++) {
        pd_a3p uint xbin = bins[i];
        pd_a3p uint ybin = bins[i + dataSize];
        pd_a3p T old = matrixLookup (z, ybin, xbin);
        z = matrixUpdate (z, ybin, xbin, old + 1);
    }

    // We can't publish exact counts. So we will find ranges for z as
    // well and replace counts with bin numbers.
    D T[[1]] zflat = reshape (z, size (z));
    T zmin = declassify (min (zflat));
    T zmax = declassify (max (zflat));
    s = _getNoOfBreaks ((uint) (zmax - zmin));
    // TODO: z = 1 is bad.
    uint zstep = max (1 :: uint, (uint) (zmax - zmin) / s);
    // TODO: remove casts when we can divide Ts.
    D T[[2]] gradient = (T) ((UT) (z - zmin) / (UT) zstep);

    D T[[2]] res (2, max (11 :: uint, size (z)));
    res[0, 0] = xmin;
    res[0, 1] = xmax;
    res[0, 2] = ymin;
    res[0, 3] = ymax;
    res[0, 4] = zmin;
    res[0, 5] = zmax;
    res[0, 6] = (T) xstep;
    res[0, 7] = (T) ystep;
    res[0, 8] = (T) zstep;
    res[0, 9] = (T) rows;
    res[0, 10] = (T) columns;

    res[1, : size (gradient)] = reshape (gradient, size (gradient));

    return res;
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
/** @} */


/** \addtogroup <discreteDistributionCount>
 *  @{
 *  @brief Find discrete distribution of an input vector
 *  @note **D** - any protection domain
 *  @note Supported types - \ref int32 "int32" / \ref int64 "int64"
 *  @param data - input vector
 *  @param isAvailable - vector indicating which elements of the input vector are available
 *  @param min - fixed lowest value in returned matrix (lower values from input vector are discarded)
 *  @param max - fixed highest value in returned matrix (higher values from input vector are discarded)
 *  @return returns a matrix where the first row contains discrete distribution values
 *  and the second row contains counts for each value
 */
/*
 * In most of the use cases stepsize is 1, so can omit that from parameters for ease of use.
 */
template<domain D>
D int32[[2]] discreteDistributionCount (D int32[[1]] data, D bool[[1]] isAvailable, D int32 min, D int32 max) {
    D int32 one = 1;    // No better idea at the moment.
    return discreteDistributionCount (data, isAvailable, min, max, one);
}

template<domain D>
D int64[[2]] discreteDistributionCount (D int64[[1]] data, D bool[[1]] isAvailable, D int64 min, D int64 max) {
    D int64 one = 1;    // No better idea at the moment.
    return discreteDistributionCount (data, isAvailable, min, max, one);
}
/** @} */

/** \addtogroup <discreteDistributionCount_stepSize>
 *  @{
 *  @brief Find discrete distribution of an input vector
 *  @note **D** - any protection domain
 *  @note Supported types - \ref int32 "int32" / \ref int64 "int64"
 *  @param data - input vector
 *  @param isAvailable - vector indicating which elements of the input vector are available
 *  @param min - fixed lowest value in returned matrix (lower values from input vector are discarded)
 *  @param max - fixed highest value in returned matrix (higher values from input vector are discarded)
 *  @param stepSize - difference between adjacent values in returned matrix
 *  (values in returned matrix are: min, min + stepSize, min + 2*stepSize, min + 3*stepSize, ...).
 *  Other values from input vector are discarded.
 *  @return returns a matrix where the first row contains discrete distribution values
 *  and the second row contains counts for each value
 */
/*
 * More possible versions of discreteDistributionCount():
 * a) instead of min/max/stepSize give vector of possible values.
 */
template<domain D>
D int32[[2]] discreteDistributionCount (D int32[[1]] data, D bool[[1]] isAvailable, D int32 min, D int32 max, D int32 stepSize) {
    return _discreteDistributionCount (data, isAvailable, min, max, stepSize);
}

template<domain D>
D int64[[2]] discreteDistributionCount (D int64[[1]] data, D bool[[1]] isAvailable, D int64 min, D int64 max, D int64 stepSize) {
    return _discreteDistributionCount (data, isAvailable, min, max, stepSize);
}

/** @} */

/** \addtogroup <heatmap>
 *  @{
 *  @brief Create a heatmap
 *  @note **D** - additive3pp
 *  @note Supported types - \ref int32 "int32" / \ref int64 "int64"
 *  @param x - first sample
 *  @param y - second sample
 *  @param xIsAvailable - vector indicating which elements of x are available
 *  @param yIsAvailable - vector indicating which elements of y are available

 *  @note A heatmap (in this case) is a plot of two variables of a set
 *  of data. It can be used to visualise data in the same way as a
 *  scatterplot while leaking less information about individual
 *  values. Values of sample x will determine coordinates on the x
 *  axis and values of sample y will determine coordinates on the y
 *  axis. Instead of displaying individual points, this function
 *  counts the number of points in fixed rectangular areas (called
 *  bins). The counts will then be replaced with ranges.

 *  @return returns a matrix with two rows. The second row is a
 *  flattened matrix with as many elements as there are bins in the
 *  heatmap. The second row may actually be longer if there's less
 *  than 11 elements in the matrix. Each bin will contain a number,
 *  starting from 1, which indicates the frequency range of the
 *  bin. When plotting, a bin with matrix coordinates (i, j) and value
 *  z will have its lower left point at (xmin + i · xstep, ymin + j ·
 *  ystep), will have dimensions (xstep, ystep) and will indicate a
 *  frequency count in the range [zmin + z · zstep, zmin + (z + 1) ·
 *  zstep).  The first 11 elements of the first row are
 *  <table><tr><td>xmin</td><td>minimum of
 *  x</td></tr><tr><td>xmax</td><td>maximum of
 *  x</td></tr><tr><td>ymin</td><td>minimum of
 *  y</td></tr><tr><td>ymax</td><td>maximum of
 *  y</td></tr><tr><td>zmin</td><td>minimum number of elements in a
 *  bin</td></tr><tr><td>zmax</td><td>maximum number of elements in a
 *  bin</td></tr><tr><td>xstep</td><td>width of a
 *  bin</td></tr><tr><td>ystep</td><td>height of a
 *  bin</td></tr><tr><td>zstep</td><td>range of a frequency
 *  class</td></tr><tr><td>rows</td><td>number of rows in the
 *  matrix</td></tr><tr><td>columns</td><td>number of columns in the
 *  matrix</td></tr></table>
 */
template <domain D : additive3pp>
D int32[[2]] heatmap (D int32[[1]] x,
                      D int32[[1]] y,
                      D bool[[1]] xIsAvailable,
                      D bool[[1]] yIsAvailable)
{
    uint32 buddy;
    return _heatmap (x, y, xIsAvailable, yIsAvailable, buddy);
}

template <domain D : additive3pp>
D int64[[2]] heatmap (D int64[[1]] x,
                      D int64[[1]] y,
                      D bool[[1]] xIsAvailable,
                      D bool[[1]] yIsAvailable)
{
    uint64 buddy;
    return _heatmap (x, y, xIsAvailable, yIsAvailable, buddy);
}
/** @} */

/** @} */
