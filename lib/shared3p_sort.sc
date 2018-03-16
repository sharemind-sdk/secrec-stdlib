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
* \cond
*/
module shared3p_sort;

import stdlib;
import shared3p;
import shared3p_random;
import oblivious;
import shared3p_oblivious;
import shared3p;

import profiling;


/**
* \endcond
*/

/**
* @file
* \defgroup shared3p_sort shared3p_sort.sc
* \defgroup sort sort
* \defgroup sort_vec sort[[1]]
* \defgroup sort_mat sort[[2]]
* \defgroup sortingnetwork sortingNetworkSort
* \defgroup sortingnetwork_vec sortingNetworkSort[[1]]
* \defgroup sortingnetwork_mat sortingNetworkSort[[2]](1 column)
* \defgroup sortingnetwork_mat2 sortingNetworkSort[[2]](2 columns)
* \defgroup sortingnetwork_mat3 sortingNetworkSort[[2]](3 columns)
* \defgroup selectk selectK
* \defgroup selectk_vec selectK[[1]]
* \defgroup selectk_mat selectK[[2]]
* \defgroup radix_sort radixSort
* \defgroup radix_sort_vector radixSort(vector)
* \defgroup radix_sort_index radixSortWithIndex
* \defgroup radix_sort_matrix radixSort(matrix)
* \defgroup unsafe_sort unsafeSort
* \defgroup quick_sort quicksort
* \defgroup quick_sort_vector quicksort(vector)
* \defgroup quick_sort_vector_direction quicksort(vector, direction)
* \defgroup quick_sort_matrix quicksort(matrix)
* \defgroup quick_sort_matrix_direction quicksort(matrix, direction)
* \defgroup quick_quick_sort quickquicksort
* \defgroup quick_quick_sort_vector quickquicksort(vector)
* \defgroup quick_quick_sort_matrix quickquicksort(matrix)
*/

/** \addtogroup shared3p_sort
*@{
* @brief Module with functions for sorting values
*/

/*******************************************************************************
********************************************************************************
**                                                                            **
**  sort                                                                      **
**                                                                            **
********************************************************************************
*******************************************************************************/

/** \addtogroup sort
 *  @{
 *  @brief Functions for sorting values
 *  @note **D** - shared3p protection domain
 *  @note **T** - any \ref data_types "data" type
 *  @note boolean values are sorted after their numerical value. **false** first then **true**
 *  @leakage{Shuffled reordering decisions are declassified \n Leaks the number of equal elements}
 */

/** \addtogroup sort_vec
 *  @{
 *  @brief Function for sorting values in a vector
 *  @note **D** - shared3p protection domain
 *  @return returns a sorted vector from smaller to bigger values
 *  @leakage{Shuffled reordering decisions are declassified \n Leaks the number of equal elements}
 */


/**
* @note Supported types - \ref bool "bool"
*  @note boolean values are sorted after their numerical value. **false** first then **true**
* @param arr - a 1-dimensonal boolean vector
*/
template <domain D : shared3p>
D bool[[1]] sort(D bool[[1]] arr) {
    D uint [[1]] vec = (uint) arr;
    D uint [[1]] ivec = 1 - vec;

    uint n = size (arr);

    D uint [[1]] pTrue (n);
    D uint acc = 0;
    for (uint i = 0; i < n; ++ i) {
        pTrue[i] = acc;
        acc += ivec[i];
    }

    D uint [[1]] pFalse (n);
    acc = n - 1;
    for (uint i = 1; i <= n; ++ i) {
        pFalse[n-i] = acc;
        acc -= vec[n-i];
    }

    // vec*pFalse + ivec*pTrue
    // ivec = 1-vec
    D uint[[1]] indexes = vec * (pFalse - pTrue) + pTrue;
    uint[[1]] publishedIndexes = declassify(indexes);
    D bool[[1]] sortedArr (n);
    for (uint i = 0; i < n; ++i) {
        sortedArr[publishedIndexes[i]] = arr[i];
    }

    return sortedArr;
}

/**
* @note **T** - any \ref data_types "data" type
* @param vec - a 1-dimensonal supported type vector
*/
template <domain D : shared3p, type T>
D T[[1]] sort(D T[[1]] vec) {
    if (size(vec) <= 1)
        return vec;

    vec = shuffle(vec);

    uint n = size(vec);
    uint compSize = (n * (n - 1)) / 2;

    // Do the comparisons:
    /// \todo do check for matrix size so that comps etc can fit into memory
    D uint[[1]] comps;
    {
        // Generate the arrays to compare:
        D T[[1]] cArray1(compSize);
        D T[[1]] cArray2(compSize);
        uint i = 0;
        for (uint r = 0; r < n - 1; ++r) {
            for (uint c = r + 1; c < n; ++c) {
                cArray1[i] = vec[r];
                cArray2[i] = vec[c];
                ++i;
            }
        }

        // Do all the actual comparisons:
        comps = (uint) (cArray1 <= cArray2);
    }

    // Privately compute the new indexes:
    D uint[[1]] newIndexes(n);
    uint constOne = 1;
    {
        uint r = 0;
        uint c = 1;
        for (uint i = 0; i < compSize; ++i) {
            D uint v = comps[i];
            newIndexes[r] += constOne - v;
            newIndexes[c] += v;

            ++c;
            assert(c <= n);
            if (c >= n) {
                ++r;
                c = r + 1;
            }
        }
    }

    uint[[1]] publishedIndexes = declassify(newIndexes);

    D T[[1]] sorted(n);
    for (uint r = 0; r < n; ++r) {
        sorted[publishedIndexes[r]] = vec[r];
    }

    return sorted;
}
/** @}*/
/** \addtogroup sort_mat
 *  @{
 *  @brief Function for sorting rows of a matrix based on values of a column
 *  @note **D** - shared3p protection domain
 *  @return returns a matrix where the input matrix rows are sorted
 *  based on values of the specified column
 *  @leakage{Shuffled reordering decisions are declassified \n Leaks the number of equal elements}
 */

/**
 *  @note Supported types - \ref bool "bool"
 *  @note boolean values are sorted after their numerical value. **false** first then **true**
 *  @param column - index of the column used for ordering
 *  @param matrix - a matrix of supported type
*/
template <domain D : shared3p>
D bool[[2]] sort(D bool[[2]] matrix, uint column) {
    uint[[1]] matShape = shape(matrix);

    D uint[[1]] sortCol = (uint) matrix[:, column];
    D uint[[1]] isortCol = 1 - sortCol;

    uint n = matShape[0];
    {
        D uint[[1]] pTrue (n);
        D uint acc = 0;
        for (uint i = 0; i < n; ++i) {
            pTrue[i] = acc;
            acc += isortCol[i];
        }

        isortCol *= pTrue;
    }

    {
        D uint[[1]] pFalse (n);
        D uint acc = n - 1;
        for (uint i = 1; i <= n; ++i) {
            pFalse[n-i] = acc;
            acc -= sortCol[n-i];
        }

        sortCol *= pFalse;
    }

    uint[[1]] publishedIndexes = declassify(sortCol + isortCol);
    D bool[[2]] sortedMatrix (matShape[0], matShape[1]);
    for (uint i = 0; i < n; ++i) {
        sortedMatrix[publishedIndexes[i], :] = matrix[i, :];
    }

    return sortedMatrix;
}

/**
 *  @note **T** - any \ref data_types "data" type
 *  @param column - index of the column used for ordering
 *  @param matrix - a matrix of supported type
*/

template <domain D : shared3p, type T>
D T[[2]] sort(D T[[2]] matrix, uint column) {
    uint n = shape(matrix)[0];
    if (n <= 1)
        return matrix;

    uint columnCount = shape(matrix)[1];
    assert(column < columnCount);
    matrix = shuffleRows(matrix);

    uint64 compSize = (n * (n - 1)) / 2;

    // Do the comparisons:
    /// \todo do check for matrix size so that comps etc can fit into memory
    D uint[[1]] comps;
    {
        // Generate the arrays to compare:
        D T[[1]] cArray1(compSize);
        D T[[1]] cArray2(compSize);
        uint i = 0;
        for (uint r = 0; r < n - 1; ++r) {
            for (uint c = r + 1; c < n; ++c) {
                cArray1[i] = matrix[r, column];
                cArray2[i] = matrix[c, column];
                ++i;
            }
        }

        // Do all the actual comparisons:
        comps = (uint) (cArray1 <= cArray2);
    }

    // Privately compute the new indexes:
    D uint[[1]] newIndexes(n);
    uint constOne = 1;
    {
        uint r = 0;
        uint c = 1;
        for (uint i = 0; i < compSize; ++i) {
            D uint v = comps[i];
            newIndexes[r] += constOne - v;
            newIndexes[c] += v;

            ++c;
            assert(c <= n);
            if (c >= n) {
                ++r;
                c = r + 1;
            }
        }
    }

    uint[[1]] publishedIndexes = declassify(newIndexes);

    D T[[2]] sorted(n, columnCount);
    for (uint r = 0; r < n; ++r)
        sorted[publishedIndexes[r], :] = matrix[r, :];

    return sorted;
}
/** @}*/
/** @}*/

/*******************************************************************************
********************************************************************************
**                                                                            **
**  sortingNetworkSort                                                        **
**                                                                            **
********************************************************************************
*******************************************************************************/

/**
* \cond
*/
uint[[1]] generateSortingNetwork(uint arraysize) {
    uint snsize = 0;
    __syscall("SortingNetwork_serializedSize", arraysize, __return snsize);
    uint[[1]] sn (snsize);
    __syscall("SortingNetwork_serialize", arraysize, __ref sn);
    return sn;
}
/**
* \endcond
*/


/** \addtogroup sortingnetwork
 *  @{
 *  @brief Functions for sorting values with sorting networks
 *  @note **D** - all protection domains
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int" / \ref xor_uint8 "xor_uint8" / \ref xor_uint16 "xor_uint16" / \ref xor_uint32 "xor_uint32" / \ref xor_uint64 "xor_uint64"
 *  @leakage{None}
 */

/** \addtogroup sortingnetwork_vec
 *  @{
 *  @brief Function for sorting values in a vector with sorting network
 *  @note **D** - all protection domains
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int" / \ref xor_uint8 "xor_uint8" / \ref xor_uint16 "xor_uint16" / \ref xor_uint32 "xor_uint32" / \ref xor_uint64 "xor_uint64"
 *  @param array - a vector of supported type
 *  @return returns a sorted vector from smaller to bigger values
 *  @leakage{None}
 */


//is this only shared3p?
//doesn't work for bools
/**
* \cond
*/
template <domain D>
D uint8[[1]] sortingNetworkSort (D uint8[[1]] array) {
    D xor_uint8[[1]] sortIn = reshare (array);
    D xor_uint8[[1]] sortOut = sortingNetworkSort (sortIn);
    return reshare(sortOut);
}

template <domain D>
D uint16[[1]] sortingNetworkSort (D uint16[[1]] array) {
    D xor_uint16[[1]] sortIn = reshare (array);
    D xor_uint16[[1]] sortOut = sortingNetworkSort (sortIn);
    return reshare(sortOut);
}

template <domain D>
D uint32[[1]] sortingNetworkSort (D uint32[[1]] array) {
    D xor_uint32[[1]] sortIn = reshare (array);
    D xor_uint32[[1]] sortOut = sortingNetworkSort (sortIn);
    return reshare(sortOut);
}

template <domain D>
D uint64[[1]] sortingNetworkSort (D uint64[[1]] array) {
    D xor_uint64[[1]] sortIn = reshare (array);
    D xor_uint64[[1]] sortOut = sortingNetworkSort (sortIn);
    return reshare(sortOut);
}

template <domain D>
D int8[[1]] sortingNetworkSort (D int8[[1]] array) {
    D uint8[[1]] y = (uint8)array + 128;
    D xor_uint8[[1]] sortIn = reshare (y);
    D xor_uint8[[1]] sortOut = sortingNetworkSort (sortIn);
    y = reshare(sortOut) - 128;
    return (int8)y;
}

template <domain D>
D int16[[1]] sortingNetworkSort (D int16[[1]] array) {
    D uint16[[1]] y = (uint16)array + 32768;
    D xor_uint16[[1]] sortIn = reshare (y);
    D xor_uint16[[1]] sortOut = sortingNetworkSort (sortIn);
    y = reshare(sortOut) - 32768;
    return (int16)y;
}

template <domain D>
D int32[[1]] sortingNetworkSort (D int32[[1]] array) {
    D uint32[[1]] y = (uint32)array + 2147483648;
    D xor_uint32[[1]] sortIn = reshare (y);
    D xor_uint32[[1]] sortOut = sortingNetworkSort (sortIn);
    y = reshare(sortOut) - 2147483648;
    return (int32)y;
}

template <domain D>
D int64[[1]] sortingNetworkSort (D int64[[1]] array) {
    D uint64[[1]] y = (uint)array + 9223372036854775808;
    D xor_uint64[[1]] sortIn = reshare (y);
    D xor_uint64[[1]] sortOut = sortingNetworkSort (sortIn);
    y = reshare(sortOut) - 9223372036854775808;
    return (int64)y;
}
/**
* \endcond
*/
template <domain D, type T>
D T[[1]] sortingNetworkSort (D T[[1]] array) {

    if (size(array) <= 1)
        return array;

    // Generate sorting network
    uint[[1]] sortnet = generateSortingNetwork (size(array));

    // We will use this offset to decode the sorting network
    uint offset = 0;

    // Extract the number of stages
    uint numOfStages = sortnet[offset++];

    for (uint stage = 0; stage < numOfStages; stage++) {
        uint sizeOfStage = sortnet[offset++];

        D T[[1]] firstVector (sizeOfStage);
        D T[[1]] secondVector (sizeOfStage);
        D bool[[1]] exchangeFlagsVector (sizeOfStage);

        // Set up first comparison vector
        for (uint i = 0; i < sizeOfStage; ++i) {
            firstVector[i] = array[sortnet[offset]];
            offset++;
        }

        // Set up second comparison vector
        for (uint i = 0; i < sizeOfStage; ++i) {
            secondVector[i] = array[sortnet[offset]];
            offset++;
        }

        // Perform compares
        exchangeFlagsVector = firstVector <= secondVector;

        D bool[[1]] expandedExchangeFlagsVector (2 * sizeOfStage);

        uint counter = 0;
        for(uint i = 0; i < 2 * sizeOfStage; i = i + 2){
            expandedExchangeFlagsVector[i] =  exchangeFlagsVector[counter];
            expandedExchangeFlagsVector[i + 1] = exchangeFlagsVector[counter];
            counter++;
        }

        // Perform exchanges
        D T[[1]] firstFactor (2 * sizeOfStage);
        D T[[1]] secondFactor (2 * sizeOfStage);

        counter = 0;
        for (uint i = 0; i < 2 * sizeOfStage; i = i + 2) {

            firstFactor[i] = firstVector[counter];
            firstFactor[i + 1] = secondVector[counter];

            // Comparison bits

            secondFactor[i] = secondVector[counter];
            secondFactor[i + 1] = firstVector[counter];
            counter++;
        }

        D T[[1]] choiceResults (2 * sizeOfStage);

        choiceResults = choose(expandedExchangeFlagsVector,firstFactor,secondFactor);

        // Finalize oblivious choices
        for (uint i = 0; i < 2 * sizeOfStage; i = i + 2) {
            array[sortnet[offset++]] = choiceResults [i];
        }
        for (uint i = 1; i < (2 * sizeOfStage + 1); i = i + 2) {
            array[sortnet[offset++]] = choiceResults [i];
        }


    }
    return array;
}



/** @}*/
/** \addtogroup sortingnetwork_mat
 *  @{
 *  @brief Function for sorting rows of a matrix based on values of a column
 *  @note **D** - all protection domains
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int" / \ref xor_uint8 "xor_uint8" / \ref xor_uint16 "xor_uint16" / \ref xor_uint32 "xor_uint32" / \ref xor_uint64 "xor_uint64"
 *  @param column - index of the column used for ordering rows of the matrix
 *  @param matrix - a matrix of supported type
 *  @return returns a matrix with sorted rows
 *  @leakage{None}
 */
template <domain D : shared3p>
D uint8[[2]] sortingNetworkSort (D uint8[[2]] matrix, uint column) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D uint8[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint8[[1]] columnToSort = reshare(shuffledMatrix[:,column]);

    uint[[1]] publicIndices = iota(size(columnToSort));
    D xor_uint8[[1]] indexVector = (uint8) publicIndices;
    indexVector = _sortingNetworkSort(columnToSort, indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D uint8[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D uint16[[2]] sortingNetworkSort (D uint16[[2]] matrix, uint column) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D uint16[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint16[[1]] columnToSort = reshare(shuffledMatrix[:,column]);

    uint[[1]] publicIndices = iota(size(columnToSort));
    D xor_uint16[[1]] indexVector = (uint16) publicIndices;
    indexVector = _sortingNetworkSort(columnToSort, indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D uint16[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D uint32[[2]] sortingNetworkSort (D uint32[[2]] matrix, uint column) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D uint32[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint32[[1]] columnToSort = reshare(shuffledMatrix[:,column]);

    uint[[1]] publicIndices = iota(size(columnToSort));
    D xor_uint32[[1]] indexVector = (uint32) publicIndices;
    indexVector = _sortingNetworkSort(columnToSort, indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D uint32[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D uint64[[2]] sortingNetworkSort (D uint64[[2]] matrix, uint column) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D uint64[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint64[[1]] columnToSort = reshare(shuffledMatrix[:,column]);

    uint[[1]] publicIndices = iota(size(columnToSort));
    D xor_uint64[[1]] indexVector = publicIndices;
    indexVector = _sortingNetworkSort(columnToSort, indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D uint64[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D int8[[2]] sortingNetworkSort (D int8[[2]] matrix, uint column) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D int8[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint8[[1]] columnToSort = reshare((uint8) shuffledMatrix[:,column] + 128);

    uint[[1]] publicIndices = iota(size(columnToSort));
    D xor_uint8[[1]] indexVector = (uint8) publicIndices;
    indexVector = _sortingNetworkSort(columnToSort, indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D int8[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D int16[[2]] sortingNetworkSort (D int16[[2]] matrix, uint column) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D int16[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint16[[1]] columnToSort = reshare((uint16) shuffledMatrix[:,column] + 32768);

    uint[[1]] publicIndices = iota(size(columnToSort));
    D xor_uint16[[1]] indexVector = (uint16) publicIndices;
    indexVector = _sortingNetworkSort(columnToSort, indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D int16[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D int32[[2]] sortingNetworkSort (D int32[[2]] matrix, uint column) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D int32[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint32[[1]] columnToSort = reshare((uint32) shuffledMatrix[:,column] + 2147483648);

    uint[[1]] publicIndices = iota(size(columnToSort));
    D xor_uint32[[1]] indexVector = (uint32) publicIndices;
    indexVector = _sortingNetworkSort(columnToSort, indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D int32[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D int64[[2]] sortingNetworkSort (D int64[[2]] matrix, uint column) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D int64[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint64[[1]] columnToSort = reshare((uint64) shuffledMatrix[:,column] + 9223372036854775808);

    uint[[1]] publicIndices = iota(size(columnToSort));
    D xor_uint64[[1]] indexVector = (uint64) publicIndices;
    indexVector = _sortingNetworkSort(columnToSort, indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D int64[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D xor_uint8[[2]] sortingNetworkSort (D xor_uint8[[2]] matrix, uint column) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D xor_uint8[[2]] shuffledMatrix = shuffleRows(matrix);

    uint[[1]] publicIndices = iota(shape(matrix)[0]);
    D xor_uint8[[1]] indexVector = (uint8) publicIndices;
    indexVector = _sortingNetworkSort(shuffledMatrix[:,column], indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D xor_uint8[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D xor_uint16[[2]] sortingNetworkSort (D xor_uint16[[2]] matrix, uint column) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D xor_uint16[[2]] shuffledMatrix = shuffleRows(matrix);

    uint[[1]] publicIndices = iota(shape(matrix)[0]);
    D xor_uint16[[1]] indexVector = (uint16) publicIndices;
    indexVector = _sortingNetworkSort(shuffledMatrix[:,column], indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D xor_uint16[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D xor_uint32[[2]] sortingNetworkSort (D xor_uint32[[2]] matrix, uint column) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D xor_uint32[[2]] shuffledMatrix = shuffleRows(matrix);

    uint[[1]] publicIndices = iota(shape(matrix)[0]);
    D xor_uint32[[1]] indexVector = (uint32) publicIndices;
    indexVector = _sortingNetworkSort(shuffledMatrix[:,column], indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D xor_uint32[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D xor_uint64[[2]] sortingNetworkSort (D xor_uint64[[2]] matrix, uint column) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D xor_uint64[[2]] shuffledMatrix = shuffleRows(matrix);

    uint[[1]] publicIndices = iota(shape(matrix)[0]);
    D xor_uint64[[1]] indexVector = (uint64) publicIndices;
    indexVector = _sortingNetworkSort(shuffledMatrix[:,column], indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D xor_uint64[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}
/**
 * \cond
 */
template <domain D : shared3p, type T>
D T[[1]] _sortingNetworkSort (D T[[1]] vector, D T[[1]] indices) {
    uint[[1]] sortnet = generateSortingNetwork(size(vector));
    uint offset = 0;
    uint numOfStages = sortnet[offset++];

    for (uint stage = 0; stage < numOfStages; stage++) {
        uint sizeOfStage = sortnet[offset++];

        D T[[1]] first(2 * sizeOfStage), second(2 * sizeOfStage);

        for (uint i = 0; i < sizeOfStage; ++i) {
            first[i]                = vector[sortnet[offset]];
            first[i + sizeOfStage]  = indices[sortnet[offset]];
            offset++;
        }

        for (uint i = 0; i < sizeOfStage; ++i) {
            second[i]               = vector[sortnet[offset]];
            second[i + sizeOfStage] = indices[sortnet[offset]];
            offset++;
        }

        D bool[[1]] exchangeFlagsVector = first[:sizeOfStage] <= second[:sizeOfStage];
        exchangeFlagsVector = cat(exchangeFlagsVector, exchangeFlagsVector);

        D T[[1]] results  = choose(exchangeFlagsVector, first, second);

        second = results ^ first ^ second;
        first = results;

        for (uint i = 0; i < sizeOfStage; ++i) {
            vector[sortnet[offset]] = first[i];
            indices[sortnet[offset]] = first[i + sizeOfStage];
            offset++;
        }

        for (uint i = 0; i < sizeOfStage; ++i) {
            vector[sortnet[offset]] = second[i];
            indices[sortnet[offset]] = second[i + sizeOfStage];
            offset++;
        }
    }

    return indices;
}
/**
 * \endcond
 */

/** @}*/
/** \addtogroup sortingnetwork_mat2
 *  @{
 *  @brief Function for sorting rows of a matrix based on values of two columns
 *  @note **D** - all protection domains
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int" / \ref xor_uint8 "xor_uint8" / \ref xor_uint16 "xor_uint16" / \ref xor_uint32 "xor_uint32" / \ref xor_uint64 "xor_uint64"
 *  @param column1 - index of the first column used for ordering
 *  @param column2 - index of the second column used for ordering
 *  @param matrix - a matrix of supported type
 *  @return returns a matrix where the rows of the input matrix have
 *  been sorted. For ordering two rows, the values in column1 are
 *  compared first, if they are equal then the values in column2 are
 *  compared.
 *  @leakage{None}
 */
template <domain D : shared3p>
D uint8[[2]] sortingNetworkSort (D uint8[[2]] matrix, uint column1, uint column2) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D uint8[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint8[[1]] columnToSort = reshare(cat(shuffledMatrix[:,column1],
                                                shuffledMatrix[:,column2]));

    uint[[1]] publicIndices = iota(shape(matrix)[0]);
    D xor_uint8[[1]] indexVector = (uint8) publicIndices;
    indexVector = _sortingNetworkSort2(columnToSort, indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D uint8[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D uint16[[2]] sortingNetworkSort (D uint16[[2]] matrix, uint column1, uint column2) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D uint16[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint16[[1]] columnToSort = reshare(cat(shuffledMatrix[:,column1],
                                                 shuffledMatrix[:,column2]));

    uint[[1]] publicIndices = iota(shape(matrix)[0]);
    D xor_uint16[[1]] indexVector = (uint16) publicIndices;
    indexVector = _sortingNetworkSort2(columnToSort, indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D uint16[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D uint32[[2]] sortingNetworkSort (D uint32[[2]] matrix, uint column1, uint column2) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D uint32[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint32[[1]] columnToSort = reshare(cat(shuffledMatrix[:,column1],
                                                 shuffledMatrix[:,column2]));

    uint[[1]] publicIndices = iota(shape(matrix)[0]);
    D xor_uint32[[1]] indexVector = (uint32) publicIndices;
    indexVector = _sortingNetworkSort2(columnToSort, indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D uint32[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D uint64[[2]] sortingNetworkSort (D uint64[[2]] matrix, uint column1, uint column2) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D uint64[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint64[[1]] columnToSort = reshare(cat(shuffledMatrix[:,column1],
                                                 shuffledMatrix[:,column2]));

    uint[[1]] publicIndices = iota(shape(matrix)[0]);
    D xor_uint64[[1]] indexVector = publicIndices;
    indexVector = _sortingNetworkSort2(columnToSort, indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D uint64[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D int8[[2]] sortingNetworkSort (D int8[[2]] matrix, uint column1, uint column2) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D int8[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint8[[1]] columnToSort = reshare(cat((uint8) shuffledMatrix[:,column1] + 128,
                                                (uint8) shuffledMatrix[:,column2] + 128));

    uint[[1]] publicIndices = iota(shape(matrix)[0]);
    D xor_uint8[[1]] indexVector = (uint8) publicIndices;
    indexVector = _sortingNetworkSort2(columnToSort, indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D int8[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D int16[[2]] sortingNetworkSort (D int16[[2]] matrix, uint column1, uint column2) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D int16[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint16[[1]] columnToSort = reshare(cat((uint16) shuffledMatrix[:,column1] + 32768,
                                                 (uint16) shuffledMatrix[:,column2] + 32768));

    uint[[1]] publicIndices = iota(shape(matrix)[0]);
    D xor_uint16[[1]] indexVector = (uint16) publicIndices;
    indexVector = _sortingNetworkSort2(columnToSort, indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D int16[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D int32[[2]] sortingNetworkSort (D int32[[2]] matrix, uint column1, uint column2) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D int32[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint32[[1]] columnToSort = reshare(cat((uint32) shuffledMatrix[:,column1] + 2147483648,
                                                 (uint32) shuffledMatrix[:,column2] + 2147483648));

    uint[[1]] publicIndices = iota(shape(matrix)[0]);
    D xor_uint32[[1]] indexVector = (uint32) publicIndices;
    indexVector = _sortingNetworkSort2(columnToSort, indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D int32[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D int64[[2]] sortingNetworkSort (D int64[[2]] matrix, uint column1, uint column2) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D int64[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint64[[1]] columnToSort = reshare(cat((uint64) shuffledMatrix[:,column1] + 9223372036854775808,
                                                 (uint64) shuffledMatrix[:,column2] + 9223372036854775808));

    uint[[1]] publicIndices = iota(shape(matrix)[0]);
    D xor_uint64[[1]] indexVector = (uint64) publicIndices;
    indexVector = _sortingNetworkSort2(columnToSort, indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D int64[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D xor_uint8[[2]] sortingNetworkSort (D xor_uint8[[2]] matrix, uint column1, uint column2) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D xor_uint8[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint8[[1]] columnToSort = cat(shuffledMatrix[:,column1],
                                        shuffledMatrix[:,column2]);

    uint[[1]] publicIndices = iota(shape(matrix)[0]);
    D xor_uint8[[1]] indexVector = (uint8) publicIndices;
    indexVector = _sortingNetworkSort2(columnToSort, indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D xor_uint8[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D xor_uint16[[2]] sortingNetworkSort (D xor_uint16[[2]] matrix, uint column1, uint column2) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D xor_uint16[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint16[[1]] columnToSort = cat(shuffledMatrix[:,column1],
                                         shuffledMatrix[:,column2]);

    uint[[1]] publicIndices = iota(shape(matrix)[0]);
    D xor_uint16[[1]] indexVector = (uint16) publicIndices;
    indexVector = _sortingNetworkSort2(columnToSort, indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D xor_uint16[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D xor_uint32[[2]] sortingNetworkSort (D xor_uint32[[2]] matrix, uint column1, uint column2) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D xor_uint32[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint32[[1]] columnToSort = cat(shuffledMatrix[:,column1],
                                         shuffledMatrix[:,column2]);

    uint[[1]] publicIndices = iota(shape(matrix)[0]);
    D xor_uint32[[1]] indexVector = (uint32) publicIndices;
    indexVector = _sortingNetworkSort2(columnToSort, indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D xor_uint32[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D xor_uint64[[2]] sortingNetworkSort (D xor_uint64[[2]] matrix, uint column1, uint column2) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D xor_uint64[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint64[[1]] columnToSort = cat(shuffledMatrix[:,column1],
                                         shuffledMatrix[:,column2]);

    uint[[1]] publicIndices = iota(shape(matrix)[0]);
    D xor_uint64[[1]] indexVector = (uint64) publicIndices;
    indexVector = _sortingNetworkSort2(columnToSort, indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D xor_uint64[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}
/**
 * \cond
 */
template <domain D : shared3p, type T>
D T[[1]] _sortingNetworkSort2 (D T[[1]] vector, D T[[1]] indices) {
    uint rows = size(vector) / 2;
    uint[[1]] sortnet = generateSortingNetwork(rows);
    uint offset = 0;
    uint numOfStages = sortnet[offset++];

    for (uint stage = 0; stage < numOfStages; stage++) {
        uint sizeOfStage = sortnet[offset++];

        D T[[1]] first(3 * sizeOfStage), second(3 * sizeOfStage);

        for (uint i = 0; i < sizeOfStage; ++i) {
            first[i]                   = vector[sortnet[offset]];
            first[i + sizeOfStage]     = vector[sortnet[offset] + rows];
            first[i + sizeOfStage * 2] = indices[sortnet[offset]];
            offset++;
        }

        for (uint i = 0; i < sizeOfStage; ++i) {
            second[i]                   = vector[sortnet[offset]];
            second[i + sizeOfStage]     = vector[sortnet[offset] + rows];
            second[i + sizeOfStage * 2] = indices[sortnet[offset]];
            offset++;
        }

        D T[[1]] compa = cat(first[:sizeOfStage], second[sizeOfStage:sizeOfStage*2]);
        D T[[1]] compb = cat(second[:sizeOfStage], first[sizeOfStage:sizeOfStage*2]);
        D bool[[1]] gte = compa >= compb;
        D bool[[1]] exchangeFlagsVector = !gte[:sizeOfStage] |
            (first[:sizeOfStage] == second[:sizeOfStage] & gte[sizeOfStage:sizeOfStage*2]);
        exchangeFlagsVector = cat(cat(exchangeFlagsVector, exchangeFlagsVector), exchangeFlagsVector);

        D T[[1]] results  = choose(exchangeFlagsVector, first, second);

        second = results ^ first ^ second;
        first = results;

        for (uint i = 0; i < sizeOfStage; ++i) {
            vector[sortnet[offset]] = first[i];
            vector[sortnet[offset] + rows] = first[i + sizeOfStage];
            indices[sortnet[offset]] = first[i + sizeOfStage * 2];
            offset++;
        }

        for (uint i = 0; i < sizeOfStage; ++i) {
            vector[sortnet[offset]] = second[i];
            vector[sortnet[offset] + rows] = second[i + sizeOfStage];
            indices[sortnet[offset]] = second[i + sizeOfStage * 2];
            offset++;
        }
    }

    return indices;
}
/**
 * \endcond
 */
/** @}*/
/** \addtogroup sortingnetwork_mat3
 *  @{
 *  @brief Function for sorting rows of a matrix based on values of three columns
 *  @note **D** - all protection domains
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int" / \ref xor_uint8 "xor_uint8" / \ref xor_uint16 "xor_uint16" / \ref xor_uint32 "xor_uint32" / \ref xor_uint64 "xor_uint64"
 *  @param column1 - index of the first column used for ordering
 *  @param column2 - index of the second column used for ordering
 *  @param column3 - index of the third column used for ordering
 *  @param matrix - a matrix of supported type
 *  @return returns a matrix where the rows of the input matrix have
 *  been sorted. For ordering two rows, the values in column1 are
 *  compared first, if they are equal then the values in column2 are
 *  compared, if they are equal then the values in column3 are
 *  compared.
 *  @leakage{None}
 */
template <domain D : shared3p>
D uint8[[2]] sortingNetworkSort (D uint8[[2]] matrix, uint column1, uint column2, uint column3) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D uint8[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint8[[1]] columnToSort = reshare(cat(cat(shuffledMatrix[:,column1],
                                                    shuffledMatrix[:,column2]),
                                                shuffledMatrix[:,column3]));

    uint[[1]] publicIndices = iota(shape(matrix)[0]);
    D xor_uint8[[1]] indexVector = (uint8) publicIndices;
    indexVector = _sortingNetworkSort3(columnToSort, indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D uint8[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D uint16[[2]] sortingNetworkSort (D uint16[[2]] matrix, uint column1, uint column2, uint column3) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D uint16[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint16[[1]] columnToSort = reshare(cat(cat(shuffledMatrix[:,column1],
                                                     shuffledMatrix[:,column2]),
                                                 shuffledMatrix[:,column3]));

    uint[[1]] publicIndices = iota(shape(matrix)[0]);
    D xor_uint16[[1]] indexVector = (uint16) publicIndices;
    indexVector = _sortingNetworkSort3(columnToSort, indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D uint16[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D uint32[[2]] sortingNetworkSort (D uint32[[2]] matrix, uint column1, uint column2, uint column3) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D uint32[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint32[[1]] columnToSort = reshare(cat(cat(shuffledMatrix[:,column1],
                                                     shuffledMatrix[:,column2]),
                                                 shuffledMatrix[:,column3]));

    uint[[1]] publicIndices = iota(shape(matrix)[0]);
    D xor_uint32[[1]] indexVector = (uint32) publicIndices;
    indexVector = _sortingNetworkSort3(columnToSort, indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D uint32[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D uint64[[2]] sortingNetworkSort (D uint64[[2]] matrix, uint column1, uint column2, uint column3) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D uint64[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint64[[1]] columnToSort = reshare(cat(cat(shuffledMatrix[:,column1],
                                                     shuffledMatrix[:,column2]),
                                                 shuffledMatrix[:,column3]));

    uint[[1]] publicIndices = iota(shape(matrix)[0]);
    D xor_uint64[[1]] indexVector = publicIndices;
    indexVector = _sortingNetworkSort3(columnToSort, indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D uint64[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D int8[[2]] sortingNetworkSort (D int8[[2]] matrix, uint column1, uint column2, uint column3) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D int8[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint8[[1]] columnToSort = reshare(cat(cat((uint8) shuffledMatrix[:,column1] + 128,
                                                    (uint8) shuffledMatrix[:,column2] + 128),
                                                (uint8) shuffledMatrix[:,column3]) + 128);

    uint[[1]] publicIndices = iota(shape(matrix)[0]);
    D xor_uint8[[1]] indexVector = (uint8) publicIndices;
    indexVector = _sortingNetworkSort3(columnToSort, indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D int8[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D int16[[2]] sortingNetworkSort (D int16[[2]] matrix, uint column1, uint column2, uint column3) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D int16[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint16[[1]] columnToSort = reshare(cat(cat((uint16) shuffledMatrix[:,column1] + 32768,
                                                     (uint16) shuffledMatrix[:,column2] + 32768),
                                                 (uint16) shuffledMatrix[:,column3]) + 32768);

    uint[[1]] publicIndices = iota(shape(matrix)[0]);
    D xor_uint16[[1]] indexVector = (uint16) publicIndices;
    indexVector = _sortingNetworkSort3(columnToSort, indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D int16[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D int32[[2]] sortingNetworkSort (D int32[[2]] matrix, uint column1, uint column2, uint column3) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D int32[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint32[[1]] columnToSort = reshare(cat(cat((uint32) shuffledMatrix[:,column1] + 2147483648,
                                                     (uint32) shuffledMatrix[:,column2] + 2147483648),
                                                 (uint32) shuffledMatrix[:,column3]) + 2147483648);

    uint[[1]] publicIndices = iota(shape(matrix)[0]);
    D xor_uint32[[1]] indexVector = (uint32) publicIndices;
    indexVector = _sortingNetworkSort3(columnToSort, indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D int32[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D int64[[2]] sortingNetworkSort (D int64[[2]] matrix, uint column1, uint column2, uint column3) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D int64[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint64[[1]] columnToSort = reshare(cat(cat((uint64) shuffledMatrix[:,column1] + 9223372036854775808,
                                                     (uint64) shuffledMatrix[:,column2] + 9223372036854775808),
                                                 (uint64) shuffledMatrix[:,column3]) + 9223372036854775808);

    uint[[1]] publicIndices = iota(shape(matrix)[0]);
    D xor_uint64[[1]] indexVector = (uint64) publicIndices;
    indexVector = _sortingNetworkSort3(columnToSort, indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D int64[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D xor_uint8[[2]] sortingNetworkSort (D xor_uint8[[2]] matrix, uint column1, uint column2, uint column3) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D xor_uint8[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint8[[1]] columnToSort = cat(cat(shuffledMatrix[:,column1],
                                            shuffledMatrix[:,column2]),
                                        shuffledMatrix[:,column3]);

    uint[[1]] publicIndices = iota(shape(matrix)[0]);
    D xor_uint8[[1]] indexVector = (uint8) publicIndices;
    indexVector = _sortingNetworkSort3(columnToSort, indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D xor_uint8[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D xor_uint16[[2]] sortingNetworkSort (D xor_uint16[[2]] matrix, uint column1, uint column2, uint column3) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D xor_uint16[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint16[[1]] columnToSort = cat(cat(shuffledMatrix[:,column1],
                                             shuffledMatrix[:,column2]),
                                         shuffledMatrix[:,column3]);

    uint[[1]] publicIndices = iota(shape(matrix)[0]);
    D xor_uint16[[1]] indexVector = (uint16) publicIndices;
    indexVector = _sortingNetworkSort3(columnToSort, indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D xor_uint16[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D xor_uint32[[2]] sortingNetworkSort (D xor_uint32[[2]] matrix, uint column1, uint column2, uint column3) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D xor_uint32[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint32[[1]] columnToSort = cat(cat(shuffledMatrix[:,column1],
                                             shuffledMatrix[:,column2]),
                                         shuffledMatrix[:,column3]);

    uint[[1]] publicIndices = iota(shape(matrix)[0]);
    D xor_uint32[[1]] indexVector = (uint32) publicIndices;
    indexVector = _sortingNetworkSort3(columnToSort, indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D xor_uint32[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D xor_uint64[[2]] sortingNetworkSort (D xor_uint64[[2]] matrix, uint column1, uint column2, uint column3) {
    if (shape(matrix)[0] <= 1)
        return matrix;

    D xor_uint64[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint64[[1]] columnToSort = cat(cat(shuffledMatrix[:,column1],
                                             shuffledMatrix[:,column2]),
                                         shuffledMatrix[:,column3]);

    uint[[1]] publicIndices = iota(shape(matrix)[0]);
    D xor_uint64[[1]] indexVector = (uint64) publicIndices;
    indexVector = _sortingNetworkSort3(columnToSort, indexVector);
    publicIndices = (uint) declassify(indexVector);

    uint rows = shape(matrix)[0];
    D xor_uint64[[2]] out(rows, shape(matrix)[1]);
    for (uint i = 0; i < rows; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}
/**
 * \cond
 */
template <domain D : shared3p, type T>
D T[[1]] _sortingNetworkSort3 (D T[[1]] vector, D T[[1]] indices) {
    uint rows = size(vector) / 3;
    uint[[1]] sortnet = generateSortingNetwork(rows);
    uint offset = 0;
    uint numOfStages = sortnet[offset++];

    for (uint stage = 0; stage < numOfStages; stage++) {
        uint sizeOfStage = sortnet[offset++];

        D T[[1]] first(4 * sizeOfStage), second(4 * sizeOfStage);

        for (uint i = 0; i < sizeOfStage; ++i) {
            first[i]                   = vector[sortnet[offset]];
            first[i + sizeOfStage]     = vector[sortnet[offset] + rows];
            first[i + sizeOfStage * 2] = vector[sortnet[offset] + rows * 2];
            first[i + sizeOfStage * 3] = indices[sortnet[offset]];
            offset++;
        }

        for (uint i = 0; i < sizeOfStage; ++i) {
            second[i]                   = vector[sortnet[offset]];
            second[i + sizeOfStage]     = vector[sortnet[offset] + rows];
            second[i + sizeOfStage * 2] = vector[sortnet[offset] + rows * 2];
            second[i + sizeOfStage * 3] = indices[sortnet[offset]];
            offset++;
        }

        D T[[1]] compa = cat(cat(first[:sizeOfStage],
                                 first[sizeOfStage:sizeOfStage*2]),
                             second[sizeOfStage*2:sizeOfStage*3]);
        D T[[1]] compb = cat(cat(second[:sizeOfStage],
                                 second[sizeOfStage:sizeOfStage*2]),
                             first[sizeOfStage*2:sizeOfStage*3]);
        D bool[[1]] gte = compa >= compb;

        compa = cat(first[:sizeOfStage], first[sizeOfStage:sizeOfStage*2]);
        compb = cat(second[:sizeOfStage], second[sizeOfStage:sizeOfStage*2]);
        D bool[[1]] eq = compa == compb;

        D bool[[1]] exchangeFlagsVector = !(gte[:sizeOfStage]) |
            (eq[:sizeOfStage] & (!(gte[sizeOfStage:sizeOfStage*2]) |
                (eq[sizeOfStage:sizeOfStage*2] & gte[sizeOfStage*2:sizeOfStage*3])));
        exchangeFlagsVector = cat(cat(cat(exchangeFlagsVector, exchangeFlagsVector),
                                      exchangeFlagsVector),
                                  exchangeFlagsVector);

        D T[[1]] results  = choose(exchangeFlagsVector, first, second);

        second = results ^ first ^ second;
        first = results;

        for (uint i = 0; i < sizeOfStage; ++i) {
            vector[sortnet[offset]] = first[i];
            vector[sortnet[offset] + rows] = first[i + sizeOfStage];
            vector[sortnet[offset] + rows * 2] = first[i + sizeOfStage * 2];
            indices[sortnet[offset]] = first[i + sizeOfStage * 3];
            offset++;
        }

        for (uint i = 0; i < sizeOfStage; ++i) {
            vector[sortnet[offset]] = second[i];
            vector[sortnet[offset] + rows] = second[i + sizeOfStage];
            vector[sortnet[offset] + rows * 2] = second[i + sizeOfStage * 2];
            indices[sortnet[offset]] = second[i + sizeOfStage * 3];
            offset++;
        }
    }

    return indices;
}
/**
 * \endcond
 */
/** @}*/
/** @}*/

/**
 *\cond
 */
bool isPowerOfTwo (uint x) {
    return (x != 0 && (x & (x - 1)) == 0);
}

uint[[1]] generateTopKSortingNetwork (uint n, uint k) {
    assert(isPowerOfTwo (n));
    assert(k <= n);
    assert(n > 0);

    uint snsize = 0;
    __syscall ("TopKSortingNetwork_serializedSize", n, k, __return snsize);
    uint[[1]] sn(snsize);
    __syscall ("TopKSortingNetwork_serialize", n, k, __ref sn);
    return sn;
}
/**
 *\endcond
 */

/** \addtogroup selectk
 *  @{
 *  @brief Functions for selecting k values from a vector/matrix according to an ordering.
 *  @note **D** - all protection domains
 *  @note Supported types \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int" / \ref xor_uint8 "xor_uint8" / \ref xor_uint16 "xor_uint16" / \ref xor_uint32 "xor_uint32" / \ref xor_uint64 "xor_uint64"
*/

/** \addtogroup selectk_vec
 *  @{
 *  @brief Function for selecting the k smallest elements of a vector.
 *  @note **D** - all protection domains
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int" / \ref xor_uint8 "xor_uint8" / \ref xor_uint16 "xor_uint16" / \ref xor_uint32 "xor_uint32" / \ref xor_uint64 "xor_uint64"
 *  @note Requires that the input array's length is a power of two.
 *  @note The algorithm behind this function is optimized for speed, accuracy is not guaranteed.
 *  @param vector - a vector of supported type
 *  @param k - the number of elements to be selected
 *  @return returns a vector of k elements selected from the input vector
 */

/**
 *\cond
 */
template <domain D : shared3p>
D uint8[[1]] selectK (D uint8[[1]] vector, uint k) {
    D xor_uint8[[1]] in = reshare (vector);
    D xor_uint8[[1]] out = selectK (in, k);
    return reshare(out);
}

template <domain D : shared3p>
D uint16[[1]] selectK (D uint16[[1]] vector, uint k) {
    D xor_uint16[[1]] in = reshare (vector);
    D xor_uint16[[1]] out = selectK (in, k);
    return reshare(out);
}

template <domain D : shared3p>
D uint32[[1]] selectK (D uint32[[1]] vector, uint k) {
    D xor_uint32[[1]] in = reshare (vector);
    D xor_uint32[[1]] out = selectK (in, k);
    return reshare(out);
}

template <domain D : shared3p>
D uint64[[1]] selectK (D uint64[[1]] vector, uint k) {
    D xor_uint64[[1]] in = reshare (vector);
    D xor_uint64[[1]] out = selectK (in, k);
    return reshare(out);
}

template <domain D : shared3p>
D int8[[1]] selectK (D int8[[1]] vector, uint k) {
    D uint8[[1]] y = (uint8)vector + 128;
    D xor_uint8[[1]] in = reshare (y);
    D xor_uint8[[1]] out = selectK (in, k);
    y = reshare (out) - 128;
    return (int8)y;
}

template <domain D : shared3p>
D int16[[1]] selectK (D int16[[1]] vector, uint k) {
    D uint16[[1]] y = (uint16)vector + 32768;
    D xor_uint16[[1]] in = reshare (y);
    D xor_uint16[[1]] out = selectK (in, k);
    y = reshare(out) - 32768;
    return (int16)y;
}

template <domain D : shared3p>
D int32[[1]] selectK (D int32[[1]] vector, uint k) {
    D uint32[[1]] y = (uint32)vector + 2147483648;
    D xor_uint32[[1]] in = reshare (y);
    D xor_uint32[[1]] out = selectK (in, k);
    y = reshare(out) - 2147483648;
    return (int32)y;
}

template <domain D : shared3p>
D int64[[1]] selectK (D int64[[1]] vector, uint k) {
    D uint64[[1]] y = (uint64)vector + 9223372036854775808;
    D xor_uint64[[1]] in = reshare (y);
    D xor_uint64[[1]] out = selectK (in, k);
    y = reshare(out) - 9223372036854775808;
    return (int64)y;
}
/**
 *\endcond
 */

template <domain D, type T>
D T[[1]] selectK (D T[[1]] vector, uint k) {
    uint[[1]] sortnet = generateTopKSortingNetwork (size(vector), k);
    uint offset = 0;
    uint numOfStages = sortnet[offset++];

    for (uint stage = 0; stage < numOfStages; stage++) {
        uint sizeOfStage = sortnet[offset++];

        D T[[1]] firstVector (sizeOfStage);
        D T[[1]] secondVector (sizeOfStage);
        D bool[[1]] exchangeFlagsVector (sizeOfStage);

        for (uint i = 0; i < sizeOfStage; i++) {
            firstVector[i] = vector[sortnet[offset]];
            secondVector[i] = vector[sortnet[offset+1]];
            offset += 2;
        }

        exchangeFlagsVector = firstVector <= secondVector;

        D bool[[2]] expandedExchangeFlagsVector (2, sizeOfStage);
        expandedExchangeFlagsVector[0,:] = exchangeFlagsVector;
        expandedExchangeFlagsVector[1,:] = exchangeFlagsVector;

        D T[[2]] firstFactor (2, sizeOfStage);
        D T[[2]] secondFactor (2, sizeOfStage);

        firstFactor[0, :] = firstVector;
        firstFactor[1, :] = secondVector;
        secondFactor[0, :] = secondVector;
        secondFactor[1, :] = firstVector;

        D T[[2]] choiceResults (2, sizeOfStage);

        choiceResults = choose(expandedExchangeFlagsVector,firstFactor,secondFactor);

        offset -= 2 * sizeOfStage;
        for (uint i = 0; i < sizeOfStage; i++) {
            vector[sortnet[offset++]] = choiceResults[0, i];
            vector[sortnet[offset++]] = choiceResults[1, i];
        }
    }

    return vector[:k];
}
/** @} */


/** \addtogroup selectk_mat
 *  @{
 *  @brief Function for selecting k rows from a matrix ordered by a column
 *  @note **D** - all protection domains
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int" / \ref xor_uint8 "xor_uint8" / \ref xor_uint16 "xor_uint16" / \ref xor_uint32 "xor_uint32" / \ref xor_uint64 "xor_uint64"
 *  @note The number of rows of the input matrix has to be a power of two.
 *  @note The algorithm behind this function is optimized for speed, accuracy is not guaranteed.
 *  @param matrix - a matrix of supported type
 *  @param k - number of elements to select
 *  @param column - column to select by
 *  @return returns a matrix with k rows selected from the input vector according to the input column index
 */
template <domain D : shared3p>
D uint8[[2]] selectK (D uint8[[2]] matrix, uint k, uint column) {
    D uint8[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint8[[1]] columnToSort = reshare(shuffledMatrix[:,column]);

    uint[[1]] publicIndices = iota(size(columnToSort));
    D xor_uint8[[1]] indexVector = (uint8) publicIndices;
    indexVector = _selectK(columnToSort, indexVector, k);
    publicIndices = (uint) declassify(indexVector);

    D uint8[[2]] out(k, shape(matrix)[1]);
    for (uint i = 0; i < k; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D uint16[[2]] selectK (D uint16[[2]] matrix, uint k, uint column) {
    D uint16[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint16[[1]] columnToSort = reshare(shuffledMatrix[:,column]);

    uint[[1]] publicIndices = iota(size(columnToSort));
    D xor_uint16[[1]] indexVector = (uint16) publicIndices;
    indexVector = _selectK(columnToSort, indexVector, k);
    publicIndices = (uint) declassify(indexVector);

    D uint16[[2]] out(k, shape(matrix)[1]);
    for (uint i = 0; i < k; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D uint32[[2]] selectK (D uint32[[2]] matrix, uint k, uint column) {
    D uint32[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint32[[1]] columnToSort = reshare(shuffledMatrix[:,column]);

    uint[[1]] publicIndices = iota(size(columnToSort));
    D xor_uint32[[1]] indexVector = (uint32) publicIndices;
    indexVector = _selectK(columnToSort, indexVector, k);
    publicIndices = (uint) declassify(indexVector);

    D uint32[[2]] out(k, shape(matrix)[1]);
    for (uint i = 0; i < k; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D uint64[[2]] selectK (D uint64[[2]] matrix, uint k, uint column) {
    D uint64[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint64[[1]] columnToSort = reshare(shuffledMatrix[:,column]);

    uint[[1]] publicIndices = iota(size(columnToSort));
    D xor_uint64[[1]] indexVector = publicIndices;
    indexVector = _selectK(columnToSort, indexVector, k);
    publicIndices = (uint) declassify(indexVector);

    D uint64[[2]] out(k, shape(matrix)[1]);
    for (uint i = 0; i < k; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D int8[[2]] selectK (D int8[[2]] matrix, uint k, uint column) {
    D int8[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint8[[1]] columnToSort = reshare((uint8) shuffledMatrix[:,column] + 128);

    uint[[1]] publicIndices = iota(size(columnToSort));
    D xor_uint8[[1]] indexVector = (uint8) publicIndices;
    indexVector = _selectK(columnToSort, indexVector, k);
    publicIndices = (uint) declassify(indexVector);

    D int8[[2]] out(k, shape(matrix)[1]);
    for (uint i = 0; i < k; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D int16[[2]] selectK (D int16[[2]] matrix, uint k, uint column) {
    D int16[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint16[[1]] columnToSort = reshare((uint16) shuffledMatrix[:,column] + 32768);

    uint[[1]] publicIndices = iota(size(columnToSort));
    D xor_uint16[[1]] indexVector = (uint16) publicIndices;
    indexVector = _selectK(columnToSort, indexVector, k);
    publicIndices = (uint) declassify(indexVector);

    D int16[[2]] out(k, shape(matrix)[1]);
    for (uint i = 0; i < k; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D int32[[2]] selectK (D int32[[2]] matrix, uint k, uint column) {
    D int32[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint32[[1]] columnToSort = reshare((uint32) shuffledMatrix[:,column] + 2147483648);

    uint[[1]] publicIndices = iota(size(columnToSort));
    D xor_uint32[[1]] indexVector = (uint32) publicIndices;
    indexVector = _selectK(columnToSort, indexVector, k);
    publicIndices = (uint) declassify(indexVector);

    D int32[[2]] out(k, shape(matrix)[1]);
    for (uint i = 0; i < k; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D int64[[2]] selectK (D int64[[2]] matrix, uint k, uint column) {
    D int64[[2]] shuffledMatrix = shuffleRows(matrix);
    D xor_uint64[[1]] columnToSort = reshare((uint64) shuffledMatrix[:,column] + 9223372036854775808);

    uint[[1]] publicIndices = iota(size(columnToSort));
    D xor_uint64[[1]] indexVector = (uint64) publicIndices;
    indexVector = _selectK(columnToSort, indexVector, k);
    publicIndices = (uint) declassify(indexVector);

    D int64[[2]] out(k, shape(matrix)[1]);
    for (uint i = 0; i < k; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i], :];
    }

    return out;
}

template <domain D : shared3p>
D xor_uint8[[2]] selectK (D xor_uint8[[2]] matrix, uint k, uint column) {
    D xor_uint8[[2]] shuffledMatrix = shuffleRows(matrix);

    uint[[1]] publicIndices = iota(shape(matrix)[0]);
    D xor_uint8[[1]] indexVector = (uint8) publicIndices;
    indexVector = _selectK(shuffledMatrix[:,column], indexVector, k);
    publicIndices = (uint) declassify(indexVector);

    D xor_uint8[[2]] out(k, shape(matrix)[1]);
    for (uint i = 0; i < k; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i],:];
    }

    return out;
}

template <domain D : shared3p>
D xor_uint16[[2]] selectK (D xor_uint16[[2]] matrix, uint k, uint column) {
    D xor_uint16[[2]] shuffledMatrix = shuffleRows(matrix);

    uint[[1]] publicIndices = iota(shape(matrix)[0]);
    D xor_uint16[[1]] indexVector = (uint16) publicIndices;
    indexVector = _selectK(shuffledMatrix[:,column], indexVector, k);
    publicIndices = (uint) declassify(indexVector);

    D xor_uint16[[2]] out(k, shape(matrix)[1]);
    for (uint i = 0; i < k; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i],:];
    }

    return out;
}

template <domain D : shared3p>
D xor_uint32[[2]] selectK (D xor_uint32[[2]] matrix, uint k, uint column) {
    D xor_uint32[[2]] shuffledMatrix = shuffleRows(matrix);

    uint[[1]] publicIndices = iota(shape(matrix)[0]);
    D xor_uint32[[1]] indexVector = (uint32) publicIndices;
    indexVector = _selectK(shuffledMatrix[:,column], indexVector, k);
    publicIndices = (uint) declassify(indexVector);

    D xor_uint32[[2]] out(k, shape(matrix)[1]);
    for (uint i = 0; i < k; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i],:];
    }

    return out;
}

template <domain D : shared3p>
D xor_uint64[[2]] selectK (D xor_uint64[[2]] matrix, uint k, uint column) {
    D xor_uint64[[2]] shuffledMatrix = shuffleRows(matrix);

    uint[[1]] publicIndices = iota(shape(matrix)[0]);
    D xor_uint64[[1]] indexVector = (uint64) publicIndices;
    indexVector = _selectK(shuffledMatrix[:,column], indexVector, k);
    publicIndices = (uint) declassify(indexVector);

    D xor_uint64[[2]] out(k, shape(matrix)[1]);
    for (uint i = 0; i < k; i++) {
        out[i, :] = shuffledMatrix[publicIndices[i],:];
    }

    return out;
}

/**
 * \cond
 */
template <domain D : shared3p, type T>
D T[[1]] _selectK (D T[[1]] vector, D T[[1]] indices, uint k) {
    uint[[1]] sortnet = generateTopKSortingNetwork (size(vector), k);
    uint offset = 0;
    uint numOfStages = sortnet[offset++];

    for (uint stage = 0; stage < numOfStages; stage++) {
        uint sizeOfStage = sortnet[offset++];

        D T[[1]] first (2 * sizeOfStage), second (2 * sizeOfStage);

        for (uint i = 0; i < sizeOfStage; ++i) {
            first[i]                = vector[sortnet[offset+0]];
            first[i + sizeOfStage]  = indices[sortnet[offset+0]];
            second[i]               = vector[sortnet[offset+1]];
            second[i + sizeOfStage] = indices[sortnet[offset+1]];
            offset += 2;
        }

        D bool[[1]] exchangeFlagsVector = first[:sizeOfStage] <= second[:sizeOfStage];
        exchangeFlagsVector = cat (exchangeFlagsVector, exchangeFlagsVector);

        D T[[1]] results = choose(exchangeFlagsVector, first, second);

        second = results ^ first ^ second;
        first = results;

        offset -= 2 * sizeOfStage;
        for (uint i = 0; i < sizeOfStage; ++ i) {
            vector[sortnet[offset+0]]  = first[i];
            indices[sortnet[offset+0]] = first[i + sizeOfStage];
            vector[sortnet[offset+1]]  = second[i];
            indices[sortnet[offset+1]] = second[i + sizeOfStage];
            offset += 2;
        }
    }

    return indices;
}
/**
 * \endcond
 */

/** @}*/
/** @}*/

/*******************************************************************************
********************************************************************************
**                                                                            **
**  radixsort                                                                 **
**                                                                            **
********************************************************************************
*******************************************************************************/



// FIXME: Actually radix sort is not for a3p

/**
* \cond
*/

template <domain D, type T>
D T[[1]] _radixSort(D T[[1]] array) {

    if (size(array) <= 1) {
        return array;
    }

    uint32 extractSectType = newSectionType ("radixsort_extract");
    uint32 castSectType = newSectionType ("radixsort_cast");
    uint32 choiceSectType = newSectionType ("radixsort_choice");
    uint32 shuffleSectType = newSectionType ("radixsort_shuffle");
    uint32 declassifySectType = newSectionType ("radixsort_declassify");

    uint n = size(array);

    // Determine the bit length of inputs
    D T x;
    D bool[[1]] xbits = bit_extract(x);
    uint nrOfBits = size(xbits);

    for (uint k = 0; k < nrOfBits; k++) {
        // TODO: Maybe we can do these transformations only once and then
        // do bool[[2]] -> xor_uint[[1]] in the end?
        // It only increases parallelism.
        uint32 extractSect = startSection(extractSectType, n);
        D bool[[1]] bitVec = bit_extract(array);
        endSection (extractSect);

        uint32 castSect = startSection(castSectType, n);
        D bool[[2]] bitMatrix = reshape(bitVec, n, nrOfBits);
        D bool[[1]] bitCol = bitMatrix[:,k];
        D uint[[1]] bitColMod2n = (uint) bitCol;
        endSection(castSect);

        uint32 choiceSect = startSection(choiceSectType, n);
        D uint nrOfZeros = n - sum(bitColMod2n);
        D uint constOne = 1;

        // Obliviously construct counter vectors for zeros and ones
        D uint[[1]] c0 (n);
        D uint[[1]] c1 (n);

        c0[0] = constOne - bitColMod2n[0];
        c1[0] = nrOfZeros + bitColMod2n[0];

        for (uint i = 1; i < n; i++) {
            c0[i] = c0[i-1] + constOne - bitColMod2n[i];
            c1[i] = c1[i-1] + bitColMod2n[i];
        }

        D uint[[1]] order = choose(bitCol, c1, c0);
        endSection(choiceSect);

        // Shuffle data vector and order vector
        uint32 shuffleSect = startSection(shuffleSectType, n);
        D uint8[[1]] key (32);
        key = randomize(key);

        order = shuffle(order, key);
        D T[[1]] shuffledArray = shuffle(array, key);
        endSection(shuffleSect);

        // Reorder data vector according to the order vector:
        // Note: Order values start at 1
        uint32 declassifySect = startSection(declassifySectType, n);
        uint[[1]] publicOrder = declassify(order);

        for (uint i = 0; i < n; i++) {
            array[publicOrder[i]-1] = shuffledArray[i];
        }
        endSection (declassifySect);
    }

    return array;
}
/**
* \endcond
*/


/** \addtogroup radix_sort
 *  @{
 *  @brief Functions for sorting values using the radix sort algorithm
 *  @note **D** - all protection domains
 *  @note Supported types - \ref uint64 "uint" / \ref xor_uint64 "xor_uint64"
 *  @leakage{Shuffled reordering decisions are declassified}
 */

/** \addtogroup radix_sort_vector
 *  @{
 *  @brief Functions for sorting values in a vector using the radix sort algorithm
 *  @note **D** - all protection domains
 *  @note Supported types - \ref uint64 "uint" / \ref xor_uint64 "xor_uint64"
 *  @param array - a vector of supported type
 *  @return returns a sorted vector from smaller to bigger values
 *  @leakage{Shuffled reordering decisions are declassified}
 */
template <domain D>
D uint64[[1]] radixSort(D uint64[[1]] array) {

    uint n = size(array);

    uint32 sortSectType = newSectionType ("radixsort_sort");
    uint32 reshareSectType = newSectionType ("radixsort_reshare");

    uint32 sortSect = startSection(sortSectType, n);
    uint32 reshareSect = startSection(reshareSectType, n);
    D xor_uint64[[1]] xor_array = reshare(array);
    endSection(reshareSect);

    D xor_uint64[[1]] sorted = _radixSort(xor_array);

    reshareSect = startSection(reshareSectType, n);
    D uint64[[1]] result = reshare(sorted);
    endSection(reshareSect);
    endSection(sortSect);

    return result;
}

template <domain D>
D xor_uint64[[1]] radixSort(D xor_uint64[[1]] array) {

    uint n = size(array);

    uint32 sortSectType = newSectionType ("radixsort_sort");
    uint32 sortSect = startSection(sortSectType, n);
    D xor_uint64[[1]] sorted = _radixSort(array);
    endSection(sortSect);

    return sorted;
}

/** @}*/

// FIXME: Actually radix sort is not for a3p


/** \addtogroup radix_sort_index
 *  @{
 *  @brief Function for sorting values in a vector using the radix sort algorithm
 *  @note **D** - all protection domains
 *  @note Supported types - \ref uint64 "uint" / \ref xor_uint64 "xor_uint64"
 *  @param array - a vector of supported type
 *  @param indexVector - a vector of indexes that correspond to a value in the input array
 *  @return returns the sorted indexVector where every index corresponds to a value in the input vector
 *  @leakage{Shuffled reordering decisions are declassified}
 */
template <domain D, type T>
D uint[[1]] radixSortWithIndex(D T[[1]] array, D uint[[1]] indexVector) {

    if (size(array) <= 1) {
        return indexVector;
    }

    assert(size(array) == size(indexVector));

    uint32 extractSectType = newSectionType ("radixsort_extract");
    uint32 castSectType = newSectionType ("radixsort_cast");
    uint32 choiceSectType = newSectionType ("radixsort_choice");
    uint32 shuffleSectType = newSectionType ("radixsort_shuffle");
    uint32 declassifySectType = newSectionType ("radixsort_declassify");

    uint n = size(array);

    // Determine the bit length of inputs
    D T x;
    D bool[[1]] xbits = bit_extract(x);
    uint nrOfBits = size(xbits);

    for (uint k = 0; k < nrOfBits; k++) {
        // TODO: Maybe we can do these transformations only once and then
        // do bool[[2]] -> xor_uint[[1]] in the end?
        // It only increases parallelism.
        uint32 extractSect = startSection(extractSectType, n);
        D bool[[1]] bitVec = bit_extract(array);
        endSection (extractSect);

        uint32 castSect = startSection(castSectType, n);
        D bool[[2]] bitMatrix = reshape(bitVec, n, nrOfBits);
        D bool[[1]] bitCol = bitMatrix[:,k];
        D uint[[1]] bitColMod2n = (uint) bitCol;
        endSection(castSect);

        uint32 choiceSect = startSection(choiceSectType, n);
        D uint nrOfZeros = n - sum(bitColMod2n);
        D uint constOne = 1;

        // Obliviously construct counter vectors for zeros and ones
        D uint[[1]] c0 (n);
        D uint[[1]] c1 (n);

        c0[0] = constOne - bitColMod2n[0];
        c1[0] = nrOfZeros + bitColMod2n[0];

        for (uint i = 1; i < n; i++) {
            c0[i] = c0[i-1] + constOne - bitColMod2n[i];
            c1[i] = c1[i-1] + bitColMod2n[i];
        }

        D uint[[1]] order = choose(bitCol, c1, c0);
        endSection(choiceSect);

        // Shuffle data vector and order vector
        uint32 shuffleSect = startSection(shuffleSectType, n);
        D uint8[[1]] key (32);
        key = randomize(key);

        order = shuffle(order, key);
        D T[[1]] shuffledArray = shuffle(array, key);
        D uint[[1]] shuffledIndex = shuffle(indexVector, key);
        endSection(shuffleSect);

        // Reorder data vector according to the order vector:
        // Note: Order values start at 1

        uint32 declassifySect = startSection(declassifySectType, n);
        uint[[1]] publicOrder = declassify(order);

        for (uint i = 0; i < n; i++) {
            array[publicOrder[i]-1] = shuffledArray[i];
            indexVector[publicOrder[i]-1] = shuffledIndex[i];
        }
        endSection(declassifySect);
    }

    return indexVector;
}

/** @}*/

/**
* \cond
*/

template <domain D, type T>
D T[[2]] _radixSort(D T[[2]] matrix, uint column1) {
    uint[[1]] matShape = shape(matrix);

    assert(matShape[1] > column1);

    // Shuffle the matrix
    uint32 shuffleSectType = newSectionType ("radixsort_shuffle");
    uint32 shuffleSect = startSection(shuffleSectType, matShape[0]);
    D T[[2]] shuffledMatrix = shuffleRows(matrix);
    endSection(shuffleSect);

    // Construct index vector
    D uint[[1]] indexVector (matShape[0]);
    for (uint i = 0; i < matShape[0]; ++i) {
        indexVector[i] = i;
    }

    // Pick column and sort
    D T[[1]] columnToSort = shuffledMatrix[:,column1];
    indexVector = radixSortWithIndex(columnToSort, indexVector);

    // Reorder the rest of the matrix by index vector
    uint[[1]] publicIndexVector = declassify(indexVector);
    for (uint i = 0; i < matShape[0]; i++) {
        matrix[i,:] = shuffledMatrix[publicIndexVector[i],:];

	}
    return matrix;
}

template <domain D>
D xor_uint64[[2]] radixSort(D xor_uint64[[2]] array, uint column1) {

    uint[[1]] matShape = shape(array);

    uint32 sortSectType = newSectionType ("radixsort_sort");
    uint32 sortSect = startSection(sortSectType, matShape[0]);
    D xor_uint64[[2]] sorted = _radixSort(array, column1);
    endSection(sortSect);

    return sorted;
}

/**
* \endcond
*/


/** \addtogroup radix_sort_matrix
 *  @{
 *  @brief Functions for sorting rows in a matrix using the radix sort algorithm
 *  @note **D** - all protection domains
 *  @note Supported types - \ref uint64 "uint" / \ref xor_uint64 "xor_uint64"
 *  @param array - a vector of supported type
 *  @param column - the index of the sorting column
 *  @return returns a matrix where the rows are sorted based on the column given
 *  @leakage{Shuffled reordering decisions are declassified}
 */
template <domain D>
D uint64[[2]] radixSort(D uint64[[2]] array, uint column1) {

    uint[[1]] matShape = shape(array);

    uint32 sortSectType = newSectionType ("radixsort_sort");
    uint32 reshareSectType = newSectionType ("radixsort_reshare");

    uint32 sortSect = startSection(sortSectType, matShape[0]);
    uint32 reshareSect = startSection(reshareSectType, matShape[0]);
    D xor_uint64[[2]] xor_array = reshare(array);
    endSection(reshareSect);

    D xor_uint64[[2]] sorted = _radixSort(xor_array, column1);

    reshareSect = startSection(reshareSectType, matShape[0]);
    D uint64[[2]] result = reshare(sorted);
    endSection(reshareSect);
    endSection(sortSect);

    return result;
}
/** @}*/
/** @}*/

/**
 * \cond
 */
template<domain D : shared3p, type T>
uint[[1]] _unsafeSort(D T[[1]] vec, D xor_uint64[[1]] indices, bool ascending) {
    assert(size(vec) == size(indices));

    uint[[1]] perm = iota(size(vec));
    __syscall("shared3p::stable_sort_$T\_vec",
              __domainid(D), vec, indices, ascending, __ref perm);

    return perm;
}
/**
 * \endcond
 */

/** \addtogroup unsafe_sort
 *  @{
 *  @brief Function for sorting a vector if the vector has been
 *  shuffled
 *  @note unsafeSort is stable
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int" / \ref xor_uint8 "xor_uint8" / \ref xor_uint16 "xor_uint16" / \ref xor_uint32 "xor_uint32" / \ref xor_uint64 "xor_uint64"
 *  @param vec - input vector
 *  @param indices - an index vector indicating which indice an
 *  element of the shuffled vector originated from
 *  @param ascending - a boolean indicating if the vector should be
 *  sorted in ascending order
 *  @return returns the sort permutation. The ith value of the
 *  permutation is the index of the input value that is in the ith
 *  position after sorting.
 *  @leakage{Nothing is leaked if the input has been shuffled \n If
 *           the input is not shuffled, the results of comparisons are
 *           leaked}
 */
template<domain D : shared3p>
uint[[1]] unsafeSort(D xor_uint8[[1]] vec, D xor_uint64[[1]] indices, bool ascending) {
    return _unsafeSort(vec, indices, ascending);
}

template<domain D : shared3p>
uint[[1]] unsafeSort(D xor_uint16[[1]] vec, D xor_uint64[[1]] indices, bool ascending) {
    return _unsafeSort(vec, indices, ascending);
}

template<domain D : shared3p>
uint[[1]] unsafeSort(D xor_uint32[[1]] vec, D xor_uint64[[1]] indices, bool ascending) {
    return _unsafeSort(vec, indices, ascending);
}

template<domain D : shared3p>
uint[[1]] unsafeSort(D xor_uint64[[1]] vec, D xor_uint64[[1]] indices, bool ascending) {
    return _unsafeSort(vec, indices, ascending);
}

template<domain D : shared3p>
uint[[1]] unsafeSort(D uint8[[1]] vec, D xor_uint64[[1]] indices, bool ascending) {
    return _unsafeSort(vec, indices, ascending);
}

template<domain D : shared3p>
uint[[1]] unsafeSort(D uint16[[1]] vec, D xor_uint64[[1]] indices, bool ascending) {
    return _unsafeSort(vec, indices, ascending);
}

template<domain D : shared3p>
uint[[1]] unsafeSort(D uint32[[1]] vec, D xor_uint64[[1]] indices, bool ascending) {
    return _unsafeSort(vec, indices, ascending);
}

template<domain D : shared3p>
uint[[1]] unsafeSort(D uint64[[1]] vec, D xor_uint64[[1]] indices, bool ascending) {
    return _unsafeSort(vec, indices, ascending);
}

template<domain D : shared3p>
uint[[1]] unsafeSort(D int8[[1]] vec, D xor_uint64[[1]] indices, bool ascending) {
    return _unsafeSort(vec, indices, ascending);
}

template<domain D : shared3p>
uint[[1]] unsafeSort(D int16[[1]] vec, D xor_uint64[[1]] indices, bool ascending) {
    return _unsafeSort(vec, indices, ascending);
}

template<domain D : shared3p>
uint[[1]] unsafeSort(D int32[[1]] vec, D xor_uint64[[1]] indices, bool ascending) {
    return _unsafeSort(vec, indices, ascending);
}

template<domain D : shared3p>
uint[[1]] unsafeSort(D int64[[1]] vec, D xor_uint64[[1]] indices, bool ascending) {
    return _unsafeSort(vec, indices, ascending);
}
/** @} */

/*******************************************************************************
********************************************************************************
**                                                                            **
**  quicksort                                                                 **
**                                                                            **
********************************************************************************
*******************************************************************************/

/** \addtogroup quick_sort
 *  @{
 *  @brief Functions for sorting values using the quicksort algorithm
 *  @note quicksort is stable
 *  @note **D** - all protection domains
 *  @note Supported types - \ref xor_uint64 "xor_uint64"
 *  @leakage{None}
 */

/**
* \cond
*/
template<domain D : shared3p, type T>
D T[[1]] _quicksort(D T[[1]] vec, bool ascending) {
    uint[[1]] idx = iota(size(vec));
    D xor_uint64[[1]] ind = idx;

    D uint8[[1]] k(32);
    k = randomize(k);
    vec = shuffle(vec, k);
    ind = shuffle(ind, k);

    uint[[1]] p = unsafeSort(vec, ind, ascending);
    D T[[1]] res(size(vec));
    for (uint i = 0; i < size(res); ++i) {
        res[i] = vec[p[i]];
    }

    return res;
}

template<domain D : shared3p, type T>
D T[[2]] _quicksort(D T[[2]] matrix, uint column, bool ascending) {
    uint[[1]] matShape = shape(matrix);
    assert(matShape[1] > column);
    uint[[1]] idx = iota(matShape[0]);
    D xor_uint64[[1]] ind = idx;

    D uint8[[1]] k(32);
    k = randomize(k);
    matrix = shuffleRows(matrix, k);
    ind = shuffle(ind, k);

    uint[[1]] p = unsafeSort(matrix[:, column], ind, ascending);
    D T[[2]] res(matShape[0], matShape[1]);
    for (uint i = 0; i < matShape[0]; ++i) {
        res[i, :] = matrix[p[i], :];
    }

    return res;
}
/**
* \endcond
*/

/** \addtogroup quick_sort_matrix
 *  @{
 *  @brief Functions for sorting rows in a matrix using the quicksort
 *  algorithm
 *  @note quicksort is stable
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int" / \ref xor_uint8 "xor_uint8" / \ref xor_uint16 "xor_uint16" / \ref xor_uint32 "xor_uint32" / \ref xor_uint64 "xor_uint64"
 *  @param matrix - a matrix of supported type
 *  @param column - the index of the sorting column
 *  @return returns a matrix where the input matrix rows are sorted based on values of the specified column
 *  @leakage{None}
 */
template<domain D : shared3p>
D xor_uint8[[2]] quicksort(D xor_uint8[[2]] matrix, uint column) {
    return _quicksort(matrix, column, true);
}

template<domain D : shared3p>
D xor_uint16[[2]] quicksort(D xor_uint16[[2]] matrix, uint column) {
    return _quicksort(matrix, column, true);
}

template<domain D : shared3p>
D xor_uint32[[2]] quicksort(D xor_uint32[[2]] matrix, uint column) {
    return _quicksort(matrix, column, true);
}

template<domain D : shared3p>
D xor_uint64[[2]] quicksort(D xor_uint64[[2]] matrix, uint column) {
    return _quicksort(matrix, column, true);
}

template<domain D : shared3p>
D uint8[[2]] quicksort(D uint8[[2]] matrix, uint column) {
    return _quicksort(matrix, column, true);
}

template<domain D : shared3p>
D uint16[[2]] quicksort(D uint16[[2]] matrix, uint column) {
    return _quicksort(matrix, column, true);
}

template<domain D : shared3p>
D uint32[[2]] quicksort(D uint32[[2]] matrix, uint column) {
    return _quicksort(matrix, column, true);
}

template<domain D : shared3p>
D uint64[[2]] quicksort(D uint64[[2]] matrix, uint column) {
    return _quicksort(matrix, column, true);
}

template<domain D : shared3p>
D int8[[2]] quicksort(D int8[[2]] matrix, uint column) {
    return _quicksort(matrix, column, true);
}

template<domain D : shared3p>
D int16[[2]] quicksort(D int16[[2]] matrix, uint column) {
    return _quicksort(matrix, column, true);
}

template<domain D : shared3p>
D int32[[2]] quicksort(D int32[[2]] matrix, uint column) {
    return _quicksort(matrix, column, true);
}

template<domain D : shared3p>
D int64[[2]] quicksort(D int64[[2]] matrix, uint column) {
    return _quicksort(matrix, column, true);
}
/** @}*/

/** \addtogroup quick_sort_matrix_direction
 *  @{
 *  @brief Functions for sorting rows in a matrix using the quicksort algorithm
 *  @note quicksort is stable
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int" / \ref xor_uint8 "xor_uint8" / \ref xor_uint16 "xor_uint16" / \ref xor_uint32 "xor_uint32" / \ref xor_uint64 "xor_uint64"
 *  @param matrix - a matrix of supported type
 *  @param column - the index of the sorting column
 *  @param ascending - a boolean indicating if the input should be sorted in ascending order
 *  @return returns a matrix where the input matrix rows are sorted based on values of the specified column
 *  @leakage{None}
 */
template<domain D : shared3p>
D xor_uint8[[2]] quicksort(D xor_uint8[[2]] matrix, uint column, bool ascending) {
    return _quicksort(matrix, column, ascending);
}

template<domain D : shared3p>
D xor_uint16[[2]] quicksort(D xor_uint16[[2]] matrix, uint column, bool ascending) {
    return _quicksort(matrix, column, ascending);
}

template<domain D : shared3p>
D xor_uint32[[2]] quicksort(D xor_uint32[[2]] matrix, uint column, bool ascending) {
    return _quicksort(matrix, column, ascending);
}

template<domain D : shared3p>
D xor_uint64[[2]] quicksort(D xor_uint64[[2]] matrix, uint column, bool ascending) {
    return _quicksort(matrix, column, ascending);
}

template<domain D : shared3p>
D uint8[[2]] quicksort(D uint8[[2]] matrix, uint column, bool ascending) {
    return _quicksort(matrix, column, ascending);
}

template<domain D : shared3p>
D uint16[[2]] quicksort(D uint16[[2]] matrix, uint column, bool ascending) {
    return _quicksort(matrix, column, ascending);
}

template<domain D : shared3p>
D uint32[[2]] quicksort(D uint32[[2]] matrix, uint column, bool ascending) {
    return _quicksort(matrix, column, ascending);
}

template<domain D : shared3p>
D uint64[[2]] quicksort(D uint64[[2]] matrix, uint column, bool ascending) {
    return _quicksort(matrix, column, ascending);
}

template<domain D : shared3p>
D int8[[2]] quicksort(D int8[[2]] matrix, uint column, bool ascending) {
    return _quicksort(matrix, column, ascending);
}

template<domain D : shared3p>
D int16[[2]] quicksort(D int16[[2]] matrix, uint column, bool ascending) {
    return _quicksort(matrix, column, ascending);
}

template<domain D : shared3p>
D int32[[2]] quicksort(D int32[[2]] matrix, uint column, bool ascending) {
    return _quicksort(matrix, column, ascending);
}

template<domain D : shared3p>
D int64[[2]] quicksort(D int64[[2]] matrix, uint column, bool ascending) {
    return _quicksort(matrix, column, ascending);
}
/** @}*/

/** \addtogroup quick_sort_vector
 *  @{
 *  @brief Functions for sorting values in a matrix using the quicksort algorithm
 *  @note quicksort is stable
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int" / \ref xor_uint8 "xor_uint8" / \ref xor_uint16 "xor_uint16" / \ref xor_uint32 "xor_uint32" / \ref xor_uint64 "xor_uint64"
 *  @param vec - a vector of supported type
 *  @return returns the input vector sorted in ascending order
 *  @leakage{None}
 */
template<domain D : shared3p>
D xor_uint8[[1]] quicksort(D xor_uint8[[1]] vec) {
    return _quicksort(vec, true);
}

template<domain D : shared3p>
D xor_uint16[[1]] quicksort(D xor_uint16[[1]] vec) {
    return _quicksort(vec, true);
}

template<domain D : shared3p>
D xor_uint32[[1]] quicksort(D xor_uint32[[1]] vec) {
    return _quicksort(vec, true);
}

template<domain D : shared3p>
D xor_uint64[[1]] quicksort(D xor_uint64[[1]] vec) {
    return _quicksort(vec, true);
}

template<domain D : shared3p>
D uint8[[1]] quicksort(D uint8[[1]] vec) {
    return _quicksort(vec, true);
}

template<domain D : shared3p>
D uint16[[1]] quicksort(D uint16[[1]] vec) {
    return _quicksort(vec, true);
}

template<domain D : shared3p>
D uint32[[1]] quicksort(D uint32[[1]] vec) {
    return _quicksort(vec, true);
}

template<domain D : shared3p>
D uint64[[1]] quicksort(D uint64[[1]] vec) {
    return _quicksort(vec, true);
}

template<domain D : shared3p>
D int8[[1]] quicksort(D int8[[1]] vec) {
    return _quicksort(vec, true);
}

template<domain D : shared3p>
D int16[[1]] quicksort(D int16[[1]] vec) {
    return _quicksort(vec, true);
}

template<domain D : shared3p>
D int32[[1]] quicksort(D int32[[1]] vec) {
    return _quicksort(vec, true);
}

template<domain D : shared3p>
D int64[[1]] quicksort(D int64[[1]] vec) {
    return _quicksort(vec, true);
}
/** @}*/

/** \addtogroup quick_sort_vector_direction
 *  @{
 *  @brief Functions for sorting values in a matrix using the quicksort algorithm
 *  @note quicksort is stable
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref uint8 "uint8" / \ref uint16 "uint16" / \ref uint32 "uint32" / \ref uint64 "uint" / \ref int8 "int8" / \ref int16 "int16" / \ref int32 "int32" / \ref int64 "int" / \ref xor_uint8 "xor_uint8" / \ref xor_uint16 "xor_uint16" / \ref xor_uint32 "xor_uint32" / \ref xor_uint64 "xor_uint64"
 *  @param vec - a vector of supported type
 *  @param ascending - whether to sort in ascending (**true**) or descending (**false**) order
 *  @return returns the sorted input vector
 *  @leakage{None}
 */
template<domain D : shared3p>
D xor_uint8[[1]] quicksort(D xor_uint8[[1]] vec, bool ascending) {
    return _quicksort(vec, ascending);
}

template<domain D : shared3p>
D xor_uint16[[1]] quicksort(D xor_uint16[[1]] vec, bool ascending) {
    return _quicksort(vec, ascending);
}

template<domain D : shared3p>
D xor_uint32[[1]] quicksort(D xor_uint32[[1]] vec, bool ascending) {
    return _quicksort(vec, ascending);
}

template<domain D : shared3p>
D xor_uint64[[1]] quicksort(D xor_uint64[[1]] vec, bool ascending) {
    return _quicksort(vec, ascending);
}

template<domain D : shared3p>
D uint8[[1]] quicksort(D uint8[[1]] vec, bool ascending) {
    return _quicksort(vec, ascending);
}

template<domain D : shared3p>
D uint16[[1]] quicksort(D uint16[[1]] vec, bool ascending) {
    return _quicksort(vec, ascending);
}

template<domain D : shared3p>
D uint32[[1]] quicksort(D uint32[[1]] vec, bool ascending) {
    return _quicksort(vec, ascending);
}

template<domain D : shared3p>
D uint64[[1]] quicksort(D uint64[[1]] vec, bool ascending) {
    return _quicksort(vec, ascending);
}

template<domain D : shared3p>
D int8[[1]] quicksort(D int8[[1]] vec, bool ascending) {
    return _quicksort(vec, ascending);
}

template<domain D : shared3p>
D int16[[1]] quicksort(D int16[[1]] vec, bool ascending) {
    return _quicksort(vec, ascending);
}

template<domain D : shared3p>
D int32[[1]] quicksort(D int32[[1]] vec, bool ascending) {
    return _quicksort(vec, ascending);
}

template<domain D : shared3p>
D int64[[1]] quicksort(D int64[[1]] vec, bool ascending) {
    return _quicksort(vec, ascending);
}
/** @}*/
/** @}*/

/*******************************************************************************
********************************************************************************
**                                                                            **
**  quick quicksort                                                           **
**                                                                            **
********************************************************************************
*******************************************************************************/

/** \addtogroup quick_quick_sort
 *  @{
 *  @brief Functions for sorting values using the quicksort algorithm
 *  @deprecated Use quicksort
 *  @note **D** - all protection domains
 *  @note Supported types - \ref xor_uint64 "xor_uint64"
 *  @leakage{Shuffled reordering decisions are declassified \n Leaks the number of equal elements}
 */


/**
* \cond
*/
template <domain D, type T>
D T[[1]] _quickquicksort(D T[[1]] array) {
    uint m = size(array);

    uint32 shuffleSectType = newSectionType ("quicksort_shuffle");
    uint32 sortSectType = newSectionType ("quicksort_sort");
    //uint32 partitionSectType = newSectionType ("quicksort_partition");
    uint32 compareSectType = newSectionType ("quicksort_comparison");
    uint32 declassifySectType = newSectionType ("quicksort_declassify");

    uint32 shuffleSect = startSection(shuffleSectType, m);
    array = shuffle(array);
    endSection(shuffleSect);

    uint32 sortSect = startSection(sortSectType, m);

    uint jobs = 0; // Number of jobs to do
    uint[[1]] begin (m); // Holds a starting index for each job
    uint[[1]] end (m); // Holds a last index for each job

    // Initialize:
    jobs = 1;
    begin[0] = 0;
    end[0] = m-1;

    D T[[1]] pivots (m); // reused
    D T[[1]] others (m);  // reused
    uint[[1]] offset (m); // reused

    // Main loop:
    while (jobs > 0) {
        uint32 compareSect = startSection(compareSectType, m);

        offset[0] = 0;

        for (uint i = 0; i < jobs; i++) {
            offset[i+1] = offset[i] + end[i] - begin[i];
            others[offset[i]:offset[i+1]] = array[begin[i]:end[i]]; // left side of '<=' op.
            pivots[offset[i]:offset[i+1]] = array[end[i]]; // right side of '<=' op.
        }

        // Do all comparisons in parallel:
        D bool[[1]] comp = (others[:offset[jobs]] <= pivots[:offset[jobs]]);
        endSection(compareSect);

        uint32 declassifySect = startSection(declassifySectType, m);
        bool[[1]] publicComp = declassify(comp);
        endSection(declassifySect);

        // Partition jobs
        uint jobs_ = 0;
        uint[[1]] begin_ (m);
        uint[[1]] end_ (m);

        for (uint i = 0; i < jobs; i++) {
            
            // Find partition point p:
            uint idx = begin[i] - 1; // It is OK even if idx < 0, as it is always increased before use

            for (uint j = begin[i]; j < end[i]; j++) {
                if (publicComp[offset[i] + j - begin[i]] == true) {
                    idx++;
                    // Swap a[idx] and a[j]
                    D T tmp = array[idx];
                    array[idx] = array[j];
                    array[j] = tmp;
                }
            }
            uint p = idx + 1;
            // Swap a[p] and a[end[i]]
            D T tmp = array[p];
            array[p] = array[end[i]];
            array[end[i]] = tmp;

            // Make new jobs, if any:
            if (p - begin[i] >= 2) {
                begin_[jobs_] = begin[i];
                end_[jobs_] = p-1;
                jobs_++;
            }
            if (end[i] - p >= 2) {
                begin_[jobs_] = p+1;
                end_[jobs_] = end[i];
                jobs_++;
            }
        }
        jobs = jobs_;
        begin = begin_;
        end = end_;
    }

    endSection (sortSect);
    return array;
}
/**
* \endcond
*/

/** \addtogroup quick_quick_sort_vector
 *  @{
 *  @brief Functions for sorting values in a matrix using the quicksort algorithm
 *  @deprecated Use quicksort
 *  @note **D** - all protection domains
 *  @note Supported types -  \ref uint64 "uint64" / \ref xor_uint64 "xor_uint64"
 *  @param array - a vector of supported type
 *  @return returns a vector where the values are sorted from smallest to greatest 
 *  @leakage{Shuffled reordering decisions are declassified \n Leaks the number of equal elements}
 */

template <domain D>
D xor_uint64[[1]] quickquicksort(D xor_uint64[[1]] array) {

    uint n = size(array);

    uint32 sortSectType = newSectionType ("quicksort_total");
    uint32 sortSect = startSection(sortSectType, n);
    D xor_uint64[[1]] sorted = _quickquicksort(array);
    endSection(sortSect);

    return sorted;
}
/** @}*/

/**
* \cond
*/
template <domain D>
D uint64[[1]] quickquicksort(D uint64[[1]] array) {

    uint n = size(array);

    uint32 sortSectType = newSectionType ("quicksort_total");
    uint32 reshareSectType = newSectionType ("quicksort_reshare");

    uint32 sortSect = startSection(sortSectType, n);
    uint32 reshareSect = startSection(reshareSectType, n);
    D xor_uint64[[1]] xor_array = reshare(array);
    endSection(reshareSect);

    D xor_uint64[[1]] sorted = _quickquicksort(xor_array);

    reshareSect = startSection(reshareSectType, n);
    D uint64[[1]] result = reshare(sorted);
    endSection(reshareSect);
    endSection(sortSect);

    return result;
}

template <domain D, type T>
D T[[2]] _quickquicksort(D T[[2]] matrix, uint column1) {
    uint[[1]] matShape = shape(matrix);

    assert(matShape[1] > column1);

    uint32 shuffleSectType = newSectionType ("quicksort_shuffle");
    uint32 sortSectType = newSectionType ("quicksort_sort");
    //uint32 partitionSectType = newSectionType ("quicksort_partition");
    uint32 compareSectType = newSectionType ("quicksort_comparison");
    uint32 declassifySectType = newSectionType ("quicksort_declassify");

    // Shuffle the matrix
    uint32 shuffleSect = startSection(shuffleSectType, product(matShape));
    D T[[2]] shuffledMatrix = shuffleRows(matrix);
    endSection(shuffleSect);

    uint32 sortSect = startSection(sortSectType, product(matShape));

    uint m = matShape[0];

    // Construct index vector
    D uint[[1]] indexVector (m);
    for (uint i = 0; i < m; ++i) {
        indexVector[i] = i;
    }

    // Pick column and sort
    D T[[1]] array = shuffledMatrix[:,column1];

    // The usual quicksort for array
    uint jobs = 0; // Number of jobs to do
    uint[[1]] begin (m); // Holds a starting index for each job
    uint[[1]] end (m); // Holds a last index for each job

    // Initialize:
    jobs = 1;
    begin[0] = 0;
    end[0] = m-1;

    D T[[1]] pivots (m); // reused
    D T[[1]] others (m);  // reused
    uint[[1]] offset (m); // reused

    // Main loop:
    while (jobs > 0) {
        uint32 compareSect = startSection(compareSectType, product(matShape));

        offset[0] = 0;

        for (uint i = 0; i < jobs; i++) {
            offset[i+1] = offset[i] + end[i] - begin[i];
            others[offset[i]:offset[i+1]] = array[begin[i]:end[i]]; // left side of '<=' op.
            pivots[offset[i]:offset[i+1]] = array[end[i]]; // right side of '<=' op.
        }

        // Do all comparisons in parallel:
        D bool[[1]] comp = (others[:offset[jobs]] <= pivots[:offset[jobs]]);
        endSection(compareSect);

        uint32 declassifySect = startSection(declassifySectType, product(matShape));
        bool[[1]] publicComp = declassify(comp);
        endSection(declassifySect);

        // Partition jobs
        uint jobs_ = 0;
        uint[[1]] begin_ (m);
        uint[[1]] end_ (m);

        for (uint i = 0; i < jobs; i++) {
            
            // Find partition point p:
            uint idx = begin[i] - 1; // It is OK even if idx < 0, as it is always increased before use

            for (uint j = begin[i]; j < end[i]; j++) {
                if (publicComp[offset[i] + j - begin[i]] == true) {
                    idx++;
                    // Swap a[idx] and a[j], do the same for index vector
                    D T tmp = array[idx];
                    array[idx] = array[j];
                    array[j] = tmp;
                    D uint tmp2 = indexVector[idx];
                    indexVector[idx] = indexVector[j];
                    indexVector[j] = tmp2;
                }
            }
            uint p = idx + 1;
            // Swap a[p] and a[end[i]], do the same for index vector
            D T tmp = array[p];
            array[p] = array[end[i]];
            array[end[i]] = tmp;
            D uint tmp2 = indexVector[p];
            indexVector[p] = indexVector[end[i]];
            indexVector[end[i]] = tmp2;

            // Make new jobs, if any:
            if (p - begin[i] >= 2) {
                begin_[jobs_] = begin[i];
                end_[jobs_] = p-1;
                jobs_++;
            }
            if (end[i] - p >= 2) {
                begin_[jobs_] = p+1;
                end_[jobs_] = end[i];
                jobs_++;
            }
        }
        jobs = jobs_;
        begin = begin_;
        end = end_;
    }

    // Reorder the rest of the matrix by index vector
    uint32 declassifySect = startSection(declassifySectType, product(matShape));
    uint[[1]] publicIndexVector = declassify(indexVector);
    endSection(declassifySect);

    for (uint i = 0; i < m; i++) {
        matrix[i,:] = shuffledMatrix[publicIndexVector[i],:];
    }

    endSection (sortSect);

    return matrix;
}
/**
* \endcond
*/


/** \addtogroup quick_quick_sort_matrix
 *  @{
 *  @brief Functions for sorting rows in a matrix using the qucikquicksort algorithm
 *  @deprecated Use quicksort
 *  @note **D** - all protection domains
 *  @note Supported types -  \ref uint64 "uint64" / \ref xor_uint64 "xor_uint64"
 *  @param array - a vector of supported type
 *  @param column - the index of the sorting column
 *  @return returns a matrix where the input matrix rows are sorted based on values of the specified column 
 *  @leakage{Shuffled reordering decisions are declassified \n Leaks the number of equal elements}
 */

template <domain D>
D xor_uint64[[2]] quickquicksort(D xor_uint64[[2]] array, uint column1) {

    uint[[1]] matShape = shape(array);

    uint32 sortSectType = newSectionType ("quicksort_total");
    uint32 sortSect = startSection(sortSectType, matShape[0]);
    D xor_uint64[[2]] sorted = _quickquicksort(array, column1);
    endSection(sortSect);

    return sorted;
}


/**
* \cond
*/
template <domain D>
D uint64[[2]] quickquicksort(D uint64[[2]] array, uint column1) {

    uint[[1]] matShape = shape(array);

    uint32 sortSectType = newSectionType ("quicksort_total");
    uint32 reshareSectType = newSectionType ("quicksort_reshare");

    uint32 sortSect = startSection(sortSectType, matShape[0]);
    uint32 reshareSect = startSection(reshareSectType, matShape[0]);
    D xor_uint64[[2]] xor_array = reshare(array);
    endSection(reshareSect);

    D xor_uint64[[2]] sorted = _quickquicksort(xor_array, column1);

    reshareSect = startSection(reshareSectType, matShape[0]);
    D uint64[[2]] result = reshare(sorted);
    endSection(reshareSect);
    endSection(sortSect);

    return result;
}
/**
* \endcond
*/



/** @}*/
/** @}*/



/** @} */
/** @} */
/** @}*/

