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
module shared3p_string;

import oblivious;
import shared3p;
import shared3p_oblivious;
import shared3p_random;
import stdlib;
import table_database;
/**
* \endcond
*/

/**
* @file
* \defgroup shared3p_string shared3p_string.sc
* \defgroup shared3p_bl_string shared3p_bl_string
* \defgroup shared3p_kl_string shared3p_kl_string
* \defgroup CRC CRC
* \defgroup CRC2 CRC(0 initial hash)
* \defgroup murmurhashervec murmurHasherVec
* \defgroup countzeroes countZeroes
* \defgroup bl_str_type BlStringVector
* \defgroup bl_str bl_str
* \defgroup bl_str_struct bl_str(struct)
* \defgroup bl_strisempty bl_strIsEmpty
* \defgroup bl_strdeclassify bl_strDeclassify
* \defgroup bl_strlength bl_strLength
* \defgroup bl_strequals bl_strEquals
* \defgroup bl_strtrim bl_strTrim
* \defgroup findsortingpermutation findSortingPermutation
* \defgroup bl_strcat bl_strCat
* \defgroup bl_streqprefixes bl_strEqPrefixes
* \defgroup zeroextend zeroExtend
* \defgroup bl_strislessthan bl_strIsLessThan
* \defgroup bl_strlevenshtein bl_strLevenshtein
* \defgroup bl_strcontains bl_strContains
* \defgroup bl_strfind bl_strFind
* \defgroup bl_strindexof bl_strIndexOf
* \defgroup bl_strhamming bl_strHamming
* \defgroup bl_strshuffle bl_strShuffle
* \defgroup bl_strshuffle_key bl_strShuffle(key)
* \defgroup bl_strlengthenbound bl_strLengthenBound
* \defgroup bl_strempty bl_strEmpty
* \defgroup bl_strlessthan_parallel bl_strLessThan(parallel)
* \defgroup bl_strvectorlength bl_strVectorLength
* \defgroup tdb_vmap_add_bl_string_value tdbVmapAddBlStringValue
* \defgroup tdb_vmap_add_bl_string_type tdbVmapAddBlStringType
* \defgroup tdb_vmap_get_bl_string tdbVmapGetBlString
* \defgroup tdb_read_bl_string_column tdbReadBlStringColumn
* \defgroup kl_str kl_str
* \defgroup kl_strdeclassify kl_strDeclassify
* \defgroup kl_strlength kl_strLength
* \defgroup kl_strequals kl_strEquals
* \defgroup kl_strislessthan kl_strIsLessThan
* \defgroup kl_strcat kl_strCat
* \defgroup kl_streqprefixes kl_strEqPrefixes
* \defgroup kl_strcontains kl_strContains
* \defgroup kl_strindexof kl_strIndexOf
* \defgroup kl_strhamming kl_strHamming
* \defgroup kl_strlevenshtein kl_strLevenshtein
* \defgroup cw128genkey cw128GenKey
* \defgroup cw128hash cw128Hash
* \defgroup cw128hash_key cw128Hash(key)
*/

/** \addtogroup shared3p_string
*@{
* @brief Module with string functions
*/

/*******************************************************************************
********************************************************************************
**                                                                            **
**  CRC                                                                       **
**                                                                            **
********************************************************************************
*******************************************************************************/

/** \addtogroup CRC
 *  @{
    @note **D** - shared3p protection domain
    @note Supported types - \ref xor_uint8 "xor_uint8"
 *  @brief Compute CRC hash of the input byte array with given initial hash.
 *  @param input - the input byte vector
 *  @param hash - the initial hash of type \ref xor_uint16 "xor_uint16" or \ref xor_uint32 "xor_uint32"
 *  @leakage{None}
 */

template <domain D : shared3p, dim N>
D xor_uint16 CRC16 (D xor_uint8 [[N]] input, D xor_uint16 hash) {
    __syscall ("shared3p::crc16_xor_vec", __domainid (D), input, hash);
    return hash;
}


template <domain D : shared3p, dim N>
D xor_uint32 CRC32 (D xor_uint8 [[N]] input, D xor_uint32 hash) {
    __syscall ("shared3p::crc32_xor_vec", __domainid (D), input, hash);
    return hash;
}

/** @}*/
/** \addtogroup CRC2
 *  @{
    @note **D** - shared3p protection domain
    @note Supported types - \ref xor_uint8 "xor_uint8"
 *  @brief Compute CRC hash of the input byte array with 0 initial hash.
 *  @param input - the input byte vector
 *  @leakage{None}
 */

template <domain D : shared3p, dim N>
D xor_uint16 CRC16 (D xor_uint8 [[N]] input) {
    D xor_uint16 hash = 0;
    return CRC16 (input, hash);
}

template <domain D : shared3p, dim N>
D xor_uint32 CRC32 (D xor_uint8 [[N]] input) {
    D xor_uint32 hash = 0;
    return CRC32 (input, hash);
}

/** @}*/

/************************************************
*************************************************
**********  utility functions for murmur  *******
*************************************************
*************************************************/
/**
* \cond
*/

template <domain D : shared3p>
D xor_uint32[[1]] bitSumVec(D xor_uint32[[1]] first, uint32[[1]] second){
    return reshare (reshare (first) + second :: D);
}

template <domain D : shared3p>
D xor_uint32[[1]] bitMultiplyVec(D xor_uint32[[1]] first, uint32[[1]] second) {
  return reshare (reshare (first) * second :: D);
}

template <domain D : shared3p>
D xor_uint32[[1]] rotBitsLeftVec (D xor_uint32[[1]] bits, int k){
    int[[1]] shifts (size (bits)) = k;
    __syscall ("shared3p::rotate_left_xor_uint32_vec", __domainid (D), bits, __cref shifts, bits);
    return bits;
}

template <domain D : shared3p, dim N>
D xor_uint32[[N]] shiftBitsLeftVec (D xor_uint32[[N]] bits, int k) {
    int[[1]] shifts (size (bits)) = k;
    __syscall ("shared3p::shift_left_xor_uint32_vec", __domainid (D), bits, __cref shifts, bits);
    return bits;
}

template <domain D : shared3p, dim N>
D xor_uint32[[N]] shiftBitsRightVec (D xor_uint32[[N]] bits, int k) {
    return shiftBitsLeftVec (bits, - k);
}

/**
* \endcond
*/

/********************************************************
*********************************************************
*************      murmurHash              **************
*********************************************************
********************************************************/

/** \addtogroup murmurhashervec
*@{
* @brief murmurHash
* @note **D** - shared3p protection domain
* @note Supported types - \ref xor_uint32 "xor_uint32"
* @leakage{None}
*/

/**
* @param hashee - the string to be encrypted
* @param seed - the encryption seed
* @return returns an encrypted version of **hashee**
* \todo test if murmur is actually implemented correctly
*/

template <domain D : shared3p>
D xor_uint32[[1]] murmurHasherVec (D xor_uint32[[1]] hashee,  public uint32[[1]] seed) {
    assert (size (hashee) == size (seed));
    uint rows = size (seed);
    uint32 len = 4; // hashee is 4 bytes long

    D xor_uint32[[1]] hash = seed;

    uint32[[1]] cee1Vec (rows) = 0xcc9e2d51;
    uint32[[1]] cee2Vec (rows) = 0x1b873593;
    uint32[[1]] ennVec (rows)  = 0xe6546b64;
    uint32[[1]] x1Vec (rows)   = 0x85ebca6b;
    uint32[[1]] x2Vec (rows)   = 0xc2b2ae35;
    uint32[[1]] fiveVec (rows) = 5;

    // for each fourByteChunk of key {
    D xor_uint32[[1]] k = hashee;
    k = bitMultiplyVec (k, cee1Vec);
    k = rotBitsLeftVec (k, 15);
    k = bitMultiplyVec (k, cee2Vec);

    hash ^= k;
    hash = rotBitsLeftVec (hash,13);
    hash = bitMultiplyVec (hash, fiveVec);
    hash = bitSumVec (hash, ennVec);
    // }

    hash ^= len;
    hash ^= shiftBitsRightVec (hash, 16);
    hash = bitMultiplyVec (hash, x1Vec);
    hash ^= shiftBitsRightVec(hash, 13);
    hash = bitMultiplyVec (hash, x2Vec);
    hash ^= shiftBitsRightVec (hash,16);
    return hash;
}
/** @}*/


/*******************************************************************************
********************************************************************************
**                                                                            **
**  Various bounded length string algorithms                                  **
**                                                                            **
********************************************************************************
*******************************************************************************/
/** \addtogroup shared3p_bl_string
* @{
* @brief Module with functions for bounded length strings
*/

/** \addtogroup bl_str_type
 *  @{
 *  @note **D** - shared3p protection domain
 *  @brief Bounded-length string vector type
 */
template<domain D>
struct BlStringVector {
    D xor_uint8[[1]] value;
    uint bound;
}
/** @} */

/** \cond */
bool _isBlString(string x) {
    return isPrefixOf("bl_string(", x);
}
/** \endcond */

/** \addtogroup countzeroes
 *  @{
    @note **D** - shared3p protection domain
    @note Supported types - \ref xor_uint8 "xor_uint8"
 *  @brief Count zeroes in the input vector
 *  @param s - a vector of supported type
 *  @return returns the number of 0 bytes in the input vector
 *  @leakage{None}
 */

template <domain D : shared3p>
D uint countZeroes (D xor_uint8[[1]] s) {
    // TODO: I think this can be optimized.
    return sum ((uint) (s == 0));
}
/** @}*/

/** \addtogroup bl_str
 *  @{
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref xor_uint8 "xor_uint8"
 *  @brief Convert a string to a vector of type xor_uint8
 */

/**
 * @return returns a bounded length XOR shared \ref string "string" from the public string and the given bound
 * @param n - an \ref uint64 "uint" type bound
 * @param s - a \ref string "string"
 * @pre the size of the given bound is no less than the length of the input string
 * @post the output string is of length n
 * @note the excess bytes in the shared string are placed in the end and are zeroed
 */
template <domain D : shared3p>
D xor_uint8[[1]] bl_str (string s, uint n) {
    uint8[[1]] bytes = __bytes_from_string (s);
    uint8[[1]] out (n);
    assert (size(bytes) <= n);
    out[:size(bytes)] = bytes;
    return out;
}

/**
 * @param s - a \ref string "string"
 * @return returns a bounded length XOR shared \ref string "string" from the public string
 * @note the bounded length aligns with the actual length, no extra zeroed bytes are added
 */
template <domain D : shared3p>
D xor_uint8[[1]] bl_str (string s) {
    return __bytes_from_string (s);
}
/** @}*/

/** \addtogroup bl_str_struct
 *  @{
 *  @param s - a \ref string "string"
 *  @param n - an \ref uint64 "uint" type bound
 *  @return returns a bounded length string vector created from the public string
 *  @post the output string is of length n
 *  @note the excess bytes in the shared string are placed in the end and are zeroed
 */
template <domain D : shared3p>
BlStringVector<D> bl_str (string s, uint n) {
    public BlStringVector<D> res;
    uint8[[1]] bytes = __bytes_from_string (s);
    uint8[[1]] out (n);
    assert (size(bytes) <= n);
    out[:size(bytes)] = bytes;
    res.value = out;
    res.bound = n;
    return res;
}

/**
 *  @param s - a \ref string "string"
 *  @return returns a bounded length string vector created from the public string
 */
template <domain D : shared3p>
BlStringVector<D> bl_str (string s) {
    public BlStringVector<D> res;
    res.value = __bytes_from_string (s);
    res.bound = size (res.value);
    return res;
}
/** @} */

/** \addtogroup bl_strisempty
 *  @{
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref xor_uint8 "xor_uint8"
 *  @note this is done by checking if all of the bytes in the string are zeroed
 *  @note See \ref bl_strempty "bl_strEmpty" for parallel version.
 *  @param s - a vector of supported type
 *  @brief Check if the input string is empty
 *  @return returns **true** if the given known length input \ref string "string" is empty
 *  @leakage{None}
 */
template <domain D : shared3p>
D bool bl_strIsEmpty (D xor_uint8[[1]] s) {
   return all (s == 0);
}
/** @}*/

/** \addtogroup bl_strdeclassify
 *  @{
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref xor_uint8 "xor_uint8"
 *  @param ps - a vector of supported type
 *  @brief Function for converting an array of type xor_uint8 to a string
 *  @return returns a declassified bounded length \ref string "string", extra bytes are removed
 *  @leakage{Leaks the input}
 */
template <domain D : shared3p>
string bl_strDeclassify (D xor_uint8[[1]] ps) {
    uint8[[1]] bytes = declassify (ps);
    uint i = 0;
    for (uint n = size (bytes); i < n; ++i) {
        if (bytes[i] == 0)
            break;
    }

    return __string_from_bytes (bytes[:i]);
}

/**
 * @param str - input string vector
 * @param i - position of string to declassify
 */
template <domain D : shared3p>
string bl_strDeclassify (BlStringVector<D> str, uint i) {
    D xor_uint8[[1]] x = str.value[str.bound * i : str.bound * (i + 1)];
    uint8[[1]] bytes = declassify (x);
    uint i = 0;
    for (uint n = size (bytes); i < n; ++i) {
        if (bytes[i] == 0)
            break;
    }
    return __string_from_bytes (bytes[:i]);
}
/** @}*/

/** \addtogroup bl_strlength
 *  @{
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref xor_uint8 "xor_uint8"
 *  @param s - a vector of supported type
 *  @brief Function for finding the length of the given input string
 *  @return returns the actual length of the bounded lengthed input \ref string "string"
 *  @leakage{None}
 */
template <domain D : shared3p>
D uint bl_strLength (D xor_uint8[[1]] s) {
    return size (s) - countZeroes (s);
}

/**
 *  @note **D** - shared3p protection domain
 *  @note param s - bounded-length string vector
 *  @note param i - position of string
 *  @return returns the actual length of the string at position i
 *  @leakage{None}
 */
template <domain D : shared3p>
D uint bl_strLength (BlStringVector<D> s, uint i) {
    D xor_uint8[[1]] x = s.value[s.bound * i : s.bound * (i + 1)];
    return size (x) - countZeroes (x);
}
/** @}*/

/** \addtogroup bl_strtrim
 *  @{
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref xor_uint8 "xor_uint8"
 *  @param s - a vector of supported type
 *  @brief Function for trimming a string
 *  @return returns a \ref string "string" with excess bytes removed
 *  @leakage{Leaks the length of the string}
 */
template <domain D : shared3p>
D xor_uint8[[1]] bl_strTrim (D xor_uint8[[1]] s) {
    uint n = size (s) - declassify (countZeroes (s));
    return s[:n];
}

/**
 *  @note **D** - shared3p protection domain
 *  @param s - bounded-length string vector.
 *  @brief Trim a bounded-length string vector.
 *  @return returns input string vector with excess bytes removed
 *  @leakage{Leaks the length of the longest string}
 */
template <domain D : shared3p>
BlStringVector<D> bl_strTrim (BlStringVector<D> s) {
    uint len = size (s.value) / s.bound;
    D uint[[1]] zeroCounts(len) = 0;
    for (uint i = 0; i < s.bound; ++i) {
        D xor_uint8[[1]] x(len);
        uint[[1]] idx(len) = iota (len) * s.bound + i;
        __syscall("shared3p::gather_xor_uint8_vec", __domainid (D), s.value, x, __cref idx);
        zeroCounts += (uint) (x == 0);
    }
    uint maxBound = s.bound - declassify (min (zeroCounts));
    s.value = _parallelTake(s.value, s.bound, maxBound);
    s.bound = maxBound;
    return s;
}
/** @} */

/** \addtogroup bl_strequals
 *  @{
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref xor_uint8 "xor_uint8"
 *  @param s,t - vectors of supported type
 *  @brief Compare two string with each other
 *  @return returns **true** if the two input strings are equal
 *  @leakage{None}
 */
template <domain D : shared3p>
D bool bl_strEquals (D xor_uint8[[1]] s, D xor_uint8[[1]] t) {
    uint n = size (s), m = size (t);

    if (n > m) {
        return bl_strEquals (t, s);
    }

    return all (s == t[:n]) & bl_strIsEmpty (t[n:]);
}

/**
 *  @note **D** - shared3p protection domain
 *  @brief Performs pointwise equality comparison of bounded-length string vectors.
 *  @param s - string vector
 *  @param t - string vector
 *  @return returns a boolean indicating if the strings in the
 *  corresponding position in s and t were equal
 *  @leakage{None}
 */
template <domain D : shared3p>
D bool[[1]] bl_strEquals(BlStringVector<D> s, BlStringVector<D> t)
{
    if (size(s.value) == 0 || size(t.value) == 0) {
        D bool[[1]] res;
        return res;
    }

    assert (s.bound > 0 && t.bound > 0);
    assert (size(s.value) % s.bound == 0 && size(t.value) % t.bound == 0);

    uint numRows = size(s.value) / s.bound;

    if (s.bound == t.bound) {
        return all(s.value == t.value, numRows);
    }

    if (s.bound > t.bound) {
        return bl_strEquals(t, s);
    }

    D bool[[1]] prefixesAreEqual = all(s.value == _parallelTake(t.value, t.bound, s.bound), numRows);
    D bool[[1]] restAreEmpty = _bl_strEmpty(_parallelDrop(t.value, t.bound, s.bound), t.bound - s.bound);
    return prefixesAreEqual & restAreEmpty;
}
/** @}*/

/** \addtogroup findsortingpermutation
 *  @{
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref bool "bool"
 *  @note performs two vectorized multiplications
 *  @param arr - a vector of supported type
 *  @brief Function for finding a stable sorting permutation
 *  @return returns a stable (in a sorting sense) permutation that moves **false** values to end
 *  @leakage{None}
 */
template <domain D : shared3p>
D uint[[1]] findSortingPermutation (D bool[[1]] arr) {
    D uint[[1]] vec = (uint) arr;
    D uint[[1]] ivec = 1 - vec;

    uint n = size (arr);

    D uint[[1]] pTrue (n);
    D uint acc = 0;
    for (uint i = 0; i < n; ++i) {
        pTrue[i] = acc;
        acc += vec[i];
    }

    D uint[[1]] pFalse (n);
    acc = n - 1;
    for (uint i = 1; i <= n; ++i) {
        pFalse[n-i] = acc;
        acc -= ivec[n-i];
    }

    return vec * (pTrue - pFalse) + pFalse;
}
/** @}*/

/** \addtogroup bl_strcat
 *  @{
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref xor_uint8 "xor_uint8"
 *  @param s,t - string vectors of supported type
 *  @brief Function for concatenating two strings
 *  @return returns a concatenation of the two input strings
 *  @leakage{None}
 */
template <domain D : shared3p>
D xor_uint8[[1]] bl_strCat (D xor_uint8[[1]] s, D xor_uint8[[1]] t) {
    uint n = size (s) + size (t);
    D xor_uint8[[1]] u = cat (s, t);
    D uint[[1]] p = findSortingPermutation (u != 0);

    { // Shuffle the permutation and concatenation using the same key
        D uint8[[1]] key (32);
        u = shuffle (u, key);
        p = shuffle (p, key);
    }

    uint[[1]] pp = declassify (p); // it's safe to declassify a random permutation
    D xor_uint8[[1]] out (n);
    for (uint i = 0; i < n; ++i) {
        out[pp[i]] = u[i];
    }

    return out;
}
/** @}*/

/** \addtogroup zeroextend
 *  @{
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref xor_uint8 "xor_uint8"
 *  @brief Extend an input string with zeroes.
 *  @param s - an input string
 *  @param n - minimum size that we want the resulting string to be
 *  @return  returns a vector that was created by zeroextending **s**
 *  @post size (output vector) >= **n**
 *  @post **s** is a prefix of the output vector
 *  @post only zeroes have been added to the output vector
 *  @post if size ( **s** ) >= **n** then the output vector == **s**
 *  @leakage{None}
 */
template <domain D : shared3p>
D xor_uint8[[1]] zeroExtend (D xor_uint8[[1]] s, uint n) {
    uint m = size (s);
    if (n > m) {
        return cat (s, reshape (0, n - m));
    }

    return s;
}
/** @}*/

/** \addtogroup bl_strislessthan
 *  @{
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref xor_uint8 "xor_uint8"
 *  @note See \ref bl_strlessthan_parallel "bl_strLessThan" for parallel version.
 *  @param s,t - input string vectors of supported type
 *  @brief function for comparing two strings alphabetically
 *  @return returns **true** if s < t
 *  @leakage{None}
 */
template <domain D : shared3p>
D bool bl_strIsLessThan (D xor_uint8[[1]] s, D xor_uint8[[1]] t) {
    s = zeroExtend (s, size (t));
    t = zeroExtend (t, size (s));

    D uint nLE = truePrefixLength (s <= t);
    D uint nEQ = truePrefixLength (s == t);
    return nLE > nEQ;
}
/** @}*/

/** \addtogroup bl_strlevenshtein
 *  @{
 *  @brief function for finding the edit distance of the two input strings
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref xor_uint8 "xor_uint8"
 *  @param s,t - input string vectors of supported type
 *  @return returns the edit distance of the two input strings
 *  @note the algorithm is almost identical to the known length one. The only difference is that the result is found somewhere in the middle of the 2D grid, and not in the bottom right corner. To find it we: a) compute a mask that's one in that position and zero otherwise, b) multiply the mask with the grid, and c) return the sum of the result.
 * \todo we can optimize by computing the entire grid and performing a single multiplication
 *  @leakage{None}
 */
template <domain D : shared3p>
D uint bl_strLevenshtein (D xor_uint8[[1]] s, D xor_uint8[[1]] t) {
    uint n = size (s),
         m = size (t);

    if (n < m) {
        return bl_strLevenshtein (t, s);
    }

    uint diagCount = n + m;

    D uint result = 0;

    uint[[1]] diagOff (diagCount), diagEnd (diagCount);
    D uint[[1]] indexMask, neqs;
    {
        uint nm = n * m;
        uint[[1]] ridx (nm), cidx (nm);

        for (uint s = 0, count = 0; s < diagCount; ++ s) {
            diagOff[s] = count;
            for (uint i = 0; i < n; ++ i) {
                if (s - i < m) {
                    ridx[count] = i;
                    cidx[count] = s - i;
                    ++ count;
                }
            }

            diagEnd[s] = count;
        }

        { // compute the inequalitied:
            D xor_uint8[[1]] ss (nm);
            D xor_uint8[[1]] ts (nm);

            for (uint x = 0; x < nm; ++ x) {
                ss[x] = s[ridx[x]];
                ts[x] = t[cidx[x]];
            }

            neqs = (uint) (ss != ts);
        }

        { // Compute the index masks:
            D uint r = bl_strLength (s);
            D uint c = bl_strLength (t);
            result = (uint) (r == 0) * c + (uint) (c == 0) * r;
            indexMask = (uint) ((r == ridx + 1) & (c == cidx + 1));
        }
    }

    D uint[[1]] prevDiag (1) = 0;
    D uint[[1]] currDiag ((uint)(m > 0) + (uint)(n > 0)) = 1;

    uint extendStart = (uint) (m > 1), extendEnd = (uint) (n > 1);
    for (uint s = 0; s < diagCount; ++ s) {
        uint len = size (currDiag);
        {
            uint prevDiagLen = size (prevDiag);
            uint startOff = (uint) (prevDiagLen >= len);
            uint endOff = prevDiagLen - (uint) (prevDiagLen > len);
            prevDiag = prevDiag[startOff:endOff];
        }


        if (s + 2 > m) extendStart = 0;
        if (s + 2 > n) extendEnd = 0;

        D uint[[1]] temp (extendStart + (diagEnd[s] - diagOff[s]) + extendEnd) = s + 2;
        D uint[[1]] smallDiag =
            min (neqs[diagOff[s]:diagEnd[s]] + prevDiag,
                 min(1 + currDiag[1:], 1 + currDiag[:len-1]) );

        temp[extendStart:size(temp)-extendEnd] = smallDiag;
        result += sum (smallDiag * indexMask[diagOff[s]:diagEnd[s]]);

        prevDiag = currDiag;
        currDiag = temp;
    }

    return result;
}
/** @}*/

/** \addtogroup bl_streqprefixes
 *  @{
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref xor_uint8 "xor_uint8"
 *  @brief Function for checking whether a pattern is a substring of the input string
 *  @pre the pattern is not longer than the input string
 *  @param str - input string vector of supported type
 *  @param pat - the pattern to look for
 *  @return returns a boolean vector indicating if the pattern is a substring of the input string at that position
 *  @leakage{None}
 */
template <domain D : shared3p>
D bool[[1]] bl_strEqPrefixes (D xor_uint8[[1]] str, D xor_uint8[[1]] pat) {
    uint n = size (str), m = size (pat);

    D xor_uint8[[1]] workList (n + m);
    workList[:n] = str;

    D bool[[2]] eqs;
    {
        D xor_uint8[[1]] strs (n*m);
        D xor_uint8[[1]] pats (n*m);
        for (uint i = 0, off = 0; i < n; ++ i) {
            strs[off : off + m] = workList[i : i + m];
            pats[off : off + m] = pat;
            off += m;
        }

        eqs = reshape ((strs == pats) | (pats == 0), n, m);
    }

    D bool[[1]] acc (n) = true;
    for (uint i = 0; i < m; ++ i) {
        acc = acc & eqs[:, i];
    }

    return acc;
}
/** @}*/

/** \addtogroup bl_strcontains
 *  @{
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref xor_uint8 "xor_uint8"
 *  @param str - input string vector of supported type
 *  @param pat - the pattern to look for
 *  @brief function for checking if a string contains the given pattern
 *  @return returns **true** if the given pattern is a substring of the given string
 *  @leakage{None}
 */
template <domain D : shared3p>
D bool bl_strContains (D xor_uint8[[1]] str, D xor_uint8[[1]] pat) {
    if (size (str) == 0) {
        return bl_strIsEmpty (pat);
    }

    return any (bl_strEqPrefixes (str, pat));
}
/** @}*/

/** \addtogroup bl_strfind
 *  @{
 *  @note **D** - shared3p protection domain
 *  @brief Searches for a string in a string vector
 *  @param needle - query string
 *  @param haystack - string vector
 *  @return returns the index of the first occurence of needle in
 *  haystack if found and -1 otherwise
 *  @leakage{None}
 */
template <domain D : shared3p>
D int64 bl_strFind (D xor_uint8[[1]] needle, BlStringVector<D> haystack) {
    uint bound = max(size(needle), haystack.bound);

    if (bound > haystack.bound) {
        haystack = bl_strLengthenBound(haystack, bound);
    }

    if (bound > size(needle)) {
        D xor_uint8[[1]] tmp(bound);
        tmp[:size(needle)] = needle;
        needle = tmp;
    }

    uint n = size(haystack.value) / haystack.bound;
    D xor_uint8[[1]] tmp(n * bound);
    uint[[1]] indices(n * bound);

    for (uint i = 0; i < n * bound; ++i) {
        indices[i] = i % bound;
    }

    __syscall("shared3p::gather_xor_uint8_vec", __domainid(D), needle, tmp, __cref indices);

    public BlStringVector<D> needleVec;
    needleVec.value = tmp;
    needleVec.bound = bound;

    D bool[[1]] eq = bl_strEquals(needleVec, haystack);
    D bool found = any(eq);
    D int64 idx = (int64) truePrefixLength(!eq);
    D int64 notFound = -1;
    idx = choose(found, idx, notFound);

    return idx;
}
/** @} */

/** \addtogroup bl_strindexof
 *  @{
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref xor_uint8 "xor_uint8"
 *  @param str - input string vector of supported type
 *  @param pat - the pattern to look for
 *  @brief function for finding the index of a given pattern
 *  @return returns the position where given pattern is contained in the string
 *  @note if the string is not found returns value that is or equal to size (str)
 *  @leakage{None}
 */
template <domain D : shared3p>
D uint bl_strIndexOf (D xor_uint8[[1]] str, D xor_uint8[[1]] pat) {
    return truePrefixLength (! bl_strEqPrefixes (str, pat));
}
/** @}*/

/** \addtogroup bl_strhamming
 *  @{
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref xor_uint8 "xor_uint8"
 *  @param s,t - input string vectors of supported type
 *  @brief Function for finding the number of bytes that the two input strings differ in
 *  @return returns the number of bytes that the inputs differ in
 *  @pre bl_strLength(s) == bl_strLength(t), otherwise the behaviour is undefined
 *  @leakage{None}
 */
template <domain D : shared3p>
D uint bl_strHamming (D xor_uint8[[1]] s, D xor_uint8[[1]] t) {
    uint k = min (size (s), size (t));
    return sum ((uint) (s[:k] != t[:k]));
}
/** @}*/

/** \addtogroup bl_strshuffle
 *  @{
 *  @note **D** - shared3p protection domain
 *  @brief Shuffle a bounded-length string vector
 *  @param s - input string vector
 *  @return returns shuffled s
 *  @leakage{None}
 */
template <domain D : shared3p>
BlStringVector<D> bl_strShuffle(BlStringVector<D> s)
{
    D xor_uint8[[1]] x = s.value;
    assert (s.bound > 0 && size(s.value) % s.bound == 0);
    __syscall ("shared3p::matshuf_xor_uint8_vec", __domainid (D), x, s.bound);
    s.value = x;
    return s;
}
/** @} */

/** \addtogroup bl_strshuffle_key
 *  @{
 *  @note **D** - shared3p protection domain
 *  @brief Shuffle a bounded-length string vector
 *  @param s - input string vector
 *  @param sLen - bound
 *  @param key - shuffle key
 *  @return returns shuffled s
 *  @leakage{None}
 */
template <domain D : shared3p>
BlStringVector<D> bl_strShuffle(BlStringVector<D> s, D uint8[[1]] key)
{
    if (size(s) == 0)
        return s;

    D xor_uint8[[1]] x = s.value;
    assert (s.bound > 0 && size(s.value) % s.bound == 0);
    assert (size(key) == 32);
    __syscall ("shared3p::matshufkey_xor_uint8_vec", __domainid (D), x, s.bound, key);
    s.value = x;
    return s;
}
/** @}*/

/** \addtogroup bl_strlengthenbound
 *  @{
 *  @note **D** - shared3p protection domain
 *  @brief Increase the bound of a bounded-length string vector
 *  @param s - input string vector
 *  @param biggerBound - new bound
 *  @return returns s with the bound increased to biggerBound
 *  @leakage{None}
 */
template <domain D : shared3p>
BlStringVector<D> bl_strLengthenBound(BlStringVector<D> s, uint biggerBound) {
    assert (biggerBound > s.bound);
    assert (size(s.value) % s.bound == 0);

    uint n = size(s.value) / s.bound;
    uint[[1]] strideOld = s.bound * iota(n);
    uint[[1]] strideNew = biggerBound * iota(n);
    D xor_uint8[[1]] result (n * biggerBound);
    D xor_uint8[[1]] row (n);
    D xor_uint8[[1]] x = s.value;
    for (uint i = 0; i < s.bound; ++ i) {
        __syscall("shared3p::gather_xor_uint8_vec",
            __domainid(D), x, row, __cref strideOld + i);

        __syscall("shared3p::scatter_xor_uint8_vec",
            __domainid(D), row, result, __cref strideNew + i);
    }

    s.bound = biggerBound;
    s.value = result;
    return s;
}
/** @} */

/** \cond */
template <domain D : shared3p>
D bool[[1]] _bl_strEmpty(D xor_uint8[[1]] s, uint bound) {
    assert (bound > 0);
    assert (size(s) % bound == 0);
    uint numRows = size(s) / bound;
    return all(s == 0, numRows);
}
/** \endcond */

/** \addtogroup bl_strempty
 *  @{
 *  @note **D** - shared3p protection domain
 *  @brief Check if a bounded-length string vector contains empty strings
 *  @param s - input string vector
 *  @return returns a boolean vector indicating if the string in the
 *  corresponding position in s was empty
 *  @leakage{None}
 */
template <domain D : shared3p>
D bool[[1]] bl_strEmpty(BlStringVector<D> s) {
    assert (s.bound > 0);
    assert (size(s.value) % s.bound == 0);
    uint numRows = size(s.value) / s.bound;
    return all(s.value == 0, numRows);
}
/** @} */

/** \cond */
template <type T, domain D : shared3p>
D T[[1]] _parallelTake(D T[[1]] s, uint rowLen, uint resultRowLen)
{
    assert (0 < resultRowLen);
    assert (0 < resultRowLen && resultRowLen <= rowLen);
    assert (size(s) % rowLen == 0);

    uint limit = 200 * 1024 * 1024; // 200M bytes

    uint numOfRows = size(s) / rowLen;
    uint resultSize = numOfRows * resultRowLen;
    uint rowLimit = limit / numOfRows;
    D T[[1]] result (resultSize);

    if (numOfRows > rowLimit) {
        uint done = 0;  // rows trimmed

        while (done < numOfRows) {
            uint batchSize = rowLimit;
            if (numOfRows - done < rowLimit) {
                batchSize = numOfRows - done;
            }

            uint[[1]] indices1(batchSize * resultRowLen);
            uint[[1]] indices2(batchSize * resultRowLen);

            for (uint i = 0; i < batchSize; i++) {
                indices1[i * resultRowLen : (i+1) * resultRowLen] = iota(resultRowLen) + (i + done) * rowLen;
                indices2[i * resultRowLen : (i+1) * resultRowLen] = iota(resultRowLen) + (i + done) * resultRowLen;
            }

            D T[[1]] singleRow (resultRowLen);
            __syscall ("shared3p::gather_$T\_vec", __domainid (D), s, singleRow, __cref indices1);

            __syscall ("shared3p::scatter_$T\_vec", __domainid (D), singleRow, result, __cref indices2);

            done += batchSize;
        }
    } else {
        uint[[1]] indices(resultSize);

        for (uint i = 0; i < numOfRows; i++) {
            indices[i * resultRowLen : (i+1) * resultRowLen] = iota(resultRowLen) + i * rowLen;
        }

        __syscall ("shared3p::gather_$T\_vec", __domainid (D), s, result, __cref indices);
    }

    return result;
}

template <type T, domain D : shared3p>
D T[[1]] _parallelDrop(D T[[1]] s, uint rowLen, uint len)
{
    assert (len <= rowLen);
    assert (size(s) % rowLen == 0);

    if (len == 0)
        return s;

    if (rowLen == len)
        return reshape(0, 0);

    uint limit = 200 * 1024 * 1024; // 200M bytes

    uint resultRowLen = rowLen - len;
    uint numOfRows = size(s) / rowLen;
    uint rowLimit = limit / numOfRows;
    D T[[1]] result (numOfRows * resultRowLen);

    if (numOfRows > rowLimit) {
        uint done = 0;  // rows trimmed

        while (done < numOfRows) {
            uint batchSize = rowLimit;
            if (numOfRows - done < rowLimit) {
                batchSize = numOfRows - done;
            }

            uint[[1]] indices1(batchSize * resultRowLen);
            uint[[1]] indices2(batchSize * resultRowLen);

            for (uint i = 0; i < batchSize; i++) {
                indices1[i * resultRowLen : (i+1) * resultRowLen] = iota(resultRowLen) + len + (i + done) * rowLen;
                indices2[i * resultRowLen : (i+1) * resultRowLen] = iota(resultRowLen) + (i + done) * resultRowLen;
            }

            D T[[1]] singleRow (resultRowLen);
            __syscall ("shared3p::gather_$T\_vec", __domainid (D), s, singleRow, __cref indices1);

            __syscall ("shared3p::scatter_$T\_vec", __domainid (D), singleRow, result, __cref indices2);

            done += batchSize;
        }
    } else {
        uint[[1]] indices(numOfRows * resultRowLen);

        for (uint i = 0; i < numOfRows; i++) {
            indices[i * resultRowLen : (i+1) * resultRowLen] = iota(resultRowLen) + len + i * rowLen;
        }

        __syscall ("shared3p::gather_$T\_vec", __domainid (D), s, result, __cref indices);
    }
    return result;
}
/** \endcond */

/** \addtogroup bl_strlessthan_parallel
 *  @{
 *  @note **D** - shared3p protection domain
 *  @brief Compares two bounded-length string vectors point-wise.
 *  @param x, y - input bounded-length string vectors
 *  @return returns a boolean vector indicating if the element of x is
 *  less than the element of y in the corresponding position
 *  @leakage{None}
 */
template<domain D : shared3p>
D bool[[1]] bl_strLessThan(BlStringVector<D> x,
                           BlStringVector<D> y)
{
    assert(x.bound > 0 && y.bound > 0);

    uint bound = max(x.bound, y.bound);
    if (x.bound < bound)
        x = bl_strLengthenBound(x, bound);

    if (y.bound < bound)
        y = bl_strLengthenBound(y, bound);

    uint len = size(x.value) / bound;
    D bool[[1]] res(len);
    D bool[[1]] prefixEq(len) = true;

    for (uint i = 0; i < bound; ++i) {
        D xor_uint8[[1]] tmpLeft(len);
        D xor_uint8[[1]] tmpRight(len);
        uint[[1]] idx = iota(len) * bound + i;

        __syscall("shared3p::gather_xor_uint8_vec", __domainid(D), x.value, tmpLeft, __cref(idx));
        __syscall("shared3p::gather_xor_uint8_vec", __domainid(D), y.value, tmpRight, __cref(idx));

        D bool[[1]] tmpCmp = tmpLeft < tmpRight;
        D bool[[1]] tmpEq = tmpLeft == tmpRight;

        res = res | (prefixEq & tmpCmp);
        prefixEq = prefixEq & tmpEq;
    }

    return res;
}
/** @} */

/** \addtogroup bl_strvectorlength
 *  @{
 *  @note **D** - shared3p protection domain
 *  @brief Calculate the length of a bounded-length string vector.
 *  @param s - input string vector
 *  @return returns the length of s
 *  @leakage{None}
 */
template<domain D : shared3p>
uint bl_strVectorLength(BlStringVector<D> s) {
    assert(s.bound > 0);
    return size(s.value) / s.bound;
}
/** @}*/

/** \addtogroup tdb_vmap_add_bl_string_value
 *  @{
 *  @note **D** - shared3p protection domain
 *  @brief Add a bounded-length string vector to a \ref table_database
 *  "table_database" vector map.
 *  @param params - vector map
 *  @param key - key of vector in vector map
 *  @param vec - bounded length string vector
 *  @leakage{None}
 */
template<domain D : shared3p>
void tdbVmapAddBlStringValue(uint params, string key, BlStringVector<D> vec) {
    uint8[[1]] bytevec(size(vec.value));
    __syscall("shared3p::get_shares_xor_uint8_vec", __domainid(D), vec.value, __ref bytevec);
    bool isScalar = false;
    uint bound = vec.bound;
    __syscall("tdb_vmap_push_back_value", params, __cref key, __cref "$D", __cref "bl_string($bound)", bound, __cref bytevec, isScalar);
}
/** @} */

/** \addtogroup tdb_vmap_add_bl_string_type
 *  @{
 *  @note **D** - shared3p protection domain
 *  @brief Add bounded-length string type to a type vector in a \ref
 *  table_database "table_database" vector map.
 *  @param vmap - vector map
 *  @param domainProxy - proxy value used to specify protection domain
 *  (actual value is not important)
 *  @param bound - string bound
 *  @leakage{None}
 */
template<domain D : shared3p>
void tdbVmapAddBlStringType(uint vmap, D uint domainProxy, uint bound) {
    __syscall("tdb_vmap_push_back_type", vmap, __cref "types", __cref "$D", __cref "bl_string($bound)", bound);
}
/** @} */

/** \addtogroup tdb_vmap_get_bl_string
 *  @{
 *  @note **D** - shared3p protection domain
 *  @brief Get bounded-length string vector from a \ref table_database
 *  "table_database" vector map.
 *  @param vmap - vector map
 *  @param string - key of vector in vector map
 *  @param idx - index of bounded-length string vector in vector map vector
 *  @return returns string vector in position idx
 *  @leakage{None}
 */
template<domain D : shared3p>
BlStringVector<D> tdbVmapGetBlString(uint vmap, string key, uint idx) {
    string rt_dom;
    __syscall("tdb_vmap_at_value_type_domain", vmap, __cref key, idx, __return rt_dom);
    assert(rt_dom == "$D");

    string rt_name;
    __syscall("tdb_vmap_at_value_type_name", vmap, __cref key, idx, __return rt_name);
    assert(_isBlString(rt_name));

    uint num_bytes;
    __syscall("tdb_vmap_at_value", vmap, __cref key, idx, __return num_bytes);
    uint8[[1]] bytes(num_bytes);
    __syscall("tdb_vmap_at_value", vmap, __cref key, idx, __ref bytes);
    D xor_uint8[[1]] str(num_bytes);
    __syscall("shared3p::set_shares_xor_uint8_vec", __domainid(D), str, __cref bytes);

    uint t_size;
    __syscall("tdb_vmap_at_value_type_size", vmap, __cref key, idx, __return t_size);

    public BlStringVector<D> res;
    res.value = str;
    res.bound = t_size;

    return res;
}
/** @} */

/** \addtogroup tdb_read_bl_string_column
 *  @{
 *  @note **D** - shared3p protection domain
 *  @brief Read a bounded-length string vector from a \ref
 *  table_database "table_database" table.
 *  @param ds - data source name
 *  @param table - table name
 *  @param col - column index
 *  @return returns the column of the table as a bounded-length string
 *  vector
 *  @leakage{None}
 */
template<domain D : shared3p>
BlStringVector<D> tdbReadBlStringColumn(string ds, string table, uint col) {
    uint vmap = tdbReadColumn(ds, table, col);
    public BlStringVector<D> res = tdbVmapGetBlString(vmap, "values", 0 :: uint);
    tdbVmapDelete(vmap);
    return res;
}

/**
 * @param ds - data source name
 * @param table - table name
 * @param name - column name
 */
template<domain D : shared3p>
BlStringVector<D> tdbReadBlStringColumn(string ds, string table, string name) {
    uint vmap = tdbReadColumn(ds, table, name);
    public BlStringVector<D> res = tdbVmapGetBlString(vmap, "values", 0 :: uint);
    tdbVmapDelete(vmap);
    return res;
}
/** @} */

/** @} */

/*******************************************************************************
********************************************************************************
**                                                                            **
**  Various known length string algorithms                                    **
**                                                                            **
********************************************************************************
*******************************************************************************/

/** \addtogroup shared3p_kl_string
*@{
* @brief Module with functions for known length strings
*/

/** \addtogroup kl_str
 *  @{
 *  @note **D** - shared3p protection domain
 *  @note Private strings are XOR shared to optimize for equality and comparison checks.
 *  @brief Function for constructing a known length string from given public string
 *  @param s - a \ref string "string"
 *  @return XOR shared byte array of length equal to the input public string.
 */

template <domain D : shared3p>
D xor_uint8[[1]] kl_str (string s) {
    return __bytes_from_string (s);
}
/** @}*/

/** \addtogroup kl_strdeclassify
 *  @{
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref xor_uint8 "xor_uint8"
 *  @param ps - a string vector of supported type
 *  @brief Function for declassifying a given XOR shared byte string.
 *  @return returns a public string
 *  @leakage{Leaks the input string}
 */
template <domain D : shared3p>
string kl_strDeclassify (D xor_uint8[[1]] ps) {
    return __string_from_bytes (declassify (ps));
}
/** @}*/

/** \addtogroup kl_strlength
 *  @{
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref xor_uint8 "xor_uint8"
 *  @param str - a string vector of supported type
 *  @brief Function for getting the length of a string
 *  @return returns a length of the string
 *  @leakage{None}
 */
template <domain D : shared3p>
uint kl_strLength (D xor_uint8[[1]] str) {
    return size(str);
}
/** @}*/

/** \addtogroup kl_strequals
 *  @{
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref xor_uint8 "xor_uint8"
 *  @brief Function for checking whether two strings are equal
 *  @param s,t - string vectors of supported type
 *  @return returns **true** if the input strings are equal, **false** it they are not
 *  @leakage{None}
 */
template <domain D : shared3p>
D bool kl_strEquals (D xor_uint8[[1]] s, D xor_uint8[[1]] t) {
    if (kl_strLength (s) != kl_strLength (t)) {
        return false;
    }

    return all (s == t);
}
/** @}*/

/** \addtogroup kl_strislessthan
 *  @{
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref xor_uint8 "xor_uint8"
 *  @brief Function for comparing two strings alphabetically
 *  @param s,t - string vectors of supported type
 *  @return returns **true** if the first input string is less than the second (in dictionary order)
 *  @leakage{None}
 */
template <domain D : shared3p>
D bool kl_strIsLessThan (D xor_uint8[[1]] s, D xor_uint8[[1]] t) {
    uint n = kl_strLength(s), m = kl_strLength(t);
    if (n == m) {
        D uint x = truePrefixLength (s >= t);
        D uint y = truePrefixLength (s == t);
        return x == y & (x != n);
    }
    else
    if (n < m) {
        t = t[:n];
        D uint x = truePrefixLength (s >= t);
        D uint y = truePrefixLength (s == t);
        return x == y;
    }
    else {
        s = s[:m];
        D uint x = truePrefixLength (s <= t);
        D uint y = truePrefixLength (s == t);
        return x != y;
    }
}
/** @}*/

/** \addtogroup kl_strcat
 *  @{
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref xor_uint8 "xor_uint8"
 *  @param s,t - string vectors of supported type
 *  @brief Function for concatenating two strings
 *  @return returns a concatenation of the two input strings
 *  @leakage{None}
 */
template <domain D : shared3p>
D xor_uint8[[1]] kl_strCat (D xor_uint8[[1]] s, D xor_uint8[[1]] t) {
    return cat (s, t);
}
/** @}*/

/** \addtogroup kl_streqprefixes
 *  @{
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref xor_uint8 "xor_uint8"
 *  @brief Function for checking whether a pattern is a substring of the input string
 *  @pre the pattern is not longer than the input string
 *  @param str - the input string vector of supported type
 *  @param pat - the substring to look for
 *  @return returns a boolean vector indicating if the pattern is a substring of the input string in that position
 *  @leakage{None}
 */
template <domain D : shared3p>
D bool[[1]] kl_strEqPrefixes (D xor_uint8[[1]] str, D xor_uint8[[1]] pat) {
    uint n = kl_strLength(str),
         m = kl_strLength(pat);

    assert (m <= n);

    // Number of m length prefixes in n:
    uint k = m * (n - m + 1);

    D bool [[2]] eqs;
    { // Compute all prefix equalities:
        D xor_uint8 [[1]] suffixes (k);
        D xor_uint8 [[1]] patterns (k);
        for (uint i = m, off = 0; i <= n; ++ i) {
            suffixes[off : off + m] = str[i - m : i];
            patterns[off : off + m] = pat;
            off += m;
        }

        eqs = reshape (suffixes == patterns, n - m + 1, m);
    }

    D bool [[1]] acc (n - m + 1) = true;
    for (uint i = 0; i < m; ++ i) {
        acc = acc & eqs[:, i];
    }

    return acc;
}
/** @}*/

/** \addtogroup kl_strcontains
 *  @{
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref xor_uint8 "xor_uint8"
 *  @brief Function for checking whether a string contains a pattern or not
 *  @pre the needle is not longer than the haystack
 *  @param str - the haystack
 *  @param pat - the needle
 *  @return returns if the needle is found within the haystack
 *  @leakage{None}
 */
template <domain D : shared3p>
D bool kl_strContains (D xor_uint8[[1]] str, D xor_uint8[[1]] pat) {
    return any (kl_strEqPrefixes (str, pat));
}
/** @}*/

/** \addtogroup kl_strindexof
 *  @{
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref xor_uint8 "xor_uint8"
 *  @brief Function for finding the index of a given pattern in the input string
 *  @pre the needle is not longer than the haystack
 *  @param str - the haystack
 *  @param pat - the needle
 *  @return returns the position of the needle in the haystack
 *  @return returns size(str) if the pat is not a substring of str
 *  @leakage{None}
 */
template <domain D : shared3p>
D uint kl_strIndexOf (D xor_uint8[[1]] str, D xor_uint8[[1]] pat) {
    uint n = size (str),
         m = size (pat);

    assert (m <= n);
    if (m == 0)
        return 0;

    uint k = n - m + 1;
    D bool [[1]] zeros (n);
    zeros[:k] = kl_strEqPrefixes (str, pat);
    return truePrefixLength (! zeros);
}
/** @}*/

/** \addtogroup kl_strhamming
 *  @{
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref xor_uint8 "xor_uint8"
 *  @brief Function for finding the number of bytes two inputs differ in
 *  @param s,t - input string vectors of supported type
 *  @pre the input strings are of equal length
 *  @return returns the number of bytes that the inputs differ in
 *  @leakage{None}
 */
template <domain D : shared3p>
D uint kl_strHamming (D xor_uint8[[1]] s, D xor_uint8[[1]] t) {
    assert (size (s) == size (t));
    return sum ((uint) (s != t));
}
/** @}*/

/** \addtogroup kl_strlevenshtein
 *  @{
 *  @note **D** - shared3p protection domain
 *  @note Supported types - \ref xor_uint8 "xor_uint8"
 *  @note this is the classic dynamic programming implementation of Levenshtein distance that's simply tuned to minimize the number of the "min" syscalls. Naive approach would invoke "min" O(n*m) times for strings of length n and m. The following implementation invokes the syscall O(n+m) times by computing the table diagonal at a time.
 *  @param s,t - input string vectors of supported type
 *  @brief Function for finding the edit distance of two input strings
 *  @return returns the edit distance of the two input strings
 *  @leakage{None}
 */
template <domain D : shared3p>
D uint kl_strLevenshtein (D xor_uint8[[1]] s, D xor_uint8[[1]] t) {
    uint n = size (s),
         m = size (t);

    if (n < m) {
        return kl_strLevenshtein (t, s);
    }

    uint diagCount = n + m;

    //
    // We are flattening the 2D grid so that the diagonals of the grid are consecutively in the array.
    // For that we comput the diagonal offsets:
    //   diagOff - offset of i'th diagonal
    //   diagEnd - end of i'th diagonal
    uint[[1]] diagOff (diagCount), diagEnd (diagCount);
    D uint[[1]] neqs;
    {
        uint nm = n * m;
        uint [[1]] ridx (nm), cidx (nm);
        for (uint s = 0, count = 0; s < diagCount; ++s) {
            diagOff[s] = count;
            for (uint i = 0; i < n; ++i) {
                if (s - i < m) {
                    ridx[count] = i;
                    cidx[count] = s - i;
                    ++count;
                }
            }

            diagEnd[s] = count;
        }

        D xor_uint8[[1]] ss (nm);
        D xor_uint8[[1]] ts (nm);

        for (uint i = 0; i < nm; ++i) {
            ss[i] = s[ridx[i]];
            ts[i] = t[cidx[i]];
        }

        neqs = (uint) (ss != ts);
    }

    //
    // Previous and current diagonal of one unit wider and taller table.
    D uint [[1]] prevDiag (1) = 0;
    D uint [[1]] currDiag ((uint)(m > 0) + (uint)(n > 0)) = 1;

    uint extendStart = (uint) (m > 1), extendEnd = (uint) (n > 1);
    for (uint i = 0; i < diagCount; ++i) {
        uint len = size (currDiag);
        {
            // TODO: can this trimming be avoided?
            uint prevDiagLen = size (prevDiag);
            uint startOff = (uint) (prevDiagLen >= len);
            uint endOff = prevDiagLen - (uint) (prevDiagLen > len);
            prevDiag = prevDiag[startOff:endOff];
        }

        if (i + 2 > m) extendStart = 0;
        if (i + 2 > n) extendEnd = 0;

        D uint[[1]] temp (extendStart + (diagEnd[i] - diagOff[i]) + extendEnd) = i + 2;
        temp[extendStart:size(temp)-extendEnd] =
            min (neqs[diagOff[i]:diagEnd[i]] + prevDiag,
                 min(1 + currDiag[1:], 1 + currDiag[:len-1]) );

        prevDiag = currDiag;
        currDiag = temp;
    }

    return prevDiag[0];
}
/** @}*/

/** @}*/

/** \addtogroup cw128genkey
 *  @{
 *  @note **D** - shared3p protection domain
 *  @param rowLen - length of hash function input in bytes
 *  @brief Generates key for 128-bit Carter-Wegman hash
 *  @leakage{None}
 */
template <domain D : shared3p>
D xor_uint8[[1]] cw128GenKey(uint rowLen) {
    uint keyLen = 128; // Must be in bits!
    D xor_uint8[[1]] key (keyLen * rowLen);
    key = randomize(key);
    return key;
}
/** @} */

/** \addtogroup cw128hash
 *  @{
 *  @note **D** - shared3p protection domain
 *  @param data - data vector
 *  @param rowLen - length of one data element in bytes
 *  @brief Hashes values in the input vector into 128-bit hashes using the Carter-Wegman algorithm
 *  @leakage{None}
 */
template <domain D : shared3p>
D xor_uint8[[1]] cw128Hash(D xor_uint8[[1]] data, uint rowLen) {
    assert (rowLen > 0);
    assert (size(data) % rowLen == 0);
    D xor_uint8[[1]] key = cw128GenKey(rowLen);
    return cw128Hash(data, key, rowLen);
}
/** @} */

/** \addtogroup cw128hash_key
 *  @{
 *  @note **D** - shared3p protection domain
 *  @param data - data vector
 *  @param key - key used for hashing. See \ref cw128genkey cw128GenKey.
 *  @param rowLen - length of one data element in bytes
 *  @brief Hashes values in the input vector into 128-bit hashes using the Carter-Wegman algorithm
 *  @leakage{None}
 */
template <domain D : shared3p>
D xor_uint8[[1]] cw128Hash(D xor_uint8[[1]] data, D xor_uint8[[1]] key, uint rowLen) {
    assert (rowLen > 0);
    assert (size(data) % rowLen == 0);
    assert (size(key) == 128 * rowLen);
    uint keyLenInBytes = 16;
    uint rows = size(data) / rowLen;
    D xor_uint8[[1]] results (keyLenInBytes * rows);
    __syscall("shared3p::cw128_xor_uint8_vec",
        __domainid (D), key, data, results);
    return results;
}
/** @} */

/** @}*/
