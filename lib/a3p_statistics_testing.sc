/*
 * This file is a part of the Sharemind framework.
 * Copyright (C) Cybernetica AS
 *
 * All rights are reserved. Reproduction in whole or part is prohibited
 * without the written consent of the copyright owner. The usage of this
 * code is subject to the appropriate license agreement.
 */

module a3p_statistics_testing;

import a3p_sort;
import a3p_statistics_common;
import a3p_statistics_summary;
import additive3pp;
import stdlib;


/**
 * @file
 * \defgroup a3p_statistics_testing a3p_statistics_testing.sc
 * \defgroup t_test tTest
 * \defgroup t_test_samples tTest(two sample vectors)
 * \defgroup paired_t_test pairedTTest
 * \defgroup chisq chiSquared
 * \defgroup chisq_cb chiSquared(with codebook)
 * \defgroup wilcoxon_rank_sum wilcoxonRankSum
 * \defgroup wilcoxon_signed_rank wilcoxonSignedRank
 * \defgroup constants constants
 */

/**
 * \addtogroup <a3p_statistics_testing>
 * @{
 * @brief Module with statistical hypothesis tests.
 */

/** \cond */
template<domain D : additive3pp, type T, type FT>
D FT _tTest (D T[[1]] data, D bool[[1]] cases, D bool[[1]] controls, bool variancesEqual) {

    assert (size (data) == size (cases) && size (data) == size (controls));
    uint sizeData = size (data);
    D T[[1]] dataCases (sizeData), dataControls (sizeData);
    D uint32 countCases, countControls;
    D FT varCases, varControls, meanCases, meanControls;
    D FT[[1]] varAndMean (2);

    D T[[1]] mulAPar (sizeData * 2), mulBPar (sizeData * 2), mulRes (sizeData * 2);
    mulAPar[0 : sizeData] = data;
    mulAPar[sizeData : sizeData * 2] = data;
    mulBPar[0 : sizeData] = (T)cases;
    mulBPar[sizeData : sizeData * 2] = (T)controls;

    mulRes = mulAPar * mulBPar;
    dataCases = mulRes[0 : sizeData];
    dataControls = mulRes[sizeData : sizeData * 2];

    countCases = sum ((uint32) cases);
    countControls = sum ((uint32) controls);

    // TODO: many variances and means in parallel?
    meanCases = mean (dataCases, cases);
    varCases = _variance (dataCases, cases, meanCases);

    meanControls = mean (dataControls, controls);
    varControls = _variance (dataControls, controls, meanControls);

    D FT commonStDev, subMean;
    D FT result;

    subMean = meanCases - meanControls;

    if (variancesEqual) {
        D FT[[1]] mulA2 (2), mulB2 (2), mulRes2 (2);
        mulA2[0] = (FT) (countCases - 1);
        mulA2[1] = (FT) (countControls - 1);
        mulB2[0] = varCases;
        mulB2[1] = varControls;
        mulRes2 = mulA2 * mulB2;

        D FT[[1]] invPar (2), resInvPar (2);
        invPar[0] = (FT) countCases;
        invPar[1] = (FT) countControls;
        resInvPar = inv (invPar);

        D FT[[1]] sqrtPar (2), resSqrtPar (2);
        sqrtPar[0] = (mulRes2[0] + mulRes2[1]) / (FT) (countCases + countControls - 2);
        sqrtPar[1] = resInvPar[0] + resInvPar[1];
        resSqrtPar = sqrt (sqrtPar);

        result = subMean / (resSqrtPar[0] * resSqrtPar[1]);

    } else {
        D FT[[1]] divAPar (2), divBPar (2), resDivPar (2);
        divAPar[0] = varCases;
        divAPar[1] = varControls;
        divBPar[0] = (FT) countCases;
        divBPar[1] = (FT) countControls;

        resDivPar = divAPar / divBPar;

        commonStDev = sqrt (resDivPar[0] + resDivPar[1]);
        result = subMean / commonStDev;
    }

    return result;
}

template<domain D : additive3pp, type T, type FT>
D FT[[1]] _tTest (D T[[1]] data1,
                  D bool[[1]] ia1,
                  D T[[1]] data2,
                  D bool[[1]] ia2,
                  bool variancesEqual)
{
    assert (size (data1) == size (ia1));
    assert (size (data2) == size (ia2));

    uint size1 = size (data1);
    uint size2 = size (data2);

    D T[[1]] datas (size1 + size2);
    D bool[[1]] ias (size1 + size2);
    datas[:size1] = data1;
    datas[size1:] = data2;
    ias[:size1] = ia1;
    ias[size1:] = ia2;

    datas = datas * (T) ias;
    data1 = datas[:size1];
    data2 = datas[size1:];

    D FT mean1 = mean (data1, ia1);
    D FT mean2 = mean (data2, ia2);
    D FT var1 = _variance (data1, ia1, mean1);
    D FT var2 = _variance (data2, ia2, mean2);
    D uint32 count1 = sum ((uint32) ia1);
    D uint32 count2 = sum ((uint32) ia2);

    /* degrees of freedom and test statistic */
    D FT[[1]] result(2);

    if (variancesEqual) {
        D FT[[1]] mulL = {(FT) count1 - 1, (FT) count2 - 1};
        D FT[[1]] mulR = {var1, var2};
        D FT[[1]] mulRes = mulL * mulR;

        D FT[[1]] inversed = {(FT) count1, (FT) count2};
        inversed = inv (inversed);

        D FT[[1]] roots = {(mulRes[0] + mulRes[1]) / (FT) (count1 + count2 - 2),
                           inversed[0] + inversed[1]};
        roots = sqrt (roots);

        result[0] = (FT) (count1 + count2 - 2);
        result[1] = (mean1 - mean2) / (roots[0] * roots[1]);
    } else {
        D FT[[1]] divL = {var1, var2};
        D FT[[1]] divR = {(FT) count1, (FT) count2};
        D FT[[1]] divRes = divL / divR;
        D FT commonStDev = sqrt (divRes[0] + divRes[1]);

        result[1] = (mean1 - mean2) / commonStDev;

        /*
        df = (abs(var1 / count1) + abs(var2 / count2))**2
        /
        ((var1 / count1)**2 / (count1 - 1) +
        (var2 / count2)**2 / (count2 - 1))
        */

        D FT[[1]] absDiv = abs (divRes);
        D FT[[1]] sqrDiv = _power (divRes, 2 :: uint);
        divR = {(FT) count1 - 1, (FT) count2 - 1};
        divRes = sqrDiv / divR;

        result[0] = _power (absDiv[0] + absDiv[1], 2 :: uint) / (divRes[0] + divRes[1]);
    }

    return result;
}
/** \endcond */

/**
 * \addtogroup <constants
 * @{
 * @brief Constants used for specifying the alternative hypothesis.
 */
int64 ALTERNATIVE_LESSER    = 0;
int64 ALTERNATIVE_GREATER   = 1;
int64 ALTERNATIVE_TWO_SIDED = 2;
/** @} */


/** \addtogroup <t_test>
 *  @{
 *  @brief Perform t-tests
 *  @note **D** - additive3pp protection domain
 *  @note Supported types - \ref int32 "int32" / \ref int64 "int64"
 *  @param data - input vector
 *  @param cases - vector indicating which elements of the input
 *  vector belong to the first sample
 *  @param controls - vector indicating which elements of the input
 *  vector belong to the second sample
 *  @param variancesEqual - indicates if the variances of the two
 *  samples should be treated as equal
 *  @return returns the test statistic
 */
template<domain D : additive3pp>
D float32 tTest (D int32[[1]] data, D bool[[1]] cases, D bool[[1]] controls, bool variancesEqual) {
    return _tTest (data, cases, controls, variancesEqual);
}

template<domain D : additive3pp>
D float64 tTest (D int64[[1]] data, D bool[[1]] cases, D bool[[1]] controls, bool variancesEqual) {
    return _tTest (data, cases, controls, variancesEqual);
}
/** @} */

/** \addtogroup <t_test_samples>
 *  @{
 *  @brief Perform t-tests
 *  @note **D** - additive3pp protection domain
 *  @note Supported types - \ref int32 "int32" / \ref int64 "int64"
 *  @param data1 - first sample
 *  @param ia1 - vector indicating which elements of the first sample are available
 *  @param data2 - second sample
 *  @param ia2 - vector indicating which elements of the second sample are available
 *  @param variancesEqual - indicates if the variances of the two
 *  samples should be treated as equal
 *  @return returns a two element vector. The first element is the
 *  degrees of freedom and the second is the test statistic.
 */
template<domain D : additive3pp>
D float32[[1]] tTest (D int32[[1]] data1,
                      D bool[[1]] ia1,
                      D int32[[1]] data2,
                      D bool[[1]] ia2,
                      bool variancesEqual)
{
    return _tTest (data1, ia1, data2, ia2, variancesEqual);
}

template<domain D : additive3pp>
D float64[[1]] tTest (D int64[[1]] data1,
                      D bool[[1]] ia1,
                      D int64[[1]] data2,
                      D bool[[1]] ia2,
                      bool variancesEqual)
{
    return _tTest (data1, ia1, data2, ia2, variancesEqual);
}
/** @} */

/** \cond */
template <domain D : additive3pp, type T, type FT>
D FT _pairedTTest (D T[[1]] sample1, D T[[1]] sample2, D bool[[1]] filter, FT constant) {

    assert (size (sample1) == size (filter) && size (sample1) == size (sample2));
    uint sizeData = size (sample1);

    D T[[1]] differences = sample1 - sample2;

    D uint32 count = sum ((uint32) filter);
    D T[[1]] filteredData (sizeData);
    filteredData = differences * (T) filter;

    D FT mean = mean (filteredData, filter);
    D FT var = _variance (filteredData, filter, mean);
    D FT stDev = sqrt (var);
    D FT sqrtN = sqrt ((FT) count);

    D FT result;
    if (constant == 0) {
        result = (mean * sqrtN) / stDev;
    } else {
        result = ((mean - constant) * sqrtN) / stDev;
    }

    return result;
}
/** \endcond */


/** \addtogroup <paired_t_test>
 *  @{
 *  @brief Perform paired t-tests
 *  @note **D** - additive3pp protection domain
 *  @note Supported types - \ref int32 "int32" / \ref int64 "int64"
 *  @param sample1 - first sample
 *  @param sample2 - second sample
 *  @param filter - vector indicating which elements of the sample to
 *  include in computing the t value
 *  @param constant - hypothesized difference of means (set to 0 if
 *  testing for equal means)
 *  @return returns the t-value
 */
template <domain D : additive3pp>
D float32 pairedTTest (D int32[[1]] sample1, D int32[[1]] sample2, D bool[[1]] filter, float32 constant) {
    return _pairedTTest (sample1, sample2, filter, constant);
}

template <domain D : additive3pp>
D float64 pairedTTest (D int64[[1]] sample1, D int64[[1]] sample2, D bool[[1]] filter, float64 constant) {
    return _pairedTTest (sample1, sample2, filter, constant);
}
/** @} */


/** \cond */
template <domain D, type T, type FT, type UT>
D FT _chiSquared2Classes (D UT[[2]] contTable, T dummy)
{
    D FT result;
    D UT a, b, c, d;

    a = contTable[0, 0];
    b = contTable[0, 1];
    c = contTable[1, 0];
    d = contTable[1, 1];

    D T x1;
    D UT y1;

    x1 = (T) (a * d) - (T) (b * c);
    x1 = x1 * x1;
    x1 = x1 * (T) (a + b + c + d);

    y1 = (a + b) * (c + d) * (a + c) * (b + d);

    result = (FT) x1 / (FT) y1;

    return result;
}

// For reference, see Test 43 in the book "100 Statistical Tests"
template <domain D, type T, type FT, type UT>
D FT _chiSquaredXClasses (D UT[[2]] contTable, T dummy) {

    // Calculate expected frequencies as {(row subtotal x column subtotal) / total}
    uint[[1]] shapeContingency = shape (contTable);
    uint k = shapeContingency[0];

    D UT[[1]] colSums (k), rowSums (2);
    colSums = contTable[: , 0] + contTable[: , 1];
    rowSums[0] = sum (contTable [: , 0]);
    rowSums[1] = sum (contTable [: , 1]);
    D UT total = rowSums[0] + rowSums[1];
    D UT[[1]] mulParA (2 * k), mulParB (2 * k), mulParRes (2 * k);

    mulParA [0 : k] = colSums;
    mulParA [k : 2 * k] = colSums;
    mulParB [0 : k] = rowSums[0];
    mulParB [k : 2 * k] = rowSums[1];
    mulParRes = mulParA * mulParB;

    D FT[[1]] totals (k * 2) = (FT) total;
    D FT[[1]] expectedFreq = (FT) mulParRes / totals;

    D FT[[1]] flatFreq (k * 2);

    flatFreq[0:k] = (FT)contTable [:, 0];
    flatFreq[k:k * 2] = (FT)contTable [:, 1];

    // Calculate sum ((realFreq - expectedFreq)**2 / expectedFreq) in parallel as much as possible
    D FT[[1]] diffs (k * 2), squares (k * 2), quotients (k * 2);

    diffs = flatFreq - expectedFreq;
    squares = diffs * diffs;
    quotients = squares / expectedFreq;

    return sum (quotients);
}
/** \endcond */

/** \addtogroup <chisq>
 *  @{
 *  @brief Perform Pearson's chi-squared test of independence
 *  @note **D** - any protection domain
 *  @note Supported types - \ref int32 "int32" / \ref int64 "int64"
 *  @note This version does not do any correction so the R equivalent
 *  is chisq.test(contingencyTable, correct=FALSE)
 *  @param contTable - contingency table in the format
 *  <table>
 *  <tr><td></td><td>Cases</td><td>Controls</td></tr>
 *  <tr><td>Option 1</td><td>c1</td><td>d1</td></tr>
 *  <tr><td>Option 2</td><td>c2</td><td>d2</td></tr>
 *  <tr><td>Option 3</td><td>c3</td><td>d3</td></tr>
 *  <tr><td>…</td><td>…</td><td>…</td></tr>
 *  </table>
 *  @return returns the test statistic
 */
template <domain D>
D float32 chiSquared (D uint32[[2]] contTable) {
    uint[[1]] tableShape = shape (contTable);
    assert (tableShape[0] >= 2 && tableShape[1] == 2);

    int32 i;

    if (shape(contTable)[0] == 2)
        return _chiSquared2Classes (contTable, i);
    else
        return _chiSquaredXClasses (contTable, i);
}

template <domain D>
D float64 chiSquared (D uint64[[2]] contTable) {
    uint[[1]] tableShape = shape (contTable);
    assert (tableShape[0] >= 2);
    assert (tableShape[1] == 2);

    int64 i;

    if (shape(contTable)[0] == 2)
        return _chiSquared2Classes (contTable, i);
    else
        return _chiSquaredXClasses (contTable, i);
}
/** @} */


/** \addtogroup <chisq_cb>
 *  @{
 *  @brief Perform Pearson's chi-squared test of independence
 *  @note **D** - any protection domain
 *  @note Supported types - \ref int32 "int32" / \ref int64 "int64"
 *  @note This version does not do any correction so the R equivalent
 *  is chisq.test(contingencyTable, correct=FALSE)
 *  @param data - input vector
 *  @param cases - vector indicating which elements of the input
 *  vector belong to the first sample
 *  @param controls - vector indicating which elements of the input
 *  vector belong to the second sample
 *  @param codeBook - matrix used for creating the contingency
 *  table. The first row contains expected values of the input vector
 *  and the second row contains the classes that these values will be
 *  put into. The classes should begin with 1.
 *  @return returns the test statistic
 */
template <domain D>
D float32 chiSquared (D int32[[1]] data,
                      D bool[[1]] cases,
                      D bool[[1]] controls,
                      uint32[[2]] codeBook)
{

    D uint32[[2]] contTable = contingencyTable ((uint32) data, cases, controls, codeBook);
    return chiSquared (contTable);
}

template <domain D>
D float64 chiSquared (D int64[[1]] data,
                      D bool[[1]] cases,
                      D bool[[1]] controls,
                      uint64[[2]] codeBook)
{
    D uint64[[2]] contTable = contingencyTable ((uint64) data, cases, controls, codeBook);
    return chiSquared (contTable);
}
/** @} */


/** \cond */
template <domain D : additive3pp, type T>
D T _wilcoxonRankSum (D T[[1]] data, D bool[[1]] cases, D bool[[1]] controls) {
    // We assume that the case and control filters are mutually exclusive.
    // The following fixes the filter if they are not.
    D bool[[1]] combinedFilter = cases || controls;
    D T[[2]] dataAndFilters (size(data), 3);
    dataAndFilters[:, 0] = data;
    dataAndFilters[:, 1] = (T)cases;
    dataAndFilters[:, 2] = (T)controls;

    // Remove all values that are neither in the case nor the control groups
    D T[[2]] cutDatabase = cut (dataAndFilters, combinedFilter);

    D T[[2]] sortedDatabase = sortingNetworkSort (cutDatabase, 0 :: uint);

    uint64[[1]] shapeSorted = shape(sortedDatabase);
    T[[1]] ranks (shapeSorted[0]);

    for (uint64 i = 0; i < shapeSorted[0]; i = i + 1){
        ranks[i] = (T)i + 1;
    }

    D T[[1]] rankCases (shapeSorted[0]), rankControls (shapeSorted[0]);
    rankCases = ranks * sortedDatabase[:, 1];
    rankControls = ranks * sortedDatabase[:, 2];

    D T rankSumCases, rankSumControls;
    rankSumCases = sum (rankCases);
    rankSumControls = sum (rankControls);
    D uint32 nCases, nControls;
    nCases = sum ((uint32) cases);
    nControls = sum ((uint32) controls);

    D T uCases = rankSumCases - (T)((nCases * (nCases + 1)) / 2);
    D T uControls = (T)nCases * (T)nControls - uCases;
    D T casesFewer = (T) (uCases < uControls);

    // Obliviously choose the one with fewer elements
    D T w = casesFewer * uCases + ((1 :: T) - casesFewer) * uControls;

    return w;
}
/** \endcond */


/** \addtogroup <wilcoxon_rank_sum>
 *  @{
 *  @brief Perform Wilcoxon rank sum tests
 *  @note **D** - additive3pp protection domain
 *  @note Supported types - \ref int32 "int32" / \ref int64 "int64"
 *  @note T-test requires the populations to be normally
 *  distributed. If the populations are not normally distributed but
 *  are ordinal or continuous then the Wilcoxon rank-sum test can be
 *  used instead.
 *  @param data - input vector
 *  @param cases - vector indicating which elements of the input
 *  vector belong to the first sample
 *  @param controls - vector indicating which elements of the input
 *  vector belong to the second sample
 *  @return returns the test statistic
 */
template <domain D : additive3pp>
D int32 wilcoxonRankSum (D int32[[1]] data, D bool[[1]] cases, D bool[[1]] controls) {
    return _wilcoxonRankSum (data, cases, controls);
}

template <domain D : additive3pp>
D int64 wilcoxonRankSum (D int64[[1]] data, D bool[[1]] cases, D bool[[1]] controls) {
    return _wilcoxonRankSum (data, cases, controls);
}
/** @} */


/** \cond */
template <domain D : additive3pp, type T, type FT>
D FT[[1]] _wilcoxonSignedRank (D T[[1]] sample1,
                               D T[[1]] sample2,
                               D bool[[1]] filter,
                               int64 alternative)
{
    assert (size (sample1) == size (sample2) && size (sample1) == size (filter));

    D T[[2]] unCutSamples (size (sample1), 2);
    unCutSamples[:, 0] = sample1;
    unCutSamples[:, 1] = sample2;
    D T[[2]] cutSamples (size (sample1), 2);
    cutSamples = cut (unCutSamples, filter);

    D T[[1]] cutSample1, cutSample2;
    cutSample1 = cutSamples[:, 0];
    cutSample2 = cutSamples[:, 1];
    uint sizeCut = size (cutSample1);

    D T[[1]] differences = cutSample1 - cutSample2;
    D T[[1]] signs = sign (differences);
    D T[[1]] absDiffs = (T)abs (differences);

    D T[[1]] sortedSigns = _sortBySigns (absDiffs, signs);
    D T countZeroes = sum ((T) (signs == 0));

    D T[[1]] ranks (sizeCut);
    for (uint64 i = 0; i < sizeCut; i = i + 1) {
        ranks[i] = (T) i + 1 - countZeroes;
    }

    D T[[1]] signedRanks = ranks * sortedSigns;
    D T signedRankSum = sum (signedRanks);

    // Calculate z
    D FT zScore;
    D uint32 n = sum ((uint32) filter);
    D FT divisor = (FT) (n * (n + 1) * (2 * n + 1)) / (FT) 6;
    zScore = (FT) signedRankSum;

    // Continuity correction
    assert (alternative == ALTERNATIVE_LESSER ||
            alternative == ALTERNATIVE_GREATER ||
            alternative == ALTERNATIVE_TWO_SIDED);

    if (alternative == ALTERNATIVE_LESSER)
        zScore += 0.5;
    else if (alternative == ALTERNATIVE_GREATER)
        zScore -= 0.5;
    else if (alternative == ALTERNATIVE_TWO_SIDED)
        zScore -= 0.5;

    zScore = zScore / divisor;

    D FT[[1]] res = {(FT) signedRankSum, zScore};

    return res;
}

// An internal helper function for Wilcoxon tests
template <domain D, type T>
D T[[1]] _sortBySigns (D T[[1]] valueToBeSortedBy, D T[[1]] signs) {
    assert (size(valueToBeSortedBy) == size(signs));

    // Generate sorting network
    uint64[[1]] sortnet = generateSortingNetwork (size(signs));

    // We will use this offset to decode the sorting network
    uint64 offset = 0;

    // Extract the number of stages
    uint64 numOfStages = sortnet[offset++];

    for (uint64 stage = 0; stage < numOfStages; stage++) {
        uint64 sizeOfStage = sortnet[offset++];

        D T[[1]] firstVectorS (sizeOfStage);
        D T[[1]] secondVectorS (sizeOfStage);
        D T[[1]] firstVectorZ (sizeOfStage);
        D T[[1]] secondVectorZ (sizeOfStage);
        D bool[[1]] exchangeFlagsVector (sizeOfStage);

        // Set up first comparison vector
        for (uint64 i = 0; i < sizeOfStage; i++) {
            firstVectorS[i] = signs[sortnet[offset]];
            firstVectorZ[i] = valueToBeSortedBy[sortnet[offset]];
            offset++;
        }

        // Set up second comparison vector
        for (uint64 i = 0; i < sizeOfStage; i++) {
            secondVectorS[i] = signs[sortnet[offset]];
            secondVectorZ[i] = valueToBeSortedBy[sortnet[offset]];
            offset++;
        }

        // Perform compares
        exchangeFlagsVector = !((firstVectorZ > secondVectorZ) || ((firstVectorZ == secondVectorZ) && (firstVectorS <= secondVectorS)));

        // Convert to integers
        D T[[1]] integerExchangeFlagsVector (sizeOfStage);
        integerExchangeFlagsVector = (T)exchangeFlagsVector;

        D T[[1]] flippedExchangeFlagsVector (sizeOfStage);
        flippedExchangeFlagsVector = 1 - integerExchangeFlagsVector;

        // Perform exchanges
        D T[[1]] firstFactor (8 * sizeOfStage);
        D T[[1]] secondFactor (8 * sizeOfStage);

        for (uint64 i = 0; i < sizeOfStage; i++) {

            firstFactor[i] = firstVectorS[i];
            firstFactor[i + sizeOfStage] = firstVectorZ[i];
            firstFactor[i + 2 * sizeOfStage] = firstVectorS[i];
            firstFactor[i + 3 * sizeOfStage] = firstVectorZ[i];
            firstFactor[i + 4 * sizeOfStage] = secondVectorS[i];
            firstFactor[i + 5 * sizeOfStage] = secondVectorZ[i];
            firstFactor[i + 6 * sizeOfStage] = secondVectorS[i];
            firstFactor[i + 7 * sizeOfStage] = secondVectorZ[i];

            // Comparison bits
            secondFactor[i] = integerExchangeFlagsVector[i];
            secondFactor[i + sizeOfStage] = integerExchangeFlagsVector[i];
            secondFactor[i + 2 * sizeOfStage] = flippedExchangeFlagsVector[i];
            secondFactor[i + 3 * sizeOfStage] = flippedExchangeFlagsVector[i];
            secondFactor[i + 4 * sizeOfStage] = integerExchangeFlagsVector[i];
            secondFactor[i + 5 * sizeOfStage] = integerExchangeFlagsVector[i];
            secondFactor[i + 6 * sizeOfStage] = flippedExchangeFlagsVector[i];
            secondFactor[i + 7 * sizeOfStage] = flippedExchangeFlagsVector[i];
        }

        // Run the largest multiplication this side of Dantoiine
        D T[[1]] choiceResults (8 * sizeOfStage);
        choiceResults = firstFactor * secondFactor;

        // Finalize oblivious choices
        for (uint64 i = 0; i < sizeOfStage; i++) {
            signs[sortnet[offset]] = choiceResults [i] + choiceResults[i + 6 * sizeOfStage];
            valueToBeSortedBy[sortnet[offset]] = choiceResults [i + sizeOfStage] + choiceResults[i + 7 * sizeOfStage];
            offset++;
        }

        for (uint64 i = 0; i < sizeOfStage; i++) {
            signs[sortnet[offset]] = choiceResults [i + 2 * sizeOfStage] + choiceResults[i + 4 * sizeOfStage];
            valueToBeSortedBy[sortnet[offset]] = choiceResults [i + 3 * sizeOfStage] + choiceResults[i + 5 * sizeOfStage];
            offset++;
        }
    }
    return signs;
}
/** \endcond */


/** \addtogroup <wilcoxon_signed_rank>
 *  @{
 *  @brief Perform Wilcoxon signed rank tests
 *  @note **D** - additive3pp protection domain
 *  @note Supported types - \ref int32 "int32" / \ref int64 "int64"
 *  @note This function does not match the one in R.
 *  @param sample1 - first  sample
 *  @param sample2 - second sample
 *  @param filter - vector indicating which elements of the sample to
 *  include in computing the t value
 *  @param alternative - the type of alternative hypothesis. Lesser -
 *  mean of sample1 is less than mean of sample2, greater - mean of
 *  sample1 is greater than mean of sample2, two-sided - means of
 *  sample1 and sample2 are different
 *  @return returns a vector where the first element is the test
 *  statistic and the second element is the z score. The z score is
 *  continuity corrected. The z score is an approximation and when
 *  there's less than 10 pairs with non-zero difference, it's
 *  incorrect.
 */
template <domain D : additive3pp>
D float32[[1]] wilcoxonSignedRank (D int32[[1]] sample1, D int32[[1]] sample2, D bool[[1]] filter, int64 alternative) {
    return _wilcoxonSignedRank (sample1, sample2, filter, alternative);
}

template <domain D : additive3pp>
D float64[[1]] wilcoxonSignedRank (D int64[[1]] sample1, D int64[[1]] sample2, D bool[[1]] filter, int64 alternative) {
    return _wilcoxonSignedRank (sample1, sample2, filter, alternative);
}
/** @} */
/** @} */
