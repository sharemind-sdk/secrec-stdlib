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
import shared3p;
import shared3p_statistics_pca;
import test_utility;
import matrix;
import shared3p_matrix;

domain pd_shared3p shared3p;

template<type T>
bool gspca_test (T data) {
    // Data generated with excel
    pd_shared3p T[[1]] a = {0.202202961100000, 1.80328265510000, 3.39392157800000, 3.69801879240000, 4.49333022110000, 6.79692193240001, 5.55933075300001, 8.15739905980001, 8.56571470900001, 9.86365336290001, 11.0543186156000, 12.0836696349000, 13.0858527878000, 13.9016389750000, 15.0881249718000, 15.4353175961000, 16.9093366933000, 17.5808554706000, 18.2093110489000, 19.3182580609000};
    pd_shared3p T[[1]] b = {1.08838755100000, 2.41419776690000, 2.89308768240000, 3.76696167520000, 4.60708128200001, 5.28552965750001, 6.89921271970001, 7.69332673240001, 8.83360609160001, 9.14030188050001, 10.0584605350000, 13.0022616981000, 12.7374739764000, 13.1842909915000, 14.9045971377000, 15.8981042443000, 18.3700422125000, 17.0139085092000, 20.3314648714000, 19.9746426946000};

    pd_shared3p T[[2]] data (20, 2);
    data[:, 0] = a;
    data[:, 1] = b;

    PCAResult<pd_shared3p, T> result = gspca (data, 2::uint, 10::uint, PCA_RETURN_LOADS | PCA_RETURN_VARIANCES | PCA_RETURN_PROPORTIONS);

    // Calculate the error
    T[[2]] publicData = declassify (data);
    T[[2]] x = declassify (result.loads);
    T[[2]] mat = matrixMultiplication (matrixMultiplication (publicData, x), transpose (x)) - publicData;
    T[[1]] flat = reshape (mat, size (mat));
    T error = sqrt (sum (flat * flat));

    print("Reconstruction error");
    if (error > 0.05)
        return false;

    T[[1]] var = declassify (result.variances);
    T[[1]] prop = declassify (result.proportions);
    T[[1]] correctVar = {67.53930837637745, 0.380297583527863};
    T[[1]] correctProp = {0.9944027, 0.005597302};

    // TODO: 1e-2 is too high
    print ("Variances");
    if (!all ((abs (var - correctVar) / correctVar) < 1e-2))
        return false;

    print ("Proportions");
    if (!all ((abs (prop - correctProp) / correctProp) < 1e-2))
        return false;

    return true;
}


void main () {
    // TODO: Currently we don't support fixed point values in all shared3p
    // modules. The current implementation of PCA uses fixed point values.
    string test_prefix = "GSPCA";
    //if the input is 32 bit, the function crahses
    test (test_prefix, gspca_test (0::float32), 0::float32);
    test (test_prefix, gspca_test (0::float64), 0::float64);

    test_report ();
}
