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
import shared3p_statistics_common;
import test_utility;
import matrix;

domain pd_shared3p shared3p;

bool cut_test(bool data) {
	pd_shared3p bool[[1]] a (7) = {true, true, true, false, false, true, true};
	
	pd_shared3p bool[[1]] mask (7) = true;
	mask[3:5] = false;
	
	pd_shared3p bool[[1]] b = cut(a, mask);
	
	if (!all(declassify(b)))
		return false;
	
	return true;

}

template<type T>
bool cut_test(T data) {
	pd_shared3p T[[1]] a (7) = {0, 1, 3, 7, 15, 31, 63};
	
	pd_shared3p bool[[1]] mask (7) = true;
	mask[3:5] = false;
	
	pd_shared3p T[[1]] b = cut(a, mask);
	
	if (sum(declassify(b)) != 98)
		return false;
	
	return true;
}


template<type T>
bool cut_test_samples(T data) {
	/*
 	 * Because cut is done on every column, the input and output of cut are transposed. 
	 * This makes the function cut every row so that the function can be tested like
	 * the regular cut function.
	 */
	
	pd_shared3p T[[2]] a (2, 7) = reshape(
		{0, 1, 3, 7, 15, 31, 63,
		 0, 1, 3, 7, 15, 31, 63},
		 2, 7);	
	
	pd_shared3p bool[[1]] mask (7) = true;
	mask[3:5] = false;
	
	printMatrix(declassify(a));
	printVector(declassify(mask));
	
	pd_shared3p T[[2]] b = cut(transpose(a), mask);
	
	printMatrix(declassify(transpose(b)));
	
	T[[1]] c = flatten(declassify(transpose(b)));
	
	if (sum(c[0:5]) != 98 && sum(c[5:10]) != 98)
		return false;

	return true;
}

void main() {
	string test_prefix = "Cut";
	{bool t; test(test_prefix, cut_test(t), t);}
	{uint8 t; test(test_prefix, cut_test(t), t);}
	{uint16 t; test(test_prefix, cut_test(t), t);}
	{uint32 t; test(test_prefix, cut_test(t), t);}
	{uint64 t; test(test_prefix, cut_test(t), t);}
	{int8 t; test(test_prefix, cut_test(t), t);}
	{int16 t; test(test_prefix, cut_test(t), t);}
	{int32 t; test(test_prefix, cut_test(t), t);}
	{int64 t; test(test_prefix, cut_test(t), t);}
	{float32 t; test(test_prefix, cut_test(t), t);}
	{float64 t; test(test_prefix, cut_test(t), t);}
	
	test_prefix = "Cut(multiple samples)";
	{int32 t; test(test_prefix, cut_test_samples(t), t);}
	{int64 t; test(test_prefix, cut_test_samples(t), t);}
	
	test_report();

}