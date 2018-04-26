/*
 * Copyright  (C) 2015 Cybernetica
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
import shared3p_statistics_summary;
import test_utility;

domain pd_shared3p shared3p;


bool mean_test(int32 proxy) {
	pd_shared3p int32[[1]] a(8) = {1, 4, 5, 10, 5, 5, 5, 6};
	pd_shared3p float32 result = mean(a);
	float32 relative_error = abs(declassify(result) - 5.125) / 5.125;
    return isNegligible(relative_error);
}

bool mean_test(int64 proxy) {
	pd_shared3p int64[[1]] a(2) = {INT64_MAX, INT64_MIN};
	pd_shared3p float64 result = mean(a);
	float64 relative_error = abs(declassify(result) + 0.5) / 0.5;
	return isNegligible(relative_error);
}

bool mean_test_filter(int32 proxy) {
	pd_shared3p int32[[1]] a(3) = {INT32_MAX, INT32_MIN, 1000};
	pd_shared3p bool[[1]] mask(3) = {true, true, false};
	pd_shared3p float32 result = mean(a, mask);
	float32 relative_error = abs(declassify(result) + 0.5) / 0.5;
	return isNegligible(relative_error);
}

bool mean_test_filter(int64 proxy) {
	pd_shared3p int64[[1]] a(3) = {INT64_MIN, INT64_MAX, 1000};
	pd_shared3p bool[[1]] mask(3) = {true, true, false};
	pd_shared3p float64 result = mean(a, mask);
	float64 relative_error = abs(declassify(result) + 0.5) / 0.5;
	return isNegligible(relative_error);
}

bool mean_test(float32 proxy) {
    pd_shared3p float32[[1]] a = {74787728.022784,68267185.4738146,-165388448.163867,185310777.043924,326456813.607365,292405887.506902,-31744567.2117174,381006319.541484,338435923.447832,-436026526.615024};
    pd_shared3p float32 result = mean(a);
    float32 err = abs(declassify(result) - 103351109.26535) / 103351109.26535;
    return isNegligible(err);
}

bool mean_test(float64 proxy) {
    pd_shared3p float64[[1]] a = {74787728.022784,68267185.4738146,-165388448.163867,185310777.043924,326456813.607365,292405887.506902,-31744567.2117174,381006319.541484,338435923.447832,-436026526.615024};
    pd_shared3p float64 result = mean(a);
    float64 err = abs(declassify(result) - 103351109.26535) / 103351109.26535;
    return isNegligible(err);
}

bool mean_test_filter(float32 proxy) {
    pd_shared3p float32[[1]] a = {74787728.022784,68267185.4738146,-165388448.163867,185310777.043924,326456813.607365,292405887.506902,-31744567.2117174,381006319.541484,338435923.447832,-436026526.615024};
    pd_shared3p bool[[1]] mask(size(a)) = true;
    mask[0] = false;
    pd_shared3p float32 result = mean(a, mask);
    float32 err = abs(declassify(result) - 106524818.292301) / 106524818.292301;
    return isNegligible(err);
}

bool mean_test_filter(float64 proxy) {
    pd_shared3p float64[[1]] a = {74787728.022784,68267185.4738146,-165388448.163867,185310777.043924,326456813.607365,292405887.506902,-31744567.2117174,381006319.541484,338435923.447832,-436026526.615024};
    pd_shared3p bool[[1]] mask(size(a)) = true;
    mask[0] = false;
    pd_shared3p float64 result = mean(a, mask);
    float64 err = abs(declassify(result) - 106524818.292301) / 106524818.292301;
    return isNegligible(err);
}

void main () {
	string test_prefix = "Mean";
	test(test_prefix, mean_test(0::int32), 0::int32);
	test(test_prefix, mean_test(0::int64), 0::int64);
    test(test_prefix, mean_test(0f32), 0f32);
    test(test_prefix, mean_test(0f64), 0f64);

	test_prefix = "Mean (filter)";
	test(test_prefix, mean_test_filter(0::int32), 0::int32);
	test(test_prefix, mean_test_filter(0::int64), 0::int64);
    test(test_prefix, mean_test_filter(0f32), 0f32);
    test(test_prefix, mean_test_filter(0f64), 0f64);

	test_report();
}
