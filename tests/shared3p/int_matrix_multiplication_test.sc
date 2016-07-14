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

import test_utility;
import stdlib;

kind shared3p;
domain pd_shared3p shared3p;

template<domain D, type T>
bool unit(uint64[[1]] l, uint64[[1]] m, uint64[[1]] n, D T[[1]] x, D T[[1]] y, T[[1]] z) {
   D T[[1]] result (size(z));
   __syscall("shared3p::mat_mult_$T\_vec", __domainid(D), x, y, result, __cref l, __cref m, __cref n);
   return all(declassify(result) == z);
}

bool unit8() {
   uint64[[1]] l (10) = {0, 1, 5, 1, 5, 1, 2, 5, 1, 2};
   uint64[[1]] m (10) = {4, 4, 1, 5, 3, 4, 4, 5, 5, 1};
   uint64[[1]] n (10) = {5, 0, 1, 4, 3, 3, 4, 0, 0, 3};
   pd_shared3p uint8[[1]] x (73) = {23, 205, 38, 132, 139, 171, 2, 206, 2, 39, 21, 156, 36, 53, 133, 190, 29, 232, 159, 248, 141, 12, 168, 24, 106, 115, 52, 138, 29, 63, 69, 163, 101, 137, 63, 83, 234, 18, 182, 234, 4, 79, 95, 56, 197, 65, 248, 168, 161, 5, 135, 27, 21, 187, 163, 103, 82, 32, 114, 54, 175, 238, 219, 137, 40, 231, 78, 129, 48, 24, 77, 29, 139};
   pd_shared3p uint8[[1]] y (81) = {80, 170, 225, 72, 165, 70, 209, 80, 6, 160, 30, 171, 130, 48, 128, 72, 16, 18, 70, 190, 200, 28, 22, 105, 198, 221, 203, 112, 233, 14, 199, 108, 119, 83, 73, 176, 95, 244, 44, 182, 36, 79, 93, 202, 70, 241, 192, 140, 20, 157, 95, 231, 10, 248, 163, 155, 101, 90, 19, 63, 205, 182, 127, 217, 201, 189, 82, 109, 147, 229, 177, 219, 117, 204, 189, 243, 235, 239, 214, 153, 54};
   uint8[[1]] z (41) = {152, 152, 144, 240, 144, 29, 165, 109, 155, 219, 115, 59, 178, 87, 104, 171, 165, 74, 72, 126, 247, 164, 18, 81, 99, 247, 36, 74, 19, 123, 26, 248, 186, 66, 76, 62, 85, 30, 50, 19, 82};
   return unit(l, m, n, x, y, z);
}

bool unit16() {
   uint64[[1]] l (10) = {3, 5, 0, 4, 5, 0, 3, 2, 2, 2};
   uint64[[1]] m (10) = {4, 2, 1, 1, 0, 2, 4, 3, 5, 5};
   uint64[[1]] n (10) = {5, 1, 4, 2, 2, 3, 5, 3, 0, 1};
   pd_shared3p uint16[[1]] x (64) = {25801, 53985, 8172, 30027, 33490, 19103, 33238, 18005, 3868, 53938, 9228, 15003, 677, 9665, 56162, 57269, 23288, 40106, 6693, 18599, 20754, 28678, 56336, 48429, 47326, 10257, 19475, 15981, 29335, 24881, 3752, 26188, 51168, 16686, 61798, 63411, 52844, 11132, 48808, 49309, 5363, 40805, 25734, 30559, 61579, 14476, 6886, 3600, 43648, 53778, 64106, 28149, 16248, 40032, 58537, 65388, 59017, 27553, 29647, 49906, 3743, 49706, 23432, 25828};
   pd_shared3p uint16[[1]] y (68) = {40163, 15016, 10188, 56396, 12023, 37267, 23054, 53572, 14444, 39516, 65287, 60247, 63673, 46749, 24666, 43082, 49606, 14145, 8376, 58354, 27011, 30205, 24188, 24691, 52027, 6093, 14808, 41862, 47609, 8766, 13461, 55531, 38383, 20899, 50262, 5929, 28811, 59365, 56074, 3765, 9894, 55528, 27930, 7297, 40679, 26167, 36579, 47303, 19894, 33342, 32384, 833, 22915, 12286, 57287, 60864, 45394, 44944, 31907, 57297, 44334, 44954, 17610, 15343, 60461, 20100, 17785, 16218};
   uint16[[1]] z (61) = {30096, 4204, 14983, 57404, 39337, 46063, 29946, 8399, 33474, 11352, 56620, 53010, 29343, 33324, 27578, 35884, 17415, 51946, 44538, 21028, 15744, 24672, 41720, 44174, 26960, 7732, 38744, 52198, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14226, 2986, 51055, 21125, 60067, 51312, 31312, 56422, 11178, 48576, 34639, 10908, 42202, 52180, 15103, 12946, 60709, 20667, 44533, 46648, 7094, 27462, 21753};
   return unit(l, m, n, x, y, z);
}

bool unit32() {
   uint64[[1]] l (10) = {2, 3, 5, 5, 3, 5, 4, 3, 4, 4};
   uint64[[1]] m (10) = {0, 2, 0, 5, 0, 5, 1, 2, 2, 1};
   uint64[[1]] n (10) = {5, 3, 4, 0, 2, 2, 1, 1, 4, 3};
   pd_shared3p uint32[[1]] x (78) = {1059529681, 1386399741, 4219625116, 4147733266, 2555930048, 1113494172, 3137892018, 3524638031, 1463231874, 3137409858, 2171462005, 1501701856, 1039912604, 2208256169, 956315855, 3424170534, 566439652, 1741657936, 1826438595, 1966117783, 3626512798, 3809458017, 2725032950, 279980870, 1795057695, 3960113506, 34730068, 1368531010, 1255685205, 3326883860, 4184816623, 313568665, 2424494546, 1549835123, 2641410687, 1745741537, 234276975, 1969570455, 2823518629, 1408022088, 1407896340, 1585339108, 1422622518, 2654342199, 2042600916, 1198740194, 4057742118, 1299405587, 1843960774, 1521362300, 829759397, 972957090, 2767525045, 542525684, 2251456923, 3176497428, 4060976782, 557014644, 1696114014, 2251634520, 2914632413, 3220730965, 387891315, 2510800801, 3318523296, 3141255562, 105394593, 1366268127, 3269078723, 2032877120, 2697316948, 3195489558, 411660540, 2046340898, 2650826738, 963233258, 676393947, 3526285817};
   pd_shared3p uint32[[1]] y (30) = {3969379685, 607924593, 228043842, 4105172045, 2030740679, 2124395346, 1830406130, 1579771732, 1632421158, 301590552, 2895880337, 792564904, 3348053887, 2793439585, 263839476, 152239961, 255643889, 1797158002, 2682722396, 824070862, 1123535707, 1979793346, 1591793049, 447028801, 1406309402, 645077026, 3769461905, 2503093721, 3982563465, 2261427670};
   uint32[[1]] z (90) = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3462850958, 279402988, 1468767724, 2047325174, 1727998426, 3724969468, 4163218860, 2802225412, 2837005688, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2493372262, 4273786036, 1754393237, 2569313048, 1769625159, 1908329438, 2934566572, 1133785481, 620592939, 819819247, 2221284270, 3981060916, 1989676414, 3048249816, 3385831670, 2108269842, 1444069080, 3201725485, 1560265185, 4225855648, 12011656, 4042024234, 3383333585, 133214534, 1928295371, 3327188526, 2340819992, 1693863060, 386122154, 2951091818, 2298244872, 1068111228, 2203958494, 669099554, 78139522, 139058764, 472311130, 1137891898, 3516875676, 2135045027, 1328154675, 3987079186, 3853093137, 4225433153, 975287078};
   return unit(l, m, n, x, y, z);
}

bool unit64() {
   uint64[[1]] l (10) = {0, 1, 4, 4, 3, 3, 3, 0, 5, 5};
   uint64[[1]] m (10) = {1, 1, 3, 1, 0, 5, 1, 1, 5, 4};
   uint64[[1]] n (10) = {1, 2, 4, 4, 5, 1, 4, 4, 2, 0};
   pd_shared3p uint64[[1]] x (80) = {11488484825003889846, 8169435468782854783, 13059913971195365629, 16693772741174220532, 10246025930880278981, 9423702995320721483, 1996811005093400105, 4074944282326813990, 14804077369550098619, 5365110556151898239, 3953515245336175749, 6061610324717690085, 5897718453748418157, 13281604776916113152, 13078733291747644209, 15236434180307191177, 7727258940568786286, 17118364679332433953, 10093850802814879646, 11279309619046153226, 14690333026771509565, 11506769107831325747, 5806237352409179502, 12821176011104254595, 12293230075538184398, 177633387771760149, 5117519393658670006, 11526386982926987111, 11587953869796993122, 6884844270946588228, 16841790828761767890, 15129006117766904236, 11198324324068418114, 13434925877016942133, 11317685939965561469, 4506400683586973107, 8938600548603734485, 12022739972589336994, 224313895638242032, 6350023166108536281, 6342542004827512617, 9555426488407074576, 12858957998419158969, 2757525310107646787, 11202644433627633531, 15168355466666419827, 8288269164468447045, 16366727602322527815, 16041759394573464433, 3284295480978536438, 6084506102527268314, 18395831120527836332, 116081207659936944, 17290750515221491997, 16114079817358204802, 11096176895514282832, 13567001913548219340, 17802862259587057127, 11056557524030629481, 16178489873394637207, 17183986163709298118, 17075383449213415402, 14624138361631684832, 2867763377722648306, 1839441175215536934, 16246815841723885566, 6608099459890833486, 553940140588473901, 534599158058233991, 15557070082385123930, 2787857945466279858, 11871983801860642532, 14410872983409805765, 11809310136301276268, 2906394818666841046, 301960289350174151, 5618878651852532974, 16983168342640538747, 3217882358545816310, 10273717231054711552};
   pd_shared3p uint64[[1]] y (42) = {7719922004940585079, 13141532161815953043, 12765533053281993584, 14799668082181386907, 14189559290878042183, 13536975749433377516, 992130304167683117, 5571997659916263871, 1198888270515557517, 2809357079742657183, 9621589758883881566, 10585152461833620642, 13327808103957464389, 12746283673992631910, 14751533655533195781, 505148621852646499, 8660100217767260778, 9903984247500878699, 6379171864466778756, 18203312646094154441, 10618803931749281384, 16496356183559359488, 6175249143754816505, 16271228672895217199, 4655993972909908433, 15346202201037666760, 9939975409160203903, 478996027748454194, 1335883083682436051, 15532910829482240994, 3007192289839529725, 5026306781067527279, 3663423136130603898, 602864157613808551, 9313261830611274841, 17404461432735299214, 4810337197123354835, 3593188870429936466, 14820218036326039651, 8745776184849113394, 3307411190510038420, 8487449098158989030};
   uint64[[1]] z (74) = {11022154360423807106, 1442925626505834912, 15031526295738890512, 18091656482435275862, 1634379742593214319, 29679901135056381, 3876253060153942318, 14319187761976439039, 17638211449785129607, 3942450042830328, 9623703538100960485, 14095390763535201732, 1207049272822297031, 10342498657721905875, 18179422279079761756, 2808553364000025445, 3075512247307750981, 11755984083157970840, 16952137125177500928, 14471401610320283136, 8520893227261648128, 549415304142183424, 17343200660403996659, 10448543118997800010, 7101455078835210363, 1451558559575125828, 13658858227583459323, 6859930164145867962, 9983723163384322883, 7729304308763366564, 4303052866126156170, 16301514823545215884, 15093936387801194234, 9279473527655864504, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 12697494115041614539, 6693688766822926957, 5919319483313039749, 4947091756872645090, 1735232125843994000, 14531563316494146238, 6157846508520869604, 6416712733652989509, 13446020908744369768, 10301096849146301003, 3697484507612377434, 9418655251934260493, 7086959415580018344, 11367019303329966595, 802945841799192426, 18395554342519023909, 12348299462772506789, 4818248561698167706, 527076931683538553, 11650426154103386171, 17485664107560268831, 14419447191399738399, 13356854422811672180, 15295930733001319512, 16088858419757284994};
   return unit(l, m, n, x, y, z);
}

string name(int64 n) {
    return "[uint$n\] Fast matrix multiplication";
}

void main() {
   test(name(8), unit8());
   test(name(16), unit16());
   test(name(32), unit32());
   test(name(64), unit64());
   test_report();
}

