/*
    CCU-Historian, a long term archive for the HomeMatic CCU
    Copyright (C) 2011-2018 MDZ (info@ccu-historian.de)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mdz.ccuhistorian.webapp

import groovy.transform.AutoClone
import javax.servlet.http.HttpServletRequest
import mdz.ccuhistorian.TrendDesign
import mdz.hc.DataPoint
import mdz.hc.persistence.DataPointStorage

@AutoClone
public class TrendParameters {

	public static int DEFAULT_WIDTH = 1000
	public static int DEFAULT_HEIGHT = 600
	
	@AutoClone
	public static class Group {
		int height=1
		List<DataPoint> dataPoints=[]
		
		@Override
		public String toString() {
			"(height: $height, dataPoints: $dataPoints.displayName)"
		}
	}
	
	int width, height
	TimeRange timeRange
	TrendDesign trendDesign
	TreeMap<Integer /* group id */, Group> groups=new TreeMap<Integer, Group>()

	public static TrendParameters from(HttpServletRequest request, DataPointStorage storage, Map<String, TrendDesign> trendDesigns) {
		// deprecated V1 parameters?
		if (request.getParameter('i')) {
			new TrendParametersV1(request, storage, trendDesigns)
		} else {
			new TrendParametersV2(request, storage, trendDesigns)
		}
	}
	
	public Map<String, String[]> getParameters() {
		Map<String, String[]> params=[:]
		if (width!=DEFAULT_WIDTH) {
			params.w=[width]
		}
		if (height!=DEFAULT_HEIGHT) {
			params.h=[height]
		}
		params << timeRange.parameters
		if (trendDesign.identifier!='default') {
			params.t=[trendDesign.identifier]
		}
		int dpIdx=1
		groups.each { Integer groupId, Group group ->
			params['gh' + groupId]=[group.height] as String[]
			group.dataPoints.each { DataPoint dp ->
				params['dp' + dpIdx]=[dp.idx] as String[]
				params['g' + dpIdx]=[groupId] as String[]
				dpIdx++
			} 
		}
		params
	}
	
	@Override
	public String toString() {
		"width: $width, height: $height, groups: $groups, begin: $timeRange.begin, end: $timeRange.end"
	}
}
