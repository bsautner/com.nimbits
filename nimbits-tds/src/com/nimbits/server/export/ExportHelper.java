package com.nimbits.server.export;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointName;

import java.util.Map;


public interface ExportHelper {
    String exportPointDataToCSVSeparateColumns(final Map<PointName, Point> points);
    String exportPointDataToDescriptiveStatistics(final Map<PointName, Point> points) throws NimbitsException;

    String exportPointDataToPossibleContinuation(Map<PointName, Point> points) throws NimbitsException;
}
