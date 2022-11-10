package com.shares.app.util;

import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.provider.LineChartDataProvider;

public interface MyLineChartDataProvider extends LineChartDataProvider {
     void notifyView();
}
