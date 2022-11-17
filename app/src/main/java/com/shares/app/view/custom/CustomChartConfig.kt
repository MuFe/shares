/*
 * Copyright 2021 WangYiqian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 *
 */

package com.shares.app.view.custom

import androidx.annotation.ColorInt
import com.github.wangyiqian.stockchart.*
import com.github.wangyiqian.stockchart.childchart.base.*
import com.github.wangyiqian.stockchart.childchart.timebar.TimeBarConfig
import com.github.wangyiqian.stockchart.listener.OnHighlightListener


class CustomChartConfig(
    height: Int = DEFAULT_TIME_BAR_HEIGHT,
    marginTop: Int = DEFAULT_CHILD_CHART_MARGIN_TOP,
    marginBottom: Int = DEFAULT_CHILD_CHART_MARGIN_BOTTOM,
    onHighlightListener: OnHighlightListener? = null,
    chartMainDisplayAreaPaddingTop: Float = 0f,
    chartMainDisplayAreaPaddingBottom: Float = 0f,
    // 背景色
    @ColorInt var backGroundColor: Int =DEFAULT_TIME_BAR_BG_COLOR,

    // 标签文本大小
    var labelTextSize: Float = DEFAULT_TIME_BAR_LABEL_TEXT_SIZE,
    // 标签文本色
    @ColorInt var labelTextColor: Int = DEFAULT_TIME_BAR_LABEL_TEXT_SIZE_COLOR,
    // 长按显示的标签文本大小
    var highlightLabelTextSize: Float = DEFAULT_TIME_BAR_HIGHLIGHT_LABEL_TEXT_SIZE,
    // 长按显示的标签文本色
    @ColorInt var highlightLabelTextColor: Int = DEFAULT_TIME_BAR_HIGHLIGHT_LABEL_TEXT_COLOR,
    // 长按显示的标签背景色
    @ColorInt var highlightLabelBgColor: Int = DEFAULT_TIME_BAR_HIGHLIGHT_LABEL_BG_COLOR,
    // 时间条样式
    var type: TimeBarConfig.Type = TimeBarConfig.Type.Day()
) : BaseChildChartConfig(
    height,
    marginTop,
    marginBottom,
    onHighlightListener,
    chartMainDisplayAreaPaddingTop,
    chartMainDisplayAreaPaddingBottom
) {

}